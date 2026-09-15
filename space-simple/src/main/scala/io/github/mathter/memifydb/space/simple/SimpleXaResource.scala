package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.space.KeyValueOperations
import io.github.mathter.memifydb.space.simple.SimpleXaResource.log
import io.github.mathter.memifydb.space.simple.Status.{ROLLING_BACK, STARTED}
import io.github.mathter.memifydb.transaction.xa.{XaException, XaResourceProvider}

import java.lang.ref.Cleaner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.logging.{Level, Logger}
import javax.transaction.xa.{XAException, XAResource, Xid}
import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.compiletime.uninitialized

private class SimpleXaResource private(val space: SimpleSpace) extends XAResource, XaResourceProvider[KeyValueOperations], AutoCloseable {
  private val reentrantLock = new ReentrantReadWriteLock()

  private val readLock = this.reentrantLock.readLock()

  private val writeLock = this.reentrantLock.writeLock()

  private val map: mutable.Map[Xid, XaOperations] = mutable.Map.empty

  private var isClosed: Boolean = uninitialized

  private var transactionTimeout: Long = uninitialized

  private val thread = new Thread(() => {
    while (!Thread.currentThread().isInterrupted) {
      val now = System.currentTimeMillis()

      this.writeLock.lock()

      try {
        this.map.values
          .filter(e => (now - e.created) > this.getTransactionTimeout)
          .foreach(e => this.map.remove(e.xid))
      } finally {
        this.writeLock.unlock()
      }

      try {
        Thread.sleep(SimpleXaResource.cleanup_intermal_millilseconds)
      } catch {
        case e: InterruptedException => {
          Thread.currentThread.interrupt()
          log.severe(s"Cleanup thread is interrupted! resource=${this}")
        }
      }
    }
  })

  def this(space: SimpleSpace, transactionTimeout: Long) = {
    this(space)
    SimpleXaResource.cleaner.register(this, () => {
      log.info(s"Cleaning up XAResource ${this}")

      try {
        this.close();
      } catch {
        case e: Exception => log.log(Level.SEVERE, s"Error closing XAResource resource=${this}", e)
      }
    })

    this.transactionTimeout = transactionTimeout
    this.thread.start()
    this.isClosed = false
  }

  override def commit(xid: Xid, onePhase: Boolean): Unit = {
    log.severe(() => s"Committing XAResource for xid=${xid}, onePhase=${onePhase}, resource=${this}")

    this.withWriteXaOperations(xid, ops => {
      if (onePhase && (ops.status eq Status.ENDED) || (ops.status eq Status.PREPARED)) {
        this.space.operations.withMapWrite(map => {
          ops.map.foreach((k, r) => {
            val origin = map.getOrElse(k, null)

            if (origin eq r.origin) {
              if (null eq r.thisVal) {
                map.remove(k)
              } else {
                map.put(k, r.thisVal)
              }
            }
          })
        })
      } else {
        throw new XaException(s"Illegal state=${ops.status}, must be in state=${if onePhase then Status.ENDED else Status.PREPARED}, resource=${this}", XAException.XAER_RMERR)
      }
    })
  }

  override def end(xid: Xid, flags: Int): Unit = {
    log.severe(() => s"Ending XAResource xid=${xid}, resource=${this}")

    this.withWriteXaOperations(xid, ops => {
      if (ops.status eq Status.STARTED) {
        ops.status = Status.ENDED
      } else {
        throw new XaException(s"Illegal state=${ops.status}, must be in state=${Status.STARTED}, resource=${this}", XAException.XAER_RMERR)
      }
    })
  }

  override def forget(xid: Xid): Unit = {
    log.severe(() => s"forget(${xid}) is called for resource=${this}")

    throw new XaException(s"Heuristic commit/rollback not supported. forget xid=${xid}, resource=${this}", XAException.XAER_NOTA)
  }

  override def getTransactionTimeout: Int = {
    log.severe(() => s"getTransactionTimeout() is called for resource=${this}")

    this.checkClosed()

    (this.transactionTimeout / 1000).asInstanceOf[Int]
  }

  override def isSameRM(xares: XAResource): Boolean = {
    log.severe(() => s"isSameRM(${xares}) is called for resource=${this}")

    this.checkClosed()

    this eq xares
  }

  override def prepare(xid: Xid): Int = {
    log.severe(() => s"prepare(${xid}) is called for resource=${this}")

    this.withWriteXaOperations(xid, ops => {
      ops.status = Status.PREPARED

      if ops.map.isEmpty then XAResource.XA_RDONLY else XAResource.XA_OK
    })
  }

  override def recover(flag: Int): Array[Xid] = {
    log.severe(() => s"recover(${flag}) is called for resource=${this}")

    if (flag != XAResource.TMSTARTRSCAN
      && flag != XAResource.TMENDRSCAN
      && flag != XAResource.TMNOFLAGS
      && flag != (XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN)) {
      throw new XaException(String.format("Invalid flags=%s, resource=%s !", flag, this), XAException.XAER_INVAL)
    }

    this.writeLock.lock()
    this.checkClosed()

    try {
      if ((flag & XAResource.TMSTARTRSCAN) == 0) {
        new Array[Xid](0)
      }
      else {
        this.map
          .values
          .filter(e => (Status.PREPARED eq e.status))
          .map(e => e.xid)
          .toArray
      }
    } finally {
      this.writeLock.unlock()
    }
  }

  override def rollback(xid: Xid): Unit = {
    log.severe(() => s"rollback($xid) is called for resource=${this}")

    this.withWriteXaOperations(xid, ops => {
      ops.status = ROLLING_BACK
      this.map.remove(xid)
    })
  }

  override def setTransactionTimeout(seconds: Int): Boolean = {
    log.severe(() => s"setTransactionTimeout(${seconds}) is called for resource=${this}")

    false
  }

  override def start(xid: Xid, flags: Int): Unit = {
    log.severe(() => s"start(${xid}, ${flags}) is called for resource=${this}")

    if (flags != XAResource.TMNOFLAGS
      && flags != XAResource.TMRESUME
      && flags != XAResource.TMJOIN) {
      throw new XaException(s"Invalid flags=${flags}, resource=${this}!", XAException.XAER_INVAL)
    }

    if (xid == null) {
      throw new XaException(s"xid must not be null! flags=${flags}, resource=${flags}", XAException.XAER_INVAL)
    }

    this.withWriteXaOperations(xid,
      fun = ops => {
        throw new XaException(s"xid=${xid} already exists", XAException.XAER_DUPID)
      },
      ifNoOps = (xid, resource) => {
        resource.map.getOrElseUpdate(xid, new XaOperations(xid, this)).status = STARTED
      }
    )
  }

  override def xa(xid: Xid): KeyValueOperations = {
    this.withReadXaOperations(xid, ops => ops)
  }

  override def close(): Unit = synchronized {
    this.writeLock.lock()

    try {
      if (!this.isClosed) {
        log.info(() => s"Close resource ${this}")

        this.thread.interrupt()
        this.map.clear()
        this.isClosed = true
      } else {
        log.info(() => s"XAResource ${this} already closed")
      }
    } finally {
      this.writeLock.unlock()
    }
  }

  private inline def withReadXaOperations[T](
                                              xid: Xid,
                                              fun: (op: XaOperations) => T,
                                              ifNoOps: (Xid, SimpleXaResource) => T = (xid, resource) => {
                                                throw XaException(s"There is no transaction with xid=${xid}, resource=${resource}", XAException.XAER_NOTA)
                                              },
                                              ifTransactionTimeOuted: (Xid, SimpleXaResource) => T = (xid, resource) => {
                                                throw new XaException(s"Transaction timed out! xid=${xid}, resource=${resource}", XAException.XA_RBTIMEOUT)
                                              }
                                            ): T = {
    this.checkClosed()
    this.readLock.lock()

    try {
      this.map.get(xid)
        .map(op => {
          if (System.currentTimeMillis() - op.created < this.transactionTimeout) {
            fun(op)
          } else {
            ifTransactionTimeOuted(xid, this)
          }
        })
        .getOrElse({
          ifNoOps(xid, this)
        })
    } finally {
      this.readLock.unlock()
    }
  }

  private inline def withWriteXaOperations[T](
                                               xid: Xid,
                                               fun: (ops: XaOperations) => T,
                                               ifNoOps: (Xid, SimpleXaResource) => T = (xid, resource) => {
                                                 throw XaException(s"There is no transaction with xid=${xid}, resource=${resource}", XAException.XAER_NOTA)
                                               },
                                               ifTransactionTimeOuted: (Xid, SimpleXaResource) => T = (xid, resource) => {
                                                 throw new XaException(s"Transaction timed out! xid=${xid}, resource=${resource}", XAException.XA_RBTIMEOUT)
                                               }
                                             ): T = {
    this.checkClosed()
    this.writeLock.lock()

    try {
      this.map.get(xid)
        .map(op => {
          if (System.currentTimeMillis() - op.created < this.transactionTimeout) {
            fun(op)
          } else {
            ifTransactionTimeOuted(xid, this)
          }
        })
        .getOrElse({
          ifNoOps(xid, this)
        })
    } finally {
      this.writeLock.unlock()
    }
  }

  @throws(classOf[XaException])
  private inline def checkClosed(): Unit = {
    if (this.isClosed) {
      throw new XaException(s"XAResource ${this} is already closed", XAException.XAER_RMFAIL)
    }
  }


  override def toString: String = {
    s"SimpleXaResource[space=${this.space}]"
  }
}

private object SimpleXaResource {
  private val log = Logger.getLogger(classOf[SimpleXaResource].getName)

  private val cleanup_intermal_millilseconds = 3000

  private val cleaner = Cleaner.create()
}