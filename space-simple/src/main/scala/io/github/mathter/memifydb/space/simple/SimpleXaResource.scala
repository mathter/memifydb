package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.space.KeyValueOperations
import io.github.mathter.memifydb.transaction.xa.XaResourceProvider

import javax.transaction.xa.{XAResource, Xid}

class SimpleXaResource extends XAResource, XaResourceProvider[KeyValueOperations] {
  override def commit(xid: Xid, onePhase: Boolean): Unit = ???

  override def end(xid: Xid, flags: Int): Unit = ???

  override def forget(xid: Xid): Unit = ???

  override def getTransactionTimeout: Int = ???

  override def isSameRM(xares: XAResource): Boolean = ???

  override def prepare(xid: Xid): Int = ???

  override def recover(flag: Int): Array[Xid] = ???

  override def rollback(xid: Xid): Unit = ???

  override def setTransactionTimeout(seconds: Int): Boolean = ???

  override def start(xid: Xid, flags: Int): Unit = ???

  override def xa(xid: Xid): KeyValueOperations = ???
}
