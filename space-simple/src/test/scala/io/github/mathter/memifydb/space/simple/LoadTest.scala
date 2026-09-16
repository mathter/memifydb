package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.common.data.ValueSerelizationFactory
import io.github.mathter.memifydb.data.cbor.fasterxml.FasterXmlValueSerializationFactory
import io.github.mathter.memifydb.space.{KeyValueOperations, Space, SpaceFactory}
import org.apache.commons.lang3.{RandomStringUtils, RandomUtils}

import java.util.concurrent.CompletableFuture

object LoadTest {
  val threadCount = 10

  val count = 1_000_000

  def main(args: Array[String]): Unit = {
    val valueFactory = ValueSerelizationFactory.apply(FasterXmlValueSerializationFactory.id)
    val space: Space[KeyValueOperations] = SpaceFactory.apply(Const.id).instance(RandomStringUtils.insecure().nextAlphabetic(10))
    val ops = space.asInstanceOf[Space[KeyValueOperations]].operations
    val keys = (0 to threadCount * count)
      .map(e => RandomUtils.insecure().randomLong())
      .map(valueFactory.serializer.serialize(_))
      .toList

    new Counting("Main", () => {
      ((0 to threadCount)
        .map(e => new Counting("Writer", () => {
          keys.slice(e * count, e * count + count)
            .map(key => {
              val person = Person(
                RandomStringUtils.insecure().nextAlphabetic(10),
                RandomStringUtils.insecure().nextAlphabetic(10),
                RandomUtils.insecure().randomInt()
              )

              ops.put(key, valueFactory.serializer.serialize(person))
            })
        }))
        .map(e => CompletableFuture.runAsync(e))
        .toList
        :::
        (0 to threadCount)
          .map(e => new Counting("Reader", () => {
            keys.foreach(key => {
              ops.get(key)
            })
          }))
          .map(CompletableFuture.runAsync(_))
          .toList)
        .foreach(e => e.join())

    }).run()

    System.exit(0)
  }

  class Counting(label: String, val runnable: Runnable) extends Runnable {
    override def run(): Unit = {
      val start = System.currentTimeMillis()
      this.runnable.run()
      val diff: Float = System.currentTimeMillis() - start
      val message = s"${Thread.currentThread().getName} ${label} Total: ${diff} One: ${diff / count / 1000}"

      println(message)
    }
  }

  case class Person(firstName: String, lastName: String, age: Int) {
  }
}