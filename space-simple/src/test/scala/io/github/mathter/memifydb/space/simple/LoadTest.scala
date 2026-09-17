package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.common.data.ValueSerelizationFactory
import io.github.mathter.memifydb.data.cbor.fasterxml.FasterXmlValueSerializationFactory
import io.github.mathter.memifydb.space.{KeyValueOperations, Space, SpaceFactory}
import org.apache.commons.lang3.{RandomStringUtils, RandomUtils}

import java.util.concurrent.CompletableFuture

/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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