/*
 * Copyright 2018-2019 ProfunKtor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.profunktor.redis4cats

import dev.profunktor.redis4cats.domain.RedisChannel
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands

object streams {

  trait RedisPubSubCommands[K, V] {
    def underlying: RedisPubSubAsyncCommands[K, V]
  }
  case class LivePubSubCommands[K, V](underlying: RedisPubSubAsyncCommands[K, V]) extends RedisPubSubCommands[K, V]

  case class Subscription[K](channel: RedisChannel[K], number: Long)

  object Subscription {
    def empty[K](channel: RedisChannel[K]): Subscription[K] =
      Subscription[K](channel, 0L)
  }

  final case class StreamingMessage[K, V](key: K, body: Map[K, V])
  final case class StreamingMessageWithId[K, V](id: MessageId, key: K, body: Map[K, V])
  final case class MessageId(value: String) extends AnyVal

  sealed trait StreamingOffset[K] extends Product with Serializable {
    def key: K
    def offset: String
  }

  object StreamingOffset {
    case class All[K](key: K) extends StreamingOffset[K] {
      override def offset: String = "0"
    }
    case class Latest[K](key: K) extends StreamingOffset[K] {
      override def offset: String = "$"
    }
    case class Custom[K](key: K, offset: String) extends StreamingOffset[K]
  }

}
