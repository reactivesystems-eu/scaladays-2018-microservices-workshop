package eu.reactivesystems.workshop.booking.impl

import com.lightbend.lagom.scaladsl.playjson
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Seq

object BookingSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    // State
    JsonSerializer[BookingRegisterState],
    // Commands and replies
    // Events
  )
}
