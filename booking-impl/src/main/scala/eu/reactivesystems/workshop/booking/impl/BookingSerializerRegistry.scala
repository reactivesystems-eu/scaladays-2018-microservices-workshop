package eu.reactivesystems.workshop.booking.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.playjson
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Seq

object BookingSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    // State
    JsonSerializer[BookingRegisterState],
    // Commands and replies
    JsonSerializer[UUID],
    JsonSerializer[RequestBooking],
    JsonSerializer[CancelBooking],
    JsonSerializer[RejectBooking],
    JsonSerializer[ListRoom.type],
    JsonSerializer[UnlistRoom.type],
    // Events
    JsonSerializer[BookingRequested],
    JsonSerializer[BookingCancelled],
    JsonSerializer[RoomListed.type],

  )
}

