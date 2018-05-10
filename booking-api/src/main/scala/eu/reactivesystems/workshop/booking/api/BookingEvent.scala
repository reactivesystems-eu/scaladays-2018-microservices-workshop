package eu.reactivesystems.workshop.booking.api

import java.util.UUID

import julienrf.json.derived
import play.api.libs.json._

/**
  * A bid event.
  */
sealed trait BookingEvent {
  val listingId: UUID
}

case class BookingCreated(listingId: UUID) extends BookingEvent

object BookingCreated {
  implicit val format: Format[BookingCreated] = Json.format

}

object BookingEvent {
  implicit val format: Format[BookingEvent] =
    derived.flat.oformat((__ \ "type").format[String])
}
