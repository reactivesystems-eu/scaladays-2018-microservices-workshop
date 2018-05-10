package eu.reactivesystems.workshop.booking.api

import java.time.LocalDate
import java.util.UUID

import play.api.libs.json.{Format, Json}

case class BookingRequest(guest: UUID,
                          startingDate: LocalDate,
                          duration: Int,
                          numberOfGuests: Int)

object BookingRequest {
  implicit val format: Format[BookingRequest] = Json.format
}
