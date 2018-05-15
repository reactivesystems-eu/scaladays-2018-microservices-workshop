package eu.reactivesystems.workshop.booking.api

import java.util.UUID

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The booking service.
  */
trait BookingService extends Service {

  def healthCheck(): ServiceCall[NotUsed, String]

  def cancelBooking(roomId: UUID, bookingId: UUID): ServiceCall[NotUsed, Done]

  def confirmBooking(roomId: UUID, bookingId: UUID): ServiceCall[NotUsed, Done]

  def requestBooking(roomId: UUID): ServiceCall[BookingRequest, UUID]

  def rejectBooking(roomId: UUID, bookingId: UUID): ServiceCall[NotUsed, Done]

  def listRoom(roomId: UUID): ServiceCall[NotUsed, Done]

  def unlistRoom(roomId: UUID): ServiceCall[NotUsed, Done]

  final override def descriptor = {
    import Service._

    named("booking")
      .withCalls(restCall(Method.GET, "/api/room/healthCheck", healthCheck),
        // confirm booking
        restCall(Method.POST, "/api/room/:roomId/bookings/:bookingId/confirm", confirmBooking _),
        // request booking
        restCall(Method.POST, "/api/room/:roomId/bookings", requestBooking _),
        // cancel booking
        restCall(Method.DELETE, "/api/room/:roomId/bookings/:bookingId", cancelBooking _),
        // reject booking
        restCall(Method.POST, "/api/room/:roomId/bookings/:bookingId/reject", rejectBooking _),
        // withdraw booking
        restCall(Method.POST, "/api/room/:roomId", listRoom _),
        restCall(Method.DELETE, "/api/room/:roomId", unlistRoom _))
      // TODO later modify booking
      .withAutoAcl(true)
  }
}
