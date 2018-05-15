package eu.reactivesystems.workshop.booking.impl

import java.util.UUID

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import eu.reactivesystems.workshop.booking.api.{BookingRequest, BookingService}

import scala.concurrent.{ExecutionContext, Future}

class BookingServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(
  implicit ec: ExecutionContext)
  extends BookingService {

  override def healthCheck(): ServiceCall[NotUsed, String] =
    request => Future.successful("OK")


  override def cancelBooking(roomId: UUID, bookingId: UUID): ServiceCall[NotUsed, Done] =
    request => Future.successful(Done)

  override def confirmBooking(roomId: UUID, bookingId: UUID): ServiceCall[NotUsed, Done] =
    request => Future.successful(Done)


  override def requestBooking(roomId: UUID): ServiceCall[BookingRequest, UUID] =
    request => entityRef(roomId).ask(RequestBooking(request))


  override def rejectBooking(roomId: UUID, bookingId: UUID): ServiceCall[NotUsed, Done] =
    request => Future.successful(Done)


  override def withdrawBooking(roomId: UUID, bookingId: UUID): ServiceCall[NotUsed, Done] =
    request => Future.successful(Done)


  override def listRoom(roomId: UUID): ServiceCall[NotUsed, Done] =
    request => Future.successful(Done)


  override def unlistRoom(roomId: UUID): ServiceCall[NotUsed, Done] =
    request => Future.successful(Done)


  private def entityRef(roomId: UUID) =
    persistentEntityRegistry.refFor[BookingRegister](roomId.toString)

}
