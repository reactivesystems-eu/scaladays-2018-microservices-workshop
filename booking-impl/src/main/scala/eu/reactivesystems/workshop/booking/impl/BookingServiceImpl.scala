package eu.reactivesystems.workshop.booking.impl

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import eu.reactivesystems.workshop.booking.api.BookingService

import scala.concurrent.{ExecutionContext, Future}

class BookingServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(
    implicit ec: ExecutionContext)
    extends BookingService {

  override def healthCheck(): ServiceCall[NotUsed, String] =
    request => Future.successful("OK")

  private def entityRef(listingId: UUID) =
    persistentEntityRegistry.refFor[BookingRegister](listingId.toString)

}
