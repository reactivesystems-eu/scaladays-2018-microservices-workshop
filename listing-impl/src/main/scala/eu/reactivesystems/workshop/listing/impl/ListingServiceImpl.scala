package eu.reactivesystems.workshop.listing.impl

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import eu.reactivesystems.workshop.booking.api.ListingService

import scala.concurrent.{ExecutionContext, Future}

class ListingServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(
  implicit ec: ExecutionContext)
  extends ListingService {

  override def healthCheck(): ServiceCall[NotUsed, String] =
    request => Future.successful("OK")

  private def entityRef(listingId: UUID) =
    persistentEntityRegistry.refFor[ListingEntity](listingId.toString)

}
