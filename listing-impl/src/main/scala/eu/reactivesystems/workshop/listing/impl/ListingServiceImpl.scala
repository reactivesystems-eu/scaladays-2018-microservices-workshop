package eu.reactivesystems.workshop.listing.impl

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import eu.reactivesystems.workshop.booking.api.ListingService
import eu.reactivesystems.workshop.listing.api.{ListingCreated, ListingEvent}

import scala.concurrent.{ExecutionContext, Future}

class ListingServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(
  implicit ec: ExecutionContext)
  extends ListingService {

  override def healthCheck(): ServiceCall[NotUsed, String] =
    request => Future.successful("OK")


  // TODO the instanceOf is weird... make this better
  override def listingEvents(): Topic[ListingEvent] = {
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(ListingESEvent.Tag, fromOffset)
          .filter {
            evt => evt.event match {
              case _ : ListingCreatedESEvent => true
              case _ => false
            }
          }.map {
          evt =>
            val myEvent = evt.event.asInstanceOf[ListingCreatedESEvent]
            (ListingCreated(myEvent.listingId), evt.offset)
        }
    }
  }


  override def createListing(): ServiceCall[NotUsed, UUID] = {
    request =>
      val uuid = UUID.randomUUID()
      entityRef(uuid).ask(CreateListing(uuid)).map(_ => uuid)
  }

  private def entityRef(listingId: UUID) =
    persistentEntityRegistry.refFor[ListingEntity](listingId.toString)

}
