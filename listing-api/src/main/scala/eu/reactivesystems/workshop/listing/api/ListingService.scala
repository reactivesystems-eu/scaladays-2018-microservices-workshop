package eu.reactivesystems.workshop.booking.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import eu.reactivesystems.workshop.listing.api.ListingEvent

/**
  * The listing service.
  */
trait ListingService extends Service {

  val listingTopicName = "listingTopic"

  def healthCheck(): ServiceCall[NotUsed, String]

  def createListing(): ServiceCall[NotUsed, UUID]

  def listingEvents(): Topic[ListingEvent]

  final override def descriptor = {
    import Service._

    named("listing")
      .withCalls(restCall(Method.GET, "/api/listing/healthCheck", healthCheck),
        restCall(Method.POST, "/api/listing", createListing))
      .withTopics(topic(listingTopicName, listingEvents))
      .withAutoAcl(true)
  }
}
