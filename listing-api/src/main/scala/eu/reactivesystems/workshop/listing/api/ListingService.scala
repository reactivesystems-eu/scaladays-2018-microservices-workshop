package eu.reactivesystems.workshop.booking.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The listing service.
  */
trait ListingService extends Service {

  def healthCheck(): ServiceCall[NotUsed, String]

  def createListing(): ServiceCall[NotUsed, UUID]

  final override def descriptor = {
    import Service._

    named("listing")
      .withCalls(restCall(Method.GET, "/api/listing/healthCheck", healthCheck),
        restCall(Method.POST, "/api/listing", createListing))
      .withAutoAcl(true)
  }
}
