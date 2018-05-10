package eu.reactivesystems.workshop.booking.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The booking service.
  */
trait BookingService extends Service {

  def healthCheck(): ServiceCall[NotUsed, String]

  final override def descriptor = {
    import Service._

    named("booking")
      .withCalls(restCall(Method.GET, "/api/booking/healthCheck", healthCheck))
      .withAutoAcl(true)
  }
}
