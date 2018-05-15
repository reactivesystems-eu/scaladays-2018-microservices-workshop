package eu.reactivesystems.workshop.booking.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import eu.reactivesystems.workshop.booking.api.{BookingService, ListingService}
import play.api.libs.ws.ahc.AhcWSComponents

abstract class BookingApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with AhcWSComponents
  with CassandraPersistenceComponents
  with LagomKafkaComponents {

  lazy val listingService = serviceClient.implement[ListingService]
  override lazy val lagomServer = serverFor[BookingService](wire[BookingServiceImpl])
  override lazy val jsonSerializerRegistry = BookingSerializerRegistry

  // Initialize everything
  persistentEntityRegistry.register(wire[BookingRegister])


}

class BookingApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) =
    new BookingApplication(context) {
      override def serviceLocator = ServiceLocator.NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext) =
    new BookingApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[BookingService])
}
