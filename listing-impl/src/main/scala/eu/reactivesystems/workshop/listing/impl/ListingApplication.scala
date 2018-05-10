package eu.reactivesystems.workshop.listing.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import eu.reactivesystems.workshop.booking.api.ListingService
import play.api.Environment
import play.api.libs.ws.ahc.AhcWSComponents

import scala.concurrent.ExecutionContext

trait ListingComponents extends LagomServerComponents
  with CassandraPersistenceComponents {

  implicit def executionContext: ExecutionContext
  def environment: Environment

  persistentEntityRegistry.register(wire[ListingEntity])

  override lazy val lagomServer = serverFor[ListingService](wire[ListingServiceImpl])
  lazy val jsonSerializerRegistry = ListingSerializerRegistry

}

abstract class ListingApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with ListingComponents
  with AhcWSComponents
  with LagomKafkaComponents {
}

class ListingApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ListingApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new ListingApplication(context) {
      override def serviceLocator = ServiceLocator.NoServiceLocator
    }

  override def describeService = Some(readDescriptor[ListingService])
}
