package eu.reactivesystems.workshop.booking.impl

import java.time.LocalDate
import java.util.UUID

import akka.actor.ActorSystem
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import eu.reactivesystems.workshop.booking.api.BookingRequest

import scala.concurrent.Await
import scala.concurrent.duration._

class BookingRegisterSpec extends WordSpecLike with Matchers with BeforeAndAfterAll {

  val system = ActorSystem("PostSpec", JsonSerializerRegistry.actorSystemSetupFor(BookingSerializerRegistry))

  override def afterAll(): Unit = {
    Await.ready(system.terminate, 10.seconds)
  }

  "The booking register" should {
    "store booking request" in {
      val driver = new PersistentEntityTestDriver(system, new BookingRegister, "1")
      val guestId = UUID.randomUUID()
      val date = LocalDate.now().plusWeeks(2)
      val commandPayload = BookingRequest(guestId, date, 3, 1)
      val command = RequestBooking(commandPayload)
      val outcome = driver.run(command)
      val uuid = outcome.replies.head.asInstanceOf[UUID]
      outcome.events should be(Seq(BookingRequested(uuid, guestId, date, 3, 1)))
      outcome.state should be(BookingRegisterState(BookingRegisterStatus.Listed, Map(uuid.toString -> Booking(uuid, guestId, date, 3, 1))))
      outcome.issues should be(Nil)
    }
  }
}