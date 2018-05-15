package eu.reactivesystems.workshop.booking.impl

import java.time.LocalDate
import java.util.UUID

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import eu.reactivesystems.workshop.booking.api.BookingRequest
import eu.reactivesystems.workshop.jsonformats.JsonFormats._
import play.api.libs.json.{Format, Json}

/**
  */
class BookingRegister extends PersistentEntity {

  override type State = BookingRegisterState
  override type Command = BookingRegisterCommand
  override type Event = BookingRegisterEvent

  override def initialState: BookingRegisterState = BookingRegisterState(BookingRegisterStatus.Listed, Map.empty)


  override def behavior: Behavior = {
    case BookingRegisterState(BookingRegisterStatus.Unlisted, _) => unlisted
    case BookingRegisterState(BookingRegisterStatus.Listed, _) => listed
  }

  /**
    * Behavior for the not created state.
    */
  private def unlisted =
    Actions().onCommand[ListRoom.type, Done] {
      case (ListRoom, ctx, state) =>
        ctx.thenPersist(RoomListed)(event => ctx.reply(Done))

    }.onEvent {
      case (RoomListed, state) => state.copy(status = BookingRegisterStatus.Listed)
    }

  private def listed =
    Actions().onCommand[RequestBooking, UUID] {
      case (RequestBooking(bookingRequest), ctx, state) =>
        if (bookingRequest.startingDate.isBefore(LocalDate.now())) {
          ctx.invalidCommand("Booking date has to be in future")
          ctx.done
        } else {
          val bookingId = UUID.randomUUID()
          val event = BookingRequested(bookingId, bookingRequest.guest,
            bookingRequest.startingDate, bookingRequest.duration,
            bookingRequest.numberOfGuests)
          ctx.thenPersist(event)(event => ctx.reply(event.bookingId))
        }
    }.onEvent {
      case (event@BookingRequested(bookingId, guest, startingDate, duration, numberOfGuests), state) =>
        state.copy(requestedBookings =
          state.requestedBookings + (event.bookingId.toString ->
            Booking(event.bookingId, event.guest, event.startingDate, event.duration, event.numberOfGuests)))
    }
}

/**
  * The state.
  */
case class BookingRegisterState(status: BookingRegisterStatus.Status, requestedBookings: Map[String, Booking])

object BookingRegisterState {
  implicit val format: Format[BookingRegisterState] = Json.format
}

case class Booking(bookingId: UUID,
                   guest: UUID,
                   startingDate: LocalDate,
                   duration: Int,
                   numberOfGuests: Int)

object Booking {
  implicit val format: Format[Booking] = Json.format
}

/**
  * Status.
  */
object BookingRegisterStatus extends Enumeration {
  type Status = Value
  val Unlisted, Listed = Value

  implicit val format: Format[Status] = enumFormat(BookingRegisterStatus)
}

/**
  * A command.
  */
sealed trait BookingRegisterCommand

case class RequestBooking(request: BookingRequest) extends BookingRegisterCommand with ReplyType[UUID]

case class CancelBooking(bookingId: UUID) extends BookingRegisterCommand with ReplyType[Done]

case class RejectBooking(bookingId: UUID) extends BookingRegisterCommand with ReplyType[Done]

case class WithdrawBooking(bookingId: UUID) extends BookingRegisterCommand with ReplyType[Done]

case object ListRoom extends BookingRegisterCommand with ReplyType[Done]

case object UnlistRoom extends BookingRegisterCommand with ReplyType[Done]


/**
  * A persisted event.
  */
sealed trait BookingRegisterEvent extends AggregateEvent[BookingRegisterEvent] {
  override def aggregateTag: AggregateEventTagger[BookingRegisterEvent] = BookingRegisterEvent.Tag
}

case class BookingRequested(bookingId: UUID,
                            guest: UUID,
                            startingDate: LocalDate,
                            duration: Int,
                            numberOfGuests: Int) extends BookingRegisterEvent

case object RoomListed extends BookingRegisterEvent


object BookingRegisterEvent {
  val Tag = AggregateEventTag[BookingRegisterEvent]
}
