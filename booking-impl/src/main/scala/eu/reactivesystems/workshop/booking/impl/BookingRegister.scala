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

  override def initialState: BookingRegisterState = BookingRegisterState(BookingRegisterStatus.Unlisted, Map.empty)


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
    }.onCommand[UnlistRoom.type, Done] {
      case (UnlistRoom, ctx, state) =>
        ctx.reply(Done)
        ctx.done
    }
      .onEvent {
        case (RoomListed, state) => state.copy(status = BookingRegisterStatus.Listed)
      }.orElse(cancelAction)

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
    }.orElse(cancelAction)


  private def cancelAction = Actions().onCommand[CancelBooking, Done] {
    case (CancelBooking(bookingId), ctx, state) =>
      state.requestedBookings.get(bookingId.toString).fold {
        ctx.invalidCommand("no such booking")
        ctx.done
      }(_ => ctx.thenPersist(BookingCancelled(bookingId))(event => ctx.reply(Done)))
  }.onEvent {
    case (BookingCancelled(bookingId), state) =>
      state.copy(requestedBookings = state.requestedBookings - bookingId.toString)
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

object RequestBooking {
  implicit val format: Format[RequestBooking] = Json.format
}

case class CancelBooking(bookingId: UUID) extends BookingRegisterCommand with ReplyType[Done]

object CancelBooking {
  implicit val format: Format[CancelBooking] = Json.format
}

case class RejectBooking(bookingId: UUID) extends BookingRegisterCommand with ReplyType[Done]

object RejectBooking {
  implicit val format: Format[RejectBooking] = Json.format
}


case object ListRoom extends BookingRegisterCommand with ReplyType[Done] {
  implicit val format: Format[ListRoom.type] = singletonFormat(ListRoom)
}


case object UnlistRoom extends BookingRegisterCommand with ReplyType[Done] {
  implicit val format: Format[UnlistRoom.type] = singletonFormat(UnlistRoom)
}


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

object BookingRequested {
  implicit val format: Format[BookingRequested] = Json.format
}

case class BookingCancelled(bookingId: UUID) extends BookingRegisterEvent

object BookingCancelled {
  implicit val format: Format[BookingCancelled] = Json.format
}

case object RoomListed extends BookingRegisterEvent {
  implicit val format: Format[RoomListed.type] = singletonFormat(RoomListed)
}


object BookingRegisterEvent {
  val Tag = AggregateEventTag[BookingRegisterEvent]
}
