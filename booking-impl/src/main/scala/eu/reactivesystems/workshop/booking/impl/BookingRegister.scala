package eu.reactivesystems.workshop.booking.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import eu.reactivesystems.workshop.jsonformats.JsonFormats._
import play.api.libs.json.{Format, Json}

/**
  */
class BookingRegister extends PersistentEntity {

  override type State = BookingRegisterState
  override type Command = BookingRegisterCommand
  override type Event = BookingRegisterEvent

  override def initialState: BookingRegisterState = BookingRegisterState(BookingRegisterStatus.NotCreated)


  override def behavior: Behavior = {
    case BookingRegisterState(BookingRegisterStatus.NotCreated) => notCreated
  }

  /**
    * Behavior for the not created state.
    */
  private def notCreated = Actions.empty

}


/**
  * The state.
  */
case class BookingRegisterState(status: BookingRegisterStatus.Status)

object BookingRegisterState {
  implicit val format: Format[BookingRegisterState] = Json.format
}

/**
  * Status.
  */
object BookingRegisterStatus extends Enumeration {
  type Status = Value
  val NotCreated = Value

  implicit val format: Format[Status] = enumFormat(BookingRegisterStatus)
}

/**
  * A command.
  */
trait BookingRegisterCommand


/**
  * A persisted event.
  */
trait BookingRegisterEvent extends AggregateEvent[BookingRegisterEvent] {
  override def aggregateTag: AggregateEventTagger[BookingRegisterEvent] = BookingRegisterEvent.Tag
}

object BookingRegisterEvent {
  val Tag = AggregateEventTag[BookingRegisterEvent]
}
