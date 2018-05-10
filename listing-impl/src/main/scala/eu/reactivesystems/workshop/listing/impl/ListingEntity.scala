package eu.reactivesystems.workshop.listing.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import eu.reactivesystems.workshop.jsonformats.JsonFormats._
import play.api.libs.json.{Format, Json}

/**
  */
class ListingEntity extends PersistentEntity {

  override type State = ListingState
  override type Command = ListingCommand
  override type Event = ListingESEvent

  override def initialState: ListingState = ListingState(ListingStatus.NotCreated)


  override def behavior: Behavior = {
    case ListingState(ListingStatus.NotCreated) => notCreated
  }

  /**
    * Behavior for the not created state.
    */
  private def notCreated = Actions.empty

}


/**
  * The state.
  */
case class ListingState(status: ListingStatus.Status)

object ListingState {
  implicit val format: Format[ListingState] = Json.format
}

/**
  * Status.
  */
object ListingStatus extends Enumeration {
  type Status = Value
  val NotCreated = Value

  implicit val format: Format[Status] = enumFormat(ListingStatus)
}

/**
  * A command.
  */
trait ListingCommand


/**
  * A persisted event.
  */
trait ListingESEvent extends AggregateEvent[ListingESEvent] {
  override def aggregateTag: AggregateEventTagger[ListingESEvent] = ListingESEvent.Tag
}

object ListingESEvent {
  val Tag = AggregateEventTag[ListingESEvent]
}
