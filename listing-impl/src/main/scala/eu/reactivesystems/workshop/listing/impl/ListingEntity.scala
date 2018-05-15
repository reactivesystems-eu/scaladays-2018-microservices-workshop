package eu.reactivesystems.workshop.listing.impl

import java.util.UUID

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
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

  override def behavior: Behavior = Actions().onCommand[CreateListing, Done] {
    case (CreateListing(uuid), ctx, state) => ctx.thenPersist(ListingCreatedESEvent(uuid))(_ => ctx.reply(Done))
  }.onEvent {
    case (ListingCreatedESEvent(_), state) => state
  }

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
sealed trait ListingCommand

case class CreateListing(listingId: UUID) extends ListingCommand with ReplyType[Done]

object CreateListing {
  implicit val format: Format[CreateListing] = Json.format
}


/**
  * A persisted event.
  */
sealed trait ListingESEvent extends AggregateEvent[ListingESEvent] {
  override def aggregateTag: AggregateEventTagger[ListingESEvent] = ListingESEvent.Tag
}

object ListingESEvent {
  val Tag = AggregateEventTag[ListingESEvent]
}

case class ListingCreatedESEvent(listingId: UUID) extends ListingESEvent

object ListingCreatedESEvent {
  implicit val format: Format[ListingCreatedESEvent] = Json.format
}
