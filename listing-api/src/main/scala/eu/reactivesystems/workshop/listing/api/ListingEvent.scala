package eu.reactivesystems.workshop.listing.api

import java.util.UUID

import julienrf.json.derived
import play.api.libs.json._
import eu.reactivesystems.workshop.jsonformats.JsonFormats._

/**
  * A listing-related event.
  */
sealed trait ListingEvent {
  val listingId: UUID
}


case class ListingCreated(listingId: UUID) extends ListingEvent

object ListingCreated {
  implicit val format: Format[ListingCreated] = Json.format
}

object ListingEvent {
  implicit val format: Format[ListingEvent] =
    derived.flat.oformat((__ \ "type").format[String])
}
