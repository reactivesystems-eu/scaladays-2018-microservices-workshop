package eu.reactivesystems.workshop.listing.api

import java.util.UUID

import play.api.libs.json.{Format, Json}

case class Listing(id: Option[UUID])

object Listing {
  implicit val format: Format[Listing] = Json.format
}