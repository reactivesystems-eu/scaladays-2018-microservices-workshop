package eu.reactivesystems.workshop.listing.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

object ListingSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[ListingState],
    JsonSerializer[UUID],
    JsonSerializer[CreateListing],
    JsonSerializer[ListingCreatedESEvent]
  )
}
