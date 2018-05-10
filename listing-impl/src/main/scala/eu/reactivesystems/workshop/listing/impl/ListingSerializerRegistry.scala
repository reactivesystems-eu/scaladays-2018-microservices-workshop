package eu.reactivesystems.workshop.listing.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

object ListingSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[ListingState]
  )
}
