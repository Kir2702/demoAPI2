package corp.jsonMappers

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize


@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(
    @JsonProperty("display_name")
    val displayName: String,
    @JsonProperty("geojson")
    val geojson: Coordinates
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Coordinates(
    @JsonDeserialize(using = CoordinatesDeserializer::class)
    val coordinates: List<Any>
)

