package corp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import corp.jsonMappers.Location
import org.springframework.stereotype.Service
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets


@Service
class PointService (
    private val httpClient: HttpClient
) {

    fun getPoint(country: String, state: String, type: String): String {
        val json = getData(country, state, type)

        val mapper = ObjectMapper().registerModule(KotlinModule())
        val locations = mapper.readValue(json, Array<Location>::class.java)
        return calculation(locations)
    }

    private fun calculation(coordinates: Array<Location>): String {
        val sb = StringBuilder()

        for (i in coordinates.indices) {
            val data = flattenCoordinates(coordinates[i].geojson.coordinates)
            sb.append("{")
            sb.append("\"displayName\": \"${coordinates[i].displayName}\",")
            sb.append("\"xAverage\": ${calculateAverage(data.first)},")
            sb.append("\"yAverage\": ${calculateAverage(data.second)}")
            sb.append("}")
            if (i < coordinates.size - 1) {
                sb.append(",")
            }
        }
        return sb.toString()
    }

    fun calculateAverage(coordinates: List<Float>): Float {
        return coordinates.sum() / coordinates.size
    }

    private fun flattenCoordinates(coordinates: List<Any>): Pair<List<Float>, List<Float>> {
        val xCoordinates = mutableListOf<Float>()
        val yCoordinates = mutableListOf<Float>()
        coordinates.forEach { coord ->
            when (coord) {
                is List<*> -> {
                    val (xs, ys) = flattenCoordinates(coord as List<Any>)
                    xCoordinates.addAll(xs)
                    yCoordinates.addAll(ys)
                }
                is Float -> {
                    if (xCoordinates.isEmpty()) {
                        xCoordinates.add(coord)
                    } else {
                        yCoordinates.add(coord)
                    }
                }
            }
        }
        return Pair(xCoordinates, yCoordinates)
    }

    private fun getData(country: String, state: String, type: String): String {

        val encodedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8)
        val encodedState = URLEncoder.encode(state, StandardCharsets.UTF_8)
        val encodedType = URLEncoder.encode(type, StandardCharsets.UTF_8)
        var response: String = ""

        // область
        if (encodedType == "area") {
            val url = "https://nominatim.openstreetmap.org/search.php?country=$encodedCountry&polygon_geojson=1&format=jsonv2&state=$encodedState"
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build()
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()
        }

        // округ
        if (encodedType == "county") {
            val url1 = "https://nominatim.openstreetmap.org/search?q=$encodedState&country=$encodedCountry&format=json&polygon_geojson=1"
            val request1 = HttpRequest.newBuilder()
                .uri(URI.create(url1))
                .build()
            response = httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).body()
        }
        return response
    }
}

