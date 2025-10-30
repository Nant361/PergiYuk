package com.example.mapsplanner.network

import com.example.mapsplanner.BuildConfig
import com.example.mapsplanner.data.DayPlanItinerary
import com.example.mapsplanner.data.DayPlanLocation
import com.example.mapsplanner.data.GenAiCandidate
import com.example.mapsplanner.data.GenAiError
import com.example.mapsplanner.data.GenAiFunctionCall
import com.example.mapsplanner.data.GenAiFunctionDeclaration
import com.example.mapsplanner.data.GenAiGenerateContentRequest
import com.example.mapsplanner.data.GenAiGenerationConfig
import com.example.mapsplanner.data.GenAiModelContent
import com.example.mapsplanner.data.GenAiModelPart
import com.example.mapsplanner.data.GenAiResponse
import com.example.mapsplanner.data.GenAiSchema
import com.example.mapsplanner.data.GenAiTool
import com.example.mapsplanner.data.LatLng
import com.example.mapsplanner.data.RouteLeg
import com.example.mapsplanner.data.double
import com.example.mapsplanner.data.int
import com.example.mapsplanner.data.jsonObjectOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val GENERATE_CONTENT_PATH = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

/**
 * Simple client to call the Gemini API with the same structured function declarations used on web.
 */
class GenAiClient(
    private val apiKey: String = BuildConfig.GEMINI_API_KEY
) {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val systemInstructions = """
        ## System Instructions for an Interactive Map Day Planner
        
        You are an expert travel planner assistant that creates detailed, map-based day itineraries.
        Always respond with function calls only. Build a logical sequence of locations (sequence 1..n) with time and duration.
        Connect each consecutive pair of stops with the `line` function describing the transport mode and travel time.
        """.trimIndent()

    private val locationSchema = GenAiFunctionDeclaration(
        name = "location",
        description = "Geographic coordinates of a location to visit.",
        parameters = GenAiSchema(
            type = "OBJECT",
            required = listOf("name", "description", "lat", "lng", "time", "duration", "sequence"),
            properties = mapOf(
                "name" to schema(Type.STRING, "Name of the location."),
                "description" to schema(Type.STRING, "Why this stop is interesting."),
                "lat" to schema(Type.STRING, "Latitude decimal."),
                "lng" to schema(Type.STRING, "Longitude decimal."),
                "time" to schema(Type.STRING, "Arrival time, e.g. 09:00."),
                "duration" to schema(Type.STRING, "Suggested stay duration."),
                "sequence" to schema(Type.NUMBER, "Order in the itinerary starting at 1."),
            )
        )
    )

    private val lineSchema = GenAiFunctionDeclaration(
        name = "line",
        description = "Connection between two itinerary locations.",
        parameters = GenAiSchema(
            type = "OBJECT",
            required = listOf("name", "start", "end", "transport", "travelTime"),
            properties = mapOf(
                "name" to schema(Type.STRING, "Name of the route or travel segment."),
                "start" to schema(Type.OBJECT, description = "Start coordinate", properties = latLngProperties(), required = listOf("lat", "lng")),
                "end" to schema(Type.OBJECT, description = "End coordinate", properties = latLngProperties(), required = listOf("lat", "lng")),
                "transport" to schema(Type.STRING, "Mode of transportation."),
                "travelTime" to schema(Type.STRING, "Estimated travel time"),
            )
        )
    )

    suspend fun generateDayPlan(prompt: String): DayPlanItinerary = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            // Return canned data so the UI can still be previewed.
            return@withContext sampleItinerary()
        }

        val requestPayload = GenAiGenerateContentRequest(
            contents = listOf(
                GenAiModelContent(
                    role = "user",
                    parts = listOf(GenAiModelPart(text = prompt))
                )
            ),
            systemInstruction = GenAiModelContent(parts = listOf(GenAiModelPart(text = systemInstructions))),
            tools = listOf(GenAiTool(functionDeclarations = listOf(locationSchema, lineSchema))),
            generationConfig = GenAiGenerationConfig(temperature = 1.0)
        )

        val body = json.encodeToString(requestPayload)
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$GENERATE_CONTENT_PATH?key=$apiKey")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        response.use { resp ->
            if (!resp.isSuccessful) {
                throw IOException("Gemini request failed: ${resp.code}")
            }

            val responseBody = resp.body?.string().orEmpty()
            if (responseBody.isBlank()) {
                throw IOException("Empty response from Gemini")
            }

            val parsed = json.decodeFromString(GenAiResponse.serializer(), responseBody)
            parsed.error?.let { err ->
                throw IOException(err.message ?: "Unknown Gemini error")
            }

            return@withContext toItinerary(parsed)
        }
    }

    private fun toItinerary(response: GenAiResponse): DayPlanItinerary {
        val locations = mutableMapOf<Int, DayPlanLocation>()
        val legs = mutableListOf<RouteLeg>()

        response.candidates
            .flatMap { it.content.parts }
            .forEach { part ->
                val call = part.functionCall ?: return@forEach
                when (call.name) {
                    "location" -> buildLocation(call.args)?.let { locations[it.sequence] = it }
                    "line" -> buildLeg(call.args)?.let(legs::add)
                }
            }

        val orderedLocations = locations.toSortedMap().values.toList()
        return DayPlanItinerary(orderedLocations, legs)
    }

    private fun buildLocation(args: JsonObject): DayPlanLocation? {
        val seq = args.int("sequence") ?: return null
        val name = args.string("name") ?: return null
        val description = args.string("description") ?: ""
        val lat = args.string("lat")?.toDoubleOrNull() ?: args.double("lat") ?: return null
        val lng = args.string("lng")?.toDoubleOrNull() ?: args.double("lng") ?: return null
        val time = args.string("time") ?: ""
        val duration = args.string("duration") ?: ""
        return DayPlanLocation(
            name = name,
            description = description,
            position = LatLng(lat, lng),
            time = time,
            duration = duration,
            sequence = seq
        )
    }

    private fun buildLeg(args: JsonObject): RouteLeg? {
        val name = args.string("name") ?: return null
        val start = args["start"]?.jsonObjectOrNull ?: return null
        val end = args["end"]?.jsonObjectOrNull ?: return null
        val startLat = start.coord("lat") ?: return null
        val startLng = start.coord("lng") ?: return null
        val endLat = end.coord("lat") ?: return null
        val endLng = end.coord("lng") ?: return null
        val transport = args.string("transport") ?: ""
        val travelTime = args.string("travelTime") ?: ""
        return RouteLeg(
            name = name,
            start = LatLng(startLat, startLng),
            end = LatLng(endLat, endLng),
            transport = transport,
            travelTime = travelTime
        )
    }

    private fun sampleItinerary(): DayPlanItinerary {
        val sampleLocations = listOf(
            DayPlanLocation(
                name = "Monas",
                description = "Ikon Jakarta untuk memulai hari dengan sejarah.",
                position = LatLng(-6.175392, 106.827153),
                time = "09:00",
                duration = "90 menit",
                sequence = 1
            ),
            DayPlanLocation(
                name = "Kota Tua",
                description = "Berjalan di area heritage dan museum Fatahillah.",
                position = LatLng(-6.135200, 106.814224),
                time = "11:00",
                duration = "120 menit",
                sequence = 2
            ),
            DayPlanLocation(
                name = "Glodok",
                description = "Kuliner siang di pecinan tertua Jakarta.",
                position = LatLng(-6.140759, 106.814211),
                time = "13:30",
                duration = "90 menit",
                sequence = 3
            ),
            DayPlanLocation(
                name = "Ancol Beach",
                description = "Sore santai menikmati laut di Ancol.",
                position = LatLng(-6.123617, 106.844497),
                time = "16:00",
                duration = "120 menit",
                sequence = 4
            )
        )

        val sampleLegs = listOf(
            RouteLeg(
                name = "Monas ke Kota Tua",
                start = sampleLocations[0].position,
                end = sampleLocations[1].position,
                transport = "MRT + berjalan",
                travelTime = "35 menit"
            ),
            RouteLeg(
                name = "Kota Tua ke Glodok",
                start = sampleLocations[1].position,
                end = sampleLocations[2].position,
                transport = "Berjalan kaki",
                travelTime = "15 menit"
            ),
            RouteLeg(
                name = "Glodok ke Ancol",
                start = sampleLocations[2].position,
                end = sampleLocations[3].position,
                transport = "Taksi / ride hailing",
                travelTime = "25 menit"
            )
        )

        return DayPlanItinerary(sampleLocations, sampleLegs)
    }

    private fun schema(
        type: String,
        description: String? = null,
        properties: Map<String, GenAiSchema> = emptyMap(),
        required: List<String> = emptyList()
    ): GenAiSchema =
        GenAiSchema(
            type = type,
            description = description,
            properties = properties,
            required = required
        )

    private fun latLngProperties(): Map<String, GenAiSchema> = mapOf(
        "lat" to schema(Type.STRING, "Latitude decimal."),
        "lng" to schema(Type.STRING, "Longitude decimal."),
    )

    private fun JsonObject.coord(name: String): Double? =
        string(name)?.toDoubleOrNull() ?: double(name)

    private fun JsonObject.string(name: String): String? =
        when (val element = get(name)) {
            is JsonPrimitive -> element.contentOrNull
            JsonNull -> null
            else -> element?.toString()
        }

    private object Type {
        const val STRING = "STRING"
        const val NUMBER = "NUMBER"
        const val OBJECT = "OBJECT"
    }
}
