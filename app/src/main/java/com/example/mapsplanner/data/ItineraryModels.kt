package com.example.mapsplanner.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Immutable
data class LatLng(
    val lat: Double,
    val lng: Double
)

@Immutable
data class DayPlanLocation(
    val name: String,
    val description: String,
    val position: LatLng,
    val time: String,
    val duration: String,
    val sequence: Int
)

@Immutable
data class RouteLeg(
    val name: String,
    val start: LatLng,
    val end: LatLng,
    val transport: String,
    val travelTime: String
)

@Immutable
data class DayPlanItinerary(
    val locations: List<DayPlanLocation> = emptyList(),
    val legs: List<RouteLeg> = emptyList()
)

@Serializable
internal data class GenAiFunctionCall(
    val name: String,
    val args: JsonObject = JsonObject(emptyMap())
)

@Serializable
internal data class GenAiModelPart(
    val text: String? = null,
    val functionCall: GenAiFunctionCall? = null
)

@Serializable
internal data class GenAiModelContent(
    val role: String? = null,
    val parts: List<GenAiModelPart> = emptyList()
)

@Serializable
internal data class GenAiCandidate(
    val content: GenAiModelContent = GenAiModelContent()
)

@Serializable
internal data class GenAiResponse(
    val candidates: List<GenAiCandidate> = emptyList(),
    val error: GenAiError? = null
)

@Serializable
internal data class GenAiError(
    val code: Int? = null,
    val message: String? = null
)

@Serializable
internal data class GenAiGenerateContentRequest(
    val contents: List<GenAiModelContent>,
    val tools: List<GenAiTool>,
    @SerialName("systemInstruction") val systemInstruction: GenAiModelContent? = null,
    @SerialName("generationConfig") val generationConfig: GenAiGenerationConfig = GenAiGenerationConfig(),
    @SerialName("safetySettings") val safetySettings: List<GenAiSafetySetting> = emptyList()
)

@Serializable
internal data class GenAiTool(
    val functionDeclarations: List<GenAiFunctionDeclaration>
)

@Serializable
internal data class GenAiFunctionDeclaration(
    val name: String,
    val description: String? = null,
    val parameters: GenAiSchema? = null
)

@Serializable
internal data class GenAiSchema(
    val type: String,
    val description: String? = null,
    val properties: Map<String, GenAiSchema> = emptyMap(),
    val required: List<String> = emptyList()
)

@Serializable
internal data class GenAiSafetySetting(
    val category: String,
    val threshold: String
)

@Serializable
internal data class GenAiGenerationConfig(
    val temperature: Double = 1.0,
    val topK: Int? = null,
    val topP: Double? = null
)

internal fun JsonObject.double(name: String): Double? =
    get(name)?.jsonPrimitive?.doubleOrNull

internal fun JsonObject.int(name: String): Int? =
    get(name)?.jsonPrimitive?.intOrNull

internal val JsonElement.jsonObjectOrNull: JsonObject?
    get() = runCatching { jsonObject }.getOrNull()
