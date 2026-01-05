package com.example.mapsplanner.repository

import com.example.mapsplanner.ui.SignedInUser
import com.example.mapsplanner.ui.SavedPlan
import com.example.mapsplanner.data.DayPlanItinerary
import com.example.mapsplanner.data.DayPlanLocation
import com.example.mapsplanner.data.RouteLeg
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val collectionName: String = "users"
) {
    private val firestore = Firebase.firestore

    suspend fun upsertUser(user: SignedInUser) {
        val emailKey = user.uid.ifBlank { user.email.ifBlank { user.name.lowercase().replace(" ", "_") } }
        val data = mapOf(
            "name" to user.name,
            "email" to user.email,
            "photoUrl" to user.photoUrl,
            "lastSignedIn" to FieldValue.serverTimestamp()
        )
        firestore.collection(collectionName)
            .document(emailKey)
            .set(data, SetOptions.merge())
            .await()
    }

    suspend fun savePlan(user: SignedInUser, plan: SavedPlan) {
        val emailKey = user.uid.ifBlank { user.email.ifBlank { user.name.lowercase().replace(" ", "_") } }
        firestore.collection(collectionName)
            .document(emailKey)
            .collection("plans")
            .document(plan.id.toString())
            .set(plan.toMap(), SetOptions.merge())
            .await()
    }

    suspend fun getSavedPlans(user: SignedInUser): List<SavedPlan> {
        val emailKey = user.uid.ifBlank { user.email.ifBlank { user.name.lowercase().replace(" ", "_") } }
        val snapshot = firestore.collection(collectionName)
            .document(emailKey)
            .collection("plans")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.data?.toSavedPlan() }
    }

    suspend fun deletePlan(user: SignedInUser, planId: Long) {
        val emailKey = user.uid.ifBlank { user.email.ifBlank { user.name.lowercase().replace(" ", "_") } }
        firestore.collection(collectionName)
            .document(emailKey)
            .collection("plans")
            .document(planId.toString())
            .delete()
            .await()
    }

    suspend fun saveTimelineHistory(user: SignedInUser, plan: SavedPlan) {
        val emailKey = user.uid.ifBlank { user.email.ifBlank { user.name.lowercase().replace(" ", "_") } }
        firestore.collection(collectionName)
            .document(emailKey)
            .collection("timelineHistory")
            .document(plan.id.toString())
            .set(plan.toMap(), SetOptions.merge())
            .await()
    }

    suspend fun getTimelineHistory(user: SignedInUser): List<SavedPlan> {
        val emailKey = user.uid.ifBlank { user.email.ifBlank { user.name.lowercase().replace(" ", "_") } }
        val snapshot = firestore.collection(collectionName)
            .document(emailKey)
            .collection("timelineHistory")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.data?.toSavedPlan() }
    }

    suspend fun deleteTimelineHistory(user: SignedInUser, planId: Long) {
        val emailKey = user.uid.ifBlank { user.email.ifBlank { user.name.lowercase().replace(" ", "_") } }
        firestore.collection(collectionName)
            .document(emailKey)
            .collection("timelineHistory")
            .document(planId.toString())
            .delete()
            .await()
    }

    private fun SavedPlan.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "createdAt" to createdAt,
        "itinerary" to itinerary.toMap()
    )

    private fun DayPlanItinerary.toMap(): Map<String, Any?> = mapOf(
        "locations" to locations.map { loc ->
            mapOf(
                "name" to loc.name,
                "description" to loc.description,
                "lat" to loc.position.lat,
                "lng" to loc.position.lng,
                "time" to loc.time,
                "duration" to loc.duration,
                "sequence" to loc.sequence
            )
        },
        "legs" to legs.map { leg ->
            mapOf(
                "name" to leg.name,
                "transport" to leg.transport,
                "travelTime" to leg.travelTime,
                "startLat" to leg.start.lat,
                "startLng" to leg.start.lng,
                "endLat" to leg.end.lat,
                "endLng" to leg.end.lng
            )
        }
    )

    private fun Map<String, Any?>.toItinerary(): DayPlanItinerary {
        val locations = (this["locations"] as? List<*>)?.mapNotNull { loc ->
            (loc as? Map<*, *>)?.let { map ->
                val lat = map["lat"].toDoubleOrNull()
                val lng = map["lng"].toDoubleOrNull()
                if (lat != null && lng != null) {
                    DayPlanLocation(
                        name = map["name"] as? String ?: "",
                        description = map["description"] as? String ?: "",
                        position = com.example.mapsplanner.data.LatLng(lat, lng),
                        time = map["time"] as? String ?: "",
                        duration = map["duration"] as? String ?: "",
                        sequence = (map["sequence"] as? Number)?.toInt() ?: 0
                    )
                } else null
            }
        } ?: emptyList()
        val legs = (this["legs"] as? List<*>)?.mapNotNull { leg ->
            (leg as? Map<*, *>)?.let { map ->
                val startLat = map["startLat"].toDoubleOrNull()
                val startLng = map["startLng"].toDoubleOrNull()
                val endLat = map["endLat"].toDoubleOrNull()
                val endLng = map["endLng"].toDoubleOrNull()
                if (startLat != null && startLng != null && endLat != null && endLng != null) {
                    RouteLeg(
                        name = map["name"] as? String ?: "",
                        start = com.example.mapsplanner.data.LatLng(startLat, startLng),
                        end = com.example.mapsplanner.data.LatLng(endLat, endLng),
                        transport = map["transport"] as? String ?: "",
                        travelTime = map["travelTime"] as? String ?: ""
                    )
                } else null
            }
        } ?: emptyList()
        return DayPlanItinerary(locations = locations, legs = legs)
    }

    private fun Map<String, Any?>.toSavedPlan(): SavedPlan? {
        val itinerary = (this["itinerary"] as? Map<String, Any?>)?.toItinerary() ?: return null
        val id = (this["id"] as? Number)?.toLong() ?: return null
        val title = this["title"] as? String ?: "Saved Plan"
        val createdAt = (this["createdAt"] as? Number)?.toLong() ?: 0L
        return SavedPlan(id = id, title = title, itinerary = itinerary, createdAt = createdAt)
    }

    private fun Any?.toDoubleOrNull(): Double? = when (this) {
        is Number -> toDouble()
        is String -> toDoubleOrNull()
        else -> null
    }
}
