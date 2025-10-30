package com.example.mapsplanner.repository

import com.example.mapsplanner.ui.SignedInUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val collectionName: String = "users"
) {
    private val firestore = Firebase.firestore

    suspend fun upsertUser(user: SignedInUser) {
        val emailKey = user.email.ifBlank { user.name.lowercase().replace(" ", "_") }
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
}
