package com.example.mapsplanner.repository

import com.example.mapsplanner.data.DayPlanItinerary
import com.example.mapsplanner.network.GenAiClient

class PlannerRepository(
    private val client: GenAiClient = GenAiClient()
) {
    suspend fun generate(prompt: String): DayPlanItinerary = client.generateDayPlan(prompt)
}
