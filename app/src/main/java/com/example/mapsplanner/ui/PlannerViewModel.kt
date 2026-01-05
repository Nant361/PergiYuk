package com.example.mapsplanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsplanner.data.DayPlanItinerary
import com.example.mapsplanner.data.DayPlanLocation
import com.example.mapsplanner.repository.PlannerRepository
import com.example.mapsplanner.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlannerViewModel(
    private val repository: PlannerRepository = PlannerRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(PlannerUiState())
    val state: StateFlow<PlannerUiState> = _state.asStateFlow()

    fun onPromptChanged(value: String) {
        _state.value = _state.value.copy(prompt = value)
    }

    fun selectLocation(location: DayPlanLocation) {
        val selected = _state.value.itinerary.locations.indexOfFirst { it.sequence == location.sequence }
        if (selected >= 0) {
            _state.value = _state.value.copy(selectedLocationIndex = selected)
        }
    }

    fun toggleTimeline(show: Boolean) {
        _state.value = _state.value.copy(showTimeline = show)
    }

    fun reset() {
        val user = _state.value.signedInUser
        val saved = _state.value.savedPlans
        val history = _state.value.timelineHistory
        _state.value = PlannerUiState(
            signedInUser = user,
            savedPlans = saved,
            timelineHistory = history
        )
    }

    fun generatePlan() {
        generatePlanWithLocation(null, null)
    }

    fun generatePlanForLocation(lat: Double, lng: Double) {
        generatePlanWithLocation(lat, lng)
    }

    fun generatePlanWithLocation(lat: Double?, lng: Double?) {
        val userPrompt = _state.value.prompt.trim()
        val titleOverride = if (userPrompt.isBlank() && lat != null && lng != null) "Smart Planner" else null
        val prompt = when {
            userPrompt.isNotBlank() && lat != null && lng != null ->
                "$userPrompt\nLokasi saya: $lat, $lng. Sesuaikan rencana di sekitar titik ini."
            userPrompt.isNotBlank() -> userPrompt
            lat != null && lng != null ->
                "Create a one-day travel plan near latitude $lat and longitude $lng."
            else -> return
        }
        fetchPlan(prompt, titleOverride)
    }

    private fun fetchPlan(prompt: String, titleOverride: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { repository.generate(prompt) }
                .onSuccess { itinerary ->
                    val historyEntry = SavedPlan(
                        id = System.currentTimeMillis(),
                        title = titleOverride ?: prompt.ifBlank { "Timeline perjalanan" },
                        itinerary = itinerary,
                        createdAt = System.currentTimeMillis()
                    )
                    _state.value.signedInUser?.let { user ->
                        runCatching { userRepository.saveTimelineHistory(user, historyEntry) }
                            .onFailure {
                                _state.value = _state.value.copy(
                                    error = it.message ?: "Gagal menyimpan riwayat timeline ke cloud."
                                )
                            }
                    }
                    _state.value = _state.value.copy(
                        itinerary = itinerary,
                        isLoading = false,
                        selectedLocationIndex = itinerary.locations.indices.firstOrNull() ?: 0,
                        timelineHistory = listOf(historyEntry) + _state.value.timelineHistory
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Terjadi kesalahan saat memuat rencana."
                    )
                }
        }
    }

    fun onUserSignedIn(user: SignedInUser) {
        _state.value = _state.value.copy(signedInUser = user, showWelcome = false)
        viewModelScope.launch {
            runCatching { userRepository.upsertUser(user) }
                .onFailure { err ->
                    _state.value = _state.value.copy(
                        error = err.message ?: "Gagal menyimpan data pengguna ke Firestore."
                    )
                }
            runCatching { userRepository.getSavedPlans(user) }
                .onSuccess { plans ->
                    _state.value = _state.value.copy(savedPlans = plans)
                }
                .onFailure { err ->
                    _state.value = _state.value.copy(
                        error = err.message ?: "Gagal memuat rencana tersimpan dari Firestore."
                    )
                }
            runCatching { userRepository.getTimelineHistory(user) }
                .onSuccess { history ->
                    _state.value = _state.value.copy(timelineHistory = history)
                }
                .onFailure { err ->
                    _state.value = _state.value.copy(
                        error = err.message ?: "Gagal memuat riwayat timeline dari Firestore."
                    )
                }
        }
    }

    fun onUserSignedOut() {
        _state.value = PlannerUiState()
    }

    fun dismissWelcome() {
        _state.value = _state.value.copy(showWelcome = false)
    }

    fun saveCurrentPlan() {
        val itinerary = _state.value.itinerary
        if (itinerary.locations.isEmpty()) return
        val user = _state.value.signedInUser
        if (user == null) {
            _state.value = _state.value.copy(error = "Masuk untuk menyimpan rencana.")
            return
        }
        val promptTitle = _state.value.prompt.trim()
        val title = when {
            promptTitle.isNotEmpty() -> promptTitle
            else -> "Smart Planner"
        }
        val newPlan = SavedPlan(
            id = System.currentTimeMillis(),
            title = title,
            itinerary = itinerary,
            createdAt = System.currentTimeMillis()
        )
        _state.value = _state.value.copy(
            savedPlans = listOf(newPlan) + _state.value.savedPlans
        )
        viewModelScope.launch {
            runCatching {
                userRepository.savePlan(user, newPlan)
                userRepository.getSavedPlans(user)
            }.onSuccess { remotePlans ->
                _state.value = _state.value.copy(savedPlans = remotePlans)
            }.onFailure {
                val message = it.message?.let { msg -> "Gagal menyimpan ke cloud: $msg" }
                    ?: "Gagal menyimpan ke cloud."
                _state.value = _state.value.copy(error = message)
            }
        }
    }

    fun loadSavedPlan(planId: Long) {
        val plan = _state.value.savedPlans.firstOrNull { it.id == planId } ?: return
        _state.value = _state.value.copy(
            itinerary = plan.itinerary,
            selectedLocationIndex = plan.itinerary.locations.indices.firstOrNull() ?: 0,
            showTimeline = true
        )
    }

    fun loadTimelinePlan(planId: Long) {
        val plan = _state.value.timelineHistory.firstOrNull { it.id == planId } ?: return
        _state.value = _state.value.copy(
            itinerary = plan.itinerary,
            selectedLocationIndex = plan.itinerary.locations.indices.firstOrNull() ?: 0,
            showTimeline = true
        )
    }

    fun deleteTimelinePlan(planId: Long) {
        _state.value = _state.value.copy(
            timelineHistory = _state.value.timelineHistory.filterNot { it.id == planId }
        )
        viewModelScope.launch {
            _state.value.signedInUser?.let { user ->
                runCatching { userRepository.deleteTimelineHistory(user, planId) }
                    .onFailure {
                        _state.value = _state.value.copy(error = "Gagal menghapus riwayat timeline di cloud.")
                    }
            }
        }
    }

    fun deleteSavedPlan(planId: Long) {
        _state.value = _state.value.copy(
            savedPlans = _state.value.savedPlans.filterNot { it.id == planId }
        )
        viewModelScope.launch {
            _state.value.signedInUser?.let { user ->
                runCatching { userRepository.deletePlan(user, planId) }
                    .onFailure {
                        _state.value = _state.value.copy(error = "Gagal menghapus rencana di cloud.")
                    }
            }
        }
    }
}

data class PlannerUiState(
    val prompt: String = "",
    val itinerary: DayPlanItinerary = DayPlanItinerary(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showTimeline: Boolean = false,
    val selectedLocationIndex: Int = 0,
    val signedInUser: SignedInUser? = null,
    val showWelcome: Boolean = false,
    val savedPlans: List<SavedPlan> = emptyList(),
    val timelineHistory: List<SavedPlan> = emptyList()
)

data class SignedInUser(
    val uid: String,
    val name: String,
    val email: String,
    val photoUrl: String?
)

data class SavedPlan(
    val id: Long,
    val title: String,
    val itinerary: DayPlanItinerary,
    val createdAt: Long
)
