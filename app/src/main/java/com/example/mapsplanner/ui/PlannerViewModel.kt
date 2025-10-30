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
        _state.value = PlannerUiState(signedInUser = user)
    }

    fun generatePlan() {
        val prompt = _state.value.prompt.takeIf { it.isNotBlank() } ?: return
        fetchPlan(prompt)
    }

    fun generatePlanForLocation(lat: Double, lng: Double) {
        val prompt = "Create a one-day plan near latitude $lat and longitude $lng."
        fetchPlan(prompt)
    }

    private fun fetchPlan(prompt: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { repository.generate(prompt) }
                .onSuccess { itinerary ->
                    _state.value = _state.value.copy(
                        itinerary = itinerary,
                        isLoading = false,
                        selectedLocationIndex = itinerary.locations.indices.firstOrNull() ?: 0
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
        _state.value = _state.value.copy(signedInUser = user)
        viewModelScope.launch {
            runCatching { userRepository.upsertUser(user) }
                .onFailure { err ->
                    _state.value = _state.value.copy(
                        error = err.message ?: "Gagal menyimpan data pengguna ke Firestore."
                    )
                }
        }
    }

    fun onUserSignedOut() {
        _state.value = _state.value.copy(signedInUser = null)
    }
}

data class PlannerUiState(
    val prompt: String = "",
    val itinerary: DayPlanItinerary = DayPlanItinerary(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showTimeline: Boolean = false,
    val selectedLocationIndex: Int = 0,
    val signedInUser: SignedInUser? = null
)

data class SignedInUser(
    val name: String,
    val email: String,
    val photoUrl: String?
)
