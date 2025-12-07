package com.example.wheretotravel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wheretotravel.data.local.PlaceEntity
import com.example.wheretotravel.data.repository.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.collect

data class PlacesUiState(
    val places: List<PlaceEntity> = emptyList(),
    val isLoading: Boolean = false
)

class PlacesViewModel(
    private val repository: PlaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlacesUiState())
    val uiState: StateFlow<PlacesUiState> = _uiState.asStateFlow()

    init {
        observePlaces()
    }

    private fun observePlaces() {
        viewModelScope.launch {
            repository.getPlaces()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { places ->
                    _uiState.value = PlacesUiState(
                        places = places,
                        isLoading = false
                    )
                }
        }
    }

    fun addPlace(
        name: String,
        description: String?,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            repository.addPlace(
                PlaceEntity(
                    name = name,
                    description = description,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }
    }

    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.deletePlace(place)
        }
    }

    fun updatePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.updatePlace(place)
        }
    }

    fun setVisited(place: PlaceEntity, visited: Boolean) {
        viewModelScope.launch {
            repository.updatePlace(place.copy(visited = visited))
        }
    }
}

class PlacesViewModelFactory(
    private val repository: PlaceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            return PlacesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


