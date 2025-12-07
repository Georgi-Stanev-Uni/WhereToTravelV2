package com.example.wheretotravel

import com.example.wheretotravel.data.local.PlaceEntity
import com.example.wheretotravel.data.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePlaceRepository : PlaceRepository {

    private val items = mutableListOf<PlaceEntity>()
    private val placesFlow = MutableStateFlow<List<PlaceEntity>>(emptyList())
    private var nextId = 1L

    override fun getPlaces(): Flow<List<PlaceEntity>> = placesFlow.asStateFlow()

    override suspend fun getPlace(id: Long): PlaceEntity? =
        items.firstOrNull { it.id == id }

    override suspend fun addPlace(place: PlaceEntity) {
        val withId = place.copy(id = nextId++)
        items.add(withId)
        emitList()
    }

    override suspend fun updatePlace(place: PlaceEntity) {
        val index = items.indexOfFirst { it.id == place.id }
        if (index != -1) {
            items[index] = place
            emitList()
        }
    }

    override suspend fun deletePlace(place: PlaceEntity) {
        items.removeAll { it.id == place.id }
        emitList()
    }

    private fun emitList() {
        placesFlow.value = items.toList()
    }
}
