package com.example.wheretotravel.data.repository

import com.example.wheretotravel.data.local.PlaceDao
import com.example.wheretotravel.data.local.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getPlaces(): Flow<List<PlaceEntity>>
    suspend fun getPlace(id: Long): PlaceEntity?
    suspend fun addPlace(place: PlaceEntity)
    suspend fun updatePlace(place: PlaceEntity)
    suspend fun deletePlace(place: PlaceEntity)
}

class PlaceRepositoryImpl(
    private val placeDao: PlaceDao
) : PlaceRepository {

    override fun getPlaces(): Flow<List<PlaceEntity>> = placeDao.getAllPlaces()

    override suspend fun getPlace(id: Long): PlaceEntity? = placeDao.getPlaceById(id)

    override suspend fun addPlace(place: PlaceEntity) {
        placeDao.insertPlace(place)
    }

    override suspend fun updatePlace(place: PlaceEntity) {
        placeDao.updatePlace(place)
    }

    override suspend fun deletePlace(place: PlaceEntity) {
        placeDao.deletePlace(place)
    }
}
