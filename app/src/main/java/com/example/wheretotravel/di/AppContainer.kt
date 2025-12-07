package com.example.wheretotravel.di

import android.content.Context
import com.example.wheretotravel.data.local.AppDatabase
import com.example.wheretotravel.data.repository.PlaceRepository
import com.example.wheretotravel.data.repository.PlaceRepositoryImpl

class AppContainer(context: Context) {

    private val database = AppDatabase.getDatabase(context)

    val placeRepository: PlaceRepository by lazy {
        PlaceRepositoryImpl(database.placeDao())
    }
}
