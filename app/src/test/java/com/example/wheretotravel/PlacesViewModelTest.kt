package com.example.wheretotravel

import com.example.wheretotravel.ui.PlacesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlacesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: FakePlaceRepository
    private lateinit var viewModel: PlacesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePlaceRepository()
        viewModel = PlacesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addPlace_updatesUiState() = runTest(testDispatcher) {
        // when
        viewModel.addPlace(
            name = "Test Place",
            description = "Description",
            latitude = 42.0,
            longitude = 23.0
        )

        advanceUntilIdle()

        // then
        val places = viewModel.uiState.value.places
        assertEquals(1, places.size)
        assertEquals("Test Place", places[0].name)
    }

    @Test
    fun updatePlace_changesExistingItem() = runTest(testDispatcher) {
        viewModel.addPlace("Old Name", null, 42.0, 23.0)
        advanceUntilIdle()
        val original = viewModel.uiState.value.places.first()

        viewModel.updatePlace(original.copy(name = "New Name"))
        advanceUntilIdle()

        val updated = viewModel.uiState.value.places.first()
        assertEquals("New Name", updated.name)
    }

    @Test
    fun deletePlace_removesItem() = runTest(testDispatcher) {
        viewModel.addPlace("Place 1", null, 42.0, 23.0)
        advanceUntilIdle()
        val place = viewModel.uiState.value.places.first()

        viewModel.deletePlace(place)
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.places.size)
    }
}
