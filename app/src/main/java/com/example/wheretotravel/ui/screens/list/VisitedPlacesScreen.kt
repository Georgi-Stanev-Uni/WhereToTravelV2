package com.example.wheretotravel.ui.screens.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wheretotravel.data.local.PlaceEntity
import com.example.wheretotravel.ui.PlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitedPlacesScreen(
    viewModel: PlacesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val visitedPlaces = state.places.filter { it.visited }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visited Places") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (visitedPlaces.isEmpty()) {
                Text(
                    text = "No visited places yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(visitedPlaces, key = { it.id }) { place ->
                        VisitedPlaceRow(
                            place = place,
                            onDelete = { viewModel.deletePlace(place) },
                            onUnvisit = {
                                viewModel.setVisited(place, false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VisitedPlaceRow(
    place: PlaceEntity,
    onDelete: () -> Unit,
    onUnvisit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(place.name, fontWeight = FontWeight.Bold)
            if (!place.description.isNullOrBlank()) {
                Text(place.description!!)
            }
            Text("Lat: ${place.latitude}, Lng: ${place.longitude}")
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onUnvisit) {
                    Text("Unvisit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}
