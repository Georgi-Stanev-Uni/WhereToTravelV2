package com.example.wheretotravel.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
fun PlacesListScreen(
    viewModel: PlacesViewModel,
    onOpenMap: () -> Unit,
    onOpenVisited: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // показваме само НЕ visited местата
    val unvisitedPlaces = state.places.filter { !it.visited }

    var dialogMode by remember { mutableStateOf<PlaceDialogMode?>(null) }
    var editingPlace by remember { mutableStateOf<PlaceEntity?>(null) }

    // състояние за нашето FAB меню
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Where To Travel") }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Малките правоъгълни бутони, които излизат НАД основния FAB
                if (menuExpanded) {
                    Column(
                        modifier = Modifier.padding(bottom = 72.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        // Add
                        ExtendedFloatingActionButton(
                            onClick = {
                                dialogMode = PlaceDialogMode.ADD
                                editingPlace = null
                                menuExpanded = false
                            }
                        ) {
                            Text("Add")
                        }

                        // Visited
                        ExtendedFloatingActionButton(
                            onClick = {
                                onOpenVisited()
                                menuExpanded = false
                            }
                        ) {
                            Text("Visited")
                        }

                        // Map
                        ExtendedFloatingActionButton(
                            onClick = {
                                onOpenMap()
                                menuExpanded = false
                            }
                        ) {
                            Text("Map")
                        }
                    }
                }

                // Основният FAB – триъгълник с удивителен (Warning иконата)
                FloatingActionButton(
                    onClick = { menuExpanded = !menuExpanded }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Menu"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (unvisitedPlaces.isEmpty()) {
                Text(
                    text = "No places to visit. Tap ! to add.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(unvisitedPlaces, key = { it.id }) { place ->
                        PlaceRow(
                            place = place,
                            onDelete = { viewModel.deletePlace(place) },
                            onEdit = {
                                editingPlace = place
                                dialogMode = PlaceDialogMode.EDIT
                            },
                            onMarkVisited = {
                                viewModel.setVisited(place, true)
                            }
                        )
                    }
                }
            }
        }
    }

    // Общият диалог за Add/Edit
    if (dialogMode != null) {
        PlaceDialog(
            mode = dialogMode!!,
            place = editingPlace,
            onDismiss = {
                dialogMode = null
                editingPlace = null
            },
            onConfirm = { name, desc, lat, lng ->
                when (dialogMode) {
                    PlaceDialogMode.ADD -> {
                        viewModel.addPlace(name, desc, lat, lng)
                    }
                    PlaceDialogMode.EDIT -> {
                        editingPlace?.let { old ->
                            viewModel.updatePlace(
                                old.copy(
                                    name = name,
                                    description = desc,
                                    latitude = lat,
                                    longitude = lng
                                )
                            )
                        }
                    }
                    null -> Unit
                }
                dialogMode = null
                editingPlace = null
            }
        )
    }
}

@Composable
private fun PlaceRow(
    place: PlaceEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onMarkVisited: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
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
                TextButton(onClick = onMarkVisited) {
                    Text("Mark visited")
                }
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}
