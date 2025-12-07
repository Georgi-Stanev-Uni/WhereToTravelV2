package com.example.wheretotravel.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.wheretotravel.ui.PlacesViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission") // we check permission before enabling location
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesMapScreen(
    viewModel: PlacesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // only places with coordinates
    val placesWithCoords = remember(state.places) {
        state.places.filter { it.latitude != null && it.longitude != null }
    }

    // 1) Runtime permission
    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isLocationPermissionGranted = granted
    }

    LaunchedEffect(Unit) {
        val grantedFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val grantedCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (grantedFine || grantedCoarse) {
            isLocationPermissionGranted = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 2) User location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(isLocationPermissionGranted) {
        if (isLocationPermissionGranted) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    // 3) Initial camera position
    val fallbackPosition = placesWithCoords.firstOrNull()?.let {
        LatLng(it.latitude!!, it.longitude!!)
    } ?: LatLng(42.6977, 23.3219) // Sofia by default

    val initialPosition = userLocation ?: fallbackPosition

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 6f)
    }

    // Move camera to user location when we get it
    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(loc, 12f)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Places Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
                    }
                }
            )
        }
    ) { paddingValues ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = isLocationPermissionGranted
            )
        ) {
            placesWithCoords.forEach { place ->
                val pos = LatLng(place.latitude!!, place.longitude!!)
                val snippet = listOfNotNull(
                    listOfNotNull(place.city, place.country).takeIf { it.isNotEmpty() }?.joinToString(", "),
                    place.description
                ).joinToString(" • ")

                Marker(
                    state = MarkerState(position = pos),
                    title = place.name,
                    snippet = if (snippet.isBlank()) null else snippet
                )
            }
        }
    }
}
