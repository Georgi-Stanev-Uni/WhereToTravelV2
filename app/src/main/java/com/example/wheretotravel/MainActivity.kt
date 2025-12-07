package com.example.wheretotravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wheretotravel.ui.PlacesViewModel
import com.example.wheretotravel.ui.PlacesViewModelFactory
import com.example.wheretotravel.ui.screens.Routes
import com.example.wheretotravel.ui.screens.list.PlacesListScreen
import com.example.wheretotravel.ui.screens.map.PlacesMapScreen
import com.example.wheretotravel.ui.theme.WhereToTravelTheme
import com.example.wheretotravel.ui.screens.list.VisitedPlacesScreen


class MainActivity : ComponentActivity() {

    private val placesViewModel: PlacesViewModel by viewModels {
        val app = application as WhereToTravelApp
        PlacesViewModelFactory(app.container.placeRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WhereToTravelTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.PLACES_LIST
                ) {
                    composable(Routes.PLACES_LIST) {
                        PlacesListScreen(
                            viewModel = placesViewModel,
                            onOpenMap = { navController.navigate(Routes.PLACES_MAP) },
                            onOpenVisited = { navController.navigate(Routes.PLACES_VISITED) }
                        )
                    }
                    composable(Routes.PLACES_MAP) {
                        PlacesMapScreen(
                            viewModel = placesViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Routes.PLACES_VISITED) {
                        VisitedPlacesScreen(
                            viewModel = placesViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }

            }
        }
    }
}
