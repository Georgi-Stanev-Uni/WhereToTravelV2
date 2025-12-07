package com.example.wheretotravel.ui.screens.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import com.example.wheretotravel.data.local.PlaceEntity

enum class PlaceDialogMode {
    ADD,
    EDIT
}

@Composable
fun PlaceDialog(
    mode: PlaceDialogMode,
    place: PlaceEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?, lat: Double, lng: Double) -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(place?.name ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(place?.description ?: "")) }
    var latitude by remember {
        mutableStateOf(
            TextFieldValue(
                place?.latitude?.toString() ?: "42.6977" // default Sofia
            )
        )
    }
    var longitude by remember {
        mutableStateOf(
            TextFieldValue(
                place?.longitude?.toString() ?: "23.3219"
            )
        )
    }

    val titleText = if (mode == PlaceDialogMode.ADD) "Add place" else "Edit place"
    val buttonText = if (mode == PlaceDialogMode.ADD) "Save" else "Update"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleText) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.testTag("nameField"),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    modifier = Modifier.testTag("descriptionField"),
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") }
                )
                OutlinedTextField(
                    modifier = Modifier.testTag("latitudeField"),
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") }
                )
                OutlinedTextField(
                    modifier = Modifier.testTag("longitudeField"),
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") }
                )
            }
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.testTag("confirmButton"),
                onClick = {
                    val lat = latitude.text.toDoubleOrNull()
                    val lng = longitude.text.toDoubleOrNull()
                    if (!name.text.isBlank() && lat != null && lng != null) {
                        onConfirm(
                            name.text,
                            description.text.ifBlank { null },
                            lat,
                            lng
                        )
                    }
                }
            ) {
                Text(buttonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
