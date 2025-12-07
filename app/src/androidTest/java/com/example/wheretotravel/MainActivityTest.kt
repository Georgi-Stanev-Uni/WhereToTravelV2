package com.example.wheretotravel

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addPlace_showsInList() {
        // initial empty text
        composeRule.onNodeWithText("No places to visit. Tap ! to add.").assertIsDisplayed()

        // open FAB menu (the ! button)
        composeRule.onNodeWithContentDescription("Menu").performClick()

        // click "Add"
        composeRule.onNodeWithText("Add").performClick()

        // edit name field
        val nameField = composeRule.onNodeWithTag("nameField")
        nameField.performTextClearance()
        nameField.performTextInput("UI Test Place")

        // confirm
        composeRule.onNodeWithTag("confirmButton").performClick()

        // new place should be visible in the list
        composeRule.onNodeWithText("UI Test Place").assertIsDisplayed()
    }
}
