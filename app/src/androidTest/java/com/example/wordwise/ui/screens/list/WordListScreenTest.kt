package com.example.wordwise.ui.screens.list

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordwise.data.db.WordEntity
import com.example.wordwise.ui.theme.WordWiseTheme
import com.example.wordwise.viewmodel.WordViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun wordListScreen_displaysEmptyStateWhenNoWords() {
        // Given
        val mockViewModel = mockk<WordViewModel>()
        every { mockViewModel.words } returns flowOf(emptyList())

        var addNewClicked = false
        var learnClicked = false

        // When
        composeTestRule.setContent {
            WordWiseTheme {
                WordListScreen(
                    onAddNew = { addNewClicked = true },
                    onLearn = { learnClicked = true },
                    onEdit = { },
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Twoje słówka").assertIsDisplayed()
        composeTestRule.onNodeWithText("Import").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eksport").assertIsDisplayed()
    }

    @Test
    fun wordListScreen_displaysWordsWhenPresent() {
        // Given
        val testWords = listOf(
            WordEntity(1, "house", "dom", "This is my house."),
            WordEntity(2, "cat", "kot", "The cat sleeps.")
        )
        val mockViewModel = mockk<WordViewModel>()
        every { mockViewModel.words } returns flowOf(testWords)

        // When
        composeTestRule.setContent {
            WordWiseTheme {
                WordListScreen(
                    onAddNew = { },
                    onLearn = { },
                    onEdit = { },
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("house").assertIsDisplayed()
        composeTestRule.onNodeWithText("dom").assertIsDisplayed()
        composeTestRule.onNodeWithText("cat").assertIsDisplayed()
        composeTestRule.onNodeWithText("kot").assertIsDisplayed()
    }

    @Test
    fun wordListScreen_callsOnAddNewWhenAddButtonClicked() {
        // Given
        val mockViewModel = mockk<WordViewModel>()
        every { mockViewModel.words } returns flowOf(emptyList())

        var addNewClicked = false

        composeTestRule.setContent {
            WordWiseTheme {
                WordListScreen(
                    onAddNew = { addNewClicked = true },
                    onLearn = { },
                    onEdit = { },
                    viewModel = mockViewModel
                )
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription("Dodaj nowe słówko").performClick()

        // Then
        assert(addNewClicked)
    }

    @Test
    fun wordListScreen_callsOnLearnWhenLearnButtonClicked() {
        // Given
        val testWords = listOf(
            WordEntity(1, "house", "dom", "This is my house.")
        )
        val mockViewModel = mockk<WordViewModel>()
        every { mockViewModel.words } returns flowOf(testWords)

        var learnClicked = false

        composeTestRule.setContent {
            WordWiseTheme {
                WordListScreen(
                    onAddNew = { },
                    onLearn = { learnClicked = true },
                    onEdit = { },
                    viewModel = mockViewModel
                )
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription("Rozpocznij naukę").performClick()

        // Then
        assert(learnClicked)
    }

    @Test
    fun wordListScreen_displaysImportAndExportButtons() {
        // Given
        val mockViewModel = mockk<WordViewModel>()
        every { mockViewModel.words } returns flowOf(emptyList())

        // When
        composeTestRule.setContent {
            WordWiseTheme {
                WordListScreen(
                    onAddNew = { },
                    onLearn = { },
                    onEdit = { },
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Import").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eksport").assertIsDisplayed()
    }
}