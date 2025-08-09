package com.example.wordwise.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.wordwise.data.db.WordEntity
import com.example.wordwise.data.repo.WordRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class WordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockRepository = mockk<WordRepository>()
    private lateinit var viewModel: WordViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockRepository.observeAll() } returns flowOf(emptyList())
        viewModel = WordViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `words flow emits empty list initially`() = runTest {
        // Given
        val testWords = emptyList<WordEntity>()
        every { mockRepository.observeAll() } returns flowOf(testWords)
        
        // When
        val newViewModel = WordViewModel(mockRepository)
        
        // Then
        assertEquals(testWords, newViewModel.words.value)
    }

    @Test
    fun `words flow emits word list from repository`() = runTest {
        // Given
        val testWords = listOf(
            WordEntity(1, "house", "dom", "This is my house."),
            WordEntity(2, "cat", "kot", "The cat sleeps.")
        )
        every { mockRepository.observeAll() } returns flowOf(testWords)
        
        // When
        val newViewModel = WordViewModel(mockRepository)
        
        // Then
        assertEquals(testWords, newViewModel.words.value)
    }

    @Test
    fun `upsert calls repository with correct entity for new word`() = runTest {
        // Given
        val term = "house"
        val translation = "dom"
        val example = "This is my house."
        coEvery { mockRepository.upsert(any()) } returns 1L

        // When
        viewModel.upsert(term, translation, example)

        // Then
        coVerify {
            mockRepository.upsert(
                match { entity ->
                    entity.term == term &&
                    entity.translation == translation &&
                    entity.example == example &&
                    entity.id == 0L
                }
            )
        }
    }

    @Test
    fun `upsert calls repository with correct entity for existing word`() = runTest {
        // Given
        val term = "house"
        val translation = "dom"
        val example = "This is my house."
        val id = 5L
        coEvery { mockRepository.upsert(any()) } returns id

        // When
        viewModel.upsert(term, translation, example, id)

        // Then
        coVerify {
            mockRepository.upsert(
                match { entity ->
                    entity.term == term &&
                    entity.translation == translation &&
                    entity.example == example &&
                    entity.id == id
                }
            )
        }
    }

    @Test
    fun `delete calls repository with correct word`() = runTest {
        // Given
        val word = WordEntity(1, "house", "dom", "This is my house.")
        coEvery { mockRepository.delete(any()) } just Runs

        // When
        viewModel.delete(word)

        // Then
        coVerify { mockRepository.delete(word) }
    }

    @Test
    fun `exportCsv calls repository with correct uri`() = runTest {
        // Given
        val uri = mockk<Uri>()
        coEvery { mockRepository.exportCsv(any()) } just Runs

        // When
        viewModel.exportCsv(uri)

        // Then
        coVerify { mockRepository.exportCsv(uri) }
    }

    @Test
    fun `importCsv calls repository with correct uri`() = runTest {
        // Given
        val uri = mockk<Uri>()
        coEvery { mockRepository.importCsv(any()) } just Runs

        // When
        viewModel.importCsv(uri)

        // Then
        coVerify { mockRepository.importCsv(uri) }
    }
}