package com.example.wordwise.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordwise.WordWiseApp
import com.example.wordwise.data.db.WordEntity
import com.example.wordwise.data.repo.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LearnViewModel(private val repository: WordRepository) : ViewModel() {
    private val _queue = MutableStateFlow<List<WordEntity>>(emptyList())
    val queue: StateFlow<List<WordEntity>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    suspend fun loadDue(limit: Int = 20) {
        _queue.value = repository.getDue(limit)
        _currentIndex.value = 0
    }

    fun mark(isCorrect: Boolean) {
        val word = _queue.value.getOrNull(_currentIndex.value) ?: return
        viewModelScope.launch {
            repository.markAnswer(word, isCorrect)
            val next = _currentIndex.value + 1
            _currentIndex.value = next
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = application as WordWiseApp
                    val repo = WordRepository(app.database.wordDao(), app.contentResolver)
                    return LearnViewModel(repo) as T
                }
            }
    }
}