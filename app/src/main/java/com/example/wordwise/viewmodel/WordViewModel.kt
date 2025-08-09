package com.example.wordwise.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordwise.WordWiseApp
import com.example.wordwise.data.db.WordEntity
import com.example.wordwise.data.repo.WordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    val words: StateFlow<List<WordEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun upsert(term: String, translation: String, example: String?, id: Long? = null) {
        viewModelScope.launch {
            val entity = WordEntity(
                id = id ?: 0,
                term = term,
                translation = translation,
                example = example
            )
            repository.upsert(entity)
        }
    }

    fun delete(word: WordEntity) {
        viewModelScope.launch { repository.delete(word) }
    }

    fun exportCsv(uri: Uri) {
        viewModelScope.launch { repository.exportCsv(uri) }
    }

    fun importCsv(uri: Uri) {
        viewModelScope.launch { repository.importCsv(uri) }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = application as WordWiseApp
                    val repo = WordRepository(app.database.wordDao(), app.contentResolver)
                    return WordViewModel(repo) as T
                }
            }
    }
}