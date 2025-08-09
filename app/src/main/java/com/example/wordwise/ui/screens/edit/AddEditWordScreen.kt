package com.example.wordwise.ui.screens.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordwise.viewmodel.WordViewModel

@Composable
fun AddEditWordScreen(
    wordId: Long? = null,
    onDone: () -> Unit,
    viewModel: WordViewModel = viewModel(factory = com.example.wordwise.viewmodel.WordViewModel.Companion.provideFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
) {
    var term by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }

    LaunchedEffect(wordId) {
        if (wordId != null) {
            val existing = viewModel.words.value.firstOrNull { it.id == wordId }
            if (existing != null) {
                term = existing.term
                translation = existing.translation
                example = existing.example ?: ""
            } else {
                // Optionally could fetch directly from repository if list not loaded
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = term, onValueChange = { term = it }, label = { Text("Słowo") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = translation, onValueChange = { translation = it }, label = { Text("Tłumaczenie") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = example, onValueChange = { example = it }, label = { Text("Przykład (opcjonalnie)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.upsert(term.trim(), translation.trim(), example.trim().ifBlank { null }, id = wordId)
            onDone()
        }, enabled = term.isNotBlank() && translation.isNotBlank()) {
            Text("Zapisz")
        }
    }
}