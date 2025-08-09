package com.example.wordwise.ui.screens.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordwise.data.db.WordEntity
import com.example.wordwise.viewmodel.WordViewModel
import com.example.wordwise.viewmodel.WordViewModel.Companion.provideFactory

@Composable
fun WordListScreen(
    onAddNew: () -> Unit,
    onLearn: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: WordViewModel = viewModel(factory = provideFactory(LocalContext.current.applicationContext as android.app.Application))
) {
    val words by viewModel.words.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) viewModel.exportCsv(uri)
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importCsv(uri)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Twoje słówka", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Row {
                TextButton(onClick = { importLauncher.launch(arrayOf("text/*", "text/csv")) }) { Text("Import") }
                TextButton(onClick = { exportLauncher.launch("words_export.csv") }) { Text("Eksport") }
                Button(onClick = onLearn) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Nauka")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(words, key = { it.id }) { word ->
                WordRow(
                    word = word,
                    onEdit = { onEdit(word.id) },
                    onDelete = { viewModel.delete(word) }
                )
                Divider()
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddNew,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Dodaj")
        }
    }
}

@Composable
private fun WordRow(word: WordEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(word.term, style = MaterialTheme.typography.titleMedium)
            Text(word.translation, style = MaterialTheme.typography.bodyMedium)
            if (!word.example.isNullOrBlank()) {
                Text(word.example!!, style = MaterialTheme.typography.bodySmall)
            }
        }
        Row {
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edytuj") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Usuń") }
        }
    }
}