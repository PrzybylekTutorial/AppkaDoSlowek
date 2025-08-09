package com.example.wordwise.ui.screens.learn

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordwise.viewmodel.LearnViewModel
import kotlinx.coroutines.launch

@Composable
fun LearnScreen(
    onClose: () -> Unit,
    viewModel: LearnViewModel = viewModel(factory = com.example.wordwise.viewmodel.LearnViewModel.Companion.provideFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
) {
    val queue by viewModel.queue.collectAsState()
    val index by viewModel.currentIndex.collectAsState()
    var answer by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadDue()
    }

    if (queue.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Brak słówek do powtórki")
        }
        return
    }

    val current = queue.getOrNull(index)

    if (current == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Koniec sesji!")
                Spacer(Modifier.height(12.dp))
                Button(onClick = onClose) { Text("Zamknij") }
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = current.term)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Tłumaczenie") })
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                val isCorrect = answer.trim().equals(current.translation.trim(), ignoreCase = true)
                viewModel.mark(isCorrect)
                answer = ""
            }) { Text("Sprawdź") }
            TextButton(onClick = {
                // Reveal translation
                answer = current.translation
            }) { Text("Pokaż odpowiedź") }
        }
    }
}