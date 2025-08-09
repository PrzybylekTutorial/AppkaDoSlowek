package com.example.wordwise.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.preferences.PreferencesGlanceStateDefinition
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.dp
import com.example.wordwise.WordWiseApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class WordOfDayWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {
        GlanceTheme {
            val context = LocalContext.current
            val words = (context.applicationContext as WordWiseApp).database.wordDao()
            val list = runBlocking { words.observeAll().first() }
            val prefs = currentState<Preferences>()
            val indexKey = intPreferencesKey("idx")
            val idx = (prefs[indexKey] ?: 0).coerceIn(0, (list.size - 1).coerceAtLeast(0))
            val item = list.getOrNull(idx)

            Column(
                modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.primaryContainer).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = item?.term ?: "Brak słówek")
                Spacer(GlanceModifier.height(4.dp))
                Text(text = item?.translation ?: "Dodaj słówka w aplikacji")
                Spacer(GlanceModifier.height(8.dp))
                Row(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "<", modifier = GlanceModifier.clickable(actionRunCallback<PrevCallback>()))
                    Spacer(GlanceModifier.width(16.dp))
                    Text(text = ">", modifier = GlanceModifier.clickable(actionRunCallback<NextCallback>()))
                }
            }
        }
    }
}

class WordOfDayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WordOfDayWidget()
}

class NextCallback : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: androidx.glance.action.ActionParameters) {
        val store = PreferencesGlanceStateDefinition.getDataStore(context, glanceId)
        val indexKey = intPreferencesKey("idx")
        store.edit { prefs ->
            val next = (prefs[indexKey] ?: 0) + 1
            prefs[indexKey] = next
        }
        WordOfDayWidget().update(context, glanceId)
    }
}

class PrevCallback : androidx.glance.appwidget.action.ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: androidx.glance.action.ActionParameters) {
        val store = PreferencesGlanceStateDefinition.getDataStore(context, glanceId)
        val indexKey = intPreferencesKey("idx")
        store.edit { prefs ->
            val prev = (prefs[indexKey] ?: 0) - 1
            prefs[indexKey] = prev.coerceAtLeast(0)
        }
        WordOfDayWidget().update(context, glanceId)
    }
}