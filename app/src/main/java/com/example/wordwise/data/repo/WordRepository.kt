package com.example.wordwise.data.repo

import android.content.ContentResolver
import android.net.Uri
import com.example.wordwise.data.db.WordDao
import com.example.wordwise.data.db.WordEntity
import com.example.wordwise.domain.srs.SrsScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.Instant

class WordRepository(
    private val dao: WordDao,
    private val contentResolver: ContentResolver
) {
    fun observeAll(): Flow<List<WordEntity>> = dao.observeAll()

    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun getById(id: Long): WordEntity? = dao.getById(id)

    suspend fun upsert(word: WordEntity): Long = dao.upsert(word)

    suspend fun delete(word: WordEntity) = dao.delete(word)

    suspend fun markAnswer(word: WordEntity, isCorrect: Boolean): WordEntity {
        val updated = if (isCorrect) {
            val newStreak = word.correctStreak + 1
            word.copy(
                correctStreak = newStreak,
                lastReviewedEpochSeconds = Instant.now().epochSecond,
                nextReviewEpochSeconds = SrsScheduler.nextIntervalEpochSeconds(newStreak)
            )
        } else {
            word.copy(
                correctStreak = 0,
                lastReviewedEpochSeconds = Instant.now().epochSecond,
                nextReviewEpochSeconds = SrsScheduler.nextIntervalEpochSeconds(0)
            )
        }
        dao.upsert(updated)
        return updated
    }

    suspend fun getDue(limit: Int): List<WordEntity> =
        dao.getDue(Instant.now().epochSecond, limit)

    suspend fun exportCsv(target: Uri) {
        contentResolver.openOutputStream(target)?.use { out ->
            OutputStreamWriter(out, StandardCharsets.UTF_8).use { writer ->
                writer.appendLine("term,translation,example")
                val list = observeAll().first()
                list.forEach { w ->
                    writer.appendLine(listOf(w.term, w.translation, w.example ?: "").joinToCsv())
                }
            }
        }
    }

    suspend fun importCsv(source: Uri) {
        contentResolver.openInputStream(source)?.use { input ->
            BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).useLines { lines ->
                lines.drop(1).forEach { line ->
                    val cols = line.parseCsv()
                    if (cols.size >= 2) {
                        val entity = WordEntity(
                            term = cols[0].trim(),
                            translation = cols[1].trim(),
                            example = cols.getOrNull(2)?.trim().takeUnless { it.isNullOrBlank() }
                        )
                        dao.upsert(entity)
                    }
                }
            }
        }
    }
}

private fun List<String>.joinToCsv(): String = buildString {
    this@joinToCsv.forEachIndexed { index, field ->
        if (index > 0) append(',')
        append('"')
        append(field.replace("\"", "\"\""))
        append('"')
    }
}

private fun String.parseCsv(): List<String> {
    val result = mutableListOf<String>()
    var current = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < this.length) {
        val c = this[i]
        when (c) {
            '"' -> {
                if (inQuotes && i + 1 < length && this[i + 1] == '"') {
                    current.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            }
            ',' -> {
                if (inQuotes) current.append(c) else {
                    result.add(current.toString())
                    current = StringBuilder()
                }
            }
            else -> current.append(c)
        }
        i++
    }
    result.add(current.toString())
    return result
}