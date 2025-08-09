package com.example.wordwise.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val term: String,
    val translation: String,
    val example: String? = null,
    val createdAtEpochSeconds: Long = Instant.now().epochSecond,
    val lastReviewedEpochSeconds: Long? = null,
    val nextReviewEpochSeconds: Long? = null,
    val correctStreak: Int = 0,
)