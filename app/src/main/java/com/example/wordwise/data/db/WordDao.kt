package com.example.wordwise.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY term ASC")
    fun observeAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(word: WordEntity): Long

    @Delete
    suspend fun delete(word: WordEntity)

    @Query("SELECT * FROM words WHERE nextReviewEpochSeconds IS NULL OR nextReviewEpochSeconds <= :now ORDER BY nextReviewEpochSeconds ASC LIMIT :limit")
    suspend fun getDue(now: Long, limit: Int): List<WordEntity>

    @Query("SELECT COUNT(*) FROM words")
    fun observeCount(): Flow<Int>
}