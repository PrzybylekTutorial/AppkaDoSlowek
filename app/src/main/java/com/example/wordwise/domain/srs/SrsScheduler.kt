package com.example.wordwise.domain.srs

import java.time.Instant
import java.time.temporal.ChronoUnit

object SrsScheduler {
    private val intervalsDays = listOf(1L, 3L, 7L, 14L, 30L, 60L, 120L)

    fun nextIntervalEpochSeconds(streak: Int): Long {
        val days = intervalsDays.getOrElse(streak.coerceAtMost(intervalsDays.lastIndex)) { intervalsDays.last() }
        return Instant.now().plus(days, ChronoUnit.DAYS).epochSecond
    }
}