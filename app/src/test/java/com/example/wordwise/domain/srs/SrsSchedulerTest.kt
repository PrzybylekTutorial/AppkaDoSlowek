package com.example.wordwise.domain.srs

import org.junit.Test
import org.junit.Assert.*
import java.time.Instant
import java.time.temporal.ChronoUnit

class SrsSchedulerTest {

    @Test
    fun `nextIntervalEpochSeconds returns correct interval for streak 0`() {
        val beforeTime = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond
        val result = SrsScheduler.nextIntervalEpochSeconds(0)
        val afterTime = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond
        
        assertTrue("Result should be within expected range", result in beforeTime..afterTime)
    }
    
    @Test
    fun `nextIntervalEpochSeconds returns correct interval for streak 1`() {
        val beforeTime = Instant.now().plus(3, ChronoUnit.DAYS).epochSecond
        val result = SrsScheduler.nextIntervalEpochSeconds(1)
        val afterTime = Instant.now().plus(3, ChronoUnit.DAYS).epochSecond
        
        assertTrue("Result should be within expected range", result in beforeTime..afterTime)
    }
    
    @Test
    fun `nextIntervalEpochSeconds returns correct interval for streak 2`() {
        val beforeTime = Instant.now().plus(7, ChronoUnit.DAYS).epochSecond
        val result = SrsScheduler.nextIntervalEpochSeconds(2)
        val afterTime = Instant.now().plus(7, ChronoUnit.DAYS).epochSecond
        
        assertTrue("Result should be within expected range", result in beforeTime..afterTime)
    }
    
    @Test
    fun `nextIntervalEpochSeconds caps at maximum interval for high streak`() {
        val beforeTime = Instant.now().plus(120, ChronoUnit.DAYS).epochSecond
        val result = SrsScheduler.nextIntervalEpochSeconds(100) // Very high streak
        val afterTime = Instant.now().plus(120, ChronoUnit.DAYS).epochSecond
        
        assertTrue("Result should be capped at maximum interval", result in beforeTime..afterTime)
    }
    
    @Test
    fun `nextIntervalEpochSeconds handles negative streak`() {
        val beforeTime = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond
        val result = SrsScheduler.nextIntervalEpochSeconds(-5)
        val afterTime = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond
        
        assertTrue("Negative streak should use first interval", result in beforeTime..afterTime)
    }
}