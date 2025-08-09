package com.example.wordwise

import android.app.Application
import com.example.wordwise.data.db.AppDatabase

class WordWiseApp : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
    }
}