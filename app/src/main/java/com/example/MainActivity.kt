package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.data.local.ClinicDatabase
import com.example.data.local.ClinicRepository
import com.example.ui.ClinicApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = ClinicDatabase.getInstance(applicationContext)
        val repository = ClinicRepository(database.clinicDao())

        // Prepopulate database with realistic clinic seed data if first run
        lifecycleScope.launch {
            repository.initializeSeedDataIfNeeded()
        }

        setContent {
            ClinicApp(repository = repository)
        }
    }
}
