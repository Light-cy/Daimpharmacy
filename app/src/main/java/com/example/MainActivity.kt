package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.AppDatabase
import com.example.data.PharmacyRepository
import com.example.ui.AppNavigation
import com.example.ui.PharmacyViewModel
import com.example.ui.PharmacyViewModelFactory
import com.example.ui.NotificationHelper
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(applicationContext)

        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
        
        // 1. Initialise local Room database & repository
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = PharmacyRepository(database)
        
        // 2. Initialise ViewModel with standard factory
        val viewModelFactory = PharmacyViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[PharmacyViewModel::class.java]

        // 3. Support full screen edge-to-edge layout
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
