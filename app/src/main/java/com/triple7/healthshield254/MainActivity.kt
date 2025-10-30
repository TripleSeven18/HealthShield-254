package com.triple7.healthshield254

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.triple7.healthshield254.navigation.AppNavHost
import com.triple7.healthshield254.ui.theme.HealthShield254Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) }
            HealthShield254Theme(darkTheme = isDarkMode) {
                AppNavHost(
                    isDarkMode = isDarkMode,
                    onDarkModeChange = { newMode ->
                        isDarkMode = newMode
                        val mode = if (newMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                        AppCompatDelegate.setDefaultNightMode(mode)
                    }
                )
            }
        }
    }
}