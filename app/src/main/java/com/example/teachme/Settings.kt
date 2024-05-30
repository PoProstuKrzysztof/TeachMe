package com.example.teachme

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.teachme.ui.theme.TeachMeTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeachMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("TeachMePreferences", Context.MODE_PRIVATE)
    var notificationsEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("notifications", true)) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Ustawienia", style = MaterialTheme.typography.headlineSmall)
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = {
                notificationsEnabled = it
                with(sharedPreferences.edit()) {
                    putBoolean("notifications", it)
                    apply()
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(text = if (notificationsEnabled) "Powiadomienia włączone" else "Powiadomienia wyłączone")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    TeachMeTheme {
        SettingsScreen()
    }
}
