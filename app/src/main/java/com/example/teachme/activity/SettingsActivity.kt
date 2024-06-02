package com.example.teachme.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.teachme.data.SettingsPreferences
import com.example.teachme.ui.theme.TeachMeTheme

class SettingsActivity : ComponentActivity() {
    private lateinit var settingsPreferences: SettingsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsPreferences = SettingsPreferences(this)
        setContent {
            TeachMeTheme {
                SettingsScreen(settingsPreferences) {
                    startActivity(Intent(this, MainScreenActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(settingsPreferences: SettingsPreferences, onSettingsFinished: () -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(settingsPreferences.notificationsEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ustawienia",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Powiadomienia")
            Switch(checked = notificationsEnabled, onCheckedChange = {
                notificationsEnabled = it
                settingsPreferences.notificationsEnabled = it
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onSettingsFinished) {
            Text(text = "Zapisz ustawienia")
        }
    }
}
