package com.example.teachme.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teachme.ui.theme.TeachMeTheme
import com.example.teachme.models.NotificationUtils
import com.example.teachme.data.SettingsPreferences

class LessonSelectionActivity : ComponentActivity() {

    private lateinit var settingsPreferences: SettingsPreferences
    private var completedLessons = mutableStateListOf(false, false, false)
    private var lessons = mutableStateListOf("Lekcja 1", "Lekcja 2", "Lekcja 3")
    private var isNotificationsEnabled by mutableStateOf(true)

    private val quizLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val lessonIndex = result.data?.getIntExtra("LESSON_INDEX", -1) ?: -1
            if (lessonIndex >= 0) {
                completedLessons[lessonIndex] = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsPreferences = SettingsPreferences(this)
        NotificationUtils.createNotificationChannel(this)
        isNotificationsEnabled = settingsPreferences.notificationsEnabled

        setContent {
            TeachMeTheme {
                LessonSelectionScreen(
                    lessons = lessons,
                    completedLessons = completedLessons,
                    onLessonSelected = { lessonIndex ->
                        val intent = Intent(this, QuizActivity::class.java)
                        intent.putExtra("LESSON_INDEX", lessonIndex)
                        quizLauncher.launch(intent)
                    },
                    onAddLesson = {
                        val newLesson = "Lekcja ${lessons.size + 1}"
                        lessons.add(newLesson)
                        completedLessons.add(false)
                        if (isNotificationsEnabled) {
                            NotificationUtils.sendNewLessonNotification(this)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LessonSelectionScreen(
    lessons: List<String>,
    completedLessons: List<Boolean>,
    onLessonSelected: (Int) -> Unit,
    onAddLesson: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        lessons.forEachIndexed { index, lesson ->
            Button(
                onClick = { onLessonSelected(index) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = if (completedLessons[index]) Color.Green else Color.Gray)
            ) {
                Text(text = lesson)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddLesson) {
            Text(text = "Dodaj nową lekcję")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LessonSelectionScreenPreview() {
    TeachMeTheme {
        LessonSelectionScreen(
            lessons = listOf("Lekcja 1", "Lekcja 2", "Lekcja 3"),
            completedLessons = listOf(false, false, false),
            onLessonSelected = {},
            onAddLesson = {}
        )
    }
}
