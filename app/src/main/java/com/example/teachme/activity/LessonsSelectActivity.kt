package com.example.teachme.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teachme.ui.theme.TeachMeTheme
import com.example.teachme.data.AppDatabase
import com.example.teachme.data.Lesson
import com.example.teachme.repositories.LessonRepository
import com.example.teachme.data.SettingsPreferences
import com.example.teachme.models.NotificationUtils
import com.example.teachme.viewmodel.LessonViewModel
import com.example.teachme.viewmodel.LessonViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class LessonSelectionActivity : ComponentActivity() {

    private lateinit var settingsPreferences: SettingsPreferences
    private var isNotificationsEnabled by mutableStateOf(true)

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    private val lessonRepository by lazy { LessonRepository(database.lessonDao()) }
    private val lessonViewModel: LessonViewModel by viewModels {
        LessonViewModelFactory(lessonRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsPreferences = SettingsPreferences(this)
        NotificationUtils.createNotificationChannel(this)
        isNotificationsEnabled = settingsPreferences.notificationsEnabled

        setContent {
            TeachMeTheme {
                val lessons by lessonViewModel.allLessons.observeAsState(emptyList())
                Log.d("LessonSelectionActivity", "Loaded lessons: ${lessons.size}")
                LessonSelectionScreen(
                    lessons = lessons,
                    onLessonSelected = { lessonId ->
                        Log.d("LessonSelectionActivity", "Selected lesson ID: $lessonId")
                        val intent = Intent(this, QuizActivity::class.java)
                        intent.putExtra("LESSON_ID", lessonId)
                        startActivity(intent)
                    },
                    onAddLesson = {
                        val newLessonNumber = lessons.size + 1
                        val newLesson = Lesson(title = "Lekcja $newLessonNumber")
                        lessonViewModel.insert(newLesson)
                        if (isNotificationsEnabled) {
                            NotificationUtils.sendNewLessonNotification(this)
                        }
                    },
                    onDeleteLesson = { lessonId ->
                        lessonViewModel.deleteLessonById(lessonId)
                    }
                )
            }
        }
    }
}

@Composable
fun LessonSelectionScreen(
    lessons: List<Lesson>,
    onLessonSelected: (Int) -> Unit,
    onAddLesson: () -> Unit,
    onDeleteLesson: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        lessons.forEach { lesson ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onLessonSelected(lesson.id) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = lesson.title)
                }
                IconButton(onClick = { onDeleteLesson(lesson.id) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Usuń lekcję")
                }
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
            lessons = listOf(
                Lesson(id = 1, title = "Lekcja 1"),
                Lesson(id = 2, title = "Lekcja 2"),
                Lesson(id = 3, title = "Lekcja 3")
            ),
            onLessonSelected = {},
            onAddLesson = {},
            onDeleteLesson = {}
        )
    }
}
