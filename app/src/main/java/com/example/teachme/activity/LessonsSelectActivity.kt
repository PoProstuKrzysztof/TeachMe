package com.example.teachme.activity

import android.content.Intent
import android.os.Bundle
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
import com.example.teachme.data.Question
import com.example.teachme.data.SettingsPreferences
import com.example.teachme.repositories.LessonRepository
import com.example.teachme.models.NotificationUtils
import com.example.teachme.viewmodel.LessonViewModel
import com.example.teachme.viewmodel.LessonViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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

        applicationScope.launch(Dispatchers.IO) {
            populateDatabaseIfNeeded()
        }

        setContent {
            TeachMeTheme {
                val lessons by lessonViewModel.allLessons.observeAsState(emptyList())
                LessonSelectionScreen(
                    lessons = lessons,
                    onLessonSelected = { lessonId ->
                        val intent = Intent(this, QuizActivity::class.java)
                        intent.putExtra("LESSON_ID", lessonId)
                    },
                    onAddLesson = {
                        val newLessonNumber = lessons.size + 1
                        val newLesson = Lesson(title = "Lesson $newLessonNumber")
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

    private suspend fun populateDatabaseIfNeeded() {
        val lessonDao = database.lessonDao()
        val questionDao = database.questionDao()

        if (lessonDao.getAllLessonsOnce().isEmpty()) {
            val lesson1 = Lesson(title = "Lesson 1: Networking Basics")
            val lesson2 = Lesson(title = "Lesson 2: IP Protocol")
            val lesson3 = Lesson(title = "Lesson 3: HTTP and HTTPS")
            lessonDao.insertLesson(lesson1)
            lessonDao.insertLesson(lesson2)
            lessonDao.insertLesson(lesson3)

            val lessons = lessonDao.getAllLessonsOnce()

            val questionsLesson1 = listOf(
                Question(
                    lessonId = lessons[0].id,
                    text = "What is an IP address?",
                    correctAnswer = "Unique address of a device in a network",
                    incorrectAnswers = listOf("Communication protocol", "Connection type", "Email address")
                ),
                Question(
                    lessonId = lessons[0].id,
                    text = "What is DNS?",
                    correctAnswer = "Domain Name System",
                    incorrectAnswers = listOf("Type of internet connection", "Network protocol", "IP address")
                )
            )

            val questionsLesson2 = listOf(
                Question(
                    lessonId = lessons[1].id,
                    text = "What does HTTP stand for?",
                    correctAnswer = "HyperText Transfer Protocol",
                    incorrectAnswers = listOf("HyperText Transmission Process", "High Transfer Protocol", "Home Transfer Protocol")
                ),
                Question(
                    lessonId = lessons[1].id,
                    text = "What is a LAN?",
                    correctAnswer = "Local Area Network",
                    incorrectAnswers = listOf("Wide Area Network", "Public Network", "Wireless Network")
                ),
                Question(
                    lessonId = lessons[1].id,
                    text = "What does VPN stand for?",
                    correctAnswer = "Virtual Private Network",
                    incorrectAnswers = listOf("Virtual Public Network", "Very Private Network", "Verified Private Network")
                )
            )

            val questionsLesson3 = listOf(
                Question(
                    lessonId = lessons[2].id,
                    text = "What does HTTPS stand for?",
                    correctAnswer = "HyperText Transfer Protocol Secure",
                    incorrectAnswers = listOf("HyperText Transmission Process Secure", "High Transfer Protocol Secure", "Home Transfer Protocol Secure")
                ),
                Question(
                    lessonId = lessons[2].id,
                    text = "Which port is used by HTTP?",
                    correctAnswer = "Port 80",
                    incorrectAnswers = listOf("Port 21", "Port 443", "Port 25")
                )
            )

            questionsLesson1.forEach { questionDao.insertQuestion(it) }
            questionsLesson2.forEach { questionDao.insertQuestion(it) }
            questionsLesson3.forEach { questionDao.insertQuestion(it) }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val lessonId = data?.getIntExtra("LESSON_ID", -1)
            if (lessonId != null && lessonId != -1) {
                lessonViewModel.markLessonAsCompleted(lessonId)
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
                val buttonColor = if (lesson.completed) Color.Green else Color.Gray
                Button(
                    onClick = { onLessonSelected(lesson.id) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(text = lesson.title)
                }
                IconButton(onClick = { onDeleteLesson(lesson.id) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete lesson")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddLesson) {
            Text(text = "Add new lesson")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LessonSelectionScreenPreview() {
    TeachMeTheme {
        LessonSelectionScreen(
            lessons = listOf(
                Lesson(id = 1, title = "Lesson 1"),
                Lesson(id = 2, title = "Lesson 2"),
                Lesson(id = 3, title = "Lesson 3")
            ),
            onLessonSelected = {},
            onAddLesson = {},
            onDeleteLesson = {}
        )
    }
}
