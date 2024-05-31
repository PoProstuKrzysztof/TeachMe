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

class LessonSelectionActivity : ComponentActivity() {
    private var completedLessons = mutableStateListOf(false, false, false)

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
        setContent {
            TeachMeTheme {
                LessonSelectionScreen(
                    completedLessons = completedLessons,
                    onLessonSelected = { lessonIndex ->
                        val intent = Intent(this, QuizActivity::class.java)
                        intent.putExtra("LESSON_INDEX", lessonIndex)
                        quizLauncher.launch(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun LessonSelectionScreen(completedLessons: List<Boolean>, onLessonSelected: (Int) -> Unit) {
    val lessons = listOf("Lekcja 1", "Lekcja 2", "Lekcja 3")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LessonSelectionScreenPreview() {
    TeachMeTheme {
        LessonSelectionScreen(completedLessons = listOf(false, false, false), onLessonSelected = {})
    }
}
