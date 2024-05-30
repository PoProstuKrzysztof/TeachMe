package com.example.teachme.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teachme.ui.theme.TeachMeTheme

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeachMeTheme {
                QuizScreen(
                    onFinish = {
                        startActivity(Intent(this, LessonSelectionActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun QuizScreen(onFinish: () -> Unit) {
    val questions = listOf(
        "Pytanie 1: Jaka jest stolica Polski?",
        "Pytanie 2: Jaka jest stolica Niemiec?",
        "Pytanie 3: Jaka jest stolica Francji?",
        "Pytanie 4: Jaka jest stolica Włoch?",
        "Pytanie 5: Jaka jest stolica Hiszpanii?"
    )

    val correctAnswers = listOf("Warszawa", "Berlin", "Paryż", "Rzym", "Madryt")

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var correctCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }
    var buttonColor by remember { mutableStateOf(Color.Gray) }
    val handler = Handler(Looper.getMainLooper())

    if (currentQuestionIndex < questions.size) {
        QuizQuestion(
            question = questions[currentQuestionIndex],
            correctAnswer = correctAnswers[currentQuestionIndex],
            onAnswer = { answer ->
                if (answer == correctAnswers[currentQuestionIndex]) {
                    buttonColor = Color.Green
                    correctCount++
                } else {
                    buttonColor = Color.Red
                    wrongCount++
                }
                handler.postDelayed({
                    currentQuestionIndex++
                    buttonColor = Color.Gray
                }, 1000)
            },
            buttonColor = buttonColor
        )
    } else {
        QuizSummary(
            correctCount = correctCount,
            wrongCount = wrongCount,
            onBackToLessons = onFinish
        )
    }
}

@Composable
fun QuizQuestion(question: String, correctAnswer: String, onAnswer: (String) -> Unit, buttonColor: Color) {
    val options = listOf(correctAnswer, "Błędna odpowiedź 1", "Błędna odpowiedź 2", "Błędna odpowiedź 3").shuffled()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        options.forEach { option ->
            Button(
                onClick = { onAnswer(option) },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = option)
            }
        }
    }
}

@Composable
fun QuizSummary(correctCount: Int, wrongCount: Int, onBackToLessons: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Podsumowanie:",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Poprawne odpowiedzi: $correctCount",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Błędne odpowiedzi: $wrongCount",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onBackToLessons) {
            Text(text = "Powrót do lekcji")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    TeachMeTheme {
        QuizScreen(onFinish = {})
    }
}
