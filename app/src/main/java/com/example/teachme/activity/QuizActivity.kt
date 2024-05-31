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
                    lessonIndex = intent.getIntExtra("LESSON_INDEX", 0),
                    onFinish = {
                        val resultIntent = Intent()
                        resultIntent.putExtra("LESSON_INDEX", it)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun QuizScreen(lessonIndex: Int, onFinish: (Int) -> Unit) {
    val questions = listOf(
        "Pytanie 1: Jaka jest stolica Polski?",
        "Pytanie 2: Jaka jest stolica Niemiec?",
        "Pytanie 3: Jaka jest stolica Francji?",
        "Pytanie 4: Jaka jest stolica Włoch?",
        "Pytanie 5: Jaka jest stolica Hiszpanii?"
    )

    val correctAnswers = listOf("Warszawa", "Berlin", "Paryż", "Rzym", "Madryt")

    val initialOptions = questions.indices.map { index ->
        listOf(correctAnswers[index], "Błędna odpowiedź 1", "Błędna odpowiedź 2", "Błędna odpowiedź 3").shuffled()
    }

    var options by remember { mutableStateOf(initialOptions) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var correctCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf(-1) }
    var buttonColors by remember { mutableStateOf(List(4) { Color.Gray }) }
    val handler = Handler(Looper.getMainLooper())

    if (currentQuestionIndex < questions.size) {
        QuizQuestion(
            question = questions[currentQuestionIndex],
            options = options[currentQuestionIndex],
            onAnswer = { answer, index ->
                selectedOptionIndex = index
                val newColors = buttonColors.toMutableList()
                if (answer == correctAnswers[currentQuestionIndex]) {
                    newColors[index] = Color.Green
                    correctCount++
                } else {
                    newColors[index] = Color.Red
                    wrongCount++
                }
                buttonColors = newColors
                handler.postDelayed({
                    currentQuestionIndex++
                    selectedOptionIndex = -1
                    buttonColors = List(4) { Color.Gray }
                }, 1000)
            },
            buttonColors = buttonColors
        )
    } else {
        if (correctCount == questions.size) {
            onFinish(lessonIndex)
        } else {
            QuizSummary(
                correctCount = correctCount,
                wrongCount = wrongCount,
                onBackToLessons = { onFinish(-1) }
            )
        }
    }
}

@Composable
fun QuizQuestion(question: String, options: List<String>, onAnswer: (String, Int) -> Unit, buttonColors: List<Color>) {
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

        options.forEachIndexed { index, option ->
            Button(
                onClick = { onAnswer(option, index) },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColors[index]),
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
        QuizScreen(lessonIndex = 0, onFinish = {})
    }
}
