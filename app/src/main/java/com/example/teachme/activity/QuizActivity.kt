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
                    },
                    onBackToLessons = { finish() }
                )
            }
        }
    }
}

@Composable
fun QuizScreen(lessonIndex: Int, onFinish: (Int) -> Unit, onBackToLessons: () -> Unit) {
    val questions = listOf(
        "Pytanie 1: Co to jest adres IP?",
        "Pytanie 2: Co to jest DNS?",
        "Pytanie 3: Co oznacza skrót HTTP?",
        "Pytanie 4: Co to jest sieć LAN?",
        "Pytanie 5: Co oznacza skrót VPN?"
    )

    val correctAnswers = listOf(
        "Unikalny adres urządzenia w sieci",
        "System nazw domenowych",
        "HyperText Transfer Protocol",
        "Lokalna sieć komputerowa",
        "Virtual Private Network"
    )

    val incorrectAnswers = listOf(
        listOf("Protokół komunikacyjny", "Typ połączenia", "Adres e-mail"),
        listOf("Rodzaj połączenia internetowego", "Protokół sieciowy", "Adres IP"),
        listOf("HyperText Transmission Process", "High Transfer Protocol", "Home Transfer Protocol"),
        listOf("Sieć rozległa", "Publiczna sieć komputerowa", "Sieć bezprzewodowa"),
        listOf("Virtual Public Network", "Very Private Network", "Verified Private Network")
    )


    val initialOptions = questions.indices.map { index ->
        listOf(correctAnswers[index]) + incorrectAnswers[index].shuffled()
    }.map { it.shuffled() }

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
            buttonColors = buttonColors,
            onBack = {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--
                    selectedOptionIndex = -1
                    buttonColors = List(4) { Color.Gray }
                } else {
                    onBackToLessons()
                }
            }
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
fun QuizQuestion(question: String, options: List<String>, onAnswer: (String, Int) -> Unit, buttonColors: List<Color>, onBack: () -> Unit) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Powrót")
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
        QuizScreen(lessonIndex = 0, onFinish = {}, onBackToLessons = {})
    }
}
