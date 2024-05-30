package com.example.teachme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.teachme.ui.theme.TeachMeTheme

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeachMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizScreen()
                }
            }
        }
    }
}

@Composable
fun QuizScreen() {
    val questions = listOf(
        Question("Jakie jest standardowe pasmo przenoszenia sieci Ethernet?", listOf("10 Mbps", "100 Mbps", "1 Gbps", "10 Gbps"), "1 Gbps"),
        Question("Co oznacza skrót HTTP?", listOf("Hyper Text Transfer Protocol", "Hyper Text Transmission Protocol", "High Text Transfer Protocol", "Hyperlink Text Transfer Protocol"), "Hyper Text Transfer Protocol")
    )
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf<String?>(null) }
    val currentQuestion = questions[currentQuestionIndex]

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = currentQuestion.question, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp))
        currentQuestion.options.forEach { option ->
            Button(
                onClick = { userAnswer = option },
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(text = option)
            }
        }
        userAnswer?.let {
            Text(
                text = if (it == currentQuestion.correctAnswer) "Poprawna odpowiedź!" else "Zła odpowiedź, spróbuj ponownie.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                        userAnswer = null
                    } else {

                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = if (currentQuestionIndex < questions.size - 1) "Następne pytanie" else "Zakończ quiz")
            }
        }
    }
}

data class Question(val question: String, val options: List<String>, val correctAnswer: String)

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    TeachMeTheme {
        QuizScreen()
    }
}
