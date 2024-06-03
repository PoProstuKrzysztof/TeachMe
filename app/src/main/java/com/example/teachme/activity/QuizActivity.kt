package com.example.teachme.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teachme.ui.theme.TeachMeTheme
import com.example.teachme.data.AppDatabase
import com.example.teachme.data.Question
import com.example.teachme.repositories.QuestionRepository
import com.example.teachme.viewmodel.QuestionViewModel
import com.example.teachme.viewmodel.QuestionViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QuizActivity : ComponentActivity() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    private val questionRepository by lazy { QuestionRepository(database.questionDao()) }
    private val questionViewModel: QuestionViewModel by viewModels {
        QuestionViewModelFactory(questionRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lessonId = intent.getIntExtra("LESSON_ID", 0)
        setContent {
            TeachMeTheme {
                QuizScreen(
                    lessonId = lessonId,
                    questionViewModel = questionViewModel,
                    onFinish = {
                        val resultIntent = Intent()
                        resultIntent.putExtra("LESSON_ID", it)
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
fun QuizScreen(
    lessonId: Int,
    questionViewModel: QuestionViewModel,
    onFinish: (Int) -> Unit,
    onBackToLessons: () -> Unit
) {
    val questions by questionViewModel.getQuestionsForLesson(lessonId).observeAsState(emptyList())
    var currentQuestionIndex by rememberSaveable { mutableStateOf(0) }
    var correctCount by rememberSaveable { mutableStateOf(0) }
    var wrongCount by rememberSaveable { mutableStateOf(0) }
    var selectedOptionIndex by rememberSaveable { mutableStateOf(-1) }
    var buttonColors by rememberSaveable { mutableStateOf(List(4) { Color.Gray }) }
    val answeredQuestions = rememberSaveable { mutableMapOf<Int, Boolean>() }
    val handler = Handler(Looper.getMainLooper())

    if (questions.isEmpty()) {
        Text("Ładowanie pytań...")
    } else if (currentQuestionIndex < questions.size) {
        QuizQuestion(
            question = questions[currentQuestionIndex],
            onAnswer = { answer, index ->
                selectedOptionIndex = index
                val isCorrect = answer == questions[currentQuestionIndex].correctAnswer
                val previousAnswer = answeredQuestions[currentQuestionIndex]

                if (previousAnswer == null) {
                    if (isCorrect) correctCount++ else wrongCount++
                } else if (previousAnswer != isCorrect) {
                    if (isCorrect) {
                        correctCount++
                        wrongCount--
                    } else {
                        correctCount--
                        wrongCount++
                    }
                }

                answeredQuestions[currentQuestionIndex] = isCorrect

                val newColors = buttonColors.toMutableList()
                newColors[index] = if (isCorrect) Color.Green else Color.Red
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
        QuizSummary(
            correctCount = correctCount,
            wrongCount = wrongCount,
            onBackToLessons = { onFinish(-1) }
        )
    }
}


@Composable
fun QuizQuestion(
    question: Question,
    onAnswer: (String, Int) -> Unit,
    buttonColors: List<Color>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val options = listOf(question.correctAnswer) + question.incorrectAnswers
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
        QuizScreen(lessonId = 0, questionViewModel = QuestionViewModel(QuestionRepository(AppDatabase.getDatabase(LocalContext.current, CoroutineScope(SupervisorJob())).questionDao())), onFinish = {}, onBackToLessons = {})
    }
}
