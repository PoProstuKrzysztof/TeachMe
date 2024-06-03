package com.example.teachme.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.teachme.converters.Converters
import com.example.teachme.interfaces.LessonDao
import com.example.teachme.interfaces.QuestionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Lesson::class, Question::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.lessonDao(), database.questionDao())
                }
            }
        }

        suspend fun populateDatabase(lessonDao: LessonDao, questionDao: QuestionDao) {
            val lesson1 = Lesson(title = "Lekcja 1: Podstawy sieci")
            val lesson2 = Lesson(title = "Lekcja 2: Protokół IP")
            val lesson3 = Lesson(title = "Lekcja 3: HTTP i HTTPS")
            lessonDao.insertLesson(lesson1)
            lessonDao.insertLesson(lesson2)
            lessonDao.insertLesson(lesson3)

            val lessons = lessonDao.getAllLessonsOnce()

            if (lessons.isNotEmpty()) {
                val questionsLesson1 = listOf(
                    Question(
                        lessonId = lessons[0].id,
                        text = "Co to jest adres IP?",
                        correctAnswer = "Unikalny adres urządzenia w sieci",
                        incorrectAnswers = listOf("Protokół komunikacyjny", "Typ połączenia", "Adres e-mail")
                    ),
                    Question(
                        lessonId = lessons[0].id,
                        text = "Co to jest DNS?",
                        correctAnswer = "System nazw domenowych",
                        incorrectAnswers = listOf("Rodzaj połączenia internetowego", "Protokół sieciowy", "Adres IP")
                    )
                )

                val questionsLesson2 = listOf(
                    Question(
                        lessonId = lessons[1].id,
                        text = "Co oznacza skrót HTTP?",
                        correctAnswer = "HyperText Transfer Protocol",
                        incorrectAnswers = listOf("HyperText Transmission Process", "High Transfer Protocol", "Home Transfer Protocol")
                    ),
                    Question(
                        lessonId = lessons[1].id,
                        text = "Co to jest sieć LAN?",
                        correctAnswer = "Lokalna sieć komputerowa",
                        incorrectAnswers = listOf("Sieć rozległa", "Publiczna sieć komputerowa", "Sieć bezprzewodowa")
                    ),
                    Question(
                        lessonId = lessons[1].id,
                        text = "Co oznacza skrót VPN?",
                        correctAnswer = "Virtual Private Network",
                        incorrectAnswers = listOf("Virtual Public Network", "Very Private Network", "Verified Private Network")
                    )
                )

                val questionsLesson3 = listOf(
                    Question(
                        lessonId = lessons[2].id,
                        text = "Co oznacza skrót HTTPS?",
                        correctAnswer = "HyperText Transfer Protocol Secure",
                        incorrectAnswers = listOf("HyperText Transmission Process Secure", "High Transfer Protocol Secure", "Home Transfer Protocol Secure")
                    ),
                    Question(
                        lessonId = lessons[2].id,
                        text = "Który port jest używany przez HTTP?",
                        correctAnswer = "Port 80",
                        incorrectAnswers = listOf("Port 21", "Port 443", "Port 25")
                    )
                )

                questionsLesson1.forEach { questionDao.insertQuestion(it) }
                questionsLesson2.forEach { questionDao.insertQuestion(it) }
                questionsLesson3.forEach { questionDao.insertQuestion(it) }

            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "teachme_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
