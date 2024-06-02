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

@Database(entities = [Lesson::class, Question::class], version = 1, exportSchema = false)
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
            lessonDao.deleteAll()
            questionDao.deleteAll()

            // Dodaj lekcje
            val lesson1 = Lesson(id = 1, title = "Lekcja 1: Podstawy sieci")
            val lesson2 = Lesson(id = 2, title = "Lekcja 2: Protokół IP")
            lessonDao.insertLesson(lesson1)
            lessonDao.insertLesson(lesson2)

            // Dodaj pytania do lekcji 1
            val questionsLesson1 = listOf(
                Question(
                    id = 1,
                    lessonId = 1,
                    text = "Co to jest adres IP?",
                    correctAnswer = "Unikalny adres urządzenia w sieci",
                    incorrectAnswers = listOf("Protokół komunikacyjny", "Typ połączenia", "Adres e-mail")
                ),
                Question(
                    id = 2,
                    lessonId = 1,
                    text = "Co to jest DNS?",
                    correctAnswer = "System nazw domenowych",
                    incorrectAnswers = listOf("Rodzaj połączenia internetowego", "Protokół sieciowy", "Adres IP")
                )
            )

            // Dodaj pytania do lekcji 2
            val questionsLesson2 = listOf(
                Question(
                    id = 3,
                    lessonId = 2,
                    text = "Co oznacza skrót HTTP?",
                    correctAnswer = "HyperText Transfer Protocol",
                    incorrectAnswers = listOf("HyperText Transmission Process", "High Transfer Protocol", "Home Transfer Protocol")
                ),
                Question(
                    id = 4,
                    lessonId = 2,
                    text = "Co to jest sieć LAN?",
                    correctAnswer = "Lokalna sieć komputerowa",
                    incorrectAnswers = listOf("Sieć rozległa", "Publiczna sieć komputerowa", "Sieć bezprzewodowa")
                ),
                Question(
                    id = 5,
                    lessonId = 2,
                    text = "Co oznacza skrót VPN?",
                    correctAnswer = "Virtual Private Network",
                    incorrectAnswers = listOf("Virtual Public Network", "Very Private Network", "Verified Private Network")
                )
            )

            questionsLesson1.forEach { questionDao.insertQuestion(it) }
            questionsLesson2.forEach { questionDao.insertQuestion(it) }
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
