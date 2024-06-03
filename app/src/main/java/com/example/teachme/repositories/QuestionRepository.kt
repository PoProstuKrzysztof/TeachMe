package com.example.teachme.repositories

import com.example.teachme.data.Question
import com.example.teachme.interfaces.QuestionDao
import kotlinx.coroutines.flow.Flow

class QuestionRepository(private val questionDao: QuestionDao) {

    fun getQuestionsForLesson(lessonId: Int): Flow<List<Question>> {
        return questionDao.getQuestionsForLesson(lessonId)
    }

    suspend fun insert(question: Question) {
        questionDao.insertQuestion(question)
    }

    suspend fun delete(question: Question) {
        questionDao.deleteQuestion(question)
    }
}
