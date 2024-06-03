package com.example.teachme.repositories

import com.example.teachme.data.Lesson
import com.example.teachme.interfaces.LessonDao
import kotlinx.coroutines.flow.Flow

class LessonRepository(private val lessonDao: LessonDao) {

    val allLessons: Flow<List<Lesson>> = lessonDao.getAllLessons()

    suspend fun insert(lesson: Lesson) {
        lessonDao.insertLesson(lesson)
    }

    suspend fun delete(lesson: Lesson) {
        lessonDao.deleteLesson(lesson)
    }

    suspend fun deleteLessonById(lessonId: Int) {
        lessonDao.deleteLessonById(lessonId)
    }

    suspend fun markLessonAsCompleted(lessonId: Int) {
        lessonDao.markLessonAsCompleted(lessonId)
    }
}
