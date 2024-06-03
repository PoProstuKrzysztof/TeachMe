package com.example.teachme.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.teachme.data.Lesson
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons")
    fun getAllLessons(): Flow<List<Lesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)

    @Delete
    suspend fun deleteLesson(lesson: Lesson)

    @Query("DELETE FROM lessons WHERE id = :lessonId")
    suspend fun deleteLessonById(lessonId: Int)

    @Query("DELETE FROM lessons")
    suspend fun deleteAll()

    @Query("SELECT * FROM lessons")
    suspend fun getAllLessonsOnce(): List<Lesson>
}
