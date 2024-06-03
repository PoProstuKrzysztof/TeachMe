package com.example.teachme.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lessonId: Int,
    val text: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)
