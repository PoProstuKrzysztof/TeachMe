package com.example.teachme.viewmodel

import androidx.lifecycle.*
import com.example.teachme.data.Lesson
import com.example.teachme.repositories.LessonRepository
import kotlinx.coroutines.launch

class LessonViewModel(private val repository: LessonRepository) : ViewModel() {

    val allLessons: LiveData<List<Lesson>> = repository.allLessons.asLiveData()

    fun insert(lesson: Lesson) = viewModelScope.launch {
        repository.insert(lesson)
    }

    fun delete(lesson: Lesson) = viewModelScope.launch {
        repository.delete(lesson)
    }

    fun deleteLessonById(lessonId: Int) = viewModelScope.launch {
        repository.deleteLessonById(lessonId)
    }

    fun markLessonAsCompleted(lessonId: Int) = viewModelScope.launch {
        repository.markLessonAsCompleted(lessonId)
    }
}

class LessonViewModelFactory(private val repository: LessonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            return LessonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
