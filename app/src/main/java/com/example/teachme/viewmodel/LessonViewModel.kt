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
}

class LessonViewModelFactory(private val repository: LessonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LessonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
