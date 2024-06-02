package com.example.teachme.viewmodel

import androidx.lifecycle.*
import com.example.teachme.data.Question
import com.example.teachme.repositories.QuestionRepository
import kotlinx.coroutines.launch

class QuestionViewModel(private val repository: QuestionRepository) : ViewModel() {

    fun getQuestionsForLesson(lessonId: Int): LiveData<List<Question>> {
        return repository.getQuestionsForLesson(lessonId).asLiveData()
    }

    fun insert(question: Question) = viewModelScope.launch {
        repository.insert(question)
    }

    fun delete(question: Question) = viewModelScope.launch {
        repository.delete(question)
    }
}

class QuestionViewModelFactory(private val repository: QuestionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuestionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
