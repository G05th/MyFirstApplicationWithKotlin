package com.example.myfirstapplication.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.myfirstapplication.data.ToDoDataRepository
import com.example.myfirstapplication.data.ToDoDatabase
import com.example.myfirstapplication.domain.AddToDoUseCase
import com.example.myfirstapplication.domain.ToDo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class ToDoViewModel(application: Application) : AndroidViewModel(application) {

    private val db: ToDoDatabase = Room.databaseBuilder(
        application.applicationContext,
        ToDoDatabase::class.java,
        "my_todo_db"
    ).build()

    private val todoRepository = ToDoDataRepository(db.toDoDao())
    private val addToDoUseCase = AddToDoUseCase(todoRepository)

    open val todos: StateFlow<List<ToDo>> = todoRepository
        .getToDo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun addTodo(title: String, description: String? = null, imageUrl: String? = null) {
        viewModelScope.launch {
            try {
                addToDoUseCase.execute(title, description, imageUrl)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }

    fun updateTodo(todo: ToDo) {
        viewModelScope.launch {
            try {
                todoRepository.updatedToDo(todo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTodo(todo: ToDo) {
        viewModelScope.launch {
            try {
                todoRepository.deletedToDo(todo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}