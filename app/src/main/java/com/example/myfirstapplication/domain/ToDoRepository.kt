package com.example.myfirstapplication.domain

import kotlinx.coroutines.flow.Flow


interface ToDoRepository {
    fun getToDo(): Flow<List<ToDo>>
    suspend fun addToDo(todo: ToDo)
    suspend fun updatedToDo(todo: ToDo)
    suspend fun deletedToDo(todo: ToDo)
}