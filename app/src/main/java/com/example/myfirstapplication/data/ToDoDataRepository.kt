package com.example.myfirstapplication.data

import com.example.myfirstapplication.domain.ToDo
import com.example.myfirstapplication.domain.ToDoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ToDoDataRepository(private val dao: ToDoDao) : ToDoRepository {
    override fun getToDo(): Flow<List<ToDo>> {
        return dao.getAllTodos().map { list ->
            list.map { entity ->
                entity.toDomain()
            }
        }
    }


    override suspend fun addToDo(todo: ToDo) {
        dao.insert(todo.toEntity())
    }


    override suspend fun updatedToDo(todo: ToDo) {
        dao.update(todo.toEntity())
    }


    override suspend fun deletedToDo(todo: ToDo) {
        dao.delete(todo.toEntity())
    }
}