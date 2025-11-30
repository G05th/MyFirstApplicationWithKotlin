package com.example.myfirstapplication.domain

class AddToDoUseCase(private val toDoRepository: ToDoRepository) {


    suspend fun execute(
        title: String,
        description: String? = null,
        imageUrl: String? = null
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) {
            throw IllegalArgumentException("title não pode ser vazio")
        }


        val now = System.currentTimeMillis()
        val todo = ToDo(
            title = cleanTitle,
            description = description?.trim(),
            imageUrl = imageUrl,
            isDone = false,
            createdAt = now,
            updatedAt = now
        )


        toDoRepository.addToDo(todo)
    }
}