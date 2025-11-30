package com.example.myfirstapplication.data

import com.example.myfirstapplication.domain.ToDo as DomainToDo

fun ToDoEntity.toDomain(): DomainToDo = DomainToDo(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    isDone = isDone,
    createdAt = createdAt,
    updatedAt = updatedAt
)


// Mapeia Domain -> Entity
fun DomainToDo.toEntity(): ToDoEntity = ToDoEntity(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    isDone = isDone,
    createdAt = createdAt,
    updatedAt = updatedAt
)