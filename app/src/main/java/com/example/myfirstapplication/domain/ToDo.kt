package com.example.myfirstapplication.domain

data class ToDo(
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val isDone: Boolean = false,


    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
