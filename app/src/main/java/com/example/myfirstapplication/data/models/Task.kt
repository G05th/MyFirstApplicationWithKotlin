package com.example.myfirstapplication.data.models

import androidx.room.PrimaryKey

data class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val descricao: String
)