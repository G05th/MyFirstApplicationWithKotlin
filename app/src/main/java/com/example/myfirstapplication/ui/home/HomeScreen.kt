package com.example.myfirstapplication.ui.home

import com.example.myfirstapplication.presentation.ui.ToDoRow
import com.example.myfirstapplication.presentation.ui.AddTodoDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfirstapplication.presentation.ToDoViewModel
import com.example.myfirstapplication.ui.todo.EditTodoDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ToDoViewModel) {
    val todos by viewModel.todos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<com.example.myfirstapplication.domain.ToDo?>(null) }


    Scaffold(
        topBar = { TopAppBar(title = { Text("To-Do List") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = null) }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (todos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Nenhuma tarefa. Clique + para adicionar.") }
            } else {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(todos, key = { it.id }) { todo ->
                        ToDoRow(todo = todo,
                            onToggleDone = { t -> viewModel.toggleTodoDone(t) },
                            onDelete = { t -> viewModel.deleteTodo(t) },
                            onEdit = { t ->
                                editingTodo = t
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }


        if (showAddDialog) {
            AddTodoDialog(onAdd = { title, description, imageUrl ->
                viewModel.addTodo(title, description, imageUrl)
                showAddDialog = false
            }, onDismiss = { showAddDialog = false })
        }


        if (showEditDialog && editingTodo != null) {
            EditTodoDialog(
                todo = editingTodo!!,
                onConfirm = { newTitle, newDescription, newImageUrl ->
                    viewModel.editTodo(editingTodo!!, newTitle, newDescription, newImageUrl)
                    showEditDialog = false
                    editingTodo = null
                },
                onDismiss = {
                    showEditDialog = false
                    editingTodo = null
                }
            )
        }
    }
}