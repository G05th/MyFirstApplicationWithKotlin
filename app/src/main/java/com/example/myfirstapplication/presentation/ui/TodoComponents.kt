package com.example.myfirstapplication.presentation.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myfirstapplication.domain.ToDo

    @Composable
    fun AddTodoDialog(
        onAdd: (title: String, description: String?, imageUri: String?) -> Unit,
        onDismiss: () -> Unit
    ) {
        val context = LocalContext.current

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var selectedUri by remember { mutableStateOf<Uri?>(null) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let {

                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {  }
                selectedUri = it
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val t = title.trim()
                    if (t.isNotEmpty()) {
                        onAdd(t, description.takeIf { it.isNotBlank() }, selectedUri?.toString())
                    }
                }) { Text("Adicionar") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            },
            title = { Text("Nova tarefa") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // preview
                        if (selectedUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(selectedUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Preview imagem",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        TextButton(onClick = {
                            // abre picker filtrando por imagens (image/*)
                            launcher.launch(arrayOf("image/*"))
                        }) {
                            Icon(Icons.Default.Photo, contentDescription = "Selecionar imagem")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (selectedUri == null) "Adicionar imagem" else "Alterar imagem")
                        }

                        // botão para remover imagem selecionada
                        if (selectedUri != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { selectedUri = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Remover imagem")
                            }
                        }
                    }
                }
            }
        )
    }

    // ToDoRow que exibe a imagem local (Uri) se houver; usa Coil AsyncImage que aceita Uri
    @Composable
    fun ToDoRow(
        todo: ToDo,
        onToggleDone: (ToDo) -> Unit,
        onDelete: (ToDo) -> Unit,
        onEdit: (ToDo) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation =  CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagem local (Uri stored as String)
                val imageUriString = todo.imageUrl
                val context = LocalContext.current

                if (!imageUriString.isNullOrBlank()) {
                    val uri = try { Uri.parse(imageUriString) } catch (_: Exception) { null }
                    if (uri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Imagem do todo",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // fallback visual
                        Box(modifier = Modifier.size(56.dp).background(Color.LightGray))
                    }
                } else {
                    // placeholder pequeno
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("IMG", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = todo.title, fontWeight = FontWeight.Bold)
                    todo.description?.let {
                        if (it.isNotBlank()) Text(text = it, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Ações (toggle / delete)
                IconButton(onClick = { onToggleDone(todo) }) {
                    Icon(
                        imageVector = if (todo.isDone) Icons.Default.RadioButtonUnchecked else Icons.Default.RadioButtonChecked,
                        contentDescription = "Toggle"
                    )
                }
                IconButton(onClick = { onEdit(todo) }) {
                    Icon(Icons.Default.Photo, contentDescription = "Editar")
                }
                IconButton(onClick = { onDelete(todo) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir")
                }
            }
        }
    }
