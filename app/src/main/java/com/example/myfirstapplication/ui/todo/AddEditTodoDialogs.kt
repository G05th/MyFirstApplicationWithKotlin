package com.example.myfirstapplication.ui.todo

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage

@Composable
fun EditTodoDialog(
    todo: com.example.myfirstapplication.domain.ToDo,
    onConfirm: (newTitle: String, newDescription: String?, newImageUrl: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var description by remember { mutableStateOf(todo.description ?: "") }
    var imageUrl by remember { mutableStateOf(todo.imageUrl ?: "") }
    var selectedUri by remember { mutableStateOf<Uri?>(todo.imageUrl?.let { runCatching { Uri.parse(it) }.getOrNull() }) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedUri = uri
        imageUrl = uri?.toString() ?: ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val t = title.trim()
                if (t.isNotEmpty()) {
                    onConfirm(t, description.takeIf { it.isNotBlank() }, imageUrl.takeIf { it.isNotBlank() })
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Editar tarefa") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") })
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Escolher imagem")
                    }

                    // ---------- aqui mostramos a imagem com Coil ----------
                    selectedUri?.let { uri ->
                        Spacer(modifier = Modifier.width(12.dp))
                        AsyncImage(
                            model = uri,
                            contentDescription = "Preview",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    )
}
