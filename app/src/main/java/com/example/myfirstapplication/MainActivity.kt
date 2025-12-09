package com.example.myfirstapplication


import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.myfirstapplication.presentation.ui.ToDoRow
import com.example.myfirstapplication.presentation.ui.AddTodoDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapplication.presentation.ToDoViewModel
import com.example.myfirstapplication.ui.theme.MyLoginTheme
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.MediaStore
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.util.Log
import com.example.myfirstapplication.navigation.AppNavHost

data class ItemData(
    val id: Int,
    val nome: String,
    val descricao: String,
    val ImgResId: Int
)

private const val TAG = "MaibActivity"
class MainActivity : ComponentActivity() {
    private val todoViewModel: ToDoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyLoginTheme {
                AppNavHost(viewModel = todoViewModel)
            }
        }
    }
}

@Composable
fun NavigationApp(todoViewModel: ToDoViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPage(navController) }
        composable("home") { HomePage(navController = navController, viewModel = todoViewModel) }
    }
}

@Composable
fun LoginPage(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Entrar",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Digite o seu email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        TextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Digite a sua senha") },
            singleLine = true,
            visualTransformation = if (senhaVisivel) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(imageVector = if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha")
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Entrar", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { /* esqueci senha */ }) {
            Text("Esqueci a minha senha")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController, viewModel: ToDoViewModel) {
    val todos by viewModel.todos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<com.example.myfirstapplication.domain.ToDo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("To-Do List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (todos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma tarefa. Clique + para adicionar.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(todos, key = { it.id }) { todo ->
                        ToDoRow(
                            todo = todo,
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
            AddTodoDialog(
                onAdd = { title, description, imageUrl ->
                    viewModel.addTodo(title, description, imageUrl)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false }
            )
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

@Composable
private fun rememberImageBitmapFromUriMain(uri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val src = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(src)
                }
                bitmap.asImageBitmap()
            } catch (e: Exception) { null }
        }
    }.value
}


@Composable
fun EditTodoDialog(
    todo: com.example.myfirstapplication.domain.ToDo,
    onConfirm: (newTitle: String, newDescription: String?, newImageUrl: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var description by remember { mutableStateOf(todo.description ?: "") }
    var imageUrl by remember { mutableStateOf(todo.imageUrl ?: "") }

    // Uri state inicial a partir do imageUrl existente (se houver)
    var selectedUri by remember { mutableStateOf<Uri?>(todo.imageUrl?.let { runCatching { Uri.parse(it) }.getOrNull() }) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedUri = uri
        imageUrl = uri?.toString() ?: ""
    }

    val previewBitmap = rememberImageBitmapFromUriMain(selectedUri)

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
                // botão de escolher imagem
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Button(onClick = { launcher.launch("image/*") }) {
                        androidx.compose.material3.Text("Escolher imagem")
                    }
                    if (previewBitmap != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Image(
                            bitmap = previewBitmap,
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
