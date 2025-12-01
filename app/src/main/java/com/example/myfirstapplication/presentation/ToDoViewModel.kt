package com.example.myfirstapplication.presentation

import android.app.Application
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.myfirstapplication.data.ToDoDataRepository
import com.example.myfirstapplication.data.ToDoDatabase
import com.example.myfirstapplication.domain.AddToDoUseCase
import com.example.myfirstapplication.domain.ToDo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ToDoViewModel(application: Application) : AndroidViewModel(application) {

    // DB local - keep simple for now (consider singleton or DI later)
    private val db: ToDoDatabase = Room.databaseBuilder(
        application.applicationContext,
        ToDoDatabase::class.java,
        "my_todo_db"
    ).build()

    private val todoRepository = ToDoDataRepository(db.toDoDao())
    private val addUseCase = AddToDoUseCase(todoRepository)

    // StateFlow exposto
    val todos: StateFlow<List<ToDo>> = todoRepository
        .getToDo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // ---------- Public API used by UI ----------

    fun addTodo(title: String, description: String? = null, imageUrl: String? = null) {
        viewModelScope.launch {
            // se for uma content:// uri -> copia para storage interno e guarda caminho local
            val storedPath = resolveAndCopyIfNeeded(imageUrl)
            addUseCase.execute(title, description, storedPath)
        }
    }

    fun toggleTodoDone(todo: ToDo) {
        viewModelScope.launch {
            todoRepository.updatedToDo(todo.copy(isDone = !todo.isDone, updatedAt = System.currentTimeMillis()))
        }
    }

    fun editTodo(todo: ToDo, newTitle: String, newDescription: String? = null, newImageUrl: String? = null) {
        viewModelScope.launch {
            // resolve/copy new image if needed
            val newStoredPath = resolveAndCopyIfNeeded(newImageUrl)

            // se substituiu a imagem (e a anterior estava guardada internamente), apaga o ficheiro antigo
            if (!todo.imageUrl.isNullOrBlank() && todo.imageUrl != newStoredPath) {
                deleteInternalFileIfOwned(todo.imageUrl)
            }

            val updated = todo.copy(
                title = newTitle,
                description = newDescription,
                imageUrl = newStoredPath,
                updatedAt = System.currentTimeMillis()
            )
            todoRepository.updatedToDo(updated)
        }
    }

    fun deleteTodo(todo: ToDo) {
        viewModelScope.launch {
            // apaga ficheiro local associado (se existir e estiver na nossa pasta)
            if (!todo.imageUrl.isNullOrBlank()) {
                deleteInternalFileIfOwned(todo.imageUrl)
            }
            todoRepository.deletedToDo(todo)
        }
    }

    private suspend fun resolveAndCopyIfNeeded(uriString: String?): String? {
        if (uriString.isNullOrBlank()) return null

        return withContext(Dispatchers.IO) {
            try {
                val uri = runCatching { Uri.parse(uriString) }.getOrNull() ?: return@withContext null
                val scheme = uri.scheme ?: ""

                when {
                    scheme.equals("content", ignoreCase = true) -> {
                        // copiar stream raw para ficheiro interno
                        copyContentUriToInternalFile(uri)
                    }
                    scheme.equals("file", ignoreCase = true) -> {
                        // já é um ficheiro local, devolve o path
                        uri.path
                    }
                    // por vezes a UI pode enviar um caminho absoluto sem scheme
                    uriString.startsWith("/") -> uriString
                    else -> {
                        // fallback: tenta abrir como content; se falhar, devolve o input original
                        val resolver = getApplication<Application>().contentResolver
                        val inStream = runCatching { resolver.openInputStream(uri) }.getOrNull()
                        if (inStream != null) {
                            inStream.close()
                            copyContentUriToInternalFile(uri)
                        } else {
                            uriString
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun copyContentUriToInternalFile(uri: Uri): String? {
        return try {
            val context = getApplication<Application>()
            val resolver = context.contentResolver

            // cria dir images dentro de filesDir
            val imagesDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }

            // tenta deduzir extension a partir do mime type; fallback para jpg
            val mime = resolver.getType(uri)
            val ext = if (!mime.isNullOrBlank()) {
                MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
            } else {
                // tentativa simples de obter da última parte do caminho
                uri.lastPathSegment?.substringAfterLast('.', "jpg")
            } ?: "jpg"

            val filename = "todo_${System.currentTimeMillis()}.$ext"
            val outFile = File(imagesDir, filename)

            resolver.openInputStream(uri)?.use { input ->
                FileOutputStream(outFile).use { output -> input.copyTo(output) }
            } ?: return null

            outFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun deleteInternalFileIfOwned(pathOrUri: String?) {
        if (pathOrUri.isNullOrBlank()) return

        withContext(Dispatchers.IO) {
            try {
                // normalizar para caminho absoluto
                val path = runCatching {
                    val u = Uri.parse(pathOrUri)
                    when (u.scheme) {
                        "file" -> u.path
                        "content" -> null // content URIs podem permanecer, mas normalmente não vamos apagar providers externos
                        else -> if (pathOrUri.startsWith("/")) pathOrUri else null
                    }
                }.getOrNull()

                path?.let {
                    val filesDir = getApplication<Application>().filesDir
                    val imagesDir = File(filesDir, "images")
                    val target = File(it)

                    // só apaga se estiver dentro da nossa pasta images
                    if (target.exists() && target.parentFile?.canonicalPath?.startsWith(imagesDir.canonicalPath) == true) {
                        target.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
