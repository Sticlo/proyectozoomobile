package com.practica.proyectozoo

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Usuario
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

@OptIn(ExperimentalMaterial3Api::class)
class UserEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(stringResource(R.string.add_user)) })
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            )
                            .padding(padding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        UserEditForm(
                            db = db,
                            modifier = Modifier.padding(top = 24.dp)
                        ) {
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditForm(
    db: DatabaseHelper,
    modifier: Modifier = Modifier,
    onDone: () -> Unit
) {
    val ctx = LocalContext.current

    // 1) Campos y estado
    var idField     by rememberSaveable { mutableStateOf("") }
    var currentUser by rememberSaveable { mutableStateOf<Usuario?>(null) }
    var username    by rememberSaveable { mutableStateOf("") }
    var email       by rememberSaveable { mutableStateOf("") }
    var password    by rememberSaveable { mutableStateOf("") }

    // 2) Cargar perfiles
    val perfiles = remember { db.getAllPerfiles() }
    var selectedPerfil by rememberSaveable {
        mutableStateOf(perfiles.firstOrNull()?.first ?: 2)
    }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var statusMsg by remember { mutableStateOf<String?>(null) }

    // 3) Textos
    val usrLabel    = stringResource(R.string.username)
    val emailLabel  = stringResource(R.string.email)
    val pwdLabel    = stringResource(R.string.password)
    val perfLabel   = stringResource(R.string.perfil_id)
    val btnSearch   = stringResource(R.string.search)
    val btnSave     = stringResource(R.string.save)
    val btnDelete   = stringResource(R.string.delete)
    val errUser     = stringResource(R.string.invalid_username)
    val errEmail    = stringResource(R.string.invalid_email)
    val errPwd      = stringResource(R.string.invalid_password)
    val errPerf     = stringResource(R.string.invalid_perfil_id)
    val msgNotFound = stringResource(R.string.user_not_found)
    val msgDeleted  = stringResource(R.string.user_deleted)
    val msgSaved    = stringResource(R.string.user_saved)

    Column(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Búsqueda por ID
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = idField,
                onValueChange = { idField = it },
                label = { Text(perfLabel) },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                val id = idField.toIntOrNull()
                if (id == null) {
                    statusMsg = errPerf
                    currentUser = null
                } else {
                    db.getUsuario(id)?.let { u ->
                        currentUser = u
                        username = u.username
                        email    = u.email
                        password = u.password
                        selectedPerfil = u.perfilId
                        statusMsg = null
                    } ?: run {
                        currentUser = null
                        statusMsg = msgNotFound
                    }
                }
            }) {
                Text(btnSearch)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Campos de usuario, correo y contraseña
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(usrLabel) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(emailLabel) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(pwdLabel) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Desplegable de perfiles dinámico
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded }
        ) {
            val selectedLabel = perfiles
                .firstOrNull { it.first == selectedPerfil }
                ?.second
                .orEmpty()

            OutlinedTextField(
                value = selectedLabel,
                onValueChange = { /* no-op */ },
                readOnly = true,                                  // evita el “Select all”
                label = { Text(perfLabel) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()                                // importante para anclar el menú
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                perfiles.forEach { (id, nombre) ->
                    DropdownMenuItem(
                        text = { Text(nombre) },
                        onClick = {
                            selectedPerfil = id
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Guardar / Eliminar
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    statusMsg = when {
                        !username.matches(Regex("^[A-Za-z0-9]+$"))      -> errUser
                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errEmail
                        password.isBlank()                                -> errPwd
                        selectedPerfil !in perfiles.map { it.first }      -> errPerf
                        else -> {
                            if (currentUser == null) {
                                db.insertUsuario(username, email, password, selectedPerfil)
                            } else {
                                db.updateUsuario(currentUser!!.id, username, email, password, selectedPerfil)
                            }
                            // limpiar
                            idField = ""
                            username = ""
                            email = ""
                            password = ""
                            selectedPerfil = perfiles.firstOrNull()?.first ?: 2
                            currentUser = null
                            msgSaved
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(btnSave)
            }
            Button(
                onClick = {
                    currentUser?.let {
                        db.deleteUsuario(it.id)
                        statusMsg = msgDeleted
                        idField = ""
                        username = ""
                        email = ""
                        password = ""
                        selectedPerfil = perfiles.firstOrNull()?.first ?: 2
                        currentUser = null
                    } ?: run {
                        statusMsg = msgNotFound
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = currentUser != null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(btnDelete)
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            idField = ""
            username = ""
            email = ""
            password = ""
            selectedPerfil = perfiles.firstOrNull()?.first ?: 2
            currentUser = null
            statusMsg = null
        }) {
            Text(stringResource(R.string.clear_form))
        }

        statusMsg?.let { msg ->
            Spacer(Modifier.height(12.dp))
            Text(
                text = msg,
                color = if (msg == msgSaved)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserEditPreview() {
    val db = DatabaseHelper(LocalContext.current)
    ProyectozooTheme {
        UserEditForm(db = db) {}
    }
}
