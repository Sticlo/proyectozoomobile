package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import android.widget.Toast
import android.util.Patterns
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Usuario
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class UserEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        val userId = intent.getIntExtra("userId", -1).takeIf { it != -1 }
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    UserEditScreen(db, userId, Modifier.padding(padding)) { finish() }
                }
            }
        }
    }
}

@Composable
fun UserEditScreen(db: DatabaseHelper, userId: Int?, modifier: Modifier = Modifier, onFinish: () -> Unit = {}) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var perfilId by remember { mutableStateOf("1") }
    val context = LocalContext.current

    LaunchedEffect(userId) {
        userId?.let {
            db.getUsuario(it)?.let { u ->
                username = u.username
                email = u.email
                password = u.password
                perfilId = u.perfilId.toString()
            }
        }
    }

    Column(modifier.padding(16.dp)) {
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text(stringResource(R.string.username)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.email)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(stringResource(R.string.password)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = perfilId, onValueChange = { perfilId = it }, label = { Text(stringResource(R.string.perfil_id)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            when {
                !username.matches(Regex("^[A-Za-z0-9]+$")) -> Toast.makeText(
                    context,
                    context.getString(R.string.invalid_username),
                    Toast.LENGTH_SHORT
                ).show()
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Toast.makeText(
                    context,
                    context.getString(R.string.invalid_email),
                    Toast.LENGTH_SHORT
                ).show()
                password.isBlank() -> Toast.makeText(
                    context,
                    context.getString(R.string.invalid_password),
                    Toast.LENGTH_SHORT
                ).show()
                perfilId.toIntOrNull() == null -> Toast.makeText(
                    context,
                    context.getString(R.string.invalid_perfil_id),
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    if (userId == null) {
                        db.insertUsuario(username, email, password, perfilId.toInt())
                        Toast.makeText(context, context.getString(R.string.user_saved), Toast.LENGTH_SHORT).show()
                    } else {
                        db.updateUsuario(userId, username, email, password, perfilId.toInt())
                        Toast.makeText(context, context.getString(R.string.user_updated), Toast.LENGTH_SHORT).show()
                    }
                    onFinish()
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.save))
        }
    }
}

@Preview
@Composable
fun UserEditPreview() {
    val context = LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        UserEditScreen(db, null)
    }
}
