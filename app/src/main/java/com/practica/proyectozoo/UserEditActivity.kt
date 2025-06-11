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
            if (userId == null) {
                db.insertUsuario(username, email, password, perfilId.toIntOrNull() ?: 1)
            } else {
                db.updateUsuario(userId, username, email, password, perfilId.toIntOrNull() ?: 1)
            }
            onFinish()
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
