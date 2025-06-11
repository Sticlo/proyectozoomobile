package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(db, Modifier.padding(innerPadding)) {
                        startActivity(Intent(this, UserListActivity::class.java))
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(db: DatabaseHelper, modifier: Modifier = Modifier, onSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(modifier.padding(16.dp)) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            error = !db.validateUser(username, password)
            if (!error) onSuccess()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.login))
        }
        TextButton(onClick = {
            val context = LocalContext.current
            context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
        }) {
            Text(stringResource(R.string.forgot_password))
        }
        if (error) {
            Text(stringResource(R.string.login_error), color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        LoginScreen(db) {}
    }
}
