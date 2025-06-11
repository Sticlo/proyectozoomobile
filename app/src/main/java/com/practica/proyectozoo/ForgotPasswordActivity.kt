package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.widget.Toast
import android.util.Patterns
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    ForgotPasswordScreen(db, Modifier.padding(padding)) {
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    db: DatabaseHelper,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var notFound by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier.padding(16.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, context.getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
            } else {
                val pass = db.getPasswordByEmail(email)
                if (pass != null) {
                    result = pass
                } else {
                    notFound = true
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.send_reminder))
        }
    }

    result?.let { password ->
        AlertDialog(
            onDismissRequest = onClose,
            confirmButton = {
                Button(onClick = onClose) {
                    Text(stringResource(R.string.accept))
                }
            },
            title = { Text(stringResource(R.string.reminder_title)) },
            text = { Text(stringResource(R.string.your_password_is, password)) }
        )
    }

    if (notFound) {
        AlertDialog(
            onDismissRequest = { notFound = false },
            confirmButton = {
                Button(onClick = { notFound = false }) {
                    Text(stringResource(R.string.accept))
                }
            },
            title = { Text(stringResource(R.string.error)) },
            text = { Text(stringResource(R.string.email_not_found)) }
        )
    }
}

@Preview
@Composable
fun ForgotPasswordPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        ForgotPasswordScreen(db) {}
    }
}
