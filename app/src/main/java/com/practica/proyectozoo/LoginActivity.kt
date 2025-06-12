package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent        // ya no usamos enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme
import androidx.compose.foundation.layout.systemBarsPadding  // <- para los insets

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold { innerPadding ->
                    LoginScreen(
                        db = db,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .systemBarsPadding(),      // respeta status & nav bars
                        onSuccess = {
                            startActivity(Intent(this, MainMenuActivity::class.java))
                            startActivity(Intent(this, UserListActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    db: DatabaseHelper,
    modifier: Modifier = Modifier,
    onSuccess: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // Captura de recursos
    val ctx = LocalContext.current
    val usrLabel  = stringResource(R.string.username)
    val pwdLabel  = stringResource(R.string.password)
    val btnLogin  = stringResource(R.string.login)
    val btnForgot = stringResource(R.string.forgot_password)
    val errUser   = stringResource(R.string.invalid_username)
    val errPass   = stringResource(R.string.invalid_password)
    val errLogin  = stringResource(R.string.invalid_login)
    val errText   = stringResource(R.string.login_error)

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(usrLabel) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(pwdLabel) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                showError = false
                when {
                    !username.matches(Regex("^[A-Za-z0-9]+$")) -> {
                        Toast.makeText(ctx, errUser, Toast.LENGTH_SHORT).show()
                        showError = true
                    }
                    password.isBlank() -> {
                        Toast.makeText(ctx, errPass, Toast.LENGTH_SHORT).show()
                        showError = true
                    }
                    !db.validateUser(username, password) -> {
                        Toast.makeText(ctx, errLogin, Toast.LENGTH_SHORT).show()
                        showError = true
                    }
                    else -> onSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(btnLogin)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = {
            ctx.startActivity(Intent(ctx, ForgotPasswordActivity::class.java))
        }) {
            Text(btnForgot)
        }

        if (showError) {
            Text(
                text = errText,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val ctx = LocalContext.current
    val db = DatabaseHelper(ctx)
    ProyectozooTheme {
        LoginScreen(db = db, onSuccess = {})
    }
}
