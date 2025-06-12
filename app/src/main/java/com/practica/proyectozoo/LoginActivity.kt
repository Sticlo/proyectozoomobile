package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ForgotPasswordActivity
import com.practica.proyectozoo.MainMenuActivity
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                // Fondo degradado con tus colores primario/secondary
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                ) {
                    // Tarjeta centrada con el formulario
                    LoginCard(
                        db = db,
                        modifier = Modifier.align(Alignment.Center),
                        onSuccess = {
                            startActivity(Intent(this, MainMenuActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginCard(
    db: DatabaseHelper,
    modifier: Modifier = Modifier,
    onSuccess: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    // Textos de recurso
    val usrLabel  = stringResource(R.string.username)
    val pwdLabel  = stringResource(R.string.password)
    val btnLogin  = stringResource(R.string.login)
    val btnForgot = stringResource(R.string.forgot_password)
    val errUser   = stringResource(R.string.invalid_username)
    val errPass   = stringResource(R.string.invalid_password)
    val errLogin  = stringResource(R.string.invalid_login)
    val errText   = stringResource(R.string.login_error)

    Card(
        modifier = modifier
            .width(320.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo/Icono de zoológico
            Icon(
                imageVector = Icons.Filled.Pets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(usrLabel) },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(pwdLabel) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(btnLogin)
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = {
                ctx.startActivity(
                    Intent(ctx, ForgotPasswordActivity::class.java)
                )
            }) {
                Text(btnForgot)
            }

            if (showError) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errText,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val ctx = LocalContext.current
    val db = DatabaseHelper(ctx)
    ProyectozooTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            LoginCard(db = db, modifier = Modifier.align(Alignment.Center), onSuccess = {})
        }
    }
}
