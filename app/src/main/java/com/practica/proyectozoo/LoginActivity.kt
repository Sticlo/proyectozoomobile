package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
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
import com.practica.proyectozoo.ui.theme.EarthBrown
import com.practica.proyectozoo.ui.theme.JungleGreen
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                // Fondo degradado sencillo
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(JungleGreen, EarthBrown)))
                ) {
                    // Formulario en el centro
                    SimpleLogin(
                        db = db,
                        onSuccess = { ctx ->
                            ctx.startActivity(Intent(ctx, MainMenuActivity::class.java))
                            finish()
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleLogin(
    db: DatabaseHelper,
    onSuccess: (ctx: android.content.Context) -> Unit,
    modifier: Modifier = Modifier
) {
    var user by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    val usrLabel  = stringResource(R.string.username)
    val pwdLabel  = stringResource(R.string.password)
    val btnLogin  = stringResource(R.string.login)
    val btnForgot = stringResource(R.string.forgot_password)
    val errUser   = stringResource(R.string.invalid_username)
    val errPass   = stringResource(R.string.invalid_password)
    val errLogin  = stringResource(R.string.invalid_login)
    val errText   = stringResource(R.string.login_error)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(JungleGreen, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Pets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text(usrLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text(pwdLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    showError = false
                    when {
                        user.isBlank() -> {
                            Toast.makeText(ctx, errUser, Toast.LENGTH_SHORT).show()
                            showError = true
                        }
                        pass.isBlank() -> {
                            Toast.makeText(ctx, errPass, Toast.LENGTH_SHORT).show()
                            showError = true
                        }
                        !db.validateUser(user, pass) -> {
                            Toast.makeText(ctx, errLogin, Toast.LENGTH_SHORT).show()
                            showError = true
                        }
                        else -> onSuccess(ctx)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
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
                Spacer(Modifier.height(8.dp))
                Text(errText, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleLogin() {
    val db = DatabaseHelper(LocalContext.current)
    ProyectozooTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(JungleGreen, EarthBrown)))
        ) {
            SimpleLogin(
                db = db,
                onSuccess = {},
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
