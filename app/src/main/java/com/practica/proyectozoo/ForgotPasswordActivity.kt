package com.practica.proyectozoo

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.EarthBrown
import com.practica.proyectozoo.ui.theme.JungleGreen
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(JungleGreen, EarthBrown)))
                        .padding(16.dp)
                ) {
                    ForgotPasswordScreen(
                        db = db,
                        modifier = Modifier.align(Alignment.Center),
                        onClose = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    db: DatabaseHelper,
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    // Estados
    var email by rememberSaveable { mutableStateOf("") }
    var statusMsg by rememberSaveable { mutableStateOf<String?>(null) }

    // Contexto y strings pre-capturados
    val ctx = LocalContext.current
    val labelEmail    = stringResource(R.string.email)
    val labelSend     = stringResource(R.string.send_reminder)
    val labelAccept   = stringResource(R.string.accept)
    val invalidEmail  = stringResource(R.string.invalid_email)
    val notFoundEmail = stringResource(R.string.email_not_found)
    val reminderTitle = stringResource(R.string.reminder_title)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MailOutline,
                contentDescription = null,
                tint = JungleGreen,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(labelEmail) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    statusMsg = when {
                        email.isBlank() -> invalidEmail
                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> invalidEmail
                        else -> {
                            db.getPasswordByEmail(email)?.let { pw ->
                                // Mostrar contraseña
                                "$reminderTitle: $pw"
                            } ?: notFoundEmail
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(labelSend)
            }

            statusMsg?.let { msg ->
                Spacer(Modifier.height(24.dp))
                Text(
                    text = msg,
                    color = if (msg.startsWith(reminderTitle)) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(labelAccept)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    val db = DatabaseHelper(LocalContext.current)
    ProyectozooTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(JungleGreen, EarthBrown)))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ForgotPasswordScreen(db = db, onClose = {})
        }
    }
}
