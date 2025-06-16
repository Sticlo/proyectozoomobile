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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.EarthBrown
import com.practica.proyectozoo.ui.theme.JungleGreen
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class RegisterActivity : ComponentActivity() {
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
                    RegisterScreen(db = db, modifier = Modifier.align(Alignment.Center)) { finish() }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(db: DatabaseHelper, modifier: Modifier = Modifier, onRegistered: () -> Unit) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    val ctx = LocalContext.current

    val lblUser = stringResource(R.string.username)
    val lblEmail = stringResource(R.string.email)
    val lblPwd = stringResource(R.string.password)
    val lblConf = stringResource(R.string.confirm_password)
    val btnReg = stringResource(R.string.register)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(lblUser) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(lblEmail) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(lblPwd) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text(lblConf) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    when {
                        !username.matches(Regex("^[A-Za-z0-9]+$")) -> Toast.makeText(ctx, stringResource(R.string.invalid_username), Toast.LENGTH_SHORT).show()
                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Toast.makeText(ctx, stringResource(R.string.invalid_email), Toast.LENGTH_SHORT).show()
                        password.isBlank() -> Toast.makeText(ctx, stringResource(R.string.invalid_password), Toast.LENGTH_SHORT).show()
                        confirm != password -> Toast.makeText(ctx, stringResource(R.string.password_mismatch), Toast.LENGTH_SHORT).show()
                        db.validateUser(username, password) -> Toast.makeText(ctx, stringResource(R.string.user_already_exists), Toast.LENGTH_SHORT).show()
                        else -> {
                            db.insertUsuario(username, email, password, 2)
                            Toast.makeText(ctx, stringResource(R.string.registration_success), Toast.LENGTH_LONG).show()
                            onRegistered()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(btnReg)
            }
        }
    }
}
