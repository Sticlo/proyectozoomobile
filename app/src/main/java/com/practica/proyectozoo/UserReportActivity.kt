package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Usuario
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

@OptIn(ExperimentalMaterial3Api::class)
class UserReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Reporte de Usuarios") }
                        )
                    }
                ) { padding ->
                    UserReportScreen(db, Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun UserReportScreen(db: DatabaseHelper, modifier: Modifier = Modifier) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var usuarios by remember { mutableStateOf(listOf<Usuario>()) }

    Column(modifier.padding(16.dp)) {
        Text("Filtra por rango de fechas (AAAA-MM-DD)", fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Fecha inicio") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("Fecha fin") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            usuarios = db.getUsuariosReport(startDate.ifBlank { null }, endDate.ifBlank { null })
        }) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Generar")
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(usuarios) { user ->
                ListItem(
                    headlineContent = { Text(user.username) },
                    supportingContent = { Text(user.fechaRegistro ?: "") }
                )
                Divider()
            }
        }
    }
}
