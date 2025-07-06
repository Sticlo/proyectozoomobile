package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Usuario
import com.practica.proyectozoo.ui.theme.ProyectozooTheme
import com.practica.proyectozoo.StatCard

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
    var totalUsuarios by remember { mutableStateOf(0) }
    var totalZoos by remember { mutableStateOf(0) }
    var totalAnimales by remember { mutableStateOf(0) }
    var totalEspecies by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        totalUsuarios = db.countUsuarios()
        totalZoos = db.countZoos()
        totalAnimales = db.countAnimales()
        totalEspecies = db.countEspecies()
    }

    Column(modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Usuarios",
                value = totalUsuarios.toString(),
                icon = Icons.Default.People,
                color = Color(0xFFD1FAE5),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Zoos",
                value = totalZoos.toString(),
                icon = Icons.Default.LocationCity,
                color = Color(0xFFD1E8FF),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Animales",
                value = totalAnimales.toString(),
                icon = Icons.Default.Pets,
                color = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Especies",
                value = totalEspecies.toString(),
                icon = Icons.Default.Eco,
                color = Color(0xFFE0F7FA),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(24.dp))
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
