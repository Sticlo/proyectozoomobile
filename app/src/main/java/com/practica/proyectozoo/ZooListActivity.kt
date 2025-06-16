package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Zoo
import com.practica.proyectozoo.StatCard
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

@OptIn(ExperimentalMaterial3Api::class)
class ZooListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Gestión de Zoos", color = Color(0xFF1740A1)) },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                        )
                    },
                    floatingActionButton = {
                        val context = LocalContext.current
                        FloatingActionButton(onClick = {
                            context.startActivity(Intent(context, ZooEditActivity::class.java))
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar Zoo")
                        }
                    }
                ) { innerPadding ->
                    ZooListScreen(
                        db = db,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ZooListScreen(
    db: DatabaseHelper,
    modifier: Modifier = Modifier
) {
    var search by rememberSaveable { mutableStateOf("") }
    val zoos = remember { mutableStateListOf<Zoo>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        zoos.clear()
        zoos.addAll(db.getAllZoosDetail())
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Administra los zoológicos registrados en el sistema",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(16.dp))

        // Estadísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Zoos",
                value = zoos.size.toString(),
                icon = Icons.Default.LocationCity,
                color = Color(0xFFD1E8FF),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Con Presupuesto",
                value = zoos.count { it.presupuestoAnual != null }.toString(),
                icon = Icons.Default.Place,
                color = Color(0xFFE8FFD1),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar por nombre de zoo") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = zoos.filter { zoo ->
                        zoo.nombre.contains(search, true)
                    },
                    key = { it.id }
                ) { zoo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF90CAF9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = zoo.nombre.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(zoo.nombre, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text("Ciudad ID: ${zoo.idCiudad}", fontSize = 13.sp, color = Color.Gray)
                            }
                            DropdownMenuZoo(
                                onEdit = {
                                    context.startActivity(
                                        Intent(context, ZooEditActivity::class.java)
                                            .putExtra("zooId", zoo.id)
                                    )
                                },
                                onDelete = {
                                    db.deleteZoo(zoo.id)
                                    zoos.remove(zoo)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuZoo(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Editar Zoo") },
                onClick = {
                    onEdit()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Eliminar Zoo", color = Color.Red) },
                onClick = {
                    onDelete()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = null) }
            )
        }
    }
}
