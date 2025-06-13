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
import com.practica.proyectozoo.data.Especie
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class EspecieListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "Gestión de Especies",
                                    color = Color(0xFF0f172a)
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White
                            )
                        )
                    },
                    floatingActionButton = {
                        val context = LocalContext.current
                        FloatingActionButton(onClick = {
                            context.startActivity(
                                Intent(context, EspecieEditActivity::class.java)
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar Especie"
                            )
                        }
                    }
                ) { innerPadding ->
                    EspecieListScreen(
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
fun EspecieListScreen(db: DatabaseHelper, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var search by rememberSaveable { mutableStateOf("") }
    val especies = remember { mutableStateListOf<Especie>() }

    LaunchedEffect(Unit) {
        especies.clear()
        especies.addAll(db.getAllEspeciesDetail())
    }

    Column(modifier.padding(16.dp)) {
        Text("Administra las especies registradas en el sistema", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Total Especies",
                value = especies.size.toString(),
                icon = Icons.Default.Pets,
                color = Color(0xFFE0F7FA),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar por nombre vulgar o científico") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(12.dp)) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(
                    items = especies.filter {
                        it.nombreVulgar.contains(search, true) || it.nombreCientifico.contains(search, true)
                    },
                    key = { it.id }
                ) { especie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
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
                                    .background(Color(0xFF81D4FA), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = especie.nombreVulgar.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(especie.nombreVulgar, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text(especie.nombreCientifico, fontSize = 13.sp, color = Color.Gray)
                            }
                            DropdownMenuEspecieItem(especie = especie, db = db, especies = especies)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DropdownMenuEspecieItem(especie: Especie, db: DatabaseHelper, especies: MutableList<Especie>) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Editar") },
                onClick = {
                    context.startActivity(
                        Intent(context, EspecieEditActivity::class.java).putExtra("especieId", especie.id)
                    )
                    expanded = false
                },
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            )
            DropdownMenuItem(
                text = { Text("Eliminar", color = Color.Red) },
                onClick = {
                    db.deleteEspecie(especie.id)
                    especies.remove(especie)
                    expanded = false
                },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            )
        }
    }
}
