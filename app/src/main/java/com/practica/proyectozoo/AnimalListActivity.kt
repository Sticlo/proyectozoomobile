package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.proyectozoo.data.Animal
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

@OptIn(ExperimentalMaterial3Api::class)
class AnimalListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Gestión de Animales", color = Color(0xFF1740A1)) },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                        )
                    },
                    floatingActionButton = {
                        val context = LocalContext.current
                        FloatingActionButton(onClick = {
                            context.startActivity(
                                Intent(context, AnimalEditActivity::class.java)
                            )
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar Animal")
                        }
                    }
                ) { innerPadding ->
                    AnimalListScreen(
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
fun AnimalListScreen(db: DatabaseHelper, modifier: Modifier = Modifier) {
    var search by rememberSaveable { mutableStateOf("") }
    val animales = remember { mutableStateListOf<Animal>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        animales.clear()
        animales.addAll(db.getAllAnimalesDetail())
    }

    Column(modifier.padding(16.dp)) {
        Text(
            "Administra los animales registrados en el sistema",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(16.dp))

        // Tarjetas estadísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Animales",
                value = animales.size.toString(),
                icon = Icons.Default.Pets,
                color = Color(0xFFE0F7FA),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Machos",
                value = animales.count { it.sexo == 'M' }.toString(),
                icon = Icons.Default.Male,
                color = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar por ID o sexo") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
        ) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(
                    items = animales.filter {
                        it.idAnimal.toString().contains(search, true) ||
                                it.sexo.toString().contains(search, true)
                    },
                    key = { it.idAnimal }
                ) { animal ->
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
                                    text = "${animal.idAnimal}${animal.sexo}",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Año: ${animal.anioNacimiento ?: "-"}",
                                    fontSize = 16.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                                )
                                Text(
                                    "${animal.paisOrigen ?: "—"}, ${animal.continente ?: "—"}",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                            DropdownMenuAnimalItem(animal = animal, db = db, animales = animales)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuAnimalItem(
    animal: Animal,
    db: DatabaseHelper,
    animales: MutableList<Animal>
) {
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
                        Intent(context, AnimalEditActivity::class.java)
                            .putExtra("animalId", animal.idAnimal)
                    )
                    expanded = false
                },
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            )
        }
    }
}
