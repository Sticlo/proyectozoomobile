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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.proyectozoo.data.Animal
import com.practica.proyectozoo.data.Especie
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
    val especies = remember { mutableStateListOf<Especie>() }
    val context = LocalContext.current

    // Cargar animales y especies
    LaunchedEffect(Unit) {
        animales.clear()
        animales.addAll(db.getAllAnimalesDetail())
        especies.clear()
        especies.addAll(db.getAllEspeciesDetail())
    }

    // Mapa para lookup rápido
    val especieMap = remember {
        mutableStateMapOf<Int, String>().also { map ->
            especies.forEach { map[it.id] = it.nombreVulgar }
        }
    }

    Column(modifier.padding(16.dp)) {
        // … tarjetas y buscador …

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
        ) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(
                    items = animales.filter {
                        it.idAnimal.toString().contains(search, true)
                                || it.sexo.toString().contains(search, true)
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
                            // Avatar con iniciales de especie
                            val spName = especieMap[animal.idEspecie] ?: "??"
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF81D4FA), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = spName.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                // Mostrar especie, sexo y año de nacimiento
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = spName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Icon(
                                        imageVector =
                                            if (animal.sexo == 'M') Icons.Default.Male
                                            else Icons.Default.Female,
                                        contentDescription = null,
                                        tint = if (animal.sexo=='M') Color.Blue else Color.Magenta
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Año: ${animal.anioNacimiento ?: "-"}",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    "${animal.paisOrigen ?: "—"}, ${animal.continente ?: "—"}",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            DropdownMenuAnimalItem(
                                animal = animal,
                                db = db,
                                animales = animales
                            )
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
                }
            )
            DropdownMenuItem(
                text = { Text("Eliminar", color = Color.Red) },
                onClick = {
                    db.deleteAnimal(animal.idAnimal)
                    animales.remove(animal)
                    expanded = false
                }
            )
        }
    }
}
