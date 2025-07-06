package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Usuario
import com.practica.proyectozoo.ui.theme.ProyectozooTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class UserListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        @file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "Gestión de Usuarios",
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
                                Intent(context, UserEditActivity::class.java)
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar Usuario"
                            )
                        }
                    }
                ) { innerPadding ->
                    UserListScreen(
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
fun UserListScreen(db: DatabaseHelper, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var search by rememberSaveable { mutableStateOf("") }
    val usuarios = remember { mutableStateListOf<Usuario>() }

    LaunchedEffect(Unit) {
        usuarios.clear()
        usuarios.addAll(db.getAllUsuariosDetail())
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Administra los usuarios de tu sistema", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Total Usuarios", usuarios.size.toString(), Icons.Default.People, Color(0xFFD1FAE5))
            StatCard("Activos", usuarios.count { it.perfilId == 1 }.toString(), Icons.Default.CheckCircle, Color(0xFFE0F2FE))
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar por usuario o email") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Aquí reemplazamos `fillMaxSize()` por weight(1f)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),         // <-- ocupa sólo el espacio que quede
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()   // ahora sí puede llenar el card
                    .padding(8.dp)
            ) {
                items(
                    items = usuarios.filter {
                        it.username.contains(search, true) ||
                                it.email.contains(search, true)
                    },
                    key = { it.id }
                ) { user ->
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
                                    .background(Color(0xFFCBD5E1), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.username.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(user.username, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                Text(user.email, fontSize = 13.sp, color = Color.Gray)
                            }
                            DropdownMenuUserItem(user = user, db = db, usuarios = usuarios)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DropdownMenuUserItem(user: Usuario, db: DatabaseHelper, usuarios: MutableList<Usuario>) {
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
                        Intent(context, UserEditActivity::class.java).putExtra("userId", user.id)
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
