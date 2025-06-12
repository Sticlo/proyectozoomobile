package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Usuario
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class UserListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(stringResource(R.string.users)) }
                        )
                    },
                    floatingActionButton = {
                        val context = LocalContext.current
                        FloatingActionButton(
                            onClick = {
                                context.startActivity(
                                    Intent(context, UserEditActivity::class.java)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_user)
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
fun UserListScreen(
    db: DatabaseHelper,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val usuarios = remember { mutableStateListOf<Usuario>() }
    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        usuarios.clear()
        usuarios.addAll(db.getAllUsuariosDetail())
    }

    Column(modifier = modifier.padding(8.dp)) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_hint)) }
        )
        Spacer(Modifier.height(8.dp))

        LazyColumn {
        items(
            items = usuarios.filter {
                it.username.contains(search, ignoreCase = true) ||
                    it.email.contains(search, ignoreCase = true)
            },
            key = { it.id }
        ) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = user.username)

                Row {
                    TextButton(onClick = {
                        context.startActivity(
                            Intent(context, UserEditActivity::class.java)
                                .putExtra("userId", user.id)
                        )
                    }) {
                        Text(stringResource(R.string.edit))
                    }
                    TextButton(onClick = {
                        db.deleteUsuario(user.id)
                        usuarios.remove(user)
                    }) {
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    val context = LocalContext.current
    val db = DatabaseHelper(context)

    ProyectozooTheme {
        UserListScreen(db = db)
    }
}
