package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import android.content.Intent
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Usuario
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class UserListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        val activity = this
        setContent {
            ProyectozooTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            activity.startActivity(Intent(activity, UserEditActivity::class.java))
                        }) {
                            androidx.compose.material3.Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_user)
                            )
                        }
                    }
                ) { padding ->
                    UserListScreen(db, Modifier.padding(padding))
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    UserListScreen(db)
                }
            }
        }
    }
}

@Composable

fun UserListScreen(db: DatabaseHelper, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val usuarios = remember { mutableStateListOf<Usuario>() }
    LaunchedEffect(Unit) {
        usuarios.clear()
        usuarios.addAll(db.getAllUsuariosDetail())
    }
    LazyColumn(modifier) {
        items(usuarios) { user ->
            androidx.compose.foundation.layout.Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Text(text = user.username)
                androidx.compose.foundation.layout.Row {
                    TextButton(onClick = {
                        context.startActivity(
                            Intent(context, UserEditActivity::class.java).putExtra("userId", user.id)
                        )
                    }) { Text(stringResource(R.string.edit)) }
                    TextButton(onClick = {
                        db.deleteUsuario(user.id)
                        usuarios.remove(user)
                    }) { Text(stringResource(R.string.delete)) }
                }
            }
fun UserListScreen(db: DatabaseHelper) {
    val usuarios = remember { db.getUsuarios() }
    LazyColumn {
        items(usuarios) { user ->
            Text(text = user)
        }
    }
}

@Preview
@Composable
fun UserListPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        UserListScreen(db)
    }
}
