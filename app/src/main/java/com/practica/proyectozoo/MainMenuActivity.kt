package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    MainMenuScreen(Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = { context.startActivity(Intent(context, UserListActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text = "Usuarios")
        }
        Button(onClick = { context.startActivity(Intent(context, ZooListActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text = "Zoos")
        }
        Button(onClick = { context.startActivity(Intent(context, EspecieListActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text = "Especies")
        }
        Button(onClick = { context.startActivity(Intent(context, AnimalListActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text = "Animales")
        }
    }
}

@Preview
@Composable
fun MainMenuPreview() {
    ProyectozooTheme {
        MainMenuScreen()
    }
}
