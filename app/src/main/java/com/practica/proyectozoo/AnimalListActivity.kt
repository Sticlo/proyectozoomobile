package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class AnimalListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AnimalListScreen(db)
                }
            }
        }
    }
}

@Composable
fun AnimalListScreen(db: DatabaseHelper) {
    val animales = remember { db.getAnimales() }
    LazyColumn {
        items(animales) { ani ->
            Text(text = ani)
        }
    }
}

@Preview
@Composable
fun AnimalListPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        AnimalListScreen(db)
    }
}
