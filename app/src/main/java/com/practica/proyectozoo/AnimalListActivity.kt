package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme
import androidx.compose.foundation.layout.PaddingValues

class AnimalListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Pass the padding through to your screen
                    AnimalListScreen(
                        db = db,
                        contentPadding = innerPadding
                    )
                }
            }
        }
    }
}

@Composable
fun AnimalListScreen(
    db: DatabaseHelper,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val animales = remember { db.getAnimales() }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(animales) { ani ->
            Text(text = ani, modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalListPreview() {
    val context = LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        Scaffold { innerPadding ->
            AnimalListScreen(db = db, contentPadding = innerPadding)
        }
    }
}
