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
import com.practica.proyectozoo.data.Especie
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class EspecieListActivity : ComponentActivity() {
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
                            activity.startActivity(Intent(activity, EspecieEditActivity::class.java))
                        }) {
                            androidx.compose.material3.Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_especie))
                        }
                    }
                ) { padding ->
                    EspecieListScreen(db, Modifier.padding(padding))
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    EspecieListScreen(db)
                }
            }
        }
    }
}

@Composable
fun EspecieListScreen(db: DatabaseHelper, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val especies = remember { mutableStateListOf<Especie>() }
    LaunchedEffect(Unit) {
        especies.clear()
        especies.addAll(db.getAllEspeciesDetail())
    }
    LazyColumn(modifier) {
        items(especies) { esp ->
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = esp.nombreVulgar)
                Row {
                    TextButton(onClick = {
                        context.startActivity(Intent(context, EspecieEditActivity::class.java).putExtra("especieId", esp.id))
                    }) { Text(stringResource(R.string.edit)) }
                    TextButton(onClick = {
                        db.deleteEspecie(esp.id)
                        especies.remove(esp)
                    }) { Text(stringResource(R.string.delete)) }
                }
            }
fun EspecieListScreen(db: DatabaseHelper) {
    val especies = remember { db.getEspecies() }
    LazyColumn {
        items(especies) { esp ->
            Text(text = esp)
        }
    }
}

@Preview
@Composable
fun EspecieListPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        EspecieListScreen(db)
    }
}
