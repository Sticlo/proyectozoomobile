package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import android.content.Intent
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Zoo
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class ZooListActivity : ComponentActivity() {
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
                            activity.startActivity(Intent(activity, ZooEditActivity::class.java))
                        }) {
                            androidx.compose.material3.Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_zoo))
                        }
                    }
                ) { padding ->
                    ZooListScreen(db, Modifier.padding(padding))
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    ZooListScreen(db)
                }
            }
        }
    }
}

@Composable
fun ZooListScreen(db: DatabaseHelper, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val zoos = remember { mutableStateListOf<Zoo>() }
    LaunchedEffect(Unit) {
        zoos.clear()
        zoos.addAll(db.getAllZoosDetail())
    }
    LazyColumn(modifier) {
        items(zoos) { zoo ->
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = zoo.nombre)
                Row {
                    TextButton(onClick = {
                        context.startActivity(Intent(context, ZooEditActivity::class.java).putExtra("zooId", zoo.id))
                    }) { Text(stringResource(R.string.edit)) }
                    TextButton(onClick = {
                        db.deleteZoo(zoo.id)
                        zoos.remove(zoo)
                    }) { Text(stringResource(R.string.delete)) }
                }
            }
fun ZooListScreen(db: DatabaseHelper) {
    val zoos = remember { db.getZoos() }
    LazyColumn {
        items(zoos) { zoo ->
            Text(text = zoo)
        }
    }
}

@Preview
@Composable
fun ZooListPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        ZooListScreen(db)
    }
}
