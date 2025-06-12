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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Zoo
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class ZooListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        val context = LocalContext.current
                        FloatingActionButton(
                            onClick = {
                                context.startActivity(
                                    Intent(context, ZooEditActivity::class.java)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_zoo)
                            )
                        }
                    }
                ) { innerPadding ->
                    ZooListScreen(
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
fun ZooListScreen(
    db: DatabaseHelper,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val zoos = remember { mutableStateListOf<Zoo>() }

    LaunchedEffect(Unit) {
        zoos.clear()
        zoos.addAll(db.getAllZoosDetail())
    }

    LazyColumn(
        modifier = modifier.padding(8.dp)
    ) {
        items(
            items = zoos,
            key = { it.id }
        ) { zoo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = zoo.nombre)

                Row {
                    TextButton(onClick = {
                        context.startActivity(
                            Intent(context, ZooEditActivity::class.java)
                                .putExtra("zooId", zoo.id)
                        )
                    }) {
                        Text(stringResource(R.string.edit))
                    }
                    TextButton(onClick = {
                        db.deleteZoo(zoo.id)
                        zoos.remove(zoo)
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
fun ZooListPreview() {
    val context = LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        ZooListScreen(db = db)
    }
}
