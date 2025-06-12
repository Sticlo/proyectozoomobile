package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Especie
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class EspecieListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        setContent {
            ProyectozooTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            startActivity(
                                Intent(this, EspecieEditActivity::class.java)
                            )
                        }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_especie)
                            )
                        }
                    }
                ) { innerPadding ->
                    EspecieListScreen(
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
fun EspecieListScreen(
    db: DatabaseHelper,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val especies = remember { mutableStateListOf<Especie>() }

    LaunchedEffect(Unit) {
        especies.clear()
        especies.addAll(db.getAllEspeciesDetail())
    }

    LazyColumn(
        modifier = modifier.padding(8.dp)
    ) {
        items(especies, key = { it.id }) { esp ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = esp.nombreVulgar)

                Row {
                    TextButton(onClick = {
                        context.startActivity(
                            Intent(context, EspecieEditActivity::class.java)
                                .putExtra("especieId", esp.id)
                        )
                    }) {
                        Text(stringResource(R.string.edit))
                    }
                    TextButton(onClick = {
                        db.deleteEspecie(esp.id)
                        especies.remove(esp)
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
fun EspecieListPreview() {
    val context = LocalContext.current
    val db = DatabaseHelper(context)

    ProyectozooTheme {
        Scaffold { innerPadding ->
            EspecieListScreen(
                db = db,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}
