package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Especie
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class EspecieEditActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(stringResource(R.string.edit_especie)) })
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(padding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        EspecieEditScreen(db, Modifier.padding(24.dp)) { finish() }
                    }
                }
            }
        }
    }
}

@Composable
fun EspecieEditScreen(db: DatabaseHelper, modifier: Modifier = Modifier, onFinish: () -> Unit = {}) {
    val context = LocalContext.current

    val db = DatabaseHelper(context)
    var idField by remember { mutableStateOf("") }
    var currentEspecie by remember { mutableStateOf<Especie?>(null) }
    var nombreVulgar by remember { mutableStateOf("") }
    var nombreCientifico by remember { mutableStateOf("") }
    var familia by remember { mutableStateOf("") }
    var enPeligro by remember { mutableStateOf(false) }
    var statusMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Buscar especie por ID
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = idField,
                onValueChange = { idField = it },
                label = { Text(stringResource(R.string.id)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(onClick = {
                val id = idField.toIntOrNull()
                if (id != null) {
                    db.getEspecie(id)?.let { especie ->
                        currentEspecie = especie
                        nombreVulgar = especie.nombreVulgar
                        nombreCientifico = especie.nombreCientifico
                        familia = especie.familia ?: ""
                        enPeligro = especie.enPeligro
                        statusMsg = null
                    } ?: run {
                        statusMsg = context.getString(R.string.especie_not_found)
                        currentEspecie = null
                    }
                } else {
                    statusMsg = context.getString(R.string.invalid_id)
                }
            }) {
                Text(context.getString(R.string.search))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Campos
        OutlinedTextField(
            value = nombreVulgar,
            onValueChange = { nombreVulgar = it },
            label = { Text(stringResource(R.string.nombre_vulgar)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = nombreCientifico,
            onValueChange = { nombreCientifico = it },
            label = { Text(stringResource(R.string.nombre_cientifico)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = familia,
            onValueChange = { familia = it },
            label = { Text(stringResource(R.string.familia)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = enPeligro, onCheckedChange = { enPeligro = it })
            Text(text = stringResource(R.string.en_peligro))
        }

        Spacer(Modifier.height(16.dp))

        // Botones: Guardar y Eliminar
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    when {
                        nombreVulgar.isBlank() -> statusMsg = context.getString(R.string.invalid_nombre_vulgar)
                        nombreCientifico.isBlank() -> statusMsg = context.getString(R.string.invalid_nombre_cientifico)
                        else -> {
                            val fam = if (familia.isBlank()) null else familia
                            if (currentEspecie == null) {
                                db.insertEspecie(nombreVulgar, nombreCientifico, fam, enPeligro)
                                statusMsg = context.getString(R.string.especie_saved)
                            } else {
                                db.updateEspecie(currentEspecie!!.id, nombreVulgar, nombreCientifico, fam, enPeligro)
                                statusMsg = context.getString(R.string.especie_updated)
                            }
                            idField = ""; nombreVulgar = ""; nombreCientifico = ""; familia = ""; enPeligro = false
                            currentEspecie = null
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(context.getString(R.string.save))
            }

            Button(
                onClick = {
                    currentEspecie?.let {
                        db.deleteEspecie(it.id)
                        statusMsg = context.getString(R.string.especie_deleted)
                        idField = ""; nombreVulgar = ""; nombreCientifico = ""; familia = ""; enPeligro = false
                        currentEspecie = null
                    } ?: run {
                        statusMsg = context.getString(R.string.especie_not_found)
                    }
                },
                enabled = currentEspecie != null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(context.getString(R.string.delete))
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            idField = ""; nombreVulgar = ""; nombreCientifico = ""; familia = ""; enPeligro = false
            currentEspecie = null
            statusMsg = null
        }) {
            Text(context.getString(R.string.clear_form))
        }

        Spacer(Modifier.height(12.dp))

        statusMsg?.let {
            Text(
                text = it,
                color = if (it == context.getString(R.string.especie_saved) || it == context.getString(R.string.especie_updated))
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EspecieEditPreview() {
    ProyectozooTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ID + Buscar
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("ID") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(onClick = {}) {
                    Text("Buscar")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Campos
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Nombre vulgar") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Nombre científico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Familia") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = {})
                Text("¿En peligro de extinción?")
            }

            Spacer(Modifier.height(16.dp))

            // Botones Guardar / Eliminar
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = {}) {
                Text("Limpiar")
            }

            Spacer(Modifier.height(12.dp))

        }
    }
}
