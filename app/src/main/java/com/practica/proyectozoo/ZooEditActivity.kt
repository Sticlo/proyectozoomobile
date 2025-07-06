@file:OptIn(ExperimentalMaterial3Api::class)

package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Zoo
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class ZooEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.edit_zoo)) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White
                            )
                        )
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            )
                            .padding(padding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ZooEditForm(db, Modifier.padding(top = 24.dp)) { finish() }
                    }
                }
            }
        }
    }
}

@Composable
fun ZooEditForm(
    db: DatabaseHelper,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit
) {
    val ctx = LocalContext.current
    var idField by rememberSaveable { mutableStateOf("") }
    var current by rememberSaveable { mutableStateOf<Zoo?>(null) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var ciudadId by rememberSaveable { mutableStateOf("") }
    var tamano by rememberSaveable { mutableStateOf("") }
    var presupuesto by rememberSaveable { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }

    // Dropdown de ciudades
    var expandedCities by remember { mutableStateOf(false) }
    var ciudades by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    var selectedCity by rememberSaveable { mutableStateOf<Pair<Int, String>?>(null) }

    // Cargar lista de ciudades desde DB
    LaunchedEffect(db) {
        ciudades = db.getAllCiudades()
    }


    Column(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Buscar por ID
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = idField,
                onValueChange = { idField = it },
                label = { Text(stringResource(R.string.id)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                status = when {
                    idField.toIntOrNull() == null -> ctx.getString(R.string.invalid_id)
                    else -> db.getZoo(idField.toInt())?.let { z ->
                        current = z
                        nombre = z.nombre
                        ciudadId = z.idCiudad.toString()
                        tamano = z.tamanoM2?.toString() ?: ""
                        presupuesto = z.presupuestoAnual?.toString() ?: ""
                        selectedCity = ciudades.find { it.first == z.idCiudad }
                        null
                    } ?: ctx.getString(R.string.zoo_not_found)
                }
            }) {
                Text(stringResource(R.string.search))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(stringResource(R.string.nombre)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        // Selector de Ciudad
        ExposedDropdownMenuBox(
            expanded = expandedCities,
            onExpandedChange = { expandedCities = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCity?.second.orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.ciudad)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCities) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedCities,
                onDismissRequest = { expandedCities = false }
            )
            {
                ciudades.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedCity = id to name
                            ciudadId = id.toString()
                            expandedCities = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = tamano,
            onValueChange = { tamano = it },
            label = { Text(stringResource(R.string.tamano)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = presupuesto,
            onValueChange = { presupuesto = it },
            label = { Text(stringResource(R.string.presupuesto)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Botones Guardar / Eliminar
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    status = when {
                        nombre.isBlank() -> ctx.getString(R.string.invalid_nombre)
                        ciudadId.toIntOrNull() == null -> ctx.getString(R.string.invalid_ciudad_id)
                        tamano.isNotBlank() && tamano.toIntOrNull() == null -> ctx.getString(R.string.invalid_tamano)
                        presupuesto.isNotBlank() && presupuesto.toDoubleOrNull() == null -> ctx.getString(
                            R.string.invalid_presupuesto
                        )

                        else -> {
                            if (current == null) {
                                db.insertZoo(
                                    nombre,
                                    ciudadId.toInt(),
                                    tamano.toIntOrNull(),
                                    presupuesto.toDoubleOrNull()
                                )
                                ctx.getString(R.string.zoo_saved)
                            } else {
                                db.updateZoo(
                                    current!!.id,
                                    nombre,
                                    ciudadId.toInt(),
                                    tamano.toIntOrNull(),
                                    presupuesto.toDoubleOrNull()
                                )
                                ctx.getString(R.string.zoo_updated)
                            }.also {
                                idField = ""; nombre = ""; ciudadId = ""; tamano = ""; presupuesto =
                                ""; current = null
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.save))
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            idField = ""; nombre = ""; ciudadId = ""; tamano = ""; presupuesto = ""; current =
            null; status = null
        }) {
            Text(stringResource(R.string.clear_form))
        }

        status?.let { msg ->
            Spacer(Modifier.height(12.dp))
            Text(
                text = msg,
                color = if (msg == stringResource(R.string.zoo_saved) || msg == stringResource(R.string.zoo_updated)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
