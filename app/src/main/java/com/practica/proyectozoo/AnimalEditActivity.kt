package com.practica.proyectozoo

import android.os.Bundle
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.ComponentActivity
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
import com.practica.proyectozoo.data.Animal
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class AnimalEditActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.edit_animal)) }
                        )
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(padding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        AnimalEditScreen(
                            db = DatabaseHelper(this@AnimalEditActivity),
                            modifier = Modifier.padding(24.dp)
                        ) { finish() }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalEditScreen(
    db: DatabaseHelper,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    val context = LocalContext.current
    var idField by remember { mutableStateOf("") }
    var currentAnimal by remember { mutableStateOf<Animal?>(null) }

    var zooIdField by remember { mutableStateOf("") }
    var especieIdField by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf('M') }
    var anioField by remember { mutableStateOf("") }
    var paisOrigen by remember { mutableStateOf("") }
    var continente by remember { mutableStateOf("") }

    var statusMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Buscar animal por ID
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
                    db.getAnimal(id)?.let { a ->
                        currentAnimal = a
                        zooIdField = a.idZoo.toString()
                        especieIdField = a.idEspecie.toString()
                        sexo = a.sexo
                        anioField = a.anioNacimiento?.toString() ?: ""
                        paisOrigen = a.paisOrigen ?: ""
                        continente = a.continente ?: ""
                        statusMsg = null
                    } ?: run {
                        statusMsg = context.getString(R.string.animal_not_found)
                        currentAnimal = null
                    }
                } else {
                    statusMsg = context.getString(R.string.invalid_id)
                }
            }) {
                Text(stringResource(R.string.search))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Campos de edición
        OutlinedTextField(
            value = zooIdField,
            onValueChange = { zooIdField = it },
            label = { Text(stringResource(R.string.zoo_id)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = especieIdField,
            onValueChange = { especieIdField = it },
            label = { Text(stringResource(R.string.especie_id)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        // Sexo selector
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.sexo) + ": ")
            Spacer(Modifier.width(8.dp))
            listOf('M', 'F').forEach { s ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (sexo == s),
                        onClick = { sexo = s }
                    )
                    Text(text = s.toString())
                    Spacer(Modifier.width(16.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = anioField,
            onValueChange = { anioField = it },
            label = { Text(stringResource(R.string.anio_nacimiento)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = paisOrigen,
            onValueChange = { paisOrigen = it },
            label = { Text(stringResource(R.string.pais_origen)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = continente,
            onValueChange = { continente = it },
            label = { Text(stringResource(R.string.continente)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Botones: Guardar y Eliminar
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    // Validaciones básicas
                    when {
                        zooIdField.toIntOrNull() == null ->
                            statusMsg = context.getString(R.string.invalid_zoo_id)
                        especieIdField.toIntOrNull() == null ->
                            statusMsg = context.getString(R.string.invalid_especie_id)
                        else -> {
                            val zid = zooIdField.toInt()
                            val eid = especieIdField.toInt()
                            val anio = anioField.toIntOrNull()
                            if (currentAnimal == null) {
                                db.insertAnimal(zid, eid, sexo, anio, paisOrigen, continente)
                                statusMsg = context.getString(R.string.animal_saved)
                            } else {
                                db.updateAnimal(
                                    currentAnimal!!.idAnimal,
                                    zid, eid, sexo, anio, paisOrigen, continente
                                )
                                statusMsg = context.getString(R.string.animal_updated)
                            }
                            // limpiar form
                            idField = ""
                            zooIdField = ""; especieIdField = ""
                            sexo = 'M'; anioField = ""
                            paisOrigen = ""; continente = ""
                            currentAnimal = null
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.save))
            }

            Button(
                onClick = {
                    currentAnimal?.let {
                        db.deleteAnimal(it.idAnimal)
                        statusMsg = context.getString(R.string.animal_deleted)
                        // limpiar form
                        idField = ""
                        zooIdField = ""; especieIdField = ""
                        sexo = 'M'; anioField = ""
                        paisOrigen = ""; continente = ""
                        currentAnimal = null
                    } ?: run {
                        statusMsg = context.getString(R.string.animal_not_found)
                    }
                },
                enabled = currentAnimal != null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.delete))
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            // Limpiar todos
            idField = ""
            zooIdField = ""; especieIdField = ""
            sexo = 'M'; anioField = ""
            paisOrigen = ""; continente = ""
            currentAnimal = null
            statusMsg = null
        }) {
            Text(stringResource(R.string.clear_form))
        }

        Spacer(Modifier.height(12.dp))

        statusMsg?.let { msg ->
            Text(
                text = msg,
                color = if (
                    msg == context.getString(R.string.animal_saved)
                    || msg == context.getString(R.string.animal_updated)
                ) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalEditPreview() {
    ProyectozooTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Aquí podrías copiar el layout del preview de Especie,
            // adaptando etiquetas a Animal (ID, Zoo ID, Especie ID, Sexo, etc.)
        }
    }
}
