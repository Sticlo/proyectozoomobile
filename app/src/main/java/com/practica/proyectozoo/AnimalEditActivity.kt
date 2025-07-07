package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.Animal
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Especie
import com.practica.proyectozoo.data.Zoo
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class AnimalEditActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)

        // 1) Leer el extra (o -1 si no viene)
        val animalId = intent.getIntExtra("animalId", -1).takeIf { it != -1 }
        // 2) Cargar el animal (o null si es creación)
        val animalToEdit = animalId?.let { db.getAnimal(it) }

        setContent {
            ProyectozooTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    if (animalToEdit == null) stringResource(R.string.new_animal)
                                    else stringResource(R.string.edit_animal)
                                )
                            }
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
                            db             = db,
                            animalToEdit   = animalToEdit,
                            modifier       = Modifier.padding(24.dp)
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
    animalToEdit: Animal?,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    val context = LocalContext.current

    // 1) Listas de Zoos y Especies
    val zoos     = remember { mutableStateListOf<Zoo>() }
    val especies = remember { mutableStateListOf<Especie>() }

    // 2) Estados de selección
    var selectedZoo     by remember { mutableStateOf<Zoo?>(null) }
    var expandedZoo     by remember { mutableStateOf(false) }
    var selectedEspecie by remember { mutableStateOf<Especie?>(null) }
    var expandedEspecie by remember { mutableStateOf(false) }

    // 3) Campos del animal (sexo, año, país, continente)
    var sexo       by remember { mutableStateOf(animalToEdit?.sexo ?: 'M') }
    var anioField  by remember { mutableStateOf(animalToEdit?.anioNacimiento?.toString().orEmpty()) }
    var paisOrigen by remember { mutableStateOf(animalToEdit?.paisOrigen.orEmpty()) }
    var continente by remember { mutableStateOf(animalToEdit?.continente.orEmpty()) }
    var statusMsg  by remember { mutableStateOf<String?>(null) }

    // 4) Carga inicial de Zoos y Especies
    LaunchedEffect(Unit) {
        zoos.clear()
        zoos.addAll(db.getAllZoosDetail())
        especies.clear()
        especies.addAll(db.getAllEspeciesDetail())
    }

    // 5) Cuando ya están cargadas las listas y viene animalToEdit, precarga la selección
    LaunchedEffect(animalToEdit, zoos, especies) {
        animalToEdit?.let { animal ->
            zoos.firstOrNull { it.id == animal.idZoo }?.let { selectedZoo = it }
            especies.firstOrNull { it.id == animal.idEspecie }?.let { selectedEspecie = it }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // — Dropdown de Zoo —
        ExposedDropdownMenuBox(
            expanded = expandedZoo,
            onExpandedChange = { expandedZoo = !expandedZoo }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedZoo?.nombre.orEmpty(),
                onValueChange = {},
                label = { Text(stringResource(R.string.zoo_id)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedZoo) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedZoo,
                onDismissRequest = { expandedZoo = false }
            ) {
                zoos.forEach { zoo ->
                    DropdownMenuItem(
                        text = { Text(zoo.nombre) },
                        onClick = {
                            selectedZoo = zoo
                            expandedZoo = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // — Dropdown de Especie —
        ExposedDropdownMenuBox(
            expanded = expandedEspecie,
            onExpandedChange = { expandedEspecie = !expandedEspecie }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedEspecie?.nombreVulgar.orEmpty(),
                onValueChange = {},
                label = { Text(stringResource(R.string.especie_id)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedEspecie) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedEspecie,
                onDismissRequest = { expandedEspecie = false }
            ) {
                especies.forEach { esp ->
                    DropdownMenuItem(
                        text = { Text(esp.nombreVulgar) },
                        onClick = {
                            selectedEspecie = esp
                            expandedEspecie = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // — Sexo —
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.sexo) + ": ")
            Spacer(Modifier.width(8.dp))
            listOf('M', 'F').forEach { s ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = sexo == s,
                        onClick = { sexo = s }
                    )
                    Text(text = s.toString())
                    Spacer(Modifier.width(16.dp))
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // — Año nacimiento —
        OutlinedTextField(
            value = anioField,
            onValueChange = { anioField = it },
            label = { Text(stringResource(R.string.anio_nacimiento)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(8.dp))

        // — País origen —
        OutlinedTextField(
            value = paisOrigen,
            onValueChange = { paisOrigen = it },
            label = { Text(stringResource(R.string.pais_origen)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // — Continente —
        OutlinedTextField(
            value = continente,
            onValueChange = { continente = it },
            label = { Text(stringResource(R.string.continente)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // — Botón Guardar / Actualizar —
        Button(
            onClick = {
                when {
                    selectedZoo == null ->
                        statusMsg = context.getString(R.string.invalid_zoo_id)
                    selectedEspecie == null ->
                        statusMsg = context.getString(R.string.invalid_especie_id)
                    else -> {
                        val zid = selectedZoo!!.id
                        val eid = selectedEspecie!!.id
                        val anio = anioField.toIntOrNull()
                        if (animalToEdit == null) {
                            db.insertAnimal(zid, eid, sexo, anio, paisOrigen, continente)
                            statusMsg = context.getString(R.string.animal_saved)
                        } else {
                            db.updateAnimal(
                                animalToEdit.idAnimal,
                                zid, eid, sexo, anio, paisOrigen, continente
                            )
                            statusMsg = context.getString(R.string.animal_updated)
                        }
                        onFinish()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                if (animalToEdit == null)
                    stringResource(R.string.save)
                else
                    stringResource(R.string.update)
            )
        }

        // — Mensaje de estado —
        statusMsg?.let {
            Spacer(Modifier.height(12.dp))
            Text(
                it,
                color = if (
                    it == context.getString(R.string.animal_saved)
                    || it == context.getString(R.string.animal_updated)
                ) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AnimalEditPreview() {
    ProyectozooTheme {
        AnimalEditScreen(
            db = DatabaseHelper(LocalContext.current),
            animalToEdit = null,
            modifier = Modifier.padding(24.dp),
            onFinish = {}
        )
    }
}
