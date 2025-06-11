package com.practica.proyectozoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.Zoo
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class ZooEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        val zooId = intent.getIntExtra("zooId", -1).takeIf { it != -1 }
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    ZooEditScreen(db, zooId, Modifier.padding(padding)) { finish() }
                }
            }
        }
    }
}

@Composable
fun ZooEditScreen(db: DatabaseHelper, zooId: Int?, modifier: Modifier = Modifier, onFinish: () -> Unit = {}) {
    var nombre by remember { mutableStateOf("") }
    var ciudadId by remember { mutableStateOf("") }
    var tamano by remember { mutableStateOf("") }
    var presupuesto by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(zooId) {
        zooId?.let {
            db.getZoo(it)?.let { z ->
                nombre = z.nombre
                ciudadId = z.idCiudad.toString()
                tamano = z.tamanoM2?.toString() ?: ""
                presupuesto = z.presupuestoAnual?.toString() ?: ""
            }
        }
    }

    Column(modifier.padding(16.dp)) {
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text(stringResource(R.string.nombre)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = ciudadId, onValueChange = { ciudadId = it }, label = { Text(stringResource(R.string.ciudad_id)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = tamano, onValueChange = { tamano = it }, label = { Text(stringResource(R.string.tamano)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = presupuesto, onValueChange = { presupuesto = it }, label = { Text(stringResource(R.string.presupuesto)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            when {
                nombre.isBlank() -> Toast.makeText(context, context.getString(R.string.invalid_nombre), Toast.LENGTH_SHORT).show()
                ciudadId.toIntOrNull() == null -> Toast.makeText(context, context.getString(R.string.invalid_ciudad_id), Toast.LENGTH_SHORT).show()
                tamano.isNotBlank() && tamano.toIntOrNull() == null -> Toast.makeText(context, context.getString(R.string.invalid_tamano), Toast.LENGTH_SHORT).show()
                presupuesto.isNotBlank() && presupuesto.toDoubleOrNull() == null -> Toast.makeText(context, context.getString(R.string.invalid_presupuesto), Toast.LENGTH_SHORT).show()
                else -> {
                    if (zooId == null) {
                        db.insertZoo(nombre, ciudadId.toInt(), tamano.toIntOrNull(), presupuesto.toDoubleOrNull())
                        Toast.makeText(context, context.getString(R.string.zoo_saved), Toast.LENGTH_SHORT).show()
                    } else {
                        db.updateZoo(zooId, nombre, ciudadId.toInt(), tamano.toIntOrNull(), presupuesto.toDoubleOrNull())
                        Toast.makeText(context, context.getString(R.string.zoo_updated), Toast.LENGTH_SHORT).show()
                    }
                    onFinish()
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.save))
        }
    }
}

@Preview
@Composable
fun ZooEditPreview() {
    val context = LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        ZooEditScreen(db, null)
    }
}
