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
import com.practica.proyectozoo.data.Especie
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class EspecieEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHelper(this)
        val espId = intent.getIntExtra("especieId", -1).takeIf { it != -1 }
        setContent {
            ProyectozooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    EspecieEditScreen(db, espId, Modifier.padding(padding)) { finish() }
                }
            }
        }
    }
}

@Composable
fun EspecieEditScreen(db: DatabaseHelper, especieId: Int?, modifier: Modifier = Modifier, onFinish: () -> Unit = {}) {
    var nombreVulgar by remember { mutableStateOf("") }
    var nombreCientifico by remember { mutableStateOf("") }
    var familia by remember { mutableStateOf("") }
    var enPeligro by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(especieId) {
        especieId?.let {
            db.getEspecie(it)?.let { e ->
                nombreVulgar = e.nombreVulgar
                nombreCientifico = e.nombreCientifico
                familia = e.familia ?: ""
                enPeligro = e.enPeligro
            }
        }
    }

    Column(modifier.padding(16.dp)) {
        OutlinedTextField(value = nombreVulgar, onValueChange = { nombreVulgar = it }, label = { Text(stringResource(R.string.nombre_vulgar)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = nombreCientifico, onValueChange = { nombreCientifico = it }, label = { Text(stringResource(R.string.nombre_cientifico)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = familia, onValueChange = { familia = it }, label = { Text(stringResource(R.string.familia)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.Checkbox(checked = enPeligro, onCheckedChange = { enPeligro = it })
        Text(text = stringResource(R.string.en_peligro))
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            when {
                nombreVulgar.isBlank() -> Toast.makeText(context, context.getString(R.string.invalid_nombre_vulgar), Toast.LENGTH_SHORT).show()
                nombreCientifico.isBlank() -> Toast.makeText(context, context.getString(R.string.invalid_nombre_cientifico), Toast.LENGTH_SHORT).show()
                else -> {
                    if (especieId == null) {
                        db.insertEspecie(nombreVulgar, nombreCientifico, if (familia.isBlank()) null else familia, enPeligro)
                        Toast.makeText(context, context.getString(R.string.especie_saved), Toast.LENGTH_SHORT).show()
                    } else {
                        db.updateEspecie(especieId, nombreVulgar, nombreCientifico, if (familia.isBlank()) null else familia, enPeligro)
                        Toast.makeText(context, context.getString(R.string.especie_updated), Toast.LENGTH_SHORT).show()
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
fun EspecieEditPreview() {
    val context = LocalContext.current
    val db = DatabaseHelper(context)
    ProyectozooTheme {
        EspecieEditScreen(db, null)
    }
}
