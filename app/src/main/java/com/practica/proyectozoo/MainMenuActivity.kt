package com.practica.proyectozoo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.practica.proyectozoo.data.DatabaseHelper
import com.practica.proyectozoo.data.MenuItemData
import com.practica.proyectozoo.ui.components.MenuCard
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

// Importa aquí tus pantallas:
import com.practica.proyectozoo.UserListActivity
import com.practica.proyectozoo.ZooListActivity
import com.practica.proyectozoo.EspecieListActivity
import com.practica.proyectozoo.EspecieEditActivity
import com.practica.proyectozoo.UserReportActivity

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Inicializa DB y recupera perfil
        val db       = DatabaseHelper(this)
        val prefs    = getSharedPreferences("session", Context.MODE_PRIVATE)
        val user     = prefs.getString("username", "") ?: ""
        val pass     = prefs.getString("password", "") ?: ""
        val perfilId = db.getPerfilId(user, pass) ?: 2
        val isAdmin  = perfilId == 1

        setContent {
            ProyectozooTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = Color(0xFFF8FAFC)
                ) {
                    MainMenuScreen(isAdmin = isAdmin)
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(isAdmin: Boolean) {
    val ctx = LocalContext.current

    val menuItems = buildList<MenuItemData> {
        // — Solo ADMIN ve Usuarios —
        if (isAdmin) {
            add(
                MenuItemData(
                    title           = "Usuarios",
                    description     = "Gestionar usuarios y permisos",
                    icon            = Icons.Default.Person,
                    backgroundColor = Color(0xFFDCFCE7),
                    iconColor       = Color(0xFF22C55E),
                    onClick         = {
                        ctx.startActivity(Intent(ctx, UserListActivity::class.java))
                    }
                )
            )
        }

        // — Especies: listado para admin, alta para usuario —
        add(
            MenuItemData(
                title           = "Especies",
                description     = if (isAdmin) "Ver especies registradas" else "Agregar nueva especie",
                icon            = Icons.Default.Eco,
                backgroundColor = Color(0xFFFEF3C7),
                iconColor       = Color(0xFFF59E0B),
                onClick         = {
                    val dest = if (isAdmin)
                        EspecieListActivity::class.java
                    else
                        EspecieEditActivity::class.java
                    ctx.startActivity(Intent(ctx, dest))
                }
            )
        )

        // — Zoos: listado para admin, alta para usuario —
        add(
            MenuItemData(
                title           = "Zoos",
                description     = if (isAdmin) "Administrar zoológicos registrados" else "Agregar nuevo zoológico",
                icon            = Icons.Default.Home,
                backgroundColor = Color(0xFFDBEAFE),
                iconColor       = Color(0xFF3B82F6),
                onClick         = {
                    val dest = if (isAdmin)
                        ZooListActivity::class.java
                    else
                        ZooEditActivity::class.java
                    ctx.startActivity(Intent(ctx, dest))
                }
            )
        )

        // — Animales: listado para admin, alta para usuario —
        add(
            MenuItemData(
                title           = "Animales",
                description     = if (isAdmin) "Registro y seguimiento de animales" else "Agregar nuevo animal",
                icon            = Icons.Default.Pets,
                backgroundColor = Color(0xFFFCE7F3),
                iconColor       = Color(0xFFEC4899),
                onClick         = {
                    val dest = if (isAdmin)
                        AnimalListActivity::class.java
                    else
                        AnimalEditActivity::class.java
                    ctx.startActivity(Intent(ctx, dest))
                }
            )
        )

        // — Solo ADMIN ve Reportes —
        if (isAdmin) {
            add(
                MenuItemData(
                    title           = "Reportes",
                    description     = "Generar informes",
                    icon            = Icons.Default.Assessment,
                    backgroundColor = Color(0xFFE0E7FF),
                    iconColor       = Color(0xFF6366F1),
                    onClick         = {
                        ctx.startActivity(Intent(ctx, UserReportActivity::class.java))
                    }
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = "ProyectoZoo",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF0F172A)
        )
        Text(
            text  = "Sistema de gestión para zoológicos",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text     = "Selecciona una opción",
            style    = MaterialTheme.typography.titleMedium,
            color    = Color(0xFF475569),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        menuItems.forEach { item ->
            MenuCard(item = item)
        }

        Spacer(Modifier.weight(1f))

        Text(
            text  = "v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
        )
    }
}
