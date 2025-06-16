package com.practica.proyectozoo

import com.practica.proyectozoo.data.MenuItemData
import android.content.Intent
import android.os.Bundle
import com.practica.proyectozoo.ui.components.MenuCard
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectozooTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8FAFC)
                ) {
                    MainMenuScreen()
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen() {
    val ctx = LocalContext.current

    val menuItems = listOf(
        MenuItemData(
            title = "Usuarios",
            description = "Gestionar usuarios y permisos",
            icon = Icons.Default.Person,
            backgroundColor = Color(0xFFDCFCE7),
            iconColor = Color(0xFF22C55E),
            onClick = { ctx.startActivity(Intent(ctx, UserListActivity::class.java)) }
        ),
        MenuItemData(
            title = "Zoos",
            description = "Administrar zoológicos registrados",
            icon = Icons.Default.Home,
            backgroundColor = Color(0xFFDBEAFE),
            iconColor = Color(0xFF3B82F6),
            onClick = { ctx.startActivity(Intent(ctx, ZooListActivity::class.java)) }
        ),
        MenuItemData(
            title = "Especies",
            description = "Catálogo de especies animales",
            icon = Icons.Default.Eco,
            backgroundColor = Color(0xFFFEF3C7),
            iconColor = Color(0xFFF59E0B),
            onClick = { ctx.startActivity(Intent(ctx, EspecieListActivity::class.java)) }
        ),
        MenuItemData(
            title = "Animales",
            description = "Registro y seguimiento de animales",
            icon = Icons.Default.Pets,
            backgroundColor = Color(0xFFFCE7F3),
            iconColor = Color(0xFFEC4899),
            onClick = { ctx.startActivity(Intent(ctx, AnimalListActivity::class.java)) }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ProyectoZoo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Sistema de gestión para zoológicos",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Selecciona una opción",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF475569),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 12.dp)
        )

        menuItems.forEach { item ->
            MenuCard(item)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "v1.0.0",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 12.dp)
        )
    }

    menuItems.forEach { item ->
        MenuCard(item)
    }
}


data class MenuItemData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color,
    val onClick: () -> Unit
)
