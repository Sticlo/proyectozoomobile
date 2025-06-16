package com.practica.proyectozoo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.practica.proyectozoo.ui.components.MenuCard
import com.practica.proyectozoo.data.MenuItemData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.proyectozoo.ui.theme.ProyectozooTheme

class UserMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectozooTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8FAFC)
                ) {
                    UserMenuScreen()
                }
            }
        }
    }
}

@Composable
fun UserMenuScreen() {
    val ctx = LocalContext.current

    val menuItems = listOf(
        MenuItemData(
            title = "Zoos",
            description = "Consultar zoológicos",
            icon = Icons.Default.Home,
            backgroundColor = Color(0xFFDBEAFE),
            iconColor = Color(0xFF3B82F6),
            onClick = { ctx.startActivity(Intent(ctx, ZooListActivity::class.java)) }
        ),
        MenuItemData(
            title = "Especies",
            description = "Ver especies registradas",
            icon = Icons.Default.Eco,
            backgroundColor = Color(0xFFFEF3C7),
            iconColor = Color(0xFFF59E0B),
            onClick = { ctx.startActivity(Intent(ctx, EspecieListActivity::class.java)) }
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
            color = Color(0xFF0F172A)
        )
        Spacer(Modifier.height(24.dp))

        menuItems.forEach { item ->
            MenuCard(item)
        }
    }
    menuItems.forEach { item ->
        MenuCard(item)
    }

}

