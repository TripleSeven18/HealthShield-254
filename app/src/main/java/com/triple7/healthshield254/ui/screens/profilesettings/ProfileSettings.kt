package com.triple7.healthshield254.ui.screens.profilesettings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(navController: NavController? = null) {
    var language by remember { mutableStateOf("English") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var offlineCacheEnabled by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = triple777
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(color = tripleSeven)
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Profile Info Section ---
//            Image(
//                painter = rememberAsyncImagePainter("https://cdn-icons-png.flaticon.com/512/3135/3135715.png"),
//                contentDescription = "Profile Picture",
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(CircleShape)
//            )
            Spacer(Modifier.height(8.dp))
            Text("Juma Jumaa", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
            Text("jumaa.juma@email.com", color = Color.Gray)
            Spacer(Modifier.height(24.dp))

            Divider()

            // --- Settings Section ---
            SettingItem(
                icon = Icons.Default.MoreVert,
                title = "Language",
                subtitle = language,
                trailing = {
                    DropdownMenuSelector(
                        selected = language,
                        options = listOf("English", "Kiswahili", "French"),
                        onSelect = { language = it }
                    )
                }
            )

            SettingToggle(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            SettingToggle(
                icon = Icons.Default.AddCircle,
                title = "Offline Cache",
                checked = offlineCacheEnabled,
                onCheckedChange = { offlineCacheEnabled = it }
            )

            SettingToggle(
                icon = Icons.Default.Lock,
                title = "Privacy Mode",
                checked = privacyAccepted,
                onCheckedChange = { privacyAccepted = it }
            )

            Spacer(Modifier.height(24.dp))

            // --- Logout Button ---
            Button(
                onClick = { /* Handle logout logic */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = triple777),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out", color = Color.White)
            }

            Spacer(Modifier.height(12.dp))
            Text("Version 1.0.0", color = Color.Black, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
        trailing?.invoke()
    }
}

@Composable
fun SettingToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Bold)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun DropdownMenuSelector(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)

fun ProfileSettingsScreenPreview(){

    ProfileSettingsScreen(rememberNavController())

}
