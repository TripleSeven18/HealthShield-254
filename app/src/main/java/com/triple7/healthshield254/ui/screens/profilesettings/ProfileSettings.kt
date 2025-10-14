package com.triple7.healthshield254.ui.screens.profilesettings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleS
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
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = tripleSeven,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .background(Color(0xFFF9FAFB))
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Profile Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = tripleS),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://cdn-icons-png.flaticon.com/512/3135/3135715.png"),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Juma Jumaa", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                    Text("jumaa.juma@email.com", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { /* Edit Profile */ },
                        colors = ButtonDefaults.buttonColors(containerColor = triple777),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Edit Profile", color = Color.White)
                    }
                }
            }

            // --- Settings Section ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    SettingItem(
                        icon = Icons.Default.Info, //Language icon
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
                    Divider()
                    SettingToggle(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    Divider()
                    SettingToggle(
                        icon = Icons.Default.Info, //CloudDownload icon
                        title = "Offline Cache",
                        checked = offlineCacheEnabled,
                        onCheckedChange = { offlineCacheEnabled = it }
                    )
                    Divider()
                    SettingToggle(
                        icon = Icons.Default.Lock,
                        title = "Privacy Mode",
                        checked = privacyAccepted,
                        onCheckedChange = { privacyAccepted = it }
                    )
                }
            }

            // --- Promo Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFA726)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Refer a Friend 🎉", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Invite and earn rewards", color = Color.White.copy(alpha = 0.8f))
                    }
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Logout Button ---
            Button(
                onClick = { /* Handle logout */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = triple777)
            ) {
                Text("Log Out", color = Color.White)
            }

            Spacer(Modifier.height(10.dp))
            Text("Version 1.0.0", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
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
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = triple777)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium)
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
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = triple777)
            Spacer(Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = triple777))
    }
}

@Composable
fun DropdownMenuSelector(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected, color = triple777)
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

@Preview(showBackground = true)
@Composable
fun ProfileSettingsScreenPreview() {
    ProfileSettingsScreen(rememberNavController())
}