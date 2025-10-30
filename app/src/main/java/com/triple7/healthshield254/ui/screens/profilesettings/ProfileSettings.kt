package com.triple7.healthshield254.ui.screens.profilesettings

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.triple7.healthshield254.navigation.ROUT_EDITPROFILE
import com.triple7.healthshield254.navigation.ROUT_LOGIN
import com.triple7.healthshield254.ui.theme.HealthShield254Theme
import com.triple7.healthshield254.ui.theme.tripleSeven

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // State Management
    val userName = remember(currentUser) { currentUser?.displayName?.takeIf { it.isNotBlank() } ?: currentUser?.email?.split('@')?.get(0)?.replaceFirstChar { it.uppercase() } ?: "User" }
    val userEmail = remember(currentUser) { currentUser?.email ?: "No email" }
    val userPhotoUrl = remember(currentUser) { currentUser?.photoUrl?.toString() ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png" }

    var language by remember { mutableStateOf("English") }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val gradientBrush = Brush.verticalGradient(colors = listOf(tripleSeven.copy(alpha = 0.1f), Color.Transparent))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Profile Card ---
            ProfileCard(navController, userPhotoUrl, userName, userEmail)

            // --- Settings Section ---
            SettingsSection(
                language = language,
                notificationsEnabled = notificationsEnabled,
                isDarkMode = isDarkMode,
                onLanguageChange = { lang, code ->
                    language = lang
                    setLocale(context, code)
                },
                onNotificationsChange = { notificationsEnabled = it },
                onDarkModeChange = onDarkModeChange
            )

            // --- Promo Card ---
            PromoCard { context.shareApp() }

            Spacer(Modifier.height(16.dp))

            // --- Logout Button ---
            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate(ROUT_LOGIN) { popUpTo(0) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Out", color = Color.White)
            }

            Spacer(Modifier.height(10.dp))
            Text("Version 1.0.0", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        }
    }
}

// --- UI Components ---

@Composable
fun ProfileCard(navController: NavController, photoUrl: String, name: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tripleSeven),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(photoUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(90.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(10.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize, color = Color.White)
            Text(email, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate(ROUT_EDITPROFILE) }, // Functional Button
                colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Edit Profile", color = Color.White)
            }
        }
    }
}

@Composable
fun SettingsSection(
    language: String,
    notificationsEnabled: Boolean,
    isDarkMode: Boolean,
    onLanguageChange: (String, String) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SettingItem(
                icon = Icons.Default.Language,  //Language icon
                title = "Language",
                subtitle = language,
                trailing = {
                    LanguageSelector(selected = language, onSelect = onLanguageChange)
                }
            )
            Divider()
            SettingToggle(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsChange
            )
            Divider()
            SettingToggle(
                icon = Icons.Default.DarkMode,  //DarkMode icon
                title = "Dark Mode",
                checked = isDarkMode,
                onCheckedChange = onDarkModeChange
            )
        }
    }
}

@Composable
fun PromoCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Refer a Friend 🎉", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text("Invite and earn rewards", color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
            }
            Icon(Icons.Default.Send, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}


@Composable
fun SettingItem(icon: ImageVector, title: String, subtitle: String, trailing: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tripleSeven)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
        trailing?.invoke()
    }
}

@Composable
fun SettingToggle(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tripleSeven)
            Spacer(Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = tripleSeven))
    }
}

@Composable
fun LanguageSelector(selected: String, onSelect: (String, String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val languages = mapOf("English" to "en", "Kiswahili" to "sw", "French" to "fr")

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected, color = tripleSeven)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languages.forEach { (lang, code) ->
                DropdownMenuItem(text = { Text(lang) }, onClick = { onSelect(lang, code); expanded = false })
            }
        }
    }
}

// --- Helper Functions ---

private fun setLocale(context: Context, languageCode: String) {
    val locale = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(locale)
}

private fun Context.shareApp() {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out the HealthShield App! Your guide to authentic medicine. [Your App Link Here]")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}


@Preview(showBackground = true)
@Composable
fun ProfileSettingsScreenPreview() {
    HealthShield254Theme {
        ProfileSettingsScreen(rememberNavController(), isDarkMode = false, onDarkModeChange = {})
    }
}
