package com.triple7.healthshield254.ui.screens.hotspotmap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.WeightedLatLng
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.theme.HealthShield254Theme
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.launch

// --- Data Models ---
data class HotspotLocation(val name: String = "", val lat: Double = 0.0, val lng: Double = 0.0, val intensity: Double = 1.0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotMapScreen(navController: NavController) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    // --- State Management ---
    val user = FirebaseAuth.getInstance().currentUser
    val userName = remember(user) {
        user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email?.split('@')?.get(0)?.replaceFirstChar { it.uppercase() } ?: "User"
    }
    var hotspots by remember { mutableStateOf<List<WeightedLatLng>>(emptyList()) }
    var locationCards by remember { mutableStateOf<List<HotspotLocation>>(emptyList()) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-1.286389, 36.817223), 6f) // Default to Kenya
    }

    // --- Data Fetching & Seeding ---
    LaunchedEffect(Unit) {
        if (!inPreview) {
            seedHotspotDataToFirebase()
            val database = FirebaseDatabase.getInstance().getReference("hotspots")
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val locations = snapshot.children.mapNotNull { it.getValue(HotspotLocation::class.java) }
                    locationCards = locations
                    hotspots = locations.map { WeightedLatLng(LatLng(it.lat, it.lng), it.intensity) }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Log or handle the error appropriately
                }
            })
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(tripleSeven.copy(alpha = 0.1f), Color.Transparent)))
                .padding(padding)
        ) {
            // --- Google Map with Heatmap ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            ) {
                if (inPreview) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
                        Text("Map Placeholder for Preview", color = Color.White)
                    }
                } else {
                    val mapStyle: MapStyleOptions? = remember {
                        MapStyleOptions.loadRawResourceStyle(context, R.raw.spreadpositivity)
                    }
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(mapStyleOptions = mapStyle)
                    ) {
                        if (hotspots.isNotEmpty()) {
                            Heatmap(points = hotspots, radius = 50)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Greeting ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(text = "Good Morning,", style = MaterialTheme.typography.titleMedium)
                Text(text = userName, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(Modifier.height(16.dp))

            // --- Location Cards ---
            Text(
                text = "Counterfeit Hotspots",
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(locationCards) { location ->
                    LocationCard(location) {
                        coroutineScope.launch {
                            cameraPositionState.animate(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(LatLng(location.lat, location.lng), 12f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Heatmap(points: List<WeightedLatLng>, radius: Int) {
    TODO("Not yet implemented")
}

@Composable
fun LocationCard(location: HotspotLocation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tripleSeven.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))  //LocationCity Icon
            Spacer(Modifier.height(8.dp))
            Text(location.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun seedHotspotDataToFirebase() {
    val dbRef = FirebaseDatabase.getInstance().getReference("hotspots")
    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists()) {
                val initialHotspots = listOf(
                    HotspotLocation("Nairobi", -1.286389, 36.817223, 100.0),
                    HotspotLocation("Mombasa", -4.043477, 39.668205, 85.0),
                    HotspotLocation("Kisumu", -0.091702, 34.767956, 70.0),
                    HotspotLocation("Nakuru", -0.303099, 36.080025, 50.0),
                    HotspotLocation("Eldoret", 0.5143, 35.2698, 40.0),
                    HotspotLocation("Malindi", -3.2199, 40.1164, 25.0)
                )
                initialHotspots.forEach { dbRef.child(it.name).setValue(it) }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Log or handle the error appropriately
        }
    })
}

@Preview(showBackground = true)
@Composable
fun PreviewHotspotMapScreen() {
        HotspotMapScreen(rememberNavController())
}
