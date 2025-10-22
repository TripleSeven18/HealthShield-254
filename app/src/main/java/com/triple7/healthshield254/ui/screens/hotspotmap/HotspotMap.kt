package com.triple7.healthshield254.ui.screens.hotspotmap

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.background
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.triple7.healthshield254.ui.screens.home.HomeScreen
import com.triple7.healthshield254.ui.theme.HealthShield254Theme
import com.triple7.healthshield254.ui.theme.tripleSeven

// --- Data Model ---
data class HotspotLocation(
    val name: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val intensity: Double = 1.0
)

// --- Preview Data ---
val previewHotspots = listOf(
    HotspotLocation("Nairobi", -1.286389, 36.817223),
    HotspotLocation("Mombasa", -4.043477, 39.668205),
    HotspotLocation("Kisumu", -0.091702, 34.767956),
    HotspotLocation("Nakuru", -0.303099, 36.080025),
    HotspotLocation("Eldoret", 0.5143, 35.2698),
    HotspotLocation("Malindi", -3.2199, 40.1164)
)

// --- Firebase Seeder ---
fun seedHotspotDataToFirebase() {
    val dbRef = FirebaseDatabase.getInstance().getReference("hotspots")
    previewHotspots.forEach { dbRef.child(it.name).setValue(it) }
}

// --- Application class ---
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        seedHotspotDataToFirebase()
    }
}

// --- Hotspot Map Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotMapScreen(navController: NavController) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current

    var hotspots by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var locationCards by remember { mutableStateOf<List<HotspotLocation>>(emptyList()) }

    // --- Firebase Real-Time Updates with auto-refresh ---
    LaunchedEffect(Unit) {
        if (!inPreview) {
            val dbRef = FirebaseDatabase.getInstance().getReference("hotspots")
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val locations = snapshot.children.mapNotNull {
                        it.getValue(HotspotLocation::class.java)
                    }
                    locationCards = locations
                    hotspots = locations.map { LatLng(it.lat, it.lng) }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            locationCards = previewHotspots
            hotspots = previewHotspots.map { LatLng(it.lat, it.lng) }
        }
    }

    Scaffold(containerColor = Color(0xFFF8F8F8)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(tripleSeven.copy(alpha = 0.1f), Color.Transparent)))
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            ) {
                if (inPreview) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Map Preview", color = Color.White)
                    }
                } else {
                    HeatmapGoogleMap(context = context, points = hotspots)
                }
            }

            Spacer(Modifier.height(16.dp))

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
                    LocationCard(location)
                }
            }
        }
    }
}

@Composable
fun HeatmapGoogleMap(context: Context, points: List<LatLng>) {
    val mapView = remember { MapView(context) }
    val lifecycleOwner = LocalContext.current as? androidx.lifecycle.LifecycleOwner

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner?.lifecycle?.addObserver(lifecycleObserver)
        onDispose { lifecycleOwner?.lifecycle?.removeObserver(lifecycleObserver) }
    }

    AndroidView(factory = {
        mapView.apply {
            getMapAsync { map ->
                map.uiSettings.isZoomControlsEnabled = true
                map.isBuildingsEnabled = true
                map.isTrafficEnabled = true
                map.isIndoorEnabled = true
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-1.286389, 36.817223), 6f))

                if (points.isNotEmpty()) {
                    val provider = HeatmapTileProvider.Builder()
                        .data(points)
                        .radius(50)
                        .build()
                    map.addTileOverlay(com.google.android.gms.maps.model.TileOverlayOptions().tileProvider(provider))
                }
            }
        }
    }, update = {
        it.getMapAsync { map ->
            map.clear()
            if (points.isNotEmpty()) {
                val provider = HeatmapTileProvider.Builder()
                    .data(points)
                    .radius(50)
                    .build()
                map.addTileOverlay(com.google.android.gms.maps.model.TileOverlayOptions().tileProvider(provider))
            }
        }
    })
}

@Composable
fun LocationCard(location: HotspotLocation) {
    Card(
        modifier = Modifier.width(150.dp).height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tripleSeven.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(location.name, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHotspotMapScreen() {
    HealthShield254Theme {
        HotspotMapScreen(navController = rememberNavController())
    }
}