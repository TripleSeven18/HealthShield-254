package com.triple7.healthshield254.ui.screens.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// --- Color Palette Definition (Option 1: Professional & Trustworthy Blue) ---
val HealthShieldBlue = Color(0xFF007BFF) // Primary professional blue
val HealthShieldTextDark = Color(0xFF1A2E35) // Dark, readable text
val HealthShieldBackgroundLight = Color(0xFFF4F7F9) // Clean, off-white background
val HealthShieldSuccess = Color(0xFF28A745) // Green for success/positive stats
val HealthShieldWarning = Color(0xFFDC3545) // Red for critical alerts
val HealthShieldAccentTeal = Color(0xFF00BFA6) // Secondary accent for vibrancy

/* --------------------------------------------------------------------------
   📊 Data Model
-------------------------------------------------------------------------- */
data class AnalyticsSummary(
    val totalOrders: Int = 0,
    val totalReports: Int = 0,
    val fakeDistributors: Int = 0,
    val criticalCases: Int = 0,
    val averagePrice: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)

/* --------------------------------------------------------------------------
   🧠 ViewModel
-------------------------------------------------------------------------- */
class AnalyticsViewModel : ViewModel() {

    private val _summary = mutableStateOf(AnalyticsSummary())
    val summary: State<AnalyticsSummary> get() = _summary

    // Firebase database references. These will be used to attach listeners.
    private var ordersRef: DatabaseReference? = null
    private var reportsRef: DatabaseReference? = null
    private var fakeRef: DatabaseReference? = null

    // ValueEventListeners automatically trigger on data changes, enabling real-time updates.
    private val ordersListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val prices = snapshot.children.mapNotNull { it.child("price").getValue(Double::class.java) }
            val avg = if (prices.isNotEmpty()) prices.average() else 0.0
            _summary.value = _summary.value.copy(
                totalOrders = snapshot.childrenCount.toInt(),
                averagePrice = avg,
                lastUpdated = System.currentTimeMillis()
            )
        }
        override fun onCancelled(error: DatabaseError) { /* Handle error */ }
    }

    private val reportsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            _summary.value = _summary.value.copy(
                totalReports = snapshot.childrenCount.toInt(),
                lastUpdated = System.currentTimeMillis()
            )
        }
        override fun onCancelled(error: DatabaseError) { /* Handle error */ }
    }

    private val fakeListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val critical = snapshot.children.count {
                it.child("severity").getValue(String::class.java)?.equals("Critical", true) == true
            }
            _summary.value = _summary.value.copy(
                fakeDistributors = snapshot.childrenCount.toInt(),
                criticalCases = critical,
                lastUpdated = System.currentTimeMillis()
            )
        }
        override fun onCancelled(error: DatabaseError) { /* Handle error */ }
    }

    /**
     * Initializes and attaches real-time listeners to Firebase database references.
     * This function is the core of the real-time data fetching mechanism.
     */
    fun startListening() {
        if (ordersRef != null) return // Listeners already active
        try {
            val db = FirebaseDatabase.getInstance()
            ordersRef = db.getReference("orders")
            reportsRef = db.getReference("reports")
            fakeRef = db.getReference("FakeMedicineReports")

            // Attach the listeners. Firebase will now push updates automatically.
            ordersRef?.addValueEventListener(ordersListener)
            reportsRef?.addValueEventListener(reportsListener)
            fakeRef?.addValueEventListener(fakeListener)
        } catch (e: Exception) {
            // In a production app, log this error to a crash reporting service.
        }
    }

    /**
     * Detaches the listeners to prevent memory leaks and unnecessary background processing
     * when the ViewModel is cleared.
     */
    fun stopListening() {
        ordersRef?.removeEventListener(ordersListener)
        reportsRef?.removeEventListener(reportsListener)
        fakeRef?.removeEventListener(fakeListener)
        ordersRef = null
        reportsRef = null
        fakeRef = null
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}

/* --------------------------------------------------------------------------
   🧱 UI Components
-------------------------------------------------------------------------- */
@Composable
fun SummaryStatCard(title: String, value: String, valueColor: Color = HealthShieldTextDark) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.Medium, color = HealthShieldTextDark.copy(alpha = 0.7f))
            Text(value, fontWeight = FontWeight.Bold, color = valueColor, fontSize = 18.sp)
        }
    }
}

@Composable
fun ProgressBarStat(label: String, value: Int, maxValue: Int, color: Color) {
    val fraction = if (maxValue > 0) value.toFloat() / maxValue else 0f
    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "")

    Column(Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontWeight = FontWeight.Medium, color = HealthShieldTextDark)
            Text("$value", fontSize = 14.sp, color = HealthShieldTextDark, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = animatedFraction,
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = color,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

/* --------------------------------------------------------------------------
   🧩 Analytics Screen
-------------------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = viewModel()) {
    val summary by viewModel.summary
    val isPreview = LocalInspectionMode.current

    // This DisposableEffect correctly manages the ViewModel's listeners,
    // starting them when the screen is displayed and stopping them when it's left.
    DisposableEffect(viewModel, isPreview) {
        if (!isPreview) {
            viewModel.startListening()
        }
        onDispose {
            if (!isPreview) {
                viewModel.stopListening()
            }
        }
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()) }

    Scaffold(
        containerColor = HealthShieldBackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Analytics Icon", tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Real-Time Analytics", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HealthShieldBlue)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = HealthShieldBlue) {
                // In a real app, these would navigate. For now, they are visual placeholders.
                NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White.copy(alpha = 0.7f)) }, selected = false, onClick = { /* navigate to home */ })
                NavigationBarItem(icon = { Icon(Icons.Default.List, contentDescription = "Reports", tint = Color.White.copy(alpha = 0.7f)) }, selected = false, onClick = { /* navigate to reports */ })
                NavigationBarItem(icon = { Icon(Icons.Default.Info, contentDescription = "Analytics", tint = Color.White) }, selected = true, onClick = { })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp).fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text("📊 Summary Overview", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = HealthShieldTextDark)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Last Updated: ${dateFormat.format(Date(summary.lastUpdated))}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }
                Spacer(Modifier.height(4.dp))
                SummaryStatCard("Total Orders", "${summary.totalOrders}")
                SummaryStatCard("Avg. Order Price", "${summary.averagePrice.roundToInt()} KES") // Adjusted currency to KES
                SummaryStatCard("Total Reports", "${summary.totalReports}")
                SummaryStatCard("Fake Distributor Reports", "${summary.fakeDistributors}", valueColor = HealthShieldAccentTeal)
                SummaryStatCard("Critical Severity Cases", "${summary.criticalCases}", valueColor = HealthShieldWarning)
            }

            item {
                Column {
                    Text("📈 Data Distribution", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = HealthShieldTextDark)
                    Spacer(Modifier.height(12.dp))
                    val maxVal = listOf(summary.totalOrders, summary.totalReports, summary.fakeDistributors, summary.criticalCases).maxOrNull()?.coerceAtLeast(1) ?: 1
                    ProgressBarStat("Orders", summary.totalOrders, maxVal, HealthShieldBlue)
                    ProgressBarStat("Reports", summary.totalReports, maxVal, HealthShieldAccentTeal)
                    ProgressBarStat("Fake Distributors", summary.fakeDistributors, maxVal, Color(0xFFFFA726)) // A friendly warning orange
                    ProgressBarStat("Critical Cases", summary.criticalCases, maxVal, HealthShieldWarning)
                }
            }
        }
    }
}

/* --------------------------------------------------------------------------
   🧪 Preview
-------------------------------------------------------------------------- */
@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun AnalyticsScreenPreview() {
    // We wrap the preview in a MaterialTheme to ensure components render correctly.
    MaterialTheme {
        AnalyticsScreen(navController = rememberNavController())
    }
}
