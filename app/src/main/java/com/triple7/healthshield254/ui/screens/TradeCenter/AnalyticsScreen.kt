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
import com.triple7.healthshield254.ui.theme.tripleSeven
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

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

    protected val _summary = mutableStateOf(AnalyticsSummary())
    val summary: State<AnalyticsSummary> get() = _summary

    private var ordersRef: DatabaseReference? = null
    private var reportsRef: DatabaseReference? = null
    private var fakeRef: DatabaseReference? = null

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

        override fun onCancelled(error: DatabaseError) {}
    }

    private val reportsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            _summary.value = _summary.value.copy(
                totalReports = snapshot.childrenCount.toInt(),
                lastUpdated = System.currentTimeMillis()
            )
        }

        override fun onCancelled(error: DatabaseError) {}
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

        override fun onCancelled(error: DatabaseError) {}
    }

    fun startListening() {
        if (ordersRef != null || reportsRef != null || fakeRef != null) return
        try {
            val db = FirebaseDatabase.getInstance()
            ordersRef = db.getReference("orders")
            reportsRef = db.getReference("reports")
            fakeRef = db.getReference("FakeMedicineReports")
            ordersRef?.addValueEventListener(ordersListener)
            reportsRef?.addValueEventListener(reportsListener)
            fakeRef?.addValueEventListener(fakeListener)
        } catch (_: Exception) {}
    }

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
fun AnalyticsScreen(title: String, value: String, color: Color = tripleSeven) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            Text(value, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun SimpleProgress(label: String, value: Int, maxValue: Int, color: Color) {
    val fraction = if (maxValue > 0) value.toFloat() / maxValue else 0f
    val animatedFraction by animateFloatAsState(targetValue = fraction)

    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label, fontWeight = FontWeight.Medium)
        Box(modifier = Modifier.fillMaxWidth().height(14.dp).background(Color.LightGray.copy(alpha = 0.3f))) {
            Box(modifier = Modifier.fillMaxWidth(animatedFraction).height(14.dp).background(color))
        }
        Text("$value", fontSize = 12.sp, color = Color.Gray)
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

    DisposableEffect(viewModel) {
        if (!isPreview) viewModel.startListening()
        onDispose { if (!isPreview) viewModel.stopListening() }
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Real-Time Analytics", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = tripleSeven) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, selected = false, onClick = { })
                NavigationBarItem(icon = { Icon(Icons.Default.List, contentDescription = "Reports") }, selected = false, onClick = { })
                NavigationBarItem(icon = { Icon(Icons.Default.Info, contentDescription = "Analytics") }, selected = true, onClick = { })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("📊 Summary Overview", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(6.dp))
                AnalyticsScreen("Total Orders", "${summary.totalOrders}")
                AnalyticsScreen("Avg. Order Price", "${summary.averagePrice.roundToInt()} USD")
                AnalyticsScreen("Total Reports", "${summary.totalReports}")
                AnalyticsScreen("Fake Distributor Reports", "${summary.fakeDistributors}")
                AnalyticsScreen("Critical Severity Cases", "${summary.criticalCases}")
                Spacer(Modifier.height(4.dp))
                Text(
                    "Last Updated: ${dateFormat.format(Date(summary.lastUpdated))}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text("📈 Data Distribution", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(6.dp))
                val max = listOf(summary.totalOrders, summary.totalReports, summary.fakeDistributors).maxOrNull() ?: 1
                SimpleProgress("Orders", summary.totalOrders, max, Color(0xFF42A5F5))
                SimpleProgress("Reports", summary.totalReports, max, Color(0xFF66BB6A))
                SimpleProgress("Fake Distributors", summary.fakeDistributors, max, Color(0xFFFF7043))
                SimpleProgress("Critical Cases", summary.criticalCases, max, Color(0xFFD32F2F))
            }
        }
    }
}

/* --------------------------------------------------------------------------
   🧪 Preview
-------------------------------------------------------------------------- */
@Preview(showBackground = true)
@Composable
fun AnalyticScreenPreview() {
    AnalyticsScreen(navController = rememberNavController())
}
