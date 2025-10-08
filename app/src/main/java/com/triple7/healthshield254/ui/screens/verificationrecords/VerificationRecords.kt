package com.triple7.healthshield254.ui.screens.verificationrecords

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/** Data model for a scanned / purchased medicine record */
data class ScanRecord(
    val id: String,                // unique id, e.g. UUID or database key
    val medicineName: String,
    val batch: String,
    val status: String,           // "Authentic", "Suspected", "Counterfeit", or "Unverified"
    val confidence: Int?,         // AI confidence %, if applicable
    val date: Long,
    var verifiedByPharmacist: Boolean = false  // whether pharmacist has verified this
)

class VerificationViewModel : ViewModel() {
    // In real app, you'd fetch this from DB or backend
    private val _records = mutableStateListOf<ScanRecord>()
    val records: List<ScanRecord> get() = _records

    init {
        // Preload some dummy / sample data for testing
        _records.addAll(
            listOf(
                ScanRecord(
                    id = "1",
                    medicineName = "Panadol Extra",
                    batch = "BX1234",
                    status = "Authentic",
                    confidence = 98,
                    date = System.currentTimeMillis() - 86400000L
                ),
                ScanRecord(
                    id = "2",
                    medicineName = "Amoxicillin 500mg",
                    batch = "AMX500B",
                    status = "Suspected",
                    confidence = 72,
                    date = System.currentTimeMillis() - 172800000L
                ),
                ScanRecord(
                    id = "3",
                    medicineName = "Cough Syrup X",
                    batch = "CSX789",
                    status = "Counterfeit",
                    confidence = 55,
                    date = System.currentTimeMillis() - 259200000L
                ),
                ScanRecord(
                    id = "4",
                    medicineName = "Vitamin C Tablets",
                    batch = "VC100",
                    status = "Authentic",
                    confidence = 96,
                    date = System.currentTimeMillis() - 3600000L
                )
            )
        )
    }

    /** Search through records by medicine name, batch, or id */
    fun search(query: String): List<ScanRecord> {
        if (query.isBlank()) return records
        val q = query.trim().lowercase()
        return records.filter {
            it.medicineName.lowercase().contains(q) ||
                    it.batch.lowercase().contains(q) ||
                    it.id.lowercase().contains(q)
        }
    }

    /** Mark a record as verified by pharmacist */
    fun verifyRecord(recordId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val idx = _records.indexOfFirst { it.id == recordId }
            if (idx != -1) {
                val rec = _records[idx]
                _records[idx] = rec.copy(verifiedByPharmacist = true)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(
    navController: NavController,
) {


    val viewModel = remember { VerificationViewModel() }

    var search by remember { mutableStateOf(TextFieldValue("")) }

    // get filtered list from VM
    val filteredRecords = remember(search, viewModel.records) {
        viewModel.search(search.text)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan History / Verification", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = tripleSeven
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search medicine / batch / ID") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tripleSeven,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = tripleSeven
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRecords) { record ->
                    ScanRecordCard(
                        record = record,
                        onVerify = { viewModel.verifyRecord(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScanRecordCard(
    record: ScanRecord,
    onVerify: (recordId: String) -> Unit
) {
    val (statusColor, icon) = when (record.status) {
        "Authentic" -> Pair(Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "Suspected" -> Pair(Color(0xFFF9A825), Icons.Default.Warning)
        "Counterfeit" -> Pair(Color(0xFFD32F2F), Icons.Default.Warning)
        else -> Pair(Color.Gray, Icons.Default.Warning)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = tripleSeven),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = record.status,
                tint = statusColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = record.medicineName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Batch: ${record.batch}",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Status: ${record.status}" +
                            if (record.verifiedByPharmacist) " (Verified)" else "",
                    color = Color.Black,
                    fontSize = 14.sp
                )
                record.confidence?.let { conf ->
                    Text(
                        text = "AI Confidence: $conf%",
                        color = Color.Black,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = "Date: ${
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            .format(Date(record.date))
                    }",
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Show verify button if not yet verified
            if (!record.verifiedByPharmacist) {
                Text(
                    text = "Verify",
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onVerify(record.id)
                        }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified",
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanHistoryScreenPreview() {
    ScanHistoryScreen(rememberNavController())
}
