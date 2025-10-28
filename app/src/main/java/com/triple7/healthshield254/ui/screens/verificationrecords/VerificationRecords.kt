package com.triple7.healthshield254.ui.screens.verificationrecords

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

/** Data model for scanned / purchased medicine record */
data class ScanRecord(
    val id: String,
    val medicineName: String,
    val batch: String,
    val status: String,
    val confidence: Int?,
    val date: Long,
    var verifiedByPharmacist: Boolean = false
)

class VerificationViewModel : ViewModel() {
    private val _records = mutableStateListOf<ScanRecord>()
    val records: List<ScanRecord> get() = _records

    init {
        _records.addAll(
            listOf(
                ScanRecord("1", "Panadol Extra", "BX1234", "Authentic", 98, System.currentTimeMillis() - 86400000L),
                ScanRecord("2", "Amoxicillin 500mg", "AMX500B", "Suspected", 72, System.currentTimeMillis() - 172800000L, verifiedByPharmacist = true),
                ScanRecord("3", "Cough Syrup X", "CSX789", "Counterfeit", 55, System.currentTimeMillis() - 259200000L),
                ScanRecord("4", "Vitamin C Tablets", "VC100", "Authentic", 96, System.currentTimeMillis() - 3600000L)
            )
        )
    }

    fun search(query: String): List<ScanRecord> {
        if (query.isBlank()) return records
        val q = query.trim().lowercase()
        return records.filter {
            it.medicineName.lowercase().contains(q) || it.batch.lowercase().contains(q) || it.id.lowercase().contains(q)
        }
    }

    fun verifyRecord(recordId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val idx = _records.indexOfFirst { it.id == recordId }
            if (idx != -1) {
                val rec = _records[idx]
                _records[idx] = rec.copy(verifiedByPharmacist = !rec.verifiedByPharmacist) // Toggle for testing
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(navController: NavController) {
    val viewModel = remember { VerificationViewModel() }
    var search by remember { mutableStateOf(TextFieldValue("")) }
    val filteredRecords = remember(search.text, viewModel.records) { viewModel.search(search.text) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Future: add new scan */ },
                containerColor = tripleSeven
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                tripleSeven.copy(alpha = 0.1f),
                                tripleSeven
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Scan History & Verification",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search medicine, batch or ID") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    .background(Color.White, RoundedCornerShape(24.dp)),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tripleSeven,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = tripleSeven
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records found",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
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
}

@Composable
fun ScanRecordCard(record: ScanRecord, onVerify: (String) -> Unit) {
    val (statusColor, icon) = when (record.status) {
        "Authentic" -> Pair(Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "Suspected" -> Pair(Color(0xFFF9A825), Icons.Default.Warning)
        "Counterfeit" -> Pair(Color(0xFFD32F2F), Icons.Default.Block)
        else -> Pair(Color.Gray, Icons.Default.Info)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onVerify(record.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tripleSeven),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(tripleSeven.copy(alpha = 0.15f), CircleShape)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = record.status,
                    tint = statusColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = record.medicineName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )

            Text(
                text = "Batch: ${record.batch}",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = record.status,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            
            if (record.verifiedByPharmacist) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = "Verified by Pharmacist",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Verified by Pharmacist",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanHistoryPreview() {
    ScanHistoryScreen(rememberNavController())
}
