package com.triple7.healthshield254.ui.screens.verificationrecords

import androidx.compose.foundation.background
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven
import java.text.SimpleDateFormat
import java.util.*

data class ScanRecord(
    val medicineName: String,
    val status: String,           // Authentic, Suspected, Counterfeit
    val confidence: Int,          // AI confidence %
    val date: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(navController: NavHostController) {
    var search by remember { mutableStateOf(TextFieldValue("")) }

    val dummyRecords = remember {
        listOf(
            ScanRecord("Panadol Extra", "Authentic", 98, System.currentTimeMillis() - 86400000),
            ScanRecord("Amoxicillin 500mg", "Suspected", 72, System.currentTimeMillis() - 172800000),
            ScanRecord("Cough Syrup X", "Counterfeit", 55, System.currentTimeMillis() - 259200000),
            ScanRecord("Vitamin C Tablets", "Authentic", 96, System.currentTimeMillis() - 3600000)
        )
    }

    val filtered = dummyRecords.filter {
        it.medicineName.contains(search.text, ignoreCase = true) || search.text.isBlank()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan History & Records", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = triple777
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)  // White background
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search Medicine") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = triple777,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = triple777
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { record ->
                    ScanRecordCard(record)
                }
            }
        }
    }
}

@Composable
fun ScanRecordCard(record: ScanRecord) {
    val (statusColor, icon) = when (record.status) {
        "Authentic" -> Pair(Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "Suspected" -> Pair(Color(0xFFF9A825), Icons.Default.Warning)
        else -> Pair(Color(0xFFD32F2F), Icons.Default.Warning)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(tripleSeven),
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
                    text = "Status: ${record.status}",
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "AI Confidence: ${record.confidence}%",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Text(
                    text = "Date: ${
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            .format(Date(record.date))
                    }",
                    color = Color.Gray,
                    fontSize = 12.sp
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
