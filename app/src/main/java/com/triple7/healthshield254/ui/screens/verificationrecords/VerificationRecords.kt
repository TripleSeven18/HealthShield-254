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
fun ScanHistoryScreen(rememberNavController: NavHostController) {
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
                title = { Text("Scan History & Records") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF6F7FB))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search Medicine") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

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
    val (color, icon) = when (record.status) {
        "Authentic" -> Pair(Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "Suspected" -> Pair(Color(0xFFF9A825), Icons.Default.Warning)
        else -> Pair(Color(0xFFD32F2F), Icons.Default.Warning)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = record.status, tint = color, modifier = Modifier.size(36.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(record.medicineName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Status: ${record.status}",
                    color = color,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "AI Confidence: ${record.confidence}%",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Text(
                    "Date: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(record.date))}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
@Composable
@Preview(showBackground = true)

fun ScanHistoryScreenPreview(){

    ScanHistoryScreen(rememberNavController())

}
