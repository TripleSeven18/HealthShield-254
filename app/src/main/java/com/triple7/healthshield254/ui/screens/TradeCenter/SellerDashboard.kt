package com.triple7.healthshield254.ui.screens.TradeCenter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.triple7.healthshield254.navigation.ROUT_ADD_MEDICINE
import com.triple7.healthshield254.navigation.ROUT_VIEWORDERS
import com.triple7.healthshield254.ui.theme.tripleSeven
import com.triple7.healthshield254.ui.theme.tripleseven

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    navController: NavController,
    currentUserType: String,
    currentUserId: String
) {
    val isPreview = LocalInspectionMode.current

    val user = if (isPreview) null else FirebaseAuth.getInstance().currentUser
    val userName = remember(user) {
        user?.displayName?.takeIf { it.isNotBlank() }
            ?: user?.email?.split('@')?.get(0)?.replaceFirstChar { it.uppercase() }
            ?: "Seller"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seller Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tripleSeven)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Greeting
            Text(
                text = "Welcome, $userName!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage your products and orders from here.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            // Upload New Product Card
            DashboardItemCard(
                title = "Upload New Product",
                description = "Add a new item to the marketplace for others to order.",
                onClick = { navController.navigate(ROUT_ADD_MEDICINE) },
                backgroundColor = tripleseven
            )

            Spacer(Modifier.height(16.dp))

            // View Incoming Orders Card
            DashboardItemCard(
                title = "View Incoming Orders",
                description = "View and manage the orders you have received from buyers.",
                onClick = { navController.navigate(ROUT_VIEWORDERS) },
                backgroundColor = tripleseven
            )
        }
    }
}

@Composable
fun DashboardItemCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text(description, fontSize = 14.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = tripleSeven)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SellerDashboardScreenPreview() {
    SellerDashboardScreen(rememberNavController(), "Pharmacist", "previewUser")
}
