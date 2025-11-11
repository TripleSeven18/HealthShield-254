package com.triple7.healthshield254.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.triple7.healthshield254.R
import com.triple7.healthshield254.navigation.ROUT_REGISTER
import com.triple7.healthshield254.ui.theme.NewBlue
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Onboarding1(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            // Top-right Icon
            Image(
                painter = painterResource(id = R.drawable.medicalinsurance),
                contentDescription = "Top Right Icon",
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = tripleSeven)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                // Lottie Animation
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.medicarepositivity))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = progress,
                    modifier = Modifier
                        .size(250.dp)
                        .padding(top = 16.dp),
                    alignment = Alignment.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Heading
                Text(
                    text = "Welcome to HealthShield254",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- Responsive Horizontal Scrollable Cards Section ---
                StaticSquareCardsInsideBigCard()

                Spacer(modifier = Modifier.height(40.dp))

                // Get Started Button
                Button(
                    onClick = { navController.navigate(ROUT_REGISTER) },
                    colors = ButtonDefaults.buttonColors(containerColor = NewBlue),
                    shape = CircleShape,
                    modifier = Modifier
                        .height(55.dp)
                        .width(250.dp)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Continue")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Continue",
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StaticSquareCardsInsideBigCard() {
    val texts = listOf(
        "Digital Shield Against Counterfeit Medicines.",
        "Scan, Verify, and Protect Your Health With Us",
        "AI-Powered Authenticity Checks."
    )

    val cardColors = listOf(
        Color(0xFFFF6A00), // Orange
        Color(0xFF4A90E2), // Blue
        Color(0xFF50E3C2)  // Teal
    )

    // Get screen width
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Responsive card width: ~30% of screen width, min 100.dp, max 150.dp
    val cardWidth = (screenWidth * 0.3f).coerceIn(100.dp, 150.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(texts.size) { index ->
                    Card(
                        modifier = Modifier
                            .width(cardWidth)
                            .aspectRatio(1f), // Keep square shape
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColors[index])
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = texts[index],
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboarding1() {
    Onboarding1(navController = rememberNavController())
}
