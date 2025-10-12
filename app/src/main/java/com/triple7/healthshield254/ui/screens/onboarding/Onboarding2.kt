package com.triple7.healthshield254.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.triple7.healthshield254.navigation.ROUT_HOME
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Onboarding2(navController: NavController) {

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            // --- Top-Right Icon ---
            Image(
                painter = painterResource(id = R.drawable.medicalinsurance),
                contentDescription = "Top Right Icon",
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                // Heading
                Text(
                    text = "Explore HealthShield254 Features",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lottie animation
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.spreadpositivity))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = progress,
                    modifier = Modifier
                        .size(250.dp)
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Carousel cards
                CarouselFeatureCards()

                Spacer(modifier = Modifier.height(24.dp))

                // Pager dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { index ->
                        val color = if (pagerState.currentPage == index) tripleSeven else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(12.dp)
                                .background(color, shape = CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Continue Button
                Button(
                    onClick = { navController.navigate(ROUT_HOME) },
                    colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                    shape = CircleShape,
                    modifier = Modifier
                        .height(55.dp)
                        .width(250.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Get Started", textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CarouselFeatureCards() {
    val features = listOf(
        Triple(R.drawable.medicalinsurance, "AI Medicine Verification", "Scan medicine QR codes instantly and verify authenticity."),
        Triple(R.drawable.nurse, "Trusted Health Insights", "Access accurate drug info and reports for confident decisions."),
        Triple(R.drawable.medicalinsurance, "Personal Health Dashboard", "Track scans, alerts, and verification results securely.")
    )

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentIndex = (currentIndex + 1) % features.size
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(features.size) { index ->
                val (imageRes, title, desc) = features[index]
                Card(
                    modifier = Modifier
                        .width(340.dp)
                        .height(180.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6A00)) // Triple Seven
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboarding2() {
    Onboarding2(navController = rememberNavController())
}