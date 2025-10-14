package com.triple7.healthshield254.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.triple7.healthshield254.R
import com.triple7.healthshield254.navigation.ROUT_ONBOARDING2
import com.triple7.healthshield254.navigation.ROUT_REGISTER
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Onboarding1(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            //Icon at top right
            Image(
                painter = painterResource(id = R.drawable.medicalinsurance), // Replace with your drawable
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
                        .size(350.dp)
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

                // --- Carousel Cards ---
                CarouselTextCardsTripleSeven()

                Spacer(modifier = Modifier.height(24.dp))

                // Pager Dots
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

                Spacer(modifier = Modifier.height(40.dp))

                // Get Started Button
                Button(
                    onClick = { navController.navigate(ROUT_REGISTER) },
                    colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
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
fun CarouselTextCardsTripleSeven() {
    val texts = listOf(
        "Digital Shield Against Counterfeit Medicines.",
        "Scan, Verify, and Protect Your Health With Us",
        "AI-Powered Authenticity Checks."
    )

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentIndex = (currentIndex + 1) % texts.size
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(texts.size) { index ->
                val text = texts[index]
                Card(
                    modifier = Modifier
                        .width(340.dp)
                        .height(180.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6A00)) // Triple Seven
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFF6A00))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = text,
                            fontSize = 22.sp,
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
fun PreviewOnboarding1() {
    Onboarding1(navController = rememberNavController())
}