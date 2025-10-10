package com.triple7.healthshield254.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Onboarding1(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scrollState = rememberScrollState()

    Scaffold(

    ) { paddingValues ->
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
                    .size(500.dp)
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

            // Paragraph
            Text(
                text = "Your digital shield against counterfeit medicines. " +
                        "Scan, verify, and protect your health with AI-powered authenticity checks.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            )




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
                onClick = { navController.navigate(ROUT_ONBOARDING2) },
                colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                shape = CircleShape,
                modifier = Modifier
                    .height(55.dp)
                    .width(250.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = "Get Started")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Get Started",
                    textAlign = TextAlign.Center
                )
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboarding1() {
    Onboarding1(navController = rememberNavController())
}
