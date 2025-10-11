package com.triple7.healthshield254.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.ui.screens.auth.LoginScreen
import com.triple7.healthshield254.ui.screens.auth.RegistrationScreen
import com.triple7.healthshield254.ui.screens.crowdsourcinghub.CrowdsourcingHubScreen
import com.triple7.healthshield254.ui.screens.educationalhub.EducationalHubScreen
import com.triple7.healthshield254.ui.screens.home.HomeScreen
import com.triple7.healthshield254.ui.screens.hotspotmap.HotspotMapScreen
import com.triple7.healthshield254.ui.screens.onboarding.Onboarding1
import com.triple7.healthshield254.ui.screens.onboarding.Onboarding2
import com.triple7.healthshield254.ui.screens.profilesettings.ProfileSettingsScreen
import com.triple7.healthshield254.ui.screens.reportmedicine.MedicineScreen
import com.triple7.healthshield254.ui.screens.reportmedicine.ReportScreen
import com.triple7.healthshield254.ui.screens.reportmedicine.ViewReport
import com.triple7.healthshield254.ui.screens.splash.SplashScreen
import com.triple7.healthshield254.ui.screens.verificationrecords.ScanHistoryScreen
import com.triple7.healthshield254.ui.screens.verify.ScanVerifyScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUT_SPLASH) {
            SplashScreen(navController)
        }
        composable(ROUT_HOME) {
            HomeScreen(navController,)
        }
        composable(ROUT_SCANVERIFY) {
            ScanVerifyScreen(navController)
        }
        composable(ROUT_REPORTMEDICINE) {
            ScanVerifyScreen(navController)
        }
        composable(ROUT_CROWDSOURCING) {
            CrowdsourcingHubScreen(navController)
        }
        composable(ROUT_EDUCATIONALHUB) {
            EducationalHubScreen(navController)
        }
        composable(ROUT_HOTSPOTMAP) {
            HotspotMapScreen(navController)
        }
        composable(ROUT_PROFILESETTINS) {
            ProfileSettingsScreen(navController)
        }
        composable(ROUT_VERIFICATIONRECORDS) {
            ScanHistoryScreen(navController)
        }
        composable(ROUT_REGISTER) {
            RegistrationScreen(navController)
        }
        composable(ROUT_LOGIN) {
            LoginScreen(navController)
        }

        composable(ROUT_MEDICINE) {
            MedicineScreen(navController)
        }



        composable(ROUT_SENDREPORT) {
            ReportScreen(navController)
        }

        composable(ROUT_VIEWREPORT) {
            ViewReport(navController)
        }

        composable(ROUT_ONBOARDING1) {
            Onboarding1(navController)
        }

        composable(ROUT_ONBOARDING2) {
            Onboarding2(navController)
        }

    }
}