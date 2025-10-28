package com.triple7.healthshield254.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.ui.screens.TradeCenter.AnalyticsScreen
import com.triple7.healthshield254.ui.screens.TradeCenter.SellerDashboardScreen
import com.triple7.healthshield254.ui.screens.TradeCenter.SupplierManufacturerDashboard
import com.triple7.healthshield254.ui.screens.admin.AdminScreen
import com.triple7.healthshield254.ui.screens.admin.UploadProductScreen
import com.triple7.healthshield254.ui.screens.admin.ViewOrdersScreen
import com.triple7.healthshield254.ui.screens.auth.LaunchScreen
import com.triple7.healthshield254.ui.screens.auth.LoginScreen
import com.triple7.healthshield254.ui.screens.auth.RegistrationScreen
import com.triple7.healthshield254.ui.screens.home.HomeScreen
import com.triple7.healthshield254.ui.screens.hotspotmap.HotspotMapScreen
import com.triple7.healthshield254.ui.screens.onboarding.Onboarding1
import com.triple7.healthshield254.ui.screens.onboarding.Onboarding2
import com.triple7.healthshield254.ui.screens.profilesettings.EditProfile
import com.triple7.healthshield254.ui.screens.profilesettings.ProfileSettingsScreen
import com.triple7.healthshield254.ui.screens.reportmedicine.MedicinesScreen
import com.triple7.healthshield254.ui.screens.reportmedicine.ReportScreen
import com.triple7.healthshield254.ui.screens.reportmedicine.ScanMedicine
import com.triple7.healthshield254.ui.screens.reportmedicine.ViewReport
import com.triple7.healthshield254.ui.screens.splash.SplashScreen
import com.triple7.healthshield254.ui.screens.TradeCenter.PlaceOrderScreen
import com.triple7.healthshield254.ui.screens.verificationrecords.ScanHistoryScreen
import com.triple7.healthshield254.ui.screens.verify.ScanVerifyScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH
) {
    // Hardcoded example user info, replace with your real user state
    val currentUserType = "Consumer" // or "Supplier" / "Manufacturer"
    val currentUserId = "user123"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUT_SPLASH) {
            SplashScreen(navController)
        }

        composable(ROUT_HOME) {
            HomeScreen(navController)
        }

        composable(ROUT_CHATBOARDSCREEN) {
            ChatScreen(navController, currentUserId = "user1")
        }

        composable(ROUT_ANALYTICSSCREEN) {
            AnalyticsScreen(navController)
        }


        composable(ROUT_SCANVERIFY) {
            ScanVerifyScreen(navController)
        }

        composable(ROUT_REPORTMEDICINE) {
            ScanVerifyScreen(navController)
        }

        composable(ROUT_ADMIN) {
            AdminScreen(navController)
        }


        composable(ROUT_HOTSPOTMAP) {
            HotspotMapScreen(navController)
        }

        composable(ROUT_EDITPROFILE) {
            EditProfile(navController)
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
        composable(ROUT_SUPPLIERMANUFACTURER) {
            SupplierManufacturerDashboard(navController)
        }

        composable(ROUT_LAUNCHSCREEN) {
            LaunchScreen(navController)
        }

        composable(ROUT_SCANMEDICINE) {
            ScanMedicine(navController)
        }
        composable(ROUT_VIEWORDERS) {
            ViewOrdersScreen(navController, currentUserId = "user1234")
        }

        // --- Place Order Screen ---
        composable(ROUT_PLACEORDER) {
            PlaceOrderScreen(
                currentUserType = currentUserType,
                currentUserId = currentUserId
            )
        }


        // Medicines
        composable(ROUT_ADD_MEDICINE) {
            UploadProductScreen(navController,currentUserType = "Pharmacist", currentUserId = "previewUser")
        }
        composable(ROUT_VIEW_MEDICINES) {
            MedicinesScreen(navController)
        }
        composable(ROUTE_SELLERDASHBOARD) {
            SellerDashboardScreen(navController,"Pharmacist", "previewUser")
        }







    }
}

@Composable
fun ChatScreen(x0: NavHostController, currentUserId: String) {
    TODO("Not yet implemented")
}
