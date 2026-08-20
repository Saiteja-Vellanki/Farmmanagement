package com.farmmanagement.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.farmmanagement.app.ui.addfarm.AddFarmScreen
import com.farmmanagement.app.ui.dashboard.FarmDashboardScreen
import com.farmmanagement.app.ui.myfarms.MyFarmsScreen
import com.farmmanagement.app.ui.reports.ReportsScreen

object Routes {
    const val MY_FARMS = "my_farms"
    const val ADD_FARM = "add_farm"
    const val DASHBOARD = "dashboard/{farmId}"
    const val REPORTS = "reports"
    fun dashboard(farmId: String) = "dashboard/$farmId"
}

@Composable
fun FarmManagementNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.MY_FARMS) {
        composable(Routes.MY_FARMS) {
            MyFarmsScreen(
                onAddFarm = { navController.navigate(Routes.ADD_FARM) },
                onOpenFarm = { farmId -> navController.navigate(Routes.dashboard(farmId)) },
                onOpenReports = { navController.navigate(Routes.REPORTS) },
            )
        }
        composable(Routes.ADD_FARM) {
            AddFarmScreen(
                onSaved = { newFarmId ->
                    navController.navigate(Routes.dashboard(newFarmId)) {
                        popUpTo(Routes.MY_FARMS)
                    }
                },
                onCancel = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.DASHBOARD,
            arguments = listOf(navArgument("farmId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getString("farmId").orEmpty()
            FarmDashboardScreen(
                farmId = farmId,
                onBack = { navController.popBackStack(Routes.MY_FARMS, inclusive = false) },
                onOpenReports = { navController.navigate(Routes.REPORTS) },
            )
        }
        composable(Routes.REPORTS) {
            ReportsScreen(onBack = { navController.popBackStack() })
        }
    }
}
