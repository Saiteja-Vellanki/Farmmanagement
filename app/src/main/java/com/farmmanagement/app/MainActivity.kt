package com.farmmanagement.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.farmmanagement.app.ui.navigation.FarmManagementNavHost
import com.farmmanagement.app.ui.theme.FarmManagementTheme

/**
 * Single-activity host for the Phase 1 flow:
 * Welcome / My Farms -> Add New Farm -> Individual Farm Dashboard.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FarmManagementTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FarmManagementNavHost()
                }
            }
        }
    }
}
