package com.farmmanagement.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.farmmanagement.app.FarmManagementApp
import com.farmmanagement.app.ui.theme.FarmGreen
import com.farmmanagement.app.ui.theme.FarmGreenDark
import com.farmmanagement.app.ui.theme.FarmGreenLight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmDashboardScreen(farmId: String, onBack: () -> Unit, onOpenReports: () -> Unit) {
    val app = LocalContext.current.applicationContext as FarmManagementApp
    val viewModel: FarmDashboardViewModel = viewModel(
        factory = FarmDashboardViewModel.Factory(app.container.farmRepository, farmId),
    )
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.farm?.farmName ?: "Farm Dashboard") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true, onClick = onBack,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                )
                NavigationBarItem(
                    selected = false, onClick = onOpenReports,
                    icon = { Icon(Icons.Outlined.Assessment, contentDescription = "Reports") },
                    label = { Text("Reports") },
                )
            }
        },
    ) { padding ->
        if (state.loading || state.farm == null) {
            Box(Modifier.padding(padding).fillMaxSize())
            return@Scaffold
        }
        val farm = state.farm!!

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(padding).fillMaxSize(),
        ) {
            item(span = { GridItemSpan(2) }) {
                Column {
                    if (farm.farmPhotoPath != null) {
                        AsyncImage(
                            model = farm.farmPhotoPath,
                            contentDescription = farm.farmName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp)),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(farm.farmName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (farm.farmAddress.isNotBlank()) Text(farm.farmAddress, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    if (state.crops.isNotEmpty()) {
                        Text(
                            "Crops: " + state.crops.joinToString(" | ") { it.plantName },
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
            item(span = { GridItemSpan(2) }) {
                WeatherPlaceholder()
            }
            item(span = { GridItemSpan(2) }) {
                Text("Farm Management", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            items(MANAGEMENT_MODULES) { moduleName ->
                ManagementCard(moduleName) {
                    scope.launch { snackbarHostState.showSnackbar("$moduleName — Coming in next phase") }
                }
            }
        }
    }
}

@Composable
private fun WeatherPlaceholder() {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = FarmGreenLight)) {
        Column(Modifier.padding(14.dp)) {
            Text("Weather", fontWeight = FontWeight.Bold, color = FarmGreenDark)
            Spacer(Modifier.height(4.dp))
            Text(
                "Temperature, condition, rain, humidity and wind will appear here once weather (Phase 2+) is connected to this farm's coordinates.",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun ManagementCard(title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(80.dp).padding(12.dp), contentAlignment = androidx.compose.ui.Alignment.CenterStart) {
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }
}
