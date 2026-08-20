package com.farmmanagement.app.ui.myfarms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.farmmanagement.app.data.db.entity.Farm
import com.farmmanagement.app.ui.theme.FarmGreen
import com.farmmanagement.app.ui.theme.FarmGreenDark
import com.farmmanagement.app.ui.theme.FarmGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFarmsScreen(onAddFarm: () -> Unit, onOpenFarm: (String) -> Unit, onOpenReports: () -> Unit) {
    val app = LocalContext.current.applicationContext as FarmManagementApp
    val viewModel: MyFarmsViewModel = viewModel(
        factory = MyFarmsViewModel.Factory(app.container.farmRepository, app.container.userProfileRepository),
    )
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.userName.isNullOrBlank()) "Welcome 👋" else "Welcome to ${state.userName} 👋",
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FarmGreenDark, titleContentColor = Color.White),
            )
        },
        floatingActionButton = {
            if (state.farms.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onAddFarm,
                    containerColor = FarmGreen,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Farm") },
                )
            }
        },
        bottomBar = { MyFarmsBottomBar(onHome = {}, onReports = onOpenReports) },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                state.loading -> {}
                state.farms.isEmpty() -> EmptyState(onAddFarm)
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(state.farms, key = { it.farmId }) { farm ->
                            FarmCard(farm = farm, onClick = { onOpenFarm(farm.farmId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(onAddFarm: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(96.dp).clip(CircleShape).background(FarmGreenLight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Agriculture, contentDescription = null, tint = FarmGreen, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("No Farms Added Yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onAddFarm,
            colors = ButtonDefaults.buttonColors(containerColor = FarmGreen),
            shape = RoundedCornerShape(24.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Add Farm")
        }
    }
}

@Composable
private fun FarmCard(farm: Farm, onClick: () -> Unit) {
    val app = LocalContext.current.applicationContext as FarmManagementApp
    val cropCount by app.container.farmRepository.observeCropCount(farm.farmId).collectAsState(initial = 0)
    val motorCount by app.container.farmRepository.observeMotorCount(farm.farmId).collectAsState(initial = 0)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            if (farm.farmPhotoPath != null) {
                AsyncImage(
                    model = farm.farmPhotoPath,
                    contentDescription = farm.farmName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                )
            } else {
                Box(
                    Modifier.fillMaxWidth().height(140.dp).background(FarmGreenLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Agriculture, contentDescription = null, tint = FarmGreen, modifier = Modifier.size(40.dp))
                }
            }
            Column(Modifier.padding(14.dp)) {
                Text(farm.farmName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (farm.farmAddress.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(farm.farmAddress, color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatChip("${farm.totalExtent} ${farm.extentUnit}")
                    StatChip("$cropCount Crops")
                    StatChip("$motorCount Motors")
                }
            }
        }
    }
}

@Composable
private fun StatChip(text: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = FarmGreenLight) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelLarge, color = FarmGreenDark)
    }
}

@Composable
private fun MyFarmsBottomBar(onHome: () -> Unit, onReports: () -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = true, onClick = onHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
        )
        NavigationBarItem(
            selected = false, onClick = onReports,
            icon = { Icon(Icons.Outlined.Assessment, contentDescription = "Reports") },
            label = { Text("Reports") },
        )
    }
}
