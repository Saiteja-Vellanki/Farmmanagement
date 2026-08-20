package com.farmmanagement.app.ui.addfarm

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.farmmanagement.app.FarmManagementApp
import com.farmmanagement.app.ui.theme.FarmGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFarmScreen(onSaved: (String) -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as FarmManagementApp
    val viewModel: AddFarmViewModel = viewModel(factory = AddFarmViewModel.Factory(app.container.farmRepository))
    val draft by viewModel.draft.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val saving by viewModel.saving.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- Camera capture ---
    // The target file's absolute path is already known and set into the draft at launch time
    // (see below); TakePicture writes directly into that file, so success needs no further action.
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (!success) {
            // Capture cancelled/failed — clear the optimistically-set path so we don't reference a nonexistent file.
            viewModel.update { it.copy(farmPhotoPath = null) }
            scope.launch { snackbarHostState.showSnackbar("Photo capture cancelled") }
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val (file, uri) = app.container.photoStorage.createCaptureTarget()
            pendingCaptureUri = uri
            viewModel.update { it.copy(farmPhotoPath = file.absolutePath) } // pre-set path; confirmed on success
            takePictureLauncher.launch(uri)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Camera permission denied") }
        }
    }

    // --- Gallery / Photo Picker ---
    val pickMediaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val path = app.container.photoStorage.copyIntoAppStorage(uri)
            if (path != null) viewModel.update { it.copy(farmPhotoPath = path) }
            else scope.launch { snackbarHostState.showSnackbar("Could not load selected image") }
        }
    }

    // --- Location ---
    var locating by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            locating = true
            scope.launch {
                val loc = app.container.locationHelper.getCurrentLocationOrNull()
                locating = false
                if (loc != null) {
                    viewModel.update { it.copy(latitude = loc.latitude, longitude = loc.longitude) }
                    snackbarHostState.showSnackbar("Location captured — enter address manually if needed")
                } else {
                    snackbarHostState.showSnackbar("Could not get GPS fix. Please enter address manually.")
                }
            }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Location permission denied") }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Farm") },
                navigationIcon = {
                    IconButton(onClick = onCancel) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        viewModel.save { result ->
                            when (result) {
                                is SaveResult.Success -> onSaved(result.farmId)
                                SaveResult.ValidationFailed -> scope.launch {
                                    snackbarHostState.showSnackbar("Please fix the highlighted fields")
                                }
                            }
                        }
                    },
                    enabled = !saving,
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                ) {
                    if (saving) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = androidx.compose.ui.graphics.Color.White)
                    else Text("SAVE FARM", fontWeight = FontWeight.Bold)
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                OutlinedTextField(
                    value = draft.farmName,
                    onValueChange = { v -> viewModel.update { it.copy(farmName = v) } },
                    label = { Text("Farm Name *") },
                    isError = errors.farmName != null,
                    supportingText = errors.farmName?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item { SectionLabel("Farm Address") }
            item {
                OutlinedTextField(
                    value = draft.farmAddress,
                    onValueChange = { v -> viewModel.update { it.copy(farmAddress = v) } },
                    label = { Text("Farm Address") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedButton(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                            android.content.pm.PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            locating = true
                            scope.launch {
                                val loc = app.container.locationHelper.getCurrentLocationOrNull()
                                locating = false
                                if (loc != null) {
                                    viewModel.update { it.copy(latitude = loc.latitude, longitude = loc.longitude) }
                                } else {
                                    snackbarHostState.showSnackbar("Could not get GPS fix. Please enter address manually.")
                                }
                            }
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (locating) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                    } else {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Use Current Location")
                }
            }
            if (draft.latitude != null && draft.longitude != null) {
                item {
                    Text(
                        "Captured: %.5f, %.5f".format(draft.latitude, draft.longitude),
                        style = MaterialTheme.typography.labelLarge,
                        color = FarmGreen,
                    )
                }
            }

            item { SectionLabel("Total Extent of Land") }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = draft.totalExtent,
                        onValueChange = { v -> viewModel.update { it.copy(totalExtent = v) } },
                        label = { Text("Total Extent") },
                        isError = errors.totalExtent != null,
                        supportingText = errors.totalExtent?.let { { Text(it) } },
                        modifier = Modifier.weight(1f),
                    )
                    var unitExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { unitExpanded = it },
                        modifier = Modifier.weight(1f),
                    ) {
                        OutlinedTextField(
                            value = draft.extentUnit, onValueChange = {}, readOnly = true,
                            label = { Text("Unit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                        )
                        ExposedDropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }) {
                            EXTENT_UNITS.forEach { unit ->
                                DropdownMenuItem(text = { Text(unit) }, onClick = {
                                    viewModel.update { it.copy(extentUnit = unit) }
                                    unitExpanded = false
                                })
                            }
                        }
                    }
                }
            }

            item { SectionLabel("Crops / Plants") }
            itemsIndexed(draft.crops) { index, row ->
                CropRowEditor(
                    row = row,
                    error = errors.cropRows[index],
                    onChange = { updated -> viewModel.update { d -> d.copy(crops = d.crops.toMutableList().also { it[index] = updated }) } },
                    onDelete = { viewModel.update { d -> d.copy(crops = d.crops.toMutableList().also { it.removeAt(index) }) } },
                )
            }
            item {
                OutlinedButton(onClick = { viewModel.update { it.copy(crops = it.crops + CropRow()) } }, modifier = Modifier.fillMaxWidth()) {
                    Text("+ Add Crop")
                }
            }

            item { SectionLabel("Irrigation / Motors") }
            itemsIndexed(draft.motors) { index, row ->
                MotorRowEditor(
                    row = row,
                    error = errors.motorRows[index],
                    onChange = { updated -> viewModel.update { d -> d.copy(motors = d.motors.toMutableList().also { it[index] = updated }) } },
                    onDelete = { viewModel.update { d -> d.copy(motors = d.motors.toMutableList().also { it.removeAt(index) }) } },
                )
            }
            item {
                OutlinedButton(onClick = { viewModel.update { it.copy(motors = it.motors + MotorRow()) } }, modifier = Modifier.fillMaxWidth()) {
                    Text("+ Add Motor")
                }
            }

            item { SectionLabel("Farm Photo") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (draft.farmPhotoPath != null) {
                        Box {
                            AsyncImage(
                                model = draft.farmPhotoPath,
                                contentDescription = "Farm photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)),
                            )
                            IconButton(
                                onClick = { viewModel.update { it.copy(farmPhotoPath = null) } },
                                modifier = Modifier.align(Alignment.TopEnd),
                            ) { Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = androidx.compose.ui.graphics.Color.White) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                                    android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    val (file, uri) = app.container.photoStorage.createCaptureTarget()
                                    pendingCaptureUri = uri
                                    takePictureLauncher.launch(uri)
                                    viewModel.update { it.copy(farmPhotoPath = file.absolutePath) }
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Camera")
                        }
                        OutlinedButton(
                            onClick = { pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            modifier = Modifier.weight(1f),
                        ) { Text("Gallery") }
                    }
                }
            }

            item { SectionLabel("Supervisor Details") }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = draft.noSupervisor, onCheckedChange = { v -> viewModel.update { it.copy(noSupervisor = v) } })
                    Spacer(Modifier.width(8.dp))
                    Text("No Supervisor")
                }
            }
            if (!draft.noSupervisor) {
                item {
                    OutlinedTextField(
                        value = draft.supervisorName,
                        onValueChange = { v -> viewModel.update { it.copy(supervisorName = v) } },
                        label = { Text("Supervisor Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    OutlinedTextField(
                        value = draft.supervisorPhone,
                        onValueChange = { v -> viewModel.update { it.copy(supervisorPhone = v) } },
                        label = { Text("Phone Number") },
                        isError = errors.supervisorPhone != null,
                        supportingText = errors.supervisorPhone?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    OutlinedTextField(
                        value = draft.supervisorSalary,
                        onValueChange = { v -> viewModel.update { it.copy(supervisorSalary = v) } },
                        label = { Text("Monthly Salary ₹") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun CropRowEditor(row: CropRow, error: String?, onChange: (CropRow) -> Unit, onDelete: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = row.plantName, onValueChange = { onChange(row.copy(plantName = it)) },
                label = { Text("Plant Name") }, modifier = Modifier.weight(1.4f),
            )
            OutlinedTextField(
                value = row.numberOfPlants, onValueChange = { onChange(row.copy(numberOfPlants = it)) },
                label = { Text("No. of Plants") }, modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Remove crop") }
        }
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MotorRowEditor(row: MotorRow, error: String?, onChange: (MotorRow) -> Unit, onDelete: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            var typeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }, modifier = Modifier.weight(1.4f)) {
                OutlinedTextField(
                    value = row.motorType, onValueChange = {}, readOnly = true,
                    label = { Text("Motor Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    MOTOR_TYPES.forEach { type ->
                        DropdownMenuItem(text = { Text(type) }, onClick = { onChange(row.copy(motorType = type)); typeExpanded = false })
                    }
                }
            }
            var hpExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = hpExpanded, onExpandedChange = { hpExpanded = it }, modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = row.motorHp, onValueChange = {}, readOnly = true,
                    label = { Text("HP") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hpExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = hpExpanded, onDismissRequest = { hpExpanded = false }) {
                    MOTOR_HP_OPTIONS.forEach { hp ->
                        DropdownMenuItem(text = { Text(hp) }, onClick = { onChange(row.copy(motorHp = hp)); hpExpanded = false })
                    }
                }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Remove motor") }
        }
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
    }
}
