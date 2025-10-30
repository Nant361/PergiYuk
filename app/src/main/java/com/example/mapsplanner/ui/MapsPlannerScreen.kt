package com.example.mapsplanner.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mapsplanner.R
import com.example.mapsplanner.data.DayPlanLocation
import com.example.mapsplanner.data.RouteLeg
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.math.abs
import kotlin.math.max

@Composable
fun MapsPlannerRoute(
    modifier: Modifier = Modifier,
    viewModel: PlannerViewModel
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var locationPermissionGranted by remember {
        mutableStateOf(hasLocationPermission(context, locationPermissions))
    }
    var signInLoading by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.entries.any { it.value }
        locationPermissionGranted = granted || hasLocationPermission(context, locationPermissions)
        if (locationPermissionGranted) {
            scope.launch {
                snackbarHostState.showSnackbar("Izin lokasi aktif.")
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Izin lokasi dibutuhkan untuk fitur ini.")
            }
        }
    }

    val signInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .build()
    }
    val googleSignInClient = remember(signInOptions) {
        GoogleSignIn.getClient(context, signInOptions)
    }

    LaunchedEffect(Unit) {
        GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
            viewModel.onUserSignedIn(account.toSignedInUser())
        }
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        signInLoading = false
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                viewModel.onUserSignedIn(account.toSignedInUser())
                scope.launch {
                    snackbarHostState.showSnackbar("Masuk sebagai ${account.displayName ?: account.email ?: "Pengguna"}")
                }
            }
        } catch (e: ApiException) {
            scope.launch {
                snackbarHostState.showSnackbar("Gagal masuk: ${e.statusCode}")
            }
        }
    }

    val handleSignOut = remember(googleSignInClient, scope, snackbarHostState) {
        {
            googleSignInClient.signOut()
                .addOnCompleteListener {
                    signInLoading = false
                    viewModel.onUserSignedOut()
                    scope.launch { snackbarHostState.showSnackbar("Berhasil keluar") }
                }
                .addOnFailureListener { error ->
                    signInLoading = false
                    scope.launch {
                        snackbarHostState.showSnackbar(error.localizedMessage ?: "Gagal keluar")
                    }
                }
        }
    }

    LaunchedEffect(uiState.signedInUser) {
        if (uiState.signedInUser == null) {
            signInLoading = false
        } else if (!locationPermissionGranted) {
            permissionLauncher.launch(locationPermissions)
        }
    }

    LaunchedEffect(uiState.signedInUser, locationPermissionGranted) {
        val user = uiState.signedInUser ?: return@LaunchedEffect
        val hasPermission = locationPermissionGranted || hasLocationPermission(context, locationPermissions)
        if (!hasPermission) {
            permissionLauncher.launch(locationPermissions)
        } else {
            signInLoading = false
            val location = try { fusedLocationClient.lastLocation.await() } catch (e: Exception) { null }
            if (location != null) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(location.toLatLng(), 13f)
                )
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (uiState.signedInUser == null) {
        SignInScreen(
            snackbarHostState = snackbarHostState,
            isLoading = signInLoading,
            onSignInClick = {
                if (!signInLoading) {
                    signInLoading = true
                    signInLauncher.launch(googleSignInClient.signInIntent)
                }
            }
        )
    } else {
        MapsPlannerScreen(
            modifier = modifier,
            state = uiState,
            snackbarHostState = snackbarHostState,
            onPromptChange = viewModel::onPromptChanged,
            onGenerate = viewModel::generatePlan,
            onReset = viewModel::reset,
            onToggleTimeline = viewModel::toggleTimeline,
            onSelectLocation = viewModel::selectLocation,
            onExportPlan = { exportPlan(context, uiState.itinerary.locations) },
            onSignOut = { handleSignOut() },
            user = uiState.signedInUser,
            hasLocationPermission = locationPermissionGranted,
            cameraPositionState = cameraPositionState
        ) {
            scope.launch {
                if (hasLocationPermission(context, locationPermissions)) {
                    locationPermissionGranted = true
                    fetchLocationAndGenerate(fusedLocationClient, viewModel, snackbarHostState)
                } else {
                    permissionLauncher.launch(locationPermissions)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapsPlannerScreen(
    modifier: Modifier,
    state: PlannerUiState,
    snackbarHostState: SnackbarHostState,
    onPromptChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onReset: () -> Unit,
    onToggleTimeline: (Boolean) -> Unit,
    onSelectLocation: (DayPlanLocation) -> Unit,
    onExportPlan: () -> Unit,
    onSignOut: () -> Unit,
    user: SignedInUser?,
    hasLocationPermission: Boolean,
    cameraPositionState: CameraPositionState,
    onUseCurrentLocation: () -> Unit
) {
    val textFieldState = remember { mutableStateOf(TextFieldValue()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(state.prompt) {
        if (textFieldState.value.text != state.prompt) {
            textFieldState.value = TextFieldValue(state.prompt)
        }
    }
    LaunchedEffect(state.itinerary.locations) {
        state.itinerary.locations.takeIf { it.isNotEmpty() }?.let { locations ->
            val builder = LatLngBounds.Builder()
            locations.forEach { builder.include(it.position.toLatLng()) }
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(builder.build(), 120))
        }
    }
    LaunchedEffect(state.selectedLocationIndex) {
        state.itinerary.locations.getOrNull(state.selectedLocationIndex)?.let { location ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location.position.toLatLng(), 13f))
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Maps Planner") },
                actions = {
                    IconButton(onClick = onExportPlan) {
                        Icon(Icons.Default.Send, contentDescription = "Ekspor")
                    }
                    user?.let { currentUser ->
                        Text(
                            text = currentUser.name,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        TextButton(onClick = onSignOut) {
                            Text(text = "Keluar")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.itinerary.locations.isNotEmpty()) {
                FloatingActionButton(onClick = { onToggleTimeline(!state.showTimeline) }) {
                    Icon(Icons.Outlined.Schedule, contentDescription = "Timeline")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = textFieldState.value,
                onValueChange = { value ->
                    textFieldState.value = value
                    onPromptChange(value.text)
                },
                placeholder = { Text(text = "Jelaskan rencana perjalananmu…") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGenerate,
                    enabled = !state.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Buat Rencana")
                }

                Button(
                    onClick = onUseCurrentLocation,
                    enabled = !state.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Lokasi Saya")
                }

                IconButton(onClick = onReset) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            ) {
                PlannerGoogleMap(
                    cameraPositionState = cameraPositionState,
                    locations = state.itinerary.locations,
                    legs = state.itinerary.legs,
                    onLocationSelected = onSelectLocation,
                    selectedIndex = state.selectedLocationIndex,
                    enableMyLocation = hasLocationPermission
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (state.itinerary.locations.isNotEmpty()) {
                LocationCarousel(
                    locations = state.itinerary.locations,
                    selectedIndex = state.selectedLocationIndex,
                    onSelectLocation = onSelectLocation
                )
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Masukkan prompt atau gunakan lokasi untuk mulai membuat itinerary harian.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (state.showTimeline && state.itinerary.locations.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { onToggleTimeline(false) },
            sheetState = sheetState
        ) {
            TimelineContent(
                locations = state.itinerary.locations,
                legs = state.itinerary.legs,
                selectedIndex = state.selectedLocationIndex,
                onSelectLocation = onSelectLocation,
                onClose = { onToggleTimeline(false) }
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun PlannerGoogleMap(
    cameraPositionState: CameraPositionState,
    locations: List<DayPlanLocation>,
    legs: List<RouteLeg>,
    onLocationSelected: (DayPlanLocation) -> Unit,
    selectedIndex: Int,
    enableMyLocation: Boolean
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = enableMyLocation),
        uiSettings = MapUiSettings(myLocationButtonEnabled = enableMyLocation, zoomControlsEnabled = false)
    ) {
        legs.forEach { leg ->
            Polyline(
                points = listOf(leg.start.toLatLng(), leg.end.toLatLng()),
                color = Color(0xFF1976D2),
                width = 6f,
                geodesic = false
            )
        }

        locations.forEachIndexed { index, location ->
            val markerState = remember(location) { MarkerState(position = location.position.toLatLng()) }
            Marker(
                state = markerState,
                title = "${location.sequence}. ${location.name}",
                snippet = "${location.time} • ${location.duration}",
                onClick = {
                    onLocationSelected(location)
                    false
                },
                icon = BitmapDescriptorFactory.defaultMarker(
                    if (index == selectedIndex) 210f else BitmapDescriptorFactory.HUE_RED
                )
            )
        }
    }
}

@Composable
private fun SignInScreen(
    snackbarHostState: SnackbarHostState,
    isLoading: Boolean,
    onSignInClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(padding)
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Maps Planner",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Masuk dengan akun Google kamu untuk membuat itinerary harian yang cerdas dan terhubung dengan Maps.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (!isLoading) onSignInClick()
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Sign in with Google",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Text(
                        text = "Kami tidak akan membagikan informasi Anda. Anda bisa keluar kapan saja di dalam aplikasi.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private sealed interface TimelineEntry {
    data class LocationItem(val location: DayPlanLocation) : TimelineEntry
    data class TransportItem(val leg: RouteLeg, val from: DayPlanLocation, val to: DayPlanLocation) : TimelineEntry
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LocationCarousel(
    locations: List<DayPlanLocation>,
    selectedIndex: Int,
    onSelectLocation: (DayPlanLocation) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = selectedIndex.coerceIn(0, max(locations.size - 1, 0)),
        pageCount = { locations.size }
    )

    LaunchedEffect(locations.size) {
        if (locations.isNotEmpty()) {
            val target = selectedIndex.coerceIn(0, locations.lastIndex)
            pagerState.scrollToPage(target)
        }
    }

    LaunchedEffect(selectedIndex, locations.size) {
        if (locations.isNotEmpty()) {
            val target = selectedIndex.coerceIn(0, locations.lastIndex)
            if (pagerState.currentPage != target) {
                pagerState.animateScrollToPage(target)
            }
        }
    }

    LaunchedEffect(pagerState, locations) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .filter { it in locations.indices }
            .collect { page ->
                if (page != selectedIndex) {
                    onSelectLocation(locations[page])
                }
            }
    }

    val currentPage = pagerState.currentPage

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                    }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Lokasi sebelumnya")
            }

            Text(
                text = "${currentPage + 1} dari ${locations.size}",
                style = MaterialTheme.typography.labelLarge
            )

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(locations.lastIndex))
                    }
                },
                enabled = pagerState.currentPage < locations.lastIndex
            ) {
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Lokasi selanjutnya")
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 260.dp)
        ) { page ->
            val location = locations[page]
            LocationCarouselCard(
                location = location,
                isSelected = page == selectedIndex,
                onClick = { onSelectLocation(location) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            locations.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == selectedIndex) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == selectedIndex) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
private fun LocationCarouselCard(
    location: DayPlanLocation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val gradient = remember(location.name) { Brush.verticalGradient(gradientColorsFor(location.name)) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(gradient)
            ) {
                if (location.sequence > 0) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        text = "${location.sequence}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (location.time.isNotBlank()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        text = location.time,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (location.duration.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = "${location.position.lat}, ${location.position.lng}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimelineContent(
    locations: List<DayPlanLocation>,
    legs: List<RouteLeg>,
    selectedIndex: Int,
    onSelectLocation: (DayPlanLocation) -> Unit,
    onClose: () -> Unit
) {
    val entries = remember(locations, legs) { buildTimelineEntries(locations, legs) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Timeline Harian", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onClose) { Text("Tutup") }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(entries) { entry ->
                when (entry) {
                    is TimelineEntry.LocationItem -> LocationTimelineCard(
                        location = entry.location,
                        selected = locations.indexOf(entry.location) == selectedIndex,
                        onSelectLocation = onSelectLocation
                    )

                    is TimelineEntry.TransportItem -> TransportTimelineCard(entry.leg)
                }
            }
        }
    }
}

@Composable
private fun LocationTimelineCard(
    location: DayPlanLocation,
    selected: Boolean,
    onSelectLocation: (DayPlanLocation) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onSelectLocation(location) },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "${location.sequence}. ${location.name}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = listOf(location.time, location.duration)
                    .filter { it.isNotBlank() }
                    .joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = location.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TransportTimelineCard(leg: RouteLeg) {
    val icon = transportIconFor(leg.transport)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = leg.transport.ifBlank { "Perjalanan" },
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = leg.name,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (leg.travelTime.isNotBlank()) {
                    Text(
                        text = leg.travelTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun buildTimelineEntries(
    locations: List<DayPlanLocation>,
    legs: List<RouteLeg>
): List<TimelineEntry> = buildList {
    locations.forEachIndexed { index, location ->
        add(TimelineEntry.LocationItem(location))
        val next = locations.getOrNull(index + 1)
        if (next != null) {
            findLegBetween(location, next, legs)?.let { leg ->
                add(TimelineEntry.TransportItem(leg, location, next))
            }
        }
    }
}

private fun findLegBetween(
    current: DayPlanLocation,
    next: DayPlanLocation,
    legs: List<RouteLeg>
): RouteLeg? = legs.firstOrNull { leg ->
    leg.start.isCloseTo(current.position) && leg.end.isCloseTo(next.position)
}

private fun com.example.mapsplanner.data.LatLng.isCloseTo(
    other: com.example.mapsplanner.data.LatLng,
    threshold: Double = 0.0005
): Boolean =
    abs(lat - other.lat) < threshold && abs(lng - other.lng) < threshold

private fun transportIconFor(transport: String): ImageVector {
    val lower = transport.lowercase()
    return when {
        lower.contains("walk") || lower.contains("foot") -> Icons.Filled.DirectionsWalk
        lower.contains("car") || lower.contains("drive") -> Icons.Filled.DirectionsCar
        lower.contains("bus") || lower.contains("transit") -> Icons.Filled.DirectionsBus
        lower.contains("train") || lower.contains("subway") || lower.contains("metro") -> Icons.Filled.Train
        lower.contains("bike") || lower.contains("cycle") -> Icons.Filled.DirectionsBike
        lower.contains("boat") || lower.contains("ferry") -> Icons.Filled.DirectionsBoat
        lower.contains("plane") || lower.contains("flight") -> Icons.Filled.FlightTakeoff
        else -> Icons.Filled.Timer
    }
}

private fun gradientColorsFor(name: String): List<Color> {
    val base = abs(name.hashCode()) % 360
    return listOf(
        Color.hsv(base.toFloat(), 0.55f, 0.95f),
        Color.hsv(((base + 30) % 360).toFloat(), 0.55f, 0.90f),
        Color.hsv(((base + 320) % 360).toFloat(), 0.55f, 0.85f)
    )
}

private fun exportPlan(context: Context, locations: List<DayPlanLocation>) {
    if (locations.isEmpty()) return
    val builder = buildString {
        locations.sortedBy { it.sequence }.forEach { location ->
            appendLine("${location.sequence}. ${location.name}")
            appendLine(location.description)
            appendLine("${location.time} • ${location.duration}")
            appendLine("${location.position.lat}, ${location.position.lng}")
            appendLine()
        }
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, builder)
    }
    ContextCompat.startActivity(context, Intent.createChooser(intent, "Bagikan rencana harian"), null)
}

private fun hasLocationPermission(context: Context, permissions: Array<String>): Boolean =
    permissions.any {
        ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

private suspend fun fetchLocationAndGenerate(
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: PlannerViewModel,
    snackbarHostState: SnackbarHostState
) {
    try {
        val location = fusedLocationClient.awaitCurrentLocation()
        if (location != null) {
            viewModel.generatePlanForLocation(location.latitude, location.longitude)
        } else {
            snackbarHostState.showSnackbar("Tidak dapat mendapatkan lokasi saat ini. Pastikan layanan lokasi aktif.")
        }
    } catch (_: SecurityException) {
        snackbarHostState.showSnackbar("Aplikasi membutuhkan izin lokasi untuk melanjutkan.")
    }
}

@SuppressLint("MissingPermission")
private suspend fun FusedLocationProviderClient.awaitCurrentLocation(): Location? =
    suspendCancellableCoroutine { continuation ->
        val tokenSource = CancellationTokenSource()
        continuation.invokeOnCancellation { tokenSource.cancel() }

        lastLocation
            .addOnSuccessListener { lastKnown ->
                if (lastKnown != null && continuation.isActive) {
                    continuation.resume(lastKnown)
                } else {
                    requestFreshLocation(tokenSource, continuation)
                }
            }
            .addOnFailureListener {
                if (continuation.isActive) {
                    requestFreshLocation(tokenSource, continuation)
                }
            }
    }

@SuppressLint("MissingPermission")
private fun FusedLocationProviderClient.requestFreshLocation(
    tokenSource: CancellationTokenSource,
    continuation: CancellableContinuation<Location?>
) {
    getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.token)
        .addOnSuccessListener { freshLocation ->
            if (freshLocation != null && continuation.isActive) {
                continuation.resume(freshLocation)
            } else if (continuation.isActive) {
                requestRealtimeLocation(tokenSource, continuation)
            }
        }
        .addOnFailureListener {
            if (continuation.isActive) requestRealtimeLocation(tokenSource, continuation)
        }
        .addOnCanceledListener {
            if (continuation.isActive) requestRealtimeLocation(tokenSource, continuation)
        }
}

@SuppressLint("MissingPermission")
private fun FusedLocationProviderClient.requestRealtimeLocation(
    tokenSource: CancellationTokenSource,
    continuation: CancellableContinuation<Location?>
) {
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
        .setWaitForAccurateLocation(true)
        .setMinUpdateIntervalMillis(500L)
        .setMaxUpdates(1)
        .build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            removeLocationUpdates(this)
            val location = result.locations.firstOrNull()
            if (continuation.isActive) {
                continuation.resume(location)
            }
        }
    }

    continuation.invokeOnCancellation {
        removeLocationUpdates(callback)
        tokenSource.cancel()
    }

    try {
        requestLocationUpdates(request, callback, Looper.getMainLooper())
    } catch (_: SecurityException) {
        removeLocationUpdates(callback)
        if (continuation.isActive) continuation.resume(null)
    } catch (_: Exception) {
        removeLocationUpdates(callback)
        if (continuation.isActive) continuation.resume(null)
    }
}

private fun GoogleSignInAccount.toSignedInUser(): SignedInUser = SignedInUser(
    name = displayName ?: email ?: "Pengguna",
    email = email ?: "",
    photoUrl = photoUrl?.toString()
)

private fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)

private fun com.example.mapsplanner.data.LatLng.toLatLng(): com.google.android.gms.maps.model.LatLng =
    com.google.android.gms.maps.model.LatLng(lat, lng)
