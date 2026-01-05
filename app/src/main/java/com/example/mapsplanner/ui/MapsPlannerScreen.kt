package com.example.mapsplanner.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Checkbox
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.mapsplanner.R
import com.example.mapsplanner.BuildConfig
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
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.FirebaseApp
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import android.os.SystemClock
import android.app.Activity
import android.content.ContextWrapper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.view.ViewTreeObserver
import java.lang.reflect.InvocationTargetException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.libraries.navigation.R as NavR
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.NavigationView
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.RoutingOptions
import com.google.android.libraries.navigation.Waypoint
import com.google.android.libraries.navigation.environment.NavApiEnvironmentManager

@Composable
fun MapsPlannerRoute(
    modifier: Modifier = Modifier,
    viewModel: PlannerViewModel
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(Unit) { ensureFirebaseConfigured(context) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()
    val firebaseAuth = remember { Firebase.auth }
    LaunchedEffect(Unit) {
        MapsInitializer.initialize(context)
    }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var locationPermissionGranted by remember {
        mutableStateOf(hasLocationPermission(context, locationPermissions))
    }
    var signInLoading by remember { mutableStateOf(false) }
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
        firebaseAuth.currentUser?.let { user ->
            viewModel.onUserSignedIn(user.toSignedInUser())
            return@LaunchedEffect
        }
        GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
            signInLoading = true
            val signedInUser = firebaseAuthWithGoogle(account, firebaseAuth, snackbarHostState)
            signInLoading = false
            if (signedInUser != null) {
                viewModel.onUserSignedIn(signedInUser)
            }
        }
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        signInLoading = false
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        scope.launch {
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val signedInUser = firebaseAuthWithGoogle(account, firebaseAuth, snackbarHostState)
                    if (signedInUser != null) {
                        viewModel.onUserSignedIn(signedInUser)
                        val displayName = signedInUser.name.ifBlank { signedInUser.email.ifBlank { "Pengguna" } }
                        snackbarHostState.showSnackbar("Masuk sebagai $displayName")
                    } else {
                        viewModel.onUserSignedOut()
                    }
                }
            } catch (e: ApiException) {
                snackbarHostState.showSnackbar("Gagal masuk: ${e.statusCode}")
            }
        }
    }

    val handleSignOut = remember(googleSignInClient, scope, snackbarHostState) {
        {
            googleSignInClient.signOut()
                .addOnCompleteListener {
                    firebaseAuth.signOut()
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
        val currentSignedInUser = uiState.signedInUser
        if (currentSignedInUser == null) {
            signInLoading = false
        } else {
            if (firebaseAuth.currentUser == null) {
                GoogleSignIn.getLastSignedInAccount(context)?.let { acct ->
                    scope.launch {
                        signInLoading = true
                        val signedInUser = firebaseAuthWithGoogle(acct, firebaseAuth, snackbarHostState)
                        signInLoading = false
                        if (signedInUser != null) {
                            viewModel.onUserSignedIn(signedInUser)
                        } else {
                            viewModel.onUserSignedOut()
                        }
                    }
                }
            } else {
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null && firebaseUser.uid != currentSignedInUser.uid) {
                    viewModel.onUserSignedIn(firebaseUser.toSignedInUser())
                }
            }
            if (!locationPermissionGranted) {
                permissionLauncher.launch(locationPermissions)
            }
        }
    }

    LaunchedEffect(uiState.signedInUser, locationPermissionGranted) {
        val user = uiState.signedInUser ?: return@LaunchedEffect
        val hasPermission = locationPermissionGranted || hasLocationPermission(context, locationPermissions)
        if (!hasPermission) {
            permissionLauncher.launch(locationPermissions)
        } else {
            signInLoading = false
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
            onGenerate = {
                scope.launch {
                    if (uiState.prompt.isBlank()) {
                        snackbarHostState.showSnackbar("Isi prompt rencana terlebih dahulu.")
                        return@launch
                    }
                    val location = if (locationPermissionGranted) fusedLocationClient.awaitCurrentLocation() else null
                    viewModel.generatePlanWithLocation(location?.latitude, location?.longitude)
                }
            },
            onSavePlan = viewModel::saveCurrentPlan,
            onOpenSavedPlan = { id ->
                viewModel.loadSavedPlan(id)
            },
            onOpenTimelinePlan = { id ->
                viewModel.loadTimelinePlan(id)
            },
            onDeleteTimelinePlan = viewModel::deleteTimelinePlan,
            onDeleteSavedPlan = viewModel::deleteSavedPlan,
            onReset = viewModel::reset,
            onToggleTimeline = viewModel::toggleTimeline,
            onSelectLocation = viewModel::selectLocation,
            onSignOut = { handleSignOut() },
            user = uiState.signedInUser,
            hasLocationPermission = locationPermissionGranted,
            onShowCurrentLocation = {
                scope.launch {
                    if (hasLocationPermission(context, locationPermissions)) {
                        locationPermissionGranted = true
                        val location = fusedLocationClient.awaitCurrentLocation()
                        if (location != null) {
                            snackbarHostState.showSnackbar("Lokasi kamu: ${location.latitude}, ${location.longitude}")
                        } else {
                            snackbarHostState.showSnackbar("Tidak dapat menemukan lokasi kamu saat ini.")
                        }
                    } else {
                        permissionLauncher.launch(locationPermissions)
                    }
                }
            }
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
    onSavePlan: () -> Unit,
    onOpenSavedPlan: (Long) -> Unit,
    onOpenTimelinePlan: (Long) -> Unit,
    onDeleteTimelinePlan: (Long) -> Unit,
    onDeleteSavedPlan: (Long) -> Unit,
    onReset: () -> Unit,
    onToggleTimeline: (Boolean) -> Unit,
    onSelectLocation: (DayPlanLocation) -> Unit,
    onSignOut: () -> Unit,
    user: SignedInUser?,
    hasLocationPermission: Boolean,
    onShowCurrentLocation: () -> Unit,
    onUseCurrentLocation: () -> Unit
) {
    val context = LocalContext.current
    val textFieldState = remember { mutableStateOf(TextFieldValue()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    var savedExpanded by remember { mutableStateOf(false) }
    var timelineExpanded by remember { mutableStateOf(false) }
    var showProfileSavedPage by remember { mutableStateOf(false) }
    var showProfileTimelinePage by remember { mutableStateOf(false) }
    var lastPlanCount by remember { mutableIntStateOf(0) }
    var mapNavUiVisible by remember { mutableStateOf(false) }
    var pendingNavLocation by remember { mutableStateOf<DayPlanLocation?>(null) }
    LaunchedEffect(state.prompt) {
        if (textFieldState.value.text != state.prompt) {
            textFieldState.value = TextFieldValue(state.prompt)
        }
    }
    LaunchedEffect(state.itinerary.locations.size) {
        val currentCount = state.itinerary.locations.size
        if (lastPlanCount == 0 && currentCount > 0) {
            selectedTab = BottomTab.Map
        }
        lastPlanCount = currentCount
    }
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val navItemColors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            )
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == BottomTab.Home,
                    onClick = {
                        selectedTab = BottomTab.Home
                        onToggleTimeline(false)
                        savedExpanded = false
                        timelineExpanded = false
                        showProfileSavedPage = false
                        showProfileTimelinePage = false
                    },
                    colors = navItemColors,
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.Map,
                    onClick = {
                        selectedTab = BottomTab.Map
                        onToggleTimeline(false)
                        savedExpanded = false
                        timelineExpanded = false
                        showProfileSavedPage = false
                        showProfileTimelinePage = false
                    },
                    colors = navItemColors,
                    icon = { Icon(Icons.Filled.Map, contentDescription = "Map") },
                    label = { Text("Map") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.Profile,
                    onClick = {
                        selectedTab = BottomTab.Profile
                        onToggleTimeline(false)
                        savedExpanded = false
                        timelineExpanded = false
                        showProfileSavedPage = false
                        showProfileTimelinePage = false
                    },
                    colors = navItemColors,
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            BottomTab.Home -> HomeDashboard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                userName = user?.name ?: "Explorer",
                state = state,
                textFieldState = textFieldState.value,
                onPromptChange = { value ->
                    textFieldState.value = value
                    onPromptChange(value.text)
                },
                onGenerate = onGenerate,
                onUseCurrentLocation = onUseCurrentLocation,
                onReset = onReset,
                onSelectLocation = onSelectLocation,
                onOpenMap = { selectedTab = BottomTab.Map },
                onOpenLocationInMap = { loc ->
                    onSelectLocation(loc)
                    pendingNavLocation = loc
                    selectedTab = BottomTab.Map
                    onToggleTimeline(false)
                },
                onShowTimeline = { onToggleTimeline(true) },
                onOpenSaved = {
                    selectedTab = BottomTab.Profile
                    savedExpanded = true
                    timelineExpanded = false
                    showProfileSavedPage = true
                    showProfileTimelinePage = false
                },
                onOpenHistory = {
                    selectedTab = BottomTab.Profile
                    timelineExpanded = true
                    savedExpanded = false
                    showProfileTimelinePage = true
                    showProfileSavedPage = false
                }
            )

            BottomTab.Map -> MapOnlyScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                state = state,
                hasLocationPermission = hasLocationPermission,
                onSelectLocation = onSelectLocation,
                onOpenInMaps = { location -> openInGoogleMaps(context, location) },
                onToggleTimeline = onToggleTimeline,
                onShowCurrentLocation = onShowCurrentLocation,
                onBackHome = { selectedTab = BottomTab.Home },
                onReset = onReset,
                startNavLocation = pendingNavLocation,
                onConsumeStartNav = { pendingNavLocation = null },
                onNavigationUiVisibilityChanged = { mapNavUiVisible = it }
            )

            BottomTab.Profile -> ProfileScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                user = user,
                onSignOut = onSignOut,
                savedPlans = state.savedPlans,
                timelineHistory = state.timelineHistory,
                onOpenSavedPlan = { id ->
                    onOpenSavedPlan(id)
                    selectedTab = BottomTab.Map
                    onToggleTimeline(true)
                    showProfileSavedPage = false
                    showProfileTimelinePage = false
                    savedExpanded = false
                    timelineExpanded = false
                },
                onOpenTimelinePlan = { id ->
                    onOpenTimelinePlan(id)
                    selectedTab = BottomTab.Map
                    onToggleTimeline(true)
                    showProfileSavedPage = false
                    showProfileTimelinePage = false
                    savedExpanded = false
                    timelineExpanded = false
                },
                onDeleteSavedPlan = onDeleteSavedPlan,
                onDeleteTimelinePlan = onDeleteTimelinePlan,
                savedExpanded = savedExpanded,
                timelineExpanded = timelineExpanded,
                onToggleSavedExpanded = { expanded ->
                    savedExpanded = expanded
                    showProfileSavedPage = expanded
                    if (expanded) {
                        timelineExpanded = false
                        showProfileTimelinePage = false
                    }
                },
                onToggleTimelineExpanded = { expanded ->
                    timelineExpanded = expanded
                    showProfileTimelinePage = expanded
                    if (expanded) {
                        savedExpanded = false
                        showProfileSavedPage = false
                    }
                },
                showSavedPage = showProfileSavedPage,
                showTimelinePage = showProfileTimelinePage,
                onBackToProfileMain = {
                    showProfileSavedPage = false
                    showProfileTimelinePage = false
                    savedExpanded = false
                    timelineExpanded = false
                }
            )
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
                onClose = { onToggleTimeline(false) },
                onSavePlan = onSavePlan
            )
        }
    }
}

@Composable
private fun HomeDashboard(
    modifier: Modifier = Modifier,
    userName: String,
    state: PlannerUiState,
    textFieldState: TextFieldValue,
    onPromptChange: (TextFieldValue) -> Unit,
    onGenerate: () -> Unit,
    onUseCurrentLocation: () -> Unit,
    onReset: () -> Unit,
    onSelectLocation: (DayPlanLocation) -> Unit,
    onOpenMap: () -> Unit,
    onOpenLocationInMap: (DayPlanLocation) -> Unit,
    onShowTimeline: () -> Unit,
    onOpenSaved: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp)
        ) {
            item {
                PlannerGreetingHeader(
                    userName = userName,
                    savedCount = state.savedPlans.size,
                    historyCount = state.timelineHistory.size,
                    onOpenSaved = onOpenSaved,
                    onOpenHistory = onOpenHistory,
                    onOpenMap = onOpenMap
                )
            }

            item {
                PromptArea(
                    value = textFieldState,
                    onValueChange = onPromptChange,
                    onGenerate = onGenerate,
                    onUseCurrentLocation = onUseCurrentLocation,
                    isLoading = state.isLoading,
                    onReset = onReset
                )
            }

            item {
                QuickAccessRow(
                    hasPlan = state.itinerary.locations.isNotEmpty(),
                    onReset = onReset
                )
            }

            if (state.itinerary.locations.isNotEmpty()) {
                item {
                    PlanOverviewHeader(
                        locationCount = state.itinerary.locations.size,
                        legCount = state.itinerary.legs.size
                    )
                }
                items(state.itinerary.locations) { location ->
                    ItineraryPreviewCard(
                        location = location,
                        isSelected = state.itinerary.locations.indexOf(location) == state.selectedLocationIndex,
                        onSelect = {
                            onSelectLocation(location)
                        },
                        onOpenMap = { onOpenLocationInMap(location) }
                    )
                }
            } else {
                item {
                    EmptyPlanState(
                        onUseCurrentLocation = onUseCurrentLocation,
                        onGenerate = onGenerate
                    )
                }
            }
        }
    }
}

@Composable
private fun PlannerGreetingHeader(
    userName: String,
    savedCount: Int,
    historyCount: Int,
    onOpenSaved: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenMap: () -> Unit
) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xFF0B3A5D),
            Color(0xFF0F5C73),
            Color(0xFF0F8B8D)
        )
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        tonalElevation = 6.dp,
        shadowElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Halo, $userName",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Atur rute, simpan ide, lalu lihat peta di halaman khusus.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.86f)
                            )
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        IconButton(onClick = onOpenMap) {
                            Icon(
                                Icons.Filled.Map,
                                contentDescription = "Buka peta",
                                tint = Color.White
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderStat(
                        icon = Icons.Filled.BookmarkBorder,
                        label = "$savedCount tersimpan",
                        onClick = onOpenSaved
                    )
                    HeaderStat(
                        icon = Icons.Outlined.Schedule,
                        label = "$historyCount riwayat",
                        onClick = onOpenHistory
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderStat(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Text(text = label, color = Color.White, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun PromptArea(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onGenerate: () -> Unit,
    onUseCurrentLocation: () -> Unit,
    isLoading: Boolean,
    onReset: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 4.dp,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Rencanamu",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f),
                    value = value,
                    onValueChange = onValueChange,
                    leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    placeholder = { Text(text = "Ceritakan tempat, tema, dan durasi rencana...") },
                    singleLine = true
                )
                Button(
                    onClick = onGenerate,
                    enabled = !isLoading,
                    modifier = Modifier.height(56.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLoading) {
                        LoadingDots(dotColor = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Membuat")
                    } else {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Buat")
                    }
                }
            }

            PromptSuggestions(
                suggestions = listOf(
                    "Sehari keliling wisata kota tua Jakarta",
                    "Trip 3 hari eksplor pantai Bali utara",
                    "Kuliner malam wajib coba di Bandung",
                    "Hidden gem alam di Jogja dalam satu hari"
                ),
                onSelect = { suggestion ->
                    onValueChange(TextFieldValue(suggestion))
                }
            )
        }
    }
}

@Composable
private fun PromptSuggestions(
    suggestions: List<String>,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(suggestions) { suggestion ->
            AssistChip(
                onClick = { onSelect(suggestion) },
                label = { Text(suggestion, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
private fun QuickAccessRow(
    hasPlan: Boolean,
    onReset: () -> Unit
) {
    if (!hasPlan) return
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onReset)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Reset rencana",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Bersihkan hasil saat ini dan mulai baru.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PlanOverviewHeader(
    locationCount: Int,
    legCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Rangkuman rencana",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "$locationCount lokasi",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ItineraryPreviewCard(
    location: DayPlanLocation,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onOpenMap: (DayPlanLocation) -> Unit
) {
    val accent = gradientColorsFor(location.name).first()
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onSelect()
                onOpenMap(location)
            }, indication = null, interactionSource = interaction),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accent.copy(alpha = 0.16f),
                tonalElevation = 0.dp
            ) {
                Text(
                    text = location.sequence.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(54.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            )
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = if (isSelected) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            } else {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (location.time.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(location.time, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (location.duration.isNotBlank()) {
                        Text(
                            text = location.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = "${location.position.lat}, ${location.position.lng}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { onOpenMap(location) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Lihat di peta")
                }
            }
        }
    }
}

@Composable
private fun EmptyPlanState(
    onUseCurrentLocation: () -> Unit,
    onGenerate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Belum ada rencana",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Mulai dengan prompt singkat atau gunakan lokasi kamu untuk membuat itinerary otomatis.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onUseCurrentLocation,
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Smart Planner")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapOnlyScreen(
    modifier: Modifier = Modifier,
    state: PlannerUiState,
    hasLocationPermission: Boolean,
    onSelectLocation: (DayPlanLocation) -> Unit,
    onOpenInMaps: (DayPlanLocation) -> Unit,
    onToggleTimeline: (Boolean) -> Unit,
    onShowCurrentLocation: () -> Unit,
    onBackHome: () -> Unit,
    onReset: () -> Unit,
    startNavLocation: DayPlanLocation? = null,
    onConsumeStartNav: () -> Unit = {},
    onNavigationUiVisibilityChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val hasPlan = state.itinerary.locations.isNotEmpty()
    val orderedLocations = remember(state.itinerary.locations) { state.itinerary.locations.sortedBy { it.sequence } }
    var showNavPicker by remember { mutableStateOf(false) }
    var selectedNavSequence by remember(state.itinerary.locations) {
        mutableStateOf(state.itinerary.locations.firstOrNull()?.sequence)
    }
    var selectedTravelMode by remember { mutableStateOf(TravelMode.Driving) }
    var navActive by remember { mutableStateOf(false) }
    var navLoading by remember { mutableStateOf(false) }
    var navError by remember { mutableStateOf<String?>(null) }
    var navigator by remember { mutableStateOf<Navigator?>(null) }
    var navAttached by remember { mutableStateOf(false) }
    val navigationView = remember { NavigationView(context) }

    LaunchedEffect(navActive, navLoading) {
        onNavigationUiVisibilityChanged(navActive || navLoading)
    }

    DisposableEffect(navigationView, lifecycleOwner) {
        navigationView.onCreate(null)
        var destroyed = false
        val tearDown = {
            if (!destroyed) {
                runCatching { navigationView.onPause() }
                runCatching { navigationView.onStop() }
                runCatching { navigationView.onDestroy() }
                destroyed = true
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> navigationView.onStart()
                Lifecycle.Event.ON_RESUME -> navigationView.onResume()
                Lifecycle.Event.ON_PAUSE -> navigationView.onPause()
                Lifecycle.Event.ON_STOP -> navigationView.onStop()
                Lifecycle.Event.ON_DESTROY -> tearDown()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            tearDown()
        }
    }

    LaunchedEffect(navError) {
        navError?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    val startNavigation: () -> Unit = startNavigation@{
        if (!hasPlan) {
            Toast.makeText(context, "Tambah lokasi dulu untuk navigasi.", Toast.LENGTH_SHORT).show()
            return@startNavigation
        }
        if (!hasLocationPermission) {
            Toast.makeText(context, "Izin lokasi dibutuhkan untuk Navigation SDK.", Toast.LENGTH_SHORT).show()
            return@startNavigation
        }
        val chosen = orderedLocations.firstOrNull { it.sequence == selectedNavSequence }
        if (chosen == null) {
            Toast.makeText(context, "Pilih minimal satu lokasi untuk dinavigasi.", Toast.LENGTH_SHORT).show()
            return@startNavigation
        }
        val activity = context.findActivity()
        if (activity == null) {
            Toast.makeText(context, "Tidak bisa menemukan Activity untuk Navigasi.", Toast.LENGTH_SHORT).show()
            return@startNavigation
        }
        navLoading = true
        navError = null
        NavigationApi.getNavigator(activity, object : NavigationApi.NavigatorListener {
            override fun onNavigatorReady(nav: Navigator) {
                val environment = NavApiEnvironmentManager.getOrCreate(activity.application)
                navigator = nav
                val attached = if (navAttached) {
                    true
                } else {
                    val attachResult = runCatching { navigationView.attachNavigator(nav, environment) }
                    when {
                        attachResult.isSuccess -> {
                            navAttached = true
                            true
                        }
                        else -> {
                            val rawError = attachResult.exceptionOrNull()
                            val rootError = (rawError as? InvocationTargetException)?.targetException ?: rawError
                            val alreadyAttached =
                                rootError is IllegalArgumentException &&
                                    (rootError.message?.contains("Observer already added", ignoreCase = true) == true)
                            if (alreadyAttached) {
                                Log.w("NavigationSDK", "Navigator already attached, ignoring duplicate observer")
                                navAttached = true
                                true
                            } else {
                                Log.e("NavigationSDK", "attachNavigator failed", rawError)
                                navError = "Gagal mengikat navigator: ${rootError?.let { it::class.java.simpleName } ?: "Error"} ${rootError?.message ?: ""}".trim()
                                navLoading = false
                                false
                            }
                        }
                    }
                }
                if (!attached) return
                val waypoints = listOf(chosen.toWaypoint())
                val routingOptions = RoutingOptions().travelMode(selectedTravelMode.toNavTravelMode())
                nav.setDestinations(waypoints, routingOptions).setOnResultListener { status ->
                    if (status == Navigator.RouteStatus.OK) {
                        nav.startGuidance()
                        navActive = true
                    } else {
                        navError = "Status rute: $status"
                    }
                    navLoading = false
                }
            }

            override fun onError(@NavigationApi.ErrorCode errorCode: Int) {
                navError = "Navigasi gagal: $errorCode"
                navLoading = false
            }
        })
    }

    val stopNavigation = remember {
        {
            navigator?.stopGuidance()
            navigator?.clearDestinations()
            navigationView.setNavigationUiEnabled(false)
            navActive = false
            navLoading = false
        }
    }

    LaunchedEffect(startNavLocation, hasPlan, navActive, navLoading) {
        val target = startNavLocation
        if (target != null && hasPlan && !navActive && !navLoading) {
            selectedNavSequence = target.sequence
            onSelectLocation(target)
            startNavigation()
            onConsumeStartNav()
        }
    }

    val locationButtonBottomPadding = if (hasPlan) 120.dp else 150.dp
    val density = LocalDensity.current

    val compassSideMarginPx = with(density) { 16.dp.roundToPx() }
    val compassDefaultBottomMarginPx = with(density) { 120.dp.roundToPx() }
    var incidentFabBottomPadding by remember { mutableStateOf(88.dp) }

    DisposableEffect(navigationView, navActive) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            navigationView.moveCompassToBottomRight(compassSideMarginPx, compassDefaultBottomMarginPx)
            navigationView.hideDuplicateCompasses()
            val anchor = navigationView.findViewById<View>(NavR.id.nav_report_incident_fab_button)
            val anchorHeight = anchor?.let { view ->
                val measured = if (view.height > 0) view.height else view.measuredHeight
                measured.takeIf { it > 0 }
            } ?: 0
            val anchorBottomMargin = (anchor?.layoutParams as? FrameLayout.LayoutParams)?.bottomMargin ?: 0
            val paddingPx = anchorBottomMargin + anchorHeight + compassSideMarginPx
            val paddingDp = with(density) { paddingPx.toDp() }
            if (paddingDp != incidentFabBottomPadding) {
                incidentFabBottomPadding = paddingDp
            }
        }
        navigationView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            if (navigationView.viewTreeObserver.isAlive) {
                navigationView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }
    }
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { navigationView }
        )

        if (navActive || navLoading) {
            if (navLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OrbitingLoader(indicatorSize = 26.dp)
                            Text("Menyiapkan navigasi...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
        }
            if (navActive && !navLoading) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = 16.dp,
                            bottom = incidentFabBottomPadding
                        ),
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = stopNavigation)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Akhiri navigasi")
                        Text("Akhiri")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (hasPlan) {
                    MapHeaderBar(
                        onBack = onBackHome,
                        onReset = onReset
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MapStatsPill(
                        modifier = Modifier.weight(1f),
                        locationCount = state.itinerary.locations.size,
                        legCount = state.itinerary.legs.size
                    )
                    if (hasPlan) {
                        MapActionTray(
                            onOpenTimeline = { onToggleTimeline(true) }
                        )
                    }
                }
                if (hasPlan) {
                    DirectionControls(
                        selectedMode = selectedTravelMode,
                        onModeSelected = { selectedTravelMode = it },
                        onStartDirections = { showNavPicker = true }
                    )
                }
            }

            if (!hasPlan) {
                MapEmptyHint(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onBackHome = onBackHome,
                    onReset = onReset
                )
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 10.dp,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OrbitingLoader(indicatorSize = 56.dp)
                        Text(
                            text = "Menyusun itinerary...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (showNavPicker && hasPlan) {
            AlertDialog(
                onDismissRequest = { showNavPicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showNavPicker = false
                            startNavigation()
                        }
                    ) { Text("Mulai") }
                },
                dismissButton = {
                    TextButton(onClick = { showNavPicker = false }) {
                        Text("Batal")
                    }
                },
                title = { Text("Pilih tujuan") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Pilih lokasi yang ingin dikunjungi dari rangkuman.", style = MaterialTheme.typography.bodySmall)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.heightIn(max = 280.dp)
                        ) {
                            items(orderedLocations, key = { it.sequence }) { loc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedNavSequence = loc.sequence
                                        }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedNavSequence == loc.sequence,
                                        onClick = { selectedNavSequence = loc.sequence }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(loc.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        if (loc.time.isNotBlank()) {
                                            Text(loc.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun MapHeaderBar(
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Kembali")
                }
                IconButton(onClick = onReset) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Reset")
                }
            }
        }
    }
}

@Composable
private fun MapStatsPill(
    modifier: Modifier = Modifier,
    locationCount: Int,
    legCount: Int
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Text(
                text = "$locationCount lokasi",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun MapActionTray(
    onOpenTimeline: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssistChip(
                onClick = onOpenTimeline,
                label = { Text("Timeline") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = "Timeline",
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

private enum class TravelMode(
    val label: String,
    val queryParam: String,
    val routeMode: String,
    val icon: ImageVector
) {
    Driving("Mobil", "driving", "DRIVE", Icons.Filled.DirectionsCar),
    Motorcycle("Motor", "two_wheeler", "TWO_WHEELER", Icons.Filled.TwoWheeler),
    Walking("Jalan kaki", "walking", "WALK", Icons.Filled.DirectionsWalk)
}

@Composable
private fun DirectionControls(
    selectedMode: TravelMode,
    onModeSelected: (TravelMode) -> Unit,
    onStartDirections: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        shadowElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Mulai perjalanan",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            TravelModeSelector(
                selectedMode = selectedMode,
                onModeSelected = onModeSelected
            )
            Button(
                onClick = onStartDirections,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(selectedMode.icon, contentDescription = selectedMode.label)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mulai perjalanan")
            }
        }
    }
}

@Composable
private fun TravelModeSelector(
    selectedMode: TravelMode,
    onModeSelected: (TravelMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Mode perjalanan",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(TravelMode.values()) { mode ->
                AssistChip(
                    onClick = { onModeSelected(mode) },
                    label = { Text(mode.label) },
                    leadingIcon = {
                        Icon(
                            imageVector = mode.icon,
                            contentDescription = mode.label,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (mode == selectedMode) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (mode == selectedMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(
                            alpha = 0.4f
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun MapEmptyHint(
    modifier: Modifier = Modifier,
    onBackHome: () -> Unit,
    onReset: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Belum ada rencana",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Buka Home untuk membuat itinerary baru lalu lihat rute-nya di peta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(
                onClick = {
                    onReset()
                    onBackHome()
                },
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Text("Buka Home")
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DraggableLocationSheet(
    locations: List<DayPlanLocation>,
    selectedIndex: Int,
    onSelectLocation: (DayPlanLocation) -> Unit,
    onOpenInMaps: (DayPlanLocation) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val scope = rememberCoroutineScope()
        val maxHeightPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current
        val peekHeightPx = max(
            maxHeightPx * 0.28f,
            with(density) { 120.dp.toPx() }
        )
        val expandedHeightPx = minOf(
            maxHeightPx * 0.9f,
            with(density) { 560.dp.toPx() }
        )
        val anchors = remember(maxHeightPx, locations.size) {
            mapOf(
                (maxHeightPx - expandedHeightPx).coerceAtLeast(0f) to LocationSheetPosition.Expanded,
                (maxHeightPx - peekHeightPx).coerceAtLeast(0f) to LocationSheetPosition.Peek
            )
        }
        val swipeableState = rememberSwipeableState(LocationSheetPosition.Peek)

        LaunchedEffect(locations.size) {
            if (locations.isNotEmpty()) swipeableState.animateTo(LocationSheetPosition.Peek)
        }
        LaunchedEffect(selectedIndex) {
            if (locations.isNotEmpty() && swipeableState.currentValue != LocationSheetPosition.Peek) {
                swipeableState.animateTo(LocationSheetPosition.Peek)
            }
        }

        val offset = swipeableState.offset.value

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, offset.coerceIn(anchors.keys.minOrNull() ?: 0f, maxHeightPx).toInt()) }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    orientation = Orientation.Vertical,
                    thresholds = { _, _ -> FractionalThreshold(0.25f) },
                    resistance = null
                )
                .shadow(12.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 48.dp, height = 4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f))
                )

                LocationCarousel(
                    modifier = Modifier.fillMaxWidth(),
                    locations = locations,
                    selectedIndex = selectedIndex,
                    onSelectLocation = onSelectLocation,
                    onOpenInMaps = onOpenInMaps
                )
            }
        }
    }
}

private enum class LocationSheetPosition {
    Expanded, Peek
}

private enum class BottomTab { Home, Map, Profile }

@Composable
private fun TimelineHistoryScreen(
    modifier: Modifier = Modifier,
    history: List<SavedPlan>,
    onOpenPlan: (Long) -> Unit,
    onDeletePlan: (Long) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val accentColors = listOf(
        Color(0xFF4C6FFF),
        Color(0xFFFF7B54),
        Color(0xFF2EAF7D),
        Color(0xFF9A6BFF)
    )
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Riwayat",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Lihat kembali timeline yang pernah dibuat dan buka ulang dalam peta.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    onBack?.let {
                        IconButton(onClick = it) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "Kembali"
                            )
                        }
                    }
                }
            }
        }
        if (history.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Belum ada riwayat",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Generate rencana terlebih dahulu, riwayatnya akan muncul di sini.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            itemsIndexed(history) { index, plan ->
                val accent = accentColors[index % accentColors.size]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
                    onClick = { onOpenPlan(plan.id) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = plan.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Dibuat: ${plan.createdAt.formattedDate()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = accent.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "Riwayat ${index + 1}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = accent
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlanMetaChip(
                                icon = Icons.Filled.Map,
                                label = "${plan.itinerary.locations.size} lokasi",
                                color = accent
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ketuk untuk membuka timeline ini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { onDeletePlan(plan.id) },
                                colors = ButtonDefaults.textButtonColors(contentColor = accent)
                            ) {
                                Text("Hapus")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedPlansScreen(
    modifier: Modifier = Modifier,
    plans: List<SavedPlan>,
    onOpenPlan: (Long) -> Unit,
    onDeletePlan: (Long) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val accentColors = listOf(
        Color(0xFF4C6FFF),
        Color(0xFFFF7B54),
        Color(0xFF2EAF7D),
        Color(0xFF9A6BFF)
    )
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Rencana tersimpan",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Simpen rute favoritmu dan buka kembali kapan saja.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                onBack?.let {
                    IconButton(onClick = it) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Kembali")
                    }
                }
            }
        }
        if (plans.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Belum ada rencana",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Simpan rencana dari halaman peta atau timeline untuk melihatnya lagi di sini.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            itemsIndexed(plans) { index, plan ->
                val accent = accentColors[index % accentColors.size]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
                    onClick = { onOpenPlan(plan.id) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = plan.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Disimpan: ${plan.createdAt.formattedDate()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = accent.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "Plan ${index + 1}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = accent
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlanMetaChip(
                                icon = Icons.Filled.Map,
                                label = "${plan.itinerary.locations.size} lokasi",
                                color = accent
                            )
                            PlanMetaChip(
                                icon = Icons.Outlined.Schedule,
                                label = "${plan.itinerary.legs.size} rute",
                                color = accent
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ketuk kartu untuk membuka rencana",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { onDeletePlan(plan.id) },
                                colors = ButtonDefaults.textButtonColors(contentColor = accent)
                            ) {
                                Text("Hapus")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    user: SignedInUser?,
    onSignOut: () -> Unit,
    savedPlans: List<SavedPlan>,
    timelineHistory: List<SavedPlan>,
    onOpenSavedPlan: (Long) -> Unit,
    onOpenTimelinePlan: (Long) -> Unit,
    onDeleteSavedPlan: (Long) -> Unit,
    onDeleteTimelinePlan: (Long) -> Unit,
    savedExpanded: Boolean,
    timelineExpanded: Boolean,
    onToggleSavedExpanded: (Boolean) -> Unit,
    onToggleTimelineExpanded: (Boolean) -> Unit,
    showSavedPage: Boolean,
    showTimelinePage: Boolean,
    onBackToProfileMain: () -> Unit
) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Kelola akun, rencana tersimpan, dan riwayatmu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showSavedPage) {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                SettingsSection(title = "Rencana tersimpan") {
                SettingsRow(
                    title = "Kembali",
                    subtitle = "Tutup dan kembali ke halaman profile",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = onBackToProfileMain
                )
                SavedPlansCompactList(
                    plans = savedPlans,
                    onOpenPlan = onOpenSavedPlan,
                    onDeletePlan = onDeleteSavedPlan
                )
                }
            }
            return@Column
        }

        if (showTimelinePage) {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                SettingsSection(title = "Riwayat timeline") {
                SettingsRow(
                    title = "Kembali",
                    subtitle = "Tutup dan kembali ke halaman profile",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = onBackToProfileMain
                )
                TimelineHistoryCompactList(
                    history = timelineHistory,
                    onOpenPlan = onOpenTimelinePlan,
                    onDeletePlan = onDeleteTimelinePlan
                )
                }
            }
            return@Column
        }

        SettingsSection(title = "Account") {
            if (user == null) {
                SettingsRow(
                    title = "Belum masuk",
                    subtitle = "Masuk untuk menyimpan rencana dan melihat profil.",
                    action = {
                        Text(
                            text = "Masuk",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
                return@SettingsSection
            }
            SettingsRow(
                title = user.name,
                subtitle = user.email.ifBlank { "Akun Google terhubung" },
                leadingIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                action = {
                    OutlinedButton(
                        onClick = onSignOut,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Keluar")
                    }
                }
            )
        }

        SettingsSection(title = "Library") {
            SettingsRow(
                title = "Rencana tersimpan",
                subtitle = "${savedPlans.size} rencana",
                leadingIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.rotate(if (savedExpanded) 90f else 0f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = { onToggleSavedExpanded(!savedExpanded) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsRow(
                title = "Riwayat timeline",
                subtitle = "${timelineHistory.size} timeline",
                leadingIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.rotate(if (timelineExpanded) 90f else 0f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = { onToggleTimelineExpanded(!timelineExpanded) }
            )
        }
    }
}

@Composable
private fun PlanMetaChip(icon: ImageVector, label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, color.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    } else {
        Modifier.fillMaxWidth()
    }
    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            action?.invoke()
            trailingIcon?.invoke()
        }
    }
}

@Composable
private fun SavedPlansCompactList(
    plans: List<SavedPlan>,
    onOpenPlan: (Long) -> Unit,
    onDeletePlan: (Long) -> Unit
) {
    if (plans.isEmpty()) {
        Text(
            text = "Tidak ada rencana tersimpan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        plans.forEachIndexed { index, plan ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPlan(plan.id) },
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 1.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = plan.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Disimpan: ${plan.createdAt.formattedDate()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        AssistChip(
                            onClick = { onOpenPlan(plan.id) },
                            label = { Text("Buka") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlanMetaChip(
                            icon = Icons.Filled.Map,
                            label = "${plan.itinerary.locations.size} lokasi",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onDeletePlan(plan.id) }) {
                            Text("Hapus")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineHistoryCompactList(
    history: List<SavedPlan>,
    onOpenPlan: (Long) -> Unit,
    onDeletePlan: (Long) -> Unit
) {
    if (history.isEmpty()) {
        Text(
            text = "Belum ada riwayat timeline.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        history.forEachIndexed { index, plan ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPlan(plan.id) },
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 1.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = plan.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Dibuat: ${plan.createdAt.formattedDate()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        AssistChip(
                            onClick = { onOpenPlan(plan.id) },
                            label = { Text("Buka") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlanMetaChip(
                            icon = Icons.Filled.Map,
                            label = "${plan.itinerary.locations.size} lokasi",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onDeletePlan(plan.id) }) {
                            Text("Hapus")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeScreen(onGetStarted: () -> Unit) {
    val gradientTop = Brush.verticalGradient(
        listOf(
            Color(0xFF0B3A5D),
            Color(0xFF0F5C73),
            Color(0xFF0F8B8D)
        )
    )
    val accent = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(gradientTop),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(240.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F8B8D).copy(alpha = 0.85f),
                                    Color(0xFF0B3A5D).copy(alpha = 0.9f)
                                )
                            )
                        )
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        drawRect(
                            color = Color.White.copy(alpha = 0.08f),
                            size = Size(w, h * 0.42f)
                        )
                        drawRect(
                            color = Color.White.copy(alpha = 0.12f),
                            topLeft = Offset(w * 0.55f, 0f),
                            size = Size(w * 0.45f, h * 0.65f)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.14f),
                            radius = w * 0.28f,
                            center = Offset(w * 0.35f, h * 0.65f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "PergiYuk Planner",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Peta, itinerary, dan navigasi\ndalam satu aplikasi.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.82f)
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Jelajahi dengan lebih terarah",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Buat itinerary, lihat rute di peta, simpan, dan mulai navigasi turn-by-turn tanpa ribet.",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accent)
        ) {
            Icon(Icons.Filled.Map, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Mulai rencanakan", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
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
                        text = "PergiYuk",
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                LoadingDots(dotColor = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "Menghubungkan",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
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

@Composable
private fun OrbitingLoader(
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 54.dp,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary
) {
    val transition = rememberInfiniteTransition(label = "orbitingLoader")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitRotation"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbitPulse"
    )

    Canvas(modifier = modifier.size(indicatorSize)) {
        val diameter = size.minDimension
        val stroke = diameter * 0.12f
        val ringRadius = diameter / 2f - stroke / 2f
        val ringTopLeft = Offset(center.x - ringRadius, center.y - ringRadius)
        val ringSize = Size(ringRadius * 2f, ringRadius * 2f)

        drawCircle(
            color = primaryColor.copy(alpha = 0.12f),
            radius = ringRadius,
            style = Stroke(width = stroke)
        )
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    secondaryColor.copy(alpha = 0.8f),
                    primaryColor.copy(alpha = 0.9f)
                ),
                center = center
            ),
            startAngle = rotation,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = ringTopLeft,
            size = ringSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        val orbitRadius = ringRadius - stroke
        val angleRad = Math.toRadians((rotation + 200f).toDouble())
        val dotCenter = Offset(
            x = center.x + (orbitRadius * cos(angleRad)).toFloat(),
            y = center.y + (orbitRadius * sin(angleRad)).toFloat()
        )
        drawCircle(
            color = secondaryColor.copy(alpha = 0.9f),
            radius = stroke * 0.45f,
            center = dotCenter
        )
        drawCircle(
            color = secondaryColor.copy(alpha = 0.25f),
            radius = stroke * 0.9f,
            center = dotCenter
        )
        drawCircle(
            color = primaryColor.copy(alpha = 0.7f),
            radius = stroke * 0.65f * pulse,
            center = center
        )
    }
}

@Composable
private fun LoadingDots(
    modifier: Modifier = Modifier,
    dotSize: Dp = 6.dp,
    dotColor: Color = MaterialTheme.colorScheme.primary
) {
    val transition = rememberInfiniteTransition(label = "loadingDots")
    val delays = listOf(0, 120, 240)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        delays.forEach { delay ->
            val scale by transition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, delayMillis = delay, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dotScale$delay"
            )
            val alpha by transition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, delayMillis = delay, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dotAlpha$delay"
            )
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(scale)
                    .background(dotColor.copy(alpha = alpha), CircleShape)
            )
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
    modifier: Modifier = Modifier,
    locations: List<DayPlanLocation>,
    selectedIndex: Int,
    onSelectLocation: (DayPlanLocation) -> Unit,
    onOpenInMaps: (DayPlanLocation) -> Unit
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

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                onClick = { onSelectLocation(location) },
                onOpenInMaps = { onOpenInMaps(location) }
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
    onClick: () -> Unit,
    onOpenInMaps: () -> Unit
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

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onOpenInMaps,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Map, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Buka di Google Maps")
                }
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
    onClose: () -> Unit,
    onSavePlan: () -> Unit
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
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
            item {
                Button(
                    onClick = onSavePlan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.BookmarkBorder, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Save Plan")
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

private fun Long.formattedDate(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(Date(this))
}

private fun Location.isFresh(maxAgeMillis: Long): Boolean {
    if (elapsedRealtimeNanos == 0L) return true
    val ageMillis = (SystemClock.elapsedRealtimeNanos() - elapsedRealtimeNanos) / 1_000_000
    return ageMillis <= maxAgeMillis
}

private fun openInGoogleMaps(context: Context, location: DayPlanLocation) {
    val lat = location.position.lat
    val lng = location.position.lng
    val query = Uri.encode("$lat,$lng (${location.name})")
    val uri = Uri.parse("geo:$lat,$lng?q=$query")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    try {
        ContextCompat.startActivity(context, intent, null)
    } catch (_: Exception) {
        ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, uri), null)
    }
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

private fun TravelMode.toNavTravelMode(): Int = when (this) {
    TravelMode.Driving -> RoutingOptions.TravelMode.DRIVING
    TravelMode.Motorcycle -> RoutingOptions.TravelMode.TWO_WHEELER
    TravelMode.Walking -> RoutingOptions.TravelMode.WALKING
}

@Throws(Exception::class)
private fun NavigationView.attachNavigator(
    navigator: Navigator,
    environment: com.google.android.libraries.navigation.environment.am
) {
    val method = this::class.java.methods.firstOrNull { m ->
        m.name == "y" &&
            m.parameterTypes.size == 3 &&
            m.parameterTypes[0] == android.os.Bundle::class.java
    } ?: throw NoSuchMethodException("NavigationView.y(Bundle, Navigator, Environment) not found")
    method.isAccessible = true
    // Pass an empty Bundle to avoid internal null dereference
    method.invoke(this, android.os.Bundle(), navigator, environment)
    // Ensure built-in UI is visible
    this.setNavigationUiEnabled(true)
}

private fun NavigationView.moveCompassToBottomRight(
    defaultSideMarginPx: Int,
    defaultBottomMarginPx: Int
) {
    val incidentAnchor = findViewById<View>(NavR.id.nav_report_incident_fab_button)
    val anchorHeight = incidentAnchor?.let { anchor ->
        val measured = if (anchor.height > 0) anchor.height else anchor.measuredHeight
        if (measured > 0) measured else null
    }
    val anchorBottomMargin = (incidentAnchor?.layoutParams as? FrameLayout.LayoutParams)?.bottomMargin
    val anchorSideMargin = (incidentAnchor?.layoutParams as? FrameLayout.LayoutParams)?.let {
        maxOf(it.marginEnd, it.rightMargin)
    }

    val sideMarginPx = max(defaultSideMarginPx, anchorSideMargin ?: 0)
    val bottomMarginPx = when {
        anchorHeight != null -> (anchorBottomMargin ?: 0) + anchorHeight + defaultSideMarginPx
        else -> defaultBottomMarginPx
    }

    val candidateIds = listOf(
        NavR.id.compass_container,
        NavR.id.above_compass_container,
        NavR.id.base_compass_button
    )
    candidateIds.forEach { id ->
        val compass = findViewById<View>(id) ?: return@forEach
        val params = (compass.layoutParams as? FrameLayout.LayoutParams)
            ?: FrameLayout.LayoutParams(
                compass.layoutParams?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT,
                compass.layoutParams?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT
            )
        params.gravity = Gravity.END or Gravity.BOTTOM
        params.setMargins(sideMarginPx, defaultSideMarginPx, sideMarginPx, bottomMarginPx)
        compass.layoutParams = params
    }
}

private fun NavigationView.hideDuplicateCompasses() {
    val compasses = mutableListOf<View>()
    fun collect(view: View) {
        val name = runCatching { resources.getResourceEntryName(view.id) }.getOrNull().orEmpty()
        if (name.contains("compass", ignoreCase = true)) compasses.add(view)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                view.getChildAt(i)?.let { collect(it) }
            }
        }
    }
    collect(this)
    if (compasses.size <= 1) return
    val keep = compasses.maxByOrNull { view ->
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        loc[0] * 10_000 + loc[1]
    } ?: return
    compasses.filter { it != keep }.forEach { it.visibility = View.GONE }
}

private fun DayPlanLocation.toWaypoint(): Waypoint =
    Waypoint.builder()
        .setTitle(name)
        .setLatLng(position.lat, position.lng)
        .build()

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@SuppressLint("MissingPermission")
private suspend fun FusedLocationProviderClient.awaitCurrentLocation(
    maxAgeMillis: Long = 5_000L
): Location? =
    suspendCancellableCoroutine { continuation ->
        val tokenSource = CancellationTokenSource()
        continuation.invokeOnCancellation { tokenSource.cancel() }

        lastLocation
            .addOnSuccessListener { lastKnown ->
                if (lastKnown != null && lastKnown.isFresh(maxAgeMillis) && continuation.isActive) {
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

private fun FirebaseUser.toSignedInUser(fallbackAccount: GoogleSignInAccount? = null): SignedInUser =
    SignedInUser(
        uid = uid,
        name = displayName
            ?: fallbackAccount?.displayName
            ?: email
            ?: fallbackAccount?.email
            ?: "Pengguna",
        email = email ?: fallbackAccount?.email.orEmpty(),
        photoUrl = photoUrl?.toString() ?: fallbackAccount?.photoUrl?.toString()
    )

private fun ensureFirebaseConfigured(context: Context) {
    if (FirebaseApp.getApps(context).isEmpty()) {
        FirebaseApp.initializeApp(context)
    }
}

private suspend fun firebaseAuthWithGoogle(
    account: GoogleSignInAccount,
    auth: FirebaseAuth,
    snackbarHostState: SnackbarHostState
): SignedInUser? {
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
    return try {
        auth.signInWithCredential(credential).await()
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            firebaseUser.toSignedInUser(account)
        } else {
            snackbarHostState.showSnackbar("Login Firebase gagal: pengguna tidak tersedia.")
            null
        }
    } catch (error: Exception) {
        snackbarHostState.showSnackbar("Login Firebase gagal: ${error.localizedMessage ?: "Unknown error"}")
        null
    }
}
