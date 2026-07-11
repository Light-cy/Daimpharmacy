@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.graphics.Bitmap
import android.content.Context
import android.content.Intent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

const val ADMIN_WHATSAPP_NUMBER = "923109551558"

// Dynamic icon mapper to convert database icon strings to Jetpack Compose Material 3 Icons
fun getCategoryIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "medication", "tablets" -> Icons.Default.Medication
        "liquor", "syrups" -> Icons.Default.LocalPharmacy
        "vaccines", "injections" -> Icons.Default.Vaccines
        "medical_services", "capsules" -> Icons.Default.MedicalServices
        else -> Icons.Default.Category
    }
}

@Composable
fun MedicineImagePlaceholder(
    category: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp,
    showCircle: Boolean = true,
    circleSize: Dp = 54.dp
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8F5)),
        contentAlignment = Alignment.Center
    ) {
        if (showCircle) {
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .background(Color(0xFFE8F5E9), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category),
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(iconSize)
                )
            }
        } else {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        Color(0xFFE2E8F0),
        Color(0xFFF1F5F9),
        Color(0xFFE2E8F0)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    )
}

@Composable
fun MedicineGridCardShimmer() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                ShimmerEffect()
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    ShimmerEffect()
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    ShimmerEffect()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        ShimmerEffect()
                    }
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        ShimmerEffect()
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    ShimmerEffect()
                }
            }
        }
    }
}

@Composable
fun AdminMedicineListShimmerItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    ShimmerEffect()
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        ShimmerEffect()
                    }
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        ShimmerEffect()
                    }
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        ShimmerEffect()
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    ShimmerEffect()
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    ShimmerEffect()
                }
            }
        }
    }
}

@Composable
fun AdminOrdersListShimmerItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        ShimmerEffect()
                    }
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        ShimmerEffect()
                    }
                }

                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    ShimmerEffect()
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    ShimmerEffect()
                }

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    ShimmerEffect()
                }
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    ShimmerEffect()
                }
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    ShimmerEffect()
                }
            }
        }
    }
}

@Composable
fun MedicineImage(
    imageUri: String?,
    category: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp,
    showCircle: Boolean = true,
    circleSize: Dp = 54.dp
) {
    if (imageUri.isNullOrBlank()) {
        MedicineImagePlaceholder(
            category = category,
            modifier = modifier,
            iconSize = iconSize,
            showCircle = showCircle,
            circleSize = circleSize
        )
    } else {
        coil.compose.SubcomposeAsyncImage(
            model = imageUri,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            loading = {
                ShimmerEffect()
            },
            error = {
                MedicineImagePlaceholder(
                    category = category,
                    iconSize = iconSize,
                    showCircle = showCircle,
                    circleSize = circleSize
                )
            }
        )
    }
}

fun saveUriToLocalFile(context: android.content.Context, uri: android.net.Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "med_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(context.filesDir, fileName)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveBitmapToLocalFile(context: android.content.Context, bitmap: android.graphics.Bitmap): String? {
    return try {
        val fileName = "med_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(context.filesDir, fileName)
        file.outputStream().use { outputStream ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveAndProcessUriToLocalFile(context: android.content.Context, uri: android.net.Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream) ?: return null
        val processedBitmap = com.example.data.LocalImageProcessor.processMedicineImage(bitmap)
        saveBitmapToLocalFile(context, processedBitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        // fallback to normal saving if processing fails
        saveUriToLocalFile(context, uri)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: PharmacyViewModel) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500) // Premium 2.5s display window
        showSplash = false
    }

    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
        },
        label = "SplashToAppTransition"
    ) { isSplash ->
        if (isSplash) {
            DaimSplashScreen()
        } else {
            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

            AnimatedContent(
                targetState = currentUser?.role,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "AppScreenTransition"
            ) { role ->
                when (role) {
                    "admin" -> AdminDashboardScreen(viewModel)
                    else -> DoctorDashboardScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun FloatingNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
fun RowScope.FloatingNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .weight(1f)
            .height(56.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = if (selected) Color(0xFFE8F5E9) else Color.Transparent,
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides if (selected) Color(0xFF2E7D32) else Color(0xFF9E9E9E)
                ) {
                    icon()
                }
            }
            AnimatedVisibility(
                visible = selected,
                enter = expandHorizontally(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeIn(),
                exit = shrinkHorizontally(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeOut()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: PharmacyViewModel,
    initialRole: String = "doctor",
    onDismissRequest: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(initialRole) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val loginLoading by viewModel.loginLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verification Required", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Branding Header
                Image(
                    painter = painterResource(id = R.drawable.ic_daim_logo),
                    contentDescription = "Daim Pharmacy Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Daim Pharmacy",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (selectedRole == "admin") "Staff / Admin Access Portal" else "Doctor Orders Portal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // Role selector
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    SegmentedButton(
                        selected = selectedRole == "doctor",
                        onClick = { selectedRole = "doctor" },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MedicalServices, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Doctor")
                        }
                    }
                    SegmentedButton(
                        selected = selectedRole == "admin",
                        onClick = { selectedRole = "admin" },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Staff / Admin")
                        }
                    }
                }

                // Input fields
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Text(
                                    text = if (isPasswordVisible) "Hide" else "Show",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (loginError != null) {
                    Text(
                        text = loginError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Action Buttons
                Button(
                    onClick = {
                        viewModel.login(email, selectedRole, password) {
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (loginLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text("Verify & Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ==================== DOCTOR INTERFACE ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(viewModel: PharmacyViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val isCategoriesLoading by viewModel.isCategoriesLoading.collectAsStateWithLifecycle()
    val filteredMedicines by viewModel.filteredMedicines.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val doctorOrders by viewModel.doctorOrders.collectAsStateWithLifecycle()
    val cartTotal by viewModel.cartTotal.collectAsStateWithLifecycle(0.0)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val gridState = rememberLazyGridState()

    val scrollThreshold = 8 // pixels, ignore tiny movements

    var previousIndex by remember { mutableStateOf(viewModel.savedMedicineGridIndex) }
    var previousOffset by remember { mutableStateOf(viewModel.savedMedicineGridOffset) }
    var isScrollingDown by remember { mutableStateOf(false) }

    val isAtEnd by remember {
        derivedStateOf { !gridState.canScrollForward }
    }

    LaunchedEffect(gridState) {
        snapshotFlow { 
            gridState.firstVisibleItemIndex to 
            gridState.firstVisibleItemScrollOffset 
        }.collect { (index, offset) ->
            val deltaOffset = when {
                index > previousIndex -> offset + (index - previousIndex) * 200
                index < previousIndex -> offset - (previousIndex - index) * 200
                else -> offset - previousOffset
            }
            if (!isAtEnd) {
                if (deltaOffset > scrollThreshold) {
                    isScrollingDown = true
                } else if (deltaOffset < -scrollThreshold) {
                    isScrollingDown = false
                }
            }
            // Only update baseline if change is significant
            if (kotlin.math.abs(deltaOffset) > scrollThreshold) {
                previousIndex = index
                previousOffset = offset
            }

            // Save position to viewmodel for restoration
            if (filteredMedicines.isNotEmpty() && (index > 0 || offset > 0 || gridState.isScrollInProgress)) {
                viewModel.savedMedicineGridIndex = index
                viewModel.savedMedicineGridOffset = offset
            } else if (filteredMedicines.isNotEmpty() && index == 0 && offset == 0) {
                viewModel.savedMedicineGridIndex = 0
                viewModel.savedMedicineGridOffset = 0
            }
        }
    }

    LaunchedEffect(filteredMedicines) {
        if (filteredMedicines.isNotEmpty() && (viewModel.savedMedicineGridIndex > 0 || viewModel.savedMedicineGridOffset > 0)) {
            val targetIndex = viewModel.savedMedicineGridIndex.coerceAtMost(filteredMedicines.lastIndex.coerceAtLeast(0))
            gridState.scrollToItem(targetIndex, viewModel.savedMedicineGridOffset)
        }
    }

    var activeTab by remember { mutableStateOf("browse") } // "browse" | "cart" | "history"
    var selectedMedicineForAdd by remember { mutableStateOf<MedicineEntity?>(null) }
    var selectedMedicineDetail by remember { mutableStateOf<MedicineEntity?>(null) }
    
    var showLoginScreen by remember { mutableStateOf(false) }
    var loginScreenPurpose by remember { mutableStateOf("doctor_order") } // "doctor_order" | "staff_login" | "doctor_login"

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (selectedMedicineDetail != null) {
        MedicineDetailScreen(
            medicine = selectedMedicineDetail!!,
            onBack = { selectedMedicineDetail = null },
            onAddToCart = { qty ->
                viewModel.addToCart(selectedMedicineDetail!!, qty)
                selectedMedicineDetail = null
            }
        )
    } else if (showLoginScreen) {
        LoginScreen(
            viewModel = viewModel,
            initialRole = if (loginScreenPurpose == "staff_login") "admin" else "doctor",
            onDismissRequest = { showLoginScreen = false },
            onLoginSuccess = {
                showLoginScreen = false
                if (loginScreenPurpose == "doctor_order") {
                    viewModel.placeOrder(
                        onSuccess = {
                            activeTab = "history"
                        },
                        onError = { errorMsg ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(errorMsg)
                            }
                        }
                    )
                }
            }
        )
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Column {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        ),
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_daim_logo),
                                    contentDescription = "Daim Pharmacy Logo",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Daim Pharmacy",
                                        color = Color(0xFF1B1B1B),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val subtitle = if (currentUser != null) {
                                        "Dr. ${currentUser?.name ?: ""}"
                                    } else {
                                        "Browse & Order"
                                    }
                                    val subtitleColor = if (currentUser != null) {
                                        Color(0xFF2E7D32)
                                    } else {
                                        Color(0xFF9E9E9E)
                                    }
                                    Text(
                                        text = subtitle,
                                        fontSize = 12.sp,
                                        color = subtitleColor,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        },
                        actions = {
                            val context = LocalContext.current
                            val sharedPrefs = remember(context) { context.getSharedPreferences("user_session_pref", Context.MODE_PRIVATE) }
                            val adminWhatsApp = remember(sharedPrefs) { sharedPrefs.getString("admin_whatsapp_number", ADMIN_WHATSAPP_NUMBER) ?: ADMIN_WHATSAPP_NUMBER }

                            IconButton(
                                onClick = {
                                    val message = "Hello, I need help with my order on Daim Pharmacy app."
                                    val uri = Uri.parse("https://wa.me/$adminWhatsApp?text=${Uri.encode(message)}")
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                            setPackage("com.whatsapp")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        } catch (ex: Exception) {}
                                    }
                                },
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HeadsetMic,
                                    contentDescription = "Help & Support",
                                    tint = Color(0xFF2E7D32)
                                )
                            }

                            if (currentUser != null) {
                                var showProfileMenu by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.padding(end = 8.dp)) {
                                    val initial = currentUser?.name?.take(1)?.uppercase() ?: "D"
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(color = Color(0xFF2E7D32), shape = CircleShape)
                                            .clickable { showProfileMenu = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initial,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showProfileMenu,
                                        onDismissRequest = { showProfileMenu = false },
                                        modifier = Modifier.background(Color.White)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                                            onClick = {
                                                showProfileMenu = false
                                                viewModel.logout()
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.ExitToApp,
                                                    contentDescription = "Logout",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        )
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        loginScreenPurpose = "staff_login"
                                        showLoginScreen = true
                                    },
                                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(50.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF2E7D32)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = "Login",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                }
            },
            bottomBar = {
                FloatingNavigationBar {
                    FloatingNavigationBarItem(
                        selected = activeTab == "browse",
                        onClick = { activeTab = "browse" },
                        icon = { Icon(Icons.Default.Medication, null) },
                        label = "Medicines"
                    )
                    FloatingNavigationBarItem(
                        selected = activeTab == "cart",
                        onClick = { activeTab = "cart" },
                        icon = {
                            BadgedBox(badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge { Text(cartItems.sumOf { it.quantity }.toString()) }
                                }
                            }) {
                                Icon(Icons.Default.ShoppingCart, null)
                            }
                        },
                        label = "My Cart"
                    )
                    FloatingNavigationBarItem(
                        selected = activeTab == "history",
                        onClick = { activeTab = "history" },
                        icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, null) },
                        label = "Orders"
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
            when (activeTab) {
                "browse" -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Header container with background color, always visible
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F8F4))
                            ) {
                                AnimatedVisibility(
                                    visible = !isScrollingDown,
                                    enter = slideInVertically(
                                        initialOffsetY = { -it },
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + expandVertically(
                                        expandFrom = Alignment.Top,
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + fadeIn(tween(200)),
                                    exit = slideOutVertically(
                                        targetOffsetY = { -it },
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + shrinkVertically(
                                        shrinkTowards = Alignment.Top,
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + fadeOut(tween(200))
                                ) {
                                    // Search bar at the top below app bar
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .widthIn(max = 600.dp)
                                            .align(Alignment.CenterHorizontally)
                                            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = searchQuery,
                                            onValueChange = { viewModel.setSearchQuery(it) },
                                            placeholder = { Text("Search", color = Color(0xFF2E7D32).copy(alpha = 0.5f)) },
                                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2E7D32)) },
                                            trailingIcon = {
                                                if (searchQuery.isNotEmpty()) {
                                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                                        Icon(Icons.Default.Clear, null, tint = Color(0xFF2E7D32))
                                                    }
                                                }
                                            },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(28.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color(0xFF0F172A),
                                                unfocusedTextColor = Color(0xFF0F172A),
                                                focusedBorderColor = Color(0xFF2E7D32),
                                                unfocusedBorderColor = Color(0xFFC8E6C9),
                                                focusedContainerColor = Color(0xFFF1F8F5),
                                                unfocusedContainerColor = Color(0xFFF1F8F5)
                                            )
                                        )


                                    }
                                }

                                // Horizontal scrollable category filter chips (All, Tablets, Capsules, Syrups, Injections)
                                if (isCategoriesLoading) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp)
                                    ) {
                                        item {
                                            CategoryChip(
                                                name = "All",
                                                selected = selectedCategory == null,
                                                onClick = { viewModel.selectCategory(null) }
                                            )
                                        }
                                        items(4) {
                                            Box(
                                                modifier = Modifier
                                                    .width(80.dp)
                                                    .height(40.dp)
                                                    .clip(RoundedCornerShape(20.dp))
                                            ) {
                                                ShimmerEffect()
                                            }
                                        }
                                    }
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp)
                                    ) {
                                        item {
                                            CategoryChip(
                                                name = "All",
                                                selected = selectedCategory == null,
                                                onClick = { viewModel.selectCategory(null) }
                                            )
                                        }

                                        val predefinedOrder = listOf("Tablets", "Capsules", "Syrups", "Injections")
                                        val sortedCategories = categories.sortedBy { predefinedOrder.indexOf(it.name) }

                                        items(sortedCategories) { category ->
                                            CategoryChip(
                                                name = category.name,
                                                selected = selectedCategory == category.name,
                                                onClick = { viewModel.selectCategory(category.name) }
                                            )
                                        }
                                    }
                                }
                            }

                            // 2-column Grid of Medicine Cards showing image placeholder, name, formula, price,
                            if (isLoading) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(bottom = if (cartItems.isNotEmpty()) 80.dp else 16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    items(6) {
                                        MedicineGridCardShimmer()
                                    }
                                }
                            } else if (filteredMedicines.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("No medicines match your search.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    state = gridState,
                                    columns = GridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(bottom = if (cartItems.isNotEmpty()) 80.dp else 16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    items(filteredMedicines) { medicine ->
                                        MedicineGridCard(
                                            medicine = medicine,
                                            onAddClick = { selectedMedicineForAdd = medicine },
                                            onDetailClick = { selectedMedicineDetail = medicine }
                                        )
                                    }
                                }
                            }
                        }

                        // Sticky floating cart button at the bottom center
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        ) {
                            StickyFloatingCart(
                                cartItems = cartItems,
                                totalPrice = cartTotal,
                                onClick = { activeTab = "cart" }
                            )
                        }
                    }
                }

                "cart" -> {
                    DoctorCartScreen(
                        viewModel = viewModel,
                        onBackToBrowse = { activeTab = "browse" },
                        onOrderPlaced = { activeTab = "history" },
                        onRequireLoginForOrder = {
                            loginScreenPurpose = "doctor_order"
                            showLoginScreen = true
                        },
                        onShowSnackbar = { errorMsg ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(errorMsg)
                            }
                        }
                    )
                }

                "history" -> {
                    if (currentUser == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(18.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ListAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    "Verification Required",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Please log in with your doctor account to view past orders and history.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Button(
                                    onClick = {
                                        loginScreenPurpose = "doctor_login"
                                        showLoginScreen = true
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Login, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify Doctor Account")
                                }
                            }
                        }
                    } else {
                        DoctorOrderHistoryScreen(
                            orders = doctorOrders,
                            viewModel = viewModel,
                            onNavigateToCart = { activeTab = "cart" },
                            onShowSnackbar = { msg ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Quick Add Quantity Selector Bottom Sheet / Dialog
        selectedMedicineForAdd?.let { medicine ->
            QuickAddDialog(
                medicine = medicine,
                onDismiss = { selectedMedicineForAdd = null },
                onConfirm = { qty ->
                    viewModel.addToCart(medicine, qty)
                    selectedMedicineForAdd = null
                }
            )
        }

    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineGridCard(
    medicine: MedicineEntity,
    onAddClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        onClick = onDetailClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MedicineImage(
                imageUri = medicine.imageUri,
                category = medicine.category,
                contentDescription = "Medicine image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = medicine.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1B5E20),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = medicine.formula,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rs. ${medicine.price}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp
                    )
                    if (medicine.stock == 0) {
                        Text(
                            text = "Out of Stock",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onAddClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add medicine to cart", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF2E7D32) else Color(0xFFF1F8F5),
        label = "bgColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF2E7D32),
        label = "contentColor"
    )
    val borderStroke = if (selected) null else BorderStroke(1.dp, Color(0xFFC8E6C9))

    Surface(
        onClick = onClick,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(20.dp),
        border = borderStroke,
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (name == "All") Icons.Default.GridView else getCategoryIcon(name),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StickyFloatingCart(
    cartItems: List<CartItem>,
    totalPrice: Double,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = cartItems.isNotEmpty(),
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300, delayMillis = 150, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(300, delayMillis = 150)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(300)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Surface(
            onClick = onClick,
            color = Color(0xFF2E7D32),
            contentColor = Color.White,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 8.dp,
            modifier = Modifier.height(56.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        val itemsText = if (cartItems.size == 1) "1 item" else "${cartItems.sumOf { it.quantity }} items"
                        Text(
                            text = itemsText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Total: Rs. ${String.format(Locale.US, "%,.2f", totalPrice)}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "View Cart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickAddDialog(
    medicine: MedicineEntity,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var quantityText by remember(quantity) { mutableStateOf(quantity.toString()) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.92f)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add to Cart",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = medicine.formula,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Remove, null)
                    }

                    BasicTextField(
                        value = quantityText,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() }
                            if (filtered.isEmpty()) {
                                quantityText = ""
                                quantity = 1
                            } else {
                                val num = filtered.toIntOrNull()
                                if (num != null) {
                                    if (num <= 0) {
                                        quantityText = "1"
                                        quantity = 1
                                    } else if (num > medicine.stock && medicine.stock > 0) {
                                        quantityText = medicine.stock.toString()
                                        quantity = medicine.stock
                                    } else {
                                        quantityText = num.toString()
                                        quantity = num
                                    }
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (quantityText.isEmpty()) {
                                    quantityText = "1"
                                }
                                keyboardController?.hide()
                            }
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .width(60.dp)
                            .testTag("quick_add_quantity_input")
                            .padding(vertical = 4.dp),
                        decorationBox = { innerTextField ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                innerTextField()
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    )

                    IconButton(
                        onClick = { if (medicine.stock == 0 || quantity < medicine.stock) quantity++ },
                        enabled = medicine.stock == 0 || quantity < medicine.stock,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, null)
                    }
                }

                Text(
                    text = "Total Price: Rs. ${String.format("%.2f", medicine.price * quantity)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(quantity) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Add to Cart")
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineDetailScreen(
    medicine: MedicineEntity,
    onBack: () -> Unit,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var quantityText by remember(quantity) { mutableStateOf(quantity.toString()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Hero Image or soft green gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                MedicineImage(
                    imageUri = medicine.imageUri,
                    category = medicine.category,
                    contentDescription = medicine.name,
                    modifier = Modifier.fillMaxSize(),
                    iconSize = 48.dp,
                    circleSize = 96.dp
                )
                
                // Back arrow button overlaid on top-left
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 16.dp, start = 16.dp)
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            // White card/surface with rounded top corners (like a sheet)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Medicine Name
                    Text(
                        text = medicine.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                    )

                    // Chemical formula (subtitle, grey)
                    Text(
                        text = "Formula: ${medicine.formula}",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )

                    // Category chip (green outlined chip)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F8F5), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(medicine.category),
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = medicine.category,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Stock availability badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = if (medicine.stock > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (medicine.stock > 0) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (medicine.stock > 0) "In Stock" else "Out of Stock",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (medicine.stock > 0) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                            )
                        }
                    }

                    // Price (large green text, 22sp) Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Unit Price",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = "Rs. ${String.format(Locale.US, "%.2f", medicine.price)}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                    // Order Quantity Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Order Quantity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color(0xFFF1F8F5),
                                    contentColor = Color(0xFF2E7D32)
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.Remove, null)
                            }

                            BasicTextField(
                                value = quantityText,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() }
                                    if (filtered.isEmpty()) {
                                        quantityText = ""
                                        quantity = 1
                                    } else {
                                        val num = filtered.toIntOrNull()
                                        if (num != null) {
                                            if (num <= 0) {
                                                quantityText = "1"
                                                quantity = 1
                                            } else if (num > medicine.stock && medicine.stock > 0) {
                                                quantityText = medicine.stock.toString()
                                                quantity = medicine.stock
                                            } else {
                                                quantityText = num.toString()
                                                quantity = num
                                            }
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (quantityText.isEmpty()) {
                                            quantityText = "1"
                                        }
                                        keyboardController?.hide()
                                    }
                                ),
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                ),
                                singleLine = true,
                                cursorBrush = SolidColor(Color(0xFF2E7D32)),
                                modifier = Modifier
                                    .width(60.dp)
                                    .testTag("quantity_input")
                                    .padding(vertical = 4.dp),
                                decorationBox = { innerTextField ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        innerTextField()
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(2.dp)
                                                .background(Color(0xFF2E7D32))
                                        )
                                    }
                                }
                            )

                            IconButton(
                                onClick = { if (medicine.stock == 0 || quantity < medicine.stock) quantity++ },
                                enabled = medicine.stock > 0 && quantity < medicine.stock,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color(0xFFF1F8F5),
                                    contentColor = Color(0xFF2E7D32)
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.Add, null)
                            }
                        }
                    }

                    Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                    // Live calculation row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Price",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Total: Rs. ${String.format(Locale.US, "%.2f", medicine.price * quantity)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        // Sticky "Add to Cart" button at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.95f),
                            Color.White
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Button(
                onClick = { onAddToCart(quantity) },
                enabled = medicine.stock > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("add_to_cart_sticky_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    disabledContainerColor = Color.LightGray,
                    contentColor = Color.White,
                    disabledContentColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (medicine.stock > 0) "Add to Cart - Rs. ${String.format(Locale.US, "%.2f", medicine.price * quantity)}" else "Out of Stock",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CartQuantityEditor(
    quantity: Int,
    maxStock: Int,
    onQuantityChanged: (Int) -> Unit
) {
    var quantityText by remember(quantity) { mutableStateOf(quantity.toString()) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = {
                if (quantity > 1) {
                    onQuantityChanged(quantity - 1)
                }
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
        }

        BasicTextField(
            value = quantityText,
            onValueChange = { input ->
                val filtered = input.filter { it.isDigit() }
                if (filtered.isEmpty()) {
                    quantityText = ""
                    onQuantityChanged(1)
                } else {
                    val num = filtered.toIntOrNull()
                    if (num != null) {
                        if (num <= 0) {
                            quantityText = "1"
                            onQuantityChanged(1)
                        } else if (num > maxStock) {
                            quantityText = maxStock.toString()
                            onQuantityChanged(maxStock)
                        } else {
                            quantityText = num.toString()
                            onQuantityChanged(num)
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (quantityText.isEmpty()) {
                        quantityText = "1"
                    }
                    keyboardController?.hide()
                }
            ),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            singleLine = true,
            cursorBrush = SolidColor(Color(0xFF2E7D32)),
            modifier = Modifier
                .width(44.dp)
                .testTag("cart_quantity_input")
                .padding(vertical = 4.dp),
            decorationBox = { innerTextField ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    innerTextField()
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                            .background(Color(0xFF2E7D32))
                    )
                }
            }
        )

        IconButton(
            onClick = {
                if (quantity < maxStock) {
                    onQuantityChanged(quantity + 1)
                }
            },
            modifier = Modifier.size(32.dp),
            enabled = quantity < maxStock
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun DoctorCartScreen(
    viewModel: PharmacyViewModel,
    onBackToBrowse: () -> Unit,
    onOrderPlaced: () -> Unit,
    onRequireLoginForOrder: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val total by viewModel.cartTotal.collectAsStateWithLifecycle(0.0)
    val allMedicines by viewModel.allMedicines.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My Order Cart", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (cartItems.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearCart() }) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                    Text("Your order cart is empty", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = onBackToBrowse) {
                        Text("Browse Medicines")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.medicineName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(item.formula, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                Text("Rs. ${item.price} each", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val med = allMedicines.find { it.id == item.medicineId }
                                val maxStock = med?.stock ?: 9999

                                CartQuantityEditor(
                                    quantity = item.quantity,
                                    maxStock = maxStock,
                                    onQuantityChanged = { newQty ->
                                        viewModel.updateCartQuantity(item.medicineId, newQty)
                                    }
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = { viewModel.removeFromCart(item.medicineId) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Checkout Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                        Text("Rs. ${String.format("%.2f", total)}", fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery / Preparation Fee", style = MaterialTheme.typography.bodyMedium)
                        Text("FREE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Order Bill", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Rs. ${String.format("%.2f", total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Button(
                onClick = {
                    if (viewModel.currentUser.value == null) {
                        onRequireLoginForOrder()
                    } else {
                        viewModel.placeOrder(
                            onSuccess = {
                                onOrderPlaced()
                            },
                            onError = { errorMsg ->
                                onShowSnackbar(errorMsg)
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm & Dispatch Order", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DoctorOrderHistoryScreen(
    orders: List<OrderEntity>,
    viewModel: PharmacyViewModel,
    onNavigateToCart: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (index, offset) ->
                if (orders.isNotEmpty() && (index > 0 || offset > 0 || listState.isScrollInProgress)) {
                    viewModel.savedDoctorOrdersIndex = index
                    viewModel.savedDoctorOrdersOffset = offset
                } else if (orders.isNotEmpty() && index == 0 && offset == 0) {
                    viewModel.savedDoctorOrdersIndex = 0
                    viewModel.savedDoctorOrdersOffset = 0
                }
            }
    }

    LaunchedEffect(orders) {
        if (orders.isNotEmpty() && (viewModel.savedDoctorOrdersIndex > 0 || viewModel.savedDoctorOrdersOffset > 0)) {
            val targetIndex = viewModel.savedDoctorOrdersIndex.coerceAtMost(orders.lastIndex.coerceAtLeast(0))
            listState.scrollToItem(targetIndex, viewModel.savedDoctorOrdersOffset)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("My Dispatched Orders", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ListAlt, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                        Text("No orders placed yet", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    val lastOrder = orders.firstOrNull()
                    if (lastOrder != null) {
                        item {
                            val itemsList = remember(lastOrder) { viewModel.jsonToCartItems(lastOrder.itemsJson) }
                            val totalQuantity = itemsList.sumOf { it.quantity }
                            val totalPrice = itemsList.sumOf { it.price * it.quantity }
                            val dateStr = remember(lastOrder) {
                                val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                                sdf.format(Date(lastOrder.createdAt))
                            }
                            val summaryText = "$totalQuantity items • Rs. ${String.format("%.2f", totalPrice)} • $dateStr"

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.repeatLastOrder()
                                        onShowSnackbar("Last order added to cart!")
                                        onNavigateToCart()
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFFC8E6C9), shape = CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Replay,
                                                contentDescription = "Repeat icon",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = "Repeat Last Order",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E3A1E)
                                            )
                                            Text(
                                                text = summaryText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF556B2F)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        color = Color(0xFF2E7D32),
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Reorder",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = "Arrow Icon",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    items(orders) { order ->
                        val itemsList = remember(order) { viewModel.jsonToCartItems(order.itemsJson) }
                        val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Order #${order.id}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    OrderStatusBadge(order.status)
                                }

                                Text(
                                    text = "Placed: ${sdf.format(Date(order.createdAt))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )

                                Divider()

                                // Render medicine names with quantities
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    for (item in itemsList) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("${item.medicineName} x ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Rs. ${String.format("%.2f", item.price * item.quantity)}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }

                                Divider()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total Amount", fontWeight = FontWeight.Bold)
                                    Text("Rs. ${String.format("%.2f", itemsList.sumOf { it.price * it.quantity })}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating WhatsApp Need Help button
        val context = LocalContext.current
        val sharedPrefs = remember(context) { context.getSharedPreferences("user_session_pref", Context.MODE_PRIVATE) }
        val adminWhatsApp = remember(sharedPrefs) { sharedPrefs.getString("admin_whatsapp_number", ADMIN_WHATSAPP_NUMBER) ?: ADMIN_WHATSAPP_NUMBER }

        ExtendedFloatingActionButton(
            onClick = {
                val message = "Hello, I need help with my order on Daim Pharmacy app."
                val uri = Uri.parse("https://wa.me/$adminWhatsApp?text=${Uri.encode(message)}")
                try {
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.whatsapp")
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    } catch (ex: Exception) {}
                }
            },
            icon = { Icon(Icons.Default.HeadsetMic, contentDescription = "Help Icon", tint = Color.White) },
            text = { Text("Need Help?", color = Color.White, fontWeight = FontWeight.Bold) },
            containerColor = Color(0xFF2E7D32),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val config = when (status.lowercase()) {
        "pending" -> BadgeConfig("Pending", Color(0xFFF59E0B), Color(0xFFFEF3C7))
        "completed" -> BadgeConfig("Completed", Color(0xFF3B82F6), Color(0xFFDBEAFE))
        else -> BadgeConfig(status, Color.Gray, Color.LightGray)
    }

    Box(
        modifier = Modifier
            .background(color = config.bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = config.label,
            color = config.textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

data class BadgeConfig(val label: String, val textColor: Color, val bgColor: Color)


// ==================== ADMIN INTERFACE ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: PharmacyViewModel) {
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()

    var activeAdminTab by remember { mutableStateOf("orders") } // "orders" | "inventory" | "categories" | "doctors"
    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }
    var showWhatsAppConfigDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_daim_logo),
                                contentDescription = "Daim Pharmacy Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(6.dp))
                             )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daim Pharmacy",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Admin Panel",
                                fontSize = 12.sp,
                                color = Color(0xFFA5D6A7),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showWhatsAppConfigDialog = true }) {
                        Icon(Icons.Default.HeadsetMic, "WhatsApp Help Settings", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, "Logout", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            FloatingNavigationBar {
                FloatingNavigationBarItem(
                    selected = activeAdminTab == "orders",
                    onClick = { activeAdminTab = "orders" },
                    icon = {
                        val pendingCount = orders.count { it.status.equals("pending", ignoreCase = true) }
                        BadgedBox(badge = {
                            if (pendingCount > 0) {
                                Badge { Text(pendingCount.toString()) }
                            }
                        }) {
                            Icon(Icons.Default.Assignment, null)
                        }
                    },
                    label = "Orders"
                )
                FloatingNavigationBarItem(
                    selected = activeAdminTab == "inventory",
                    onClick = { activeAdminTab = "inventory" },
                    icon = { Icon(Icons.Default.Inventory, null) },
                    label = "Medicines"
                )
                FloatingNavigationBarItem(
                    selected = activeAdminTab == "categories",
                    onClick = { activeAdminTab = "categories" },
                    icon = { Icon(Icons.Default.Category, null) },
                    label = "Categories"
                )
                FloatingNavigationBarItem(
                    selected = activeAdminTab == "doctors",
                    onClick = { activeAdminTab = "doctors" },
                    icon = { Icon(Icons.Default.People, null) },
                    label = "Doctors"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            when (activeAdminTab) {
                "orders" -> {
                    AdminOrdersScreen(
                        orders = orders,
                        onOrderClick = { selectedOrderForDetail = it },
                        viewModel = viewModel
                    )
                }

                "inventory" -> {
                    AdminInventoryScreen(medicines = medicines, categories = categories, viewModel = viewModel)
                }

                "categories" -> {
                    AdminCategoriesScreen(categories = categories, viewModel = viewModel)
                }

                "doctors" -> {
                    AdminDoctorsScreen(users = users, viewModel = viewModel)
                }
            }
        }

        // Selected Order Details & Print Slip Dialog
        selectedOrderForDetail?.let { order ->
            // Re-fetch current order state from real list to reflect dynamic status changes in real-time
            val activeOrder = orders.find { it.id == order.id } ?: order
            AdminOrderDetailDialog(
                order = activeOrder,
                viewModel = viewModel,
                onDismiss = { selectedOrderForDetail = null }
            )
        }

        if (showWhatsAppConfigDialog) {
            AdminWhatsAppConfigDialog(
                onDismiss = { showWhatsAppConfigDialog = false }
            )
        }
    }
}

@Composable
fun AdminOrdersScreen(
    orders: List<OrderEntity>,
    onOrderClick: (OrderEntity) -> Unit,
    viewModel: PharmacyViewModel
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (index, offset) ->
                if (orders.isNotEmpty() && (index > 0 || offset > 0 || listState.isScrollInProgress)) {
                    viewModel.savedAdminOrdersIndex = index
                    viewModel.savedAdminOrdersOffset = offset
                } else if (orders.isNotEmpty() && index == 0 && offset == 0) {
                    viewModel.savedAdminOrdersIndex = 0
                    viewModel.savedAdminOrdersOffset = 0
                }
            }
    }

    LaunchedEffect(orders) {
        if (orders.isNotEmpty() && (viewModel.savedAdminOrdersIndex > 0 || viewModel.savedAdminOrdersOffset > 0)) {
            val targetIndex = viewModel.savedAdminOrdersIndex.coerceAtMost(orders.lastIndex.coerceAtLeast(0))
            listState.scrollToItem(targetIndex, viewModel.savedAdminOrdersOffset)
        }
    }

    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshData() },
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                color = Color(0xFF2E7D32)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        val todayStart = remember(orders) {
            java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        // Stats Overview Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminStatCard("Today's Orders", orders.size.toString(), Icons.Default.TrendingUp, modifier = Modifier.weight(1f))
            AdminStatCard("Pending", orders.count { it.status == "pending" }.toString(), Icons.Default.HourglassEmpty, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
            AdminStatCard("Completed", orders.count { it.status == "completed" && it.createdAt >= todayStart }.toString(), Icons.Default.CheckCircle, modifier = Modifier.weight(1f), color = Color(0xFF10B981))
        }

        Text("Active Dispatch Queue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        if (isLoading) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(4) {
                    AdminOrdersListShimmerItem()
                }
            }
        } else if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.HourglassEmpty, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                    Text("No orders placed yet.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(orders) { order ->
                    val itemsList = remember(order) { viewModel.jsonToCartItems(order.itemsJson) }
                    Card(
                        onClick = { onOrderClick(order) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Order #${order.id}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OrderStatusBadge(order.status)
                                }

                                Text(
                                    "Doctor: ${order.doctorName}",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    "Items: ${itemsList.joinToString { it.medicineName }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    "Time: ${sdf.format(Date(order.createdAt))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Rs. ${itemsList.sumOf { it.price * it.quantity }}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (order.status.lowercase() == "pending") {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(order.id, "completed") },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Mark Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                } else {
                                    Text(
                                        "Completed",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
fun AdminStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = color)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun AdminOrderDetailDialog(
    order: OrderEntity,
    viewModel: PharmacyViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val itemsList = remember(order) { viewModel.jsonToCartItems(order.itemsJson) }
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    var printSuccessMsg by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val maxDialogHeight = maxHeight * 0.9f
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(0.95f)
                    .heightIn(max = maxDialogHeight)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Order Execution", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, null)
                        }
                    }

                    // Polished Thermal Pharmacy Slip Preview
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Header and Logo Placeholder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFF0F766E), shape = RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "DAIM PHARMACY",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color.Black
                                )
                            }
                            
                            Text(
                                "ORDER PREPARATION SLIP",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.DarkGray
                            )
                            Divider(color = Color(0xFFCBD5E1), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                            Text("Order ID: #${order.id}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = Color.Black)
                            Text("Doctor:   Dr. ${order.doctorName}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = Color.DarkGray)
                            Text("Date:     ${sdf.format(Date(order.createdAt))}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = Color.DarkGray)
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Status:   ", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = Color.DarkGray)
                                Text(
                                    order.status.uppercase(),
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                    color = when (order.status) {
                                        "pending" -> Color(0xFFD97706)
                                        "ready" -> Color(0xFF059669)
                                        else -> Color(0xFF2563EB)
                                    }
                                )
                            }

                            Divider(color = Color(0xFFCBD5E1), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                            // Detailed Itemized List
                            Text(
                                "ITEMS SUMMARY:",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                            
                            // Scrollable list container
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f, fill = false)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (item in itemsList) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    item.medicineName,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                                    color = Color.Black
                                                )
                                                Text(
                                                    " x ${item.quantity}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                                    color = Color.Black
                                                )
                                            }
                                            Text(
                                                "Formula: ${item.formula}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (order.status.lowercase() == "pending") {
                        // Interactive Quick Actions (Pending -> Completed)
                        Text("Update Order Progress", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)

                        Button(
                            onClick = { viewModel.updateOrderStatus(order.id, "completed") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                        ) {
                            Icon(Icons.Default.DoneAll, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark as Completed")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Actual native Print Slip button
                        Button(
                            onClick = {
                                try {
                                    PrintHelper.printSlip(
                                        context,
                                        order,
                                        itemsList,
                                        sdf.format(Date(order.createdAt))
                                    )
                                    printSuccessMsg = "Sent to System Print Spooler!"
                                } catch (e: Exception) {
                                    printSuccessMsg = "Print Error: ${e.localizedMessage}"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Print, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print Slip")
                        }

                        // Share as Text button
                        OutlinedButton(
                            onClick = {
                                try {
                                    PrintHelper.shareOrderAsText(
                                        context,
                                        order,
                                        itemsList,
                                        sdf.format(Date(order.createdAt))
                                    )
                                } catch (e: Exception) {
                                    printSuccessMsg = "Share Error: ${e.localizedMessage}"
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Text")
                        }
                    }

                    printSuccessMsg?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ==================== MEDICINE INVENTORY MANAGEMENT ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInventoryScreen(
    medicines: List<MedicineEntity>,
    categories: List<CategoryEntity>,
    viewModel: PharmacyViewModel
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (index, offset) ->
                if (medicines.isNotEmpty() && (index > 0 || offset > 0 || listState.isScrollInProgress)) {
                    viewModel.savedAdminMedicinesIndex = index
                    viewModel.savedAdminMedicinesOffset = offset
                } else if (medicines.isNotEmpty() && index == 0 && offset == 0) {
                    viewModel.savedAdminMedicinesIndex = 0
                    viewModel.savedAdminMedicinesOffset = 0
                }
            }
    }

    LaunchedEffect(medicines) {
        if (medicines.isNotEmpty() && (viewModel.savedAdminMedicinesIndex > 0 || viewModel.savedAdminMedicinesOffset > 0)) {
            val targetIndex = viewModel.savedAdminMedicinesIndex.coerceAtMost(medicines.lastIndex.coerceAtLeast(0))
            listState.scrollToItem(targetIndex, viewModel.savedAdminMedicinesOffset)
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMedicineForEdit by remember { mutableStateOf<MedicineEntity?>(null) }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    var selectedFilter by remember { mutableStateOf("all") } // "all" | "expiring" | "low_stock"

    val expiringCount = remember(medicines) {
        medicines.count { getExpiryStatus(it.expiryDate) == "expiring_next_month" }
    }
    val lowStockCount = remember(medicines) {
        medicines.count { it.stock < 20 }
    }

    val filteredMeds = remember(medicines, selectedFilter) {
        when (selectedFilter) {
            "expiring" -> medicines.filter { getExpiryStatus(it.expiryDate) == "expiring_next_month" }
            "low_stock" -> medicines.filter { it.stock < 20 }
            else -> medicines
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshData() },
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                color = Color(0xFF2E7D32)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medicine Master Database",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New")
                }
            }

            // Inventory Filter Options
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == "all",
                        onClick = { selectedFilter = "all" },
                        label = { Text("All Medicines (${medicines.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "expiring",
                        onClick = { selectedFilter = "expiring" },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Expiring Next Month")
                                if (expiringCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Text(expiringCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "low_stock",
                        onClick = { selectedFilter = "low_stock" },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Low Stock")
                                if (lowStockCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFFF9800),
                                        contentColor = Color.White
                                    ) {
                                        Text(lowStockCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    )
                }
            }

            if (isLoading) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(6) {
                        AdminMedicineListShimmerItem()
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredMeds) { med ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    MedicineImage(
                                        imageUri = med.imageUri,
                                        category = med.category,
                                        contentDescription = med.name,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        showCircle = false,
                                        iconSize = 28.dp
                                    )

                                    Column {
                                        Text(med.name, fontWeight = FontWeight.Bold)
                                        Text("Formula: ${med.formula}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                        Text("Category: ${med.category} | Price: Rs. ${med.price} | Stock: ${med.stock}", style = MaterialTheme.typography.bodySmall)
                                        if (!med.expiryDate.isNullOrBlank()) {
                                            val status = getExpiryStatus(med.expiryDate)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(top = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Event,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = if (status == "expiring_next_month" || status == "expired" || status == "expired_soon_current_month") {
                                                        MaterialTheme.colorScheme.error
                                                    } else {
                                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                                    }
                                                )
                                                Text(
                                                    text = "Expiry: ${med.expiryDate}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (status == "expiring_next_month" || status == "expired" || status == "expired_soon_current_month") {
                                                        MaterialTheme.colorScheme.error
                                                    } else {
                                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                                    },
                                                    fontWeight = if (status != "active") FontWeight.Bold else FontWeight.Normal
                                                )
                                                
                                                when (status) {
                                                    "expired" -> {
                                                        SuggestionChip(
                                                            onClick = {},
                                                            label = { Text("Expired", fontSize = 10.sp, color = MaterialTheme.colorScheme.onError) },
                                                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.error)
                                                        )
                                                    }
                                                    "expired_soon_current_month" -> {
                                                        SuggestionChip(
                                                            onClick = {},
                                                            label = { Text("Expiring This Month", fontSize = 10.sp, color = Color.White) },
                                                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFD84315))
                                                        )
                                                    }
                                                    "expiring_next_month" -> {
                                                        SuggestionChip(
                                                            onClick = {},
                                                            label = { Text("Expiring Next Month", fontSize = 10.sp, color = Color.White) },
                                                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFFF8F00))
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { selectedMedicineForEdit = med }) {
                                        Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.deleteMedicine(med) }) {
                                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showAddDialog) {
                AddMedicineDialog(
                    categories = categories,
                    onDismiss = { showAddDialog = false },
                    onAdd = { name, formula, cat, price, stock, imageUri, expiryDate ->
                        viewModel.addMedicine(name, formula, cat, price, stock, imageUri, expiryDate)
                        showAddDialog = false
                    }
                )
            }

            selectedMedicineForEdit?.let { med ->
                EditMedicineDialog(
                    medicine = med,
                    categories = categories,
                    onDismiss = { selectedMedicineForEdit = null },
                    onSave = { updated ->
                        viewModel.updateMedicine(updated)
                        selectedMedicineForEdit = null
                    }
                )
            }
        }
    }
}

@Composable
fun AddMedicineDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Double, Int, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(categories.firstOrNull()?.name ?: "Tablets") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    var imageUriPath by remember { mutableStateOf<String?>(null) }
    var imageUrlField by remember { mutableStateOf("") }

    var isScanning by remember { mutableStateOf(false) }
    var scanError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val localPath = saveAndProcessUriToLocalFile(context, it)
            if (localPath != null) {
                imageUriPath = localPath
                imageUrlField = "" // Clear URL field if we picked a local file
            }
        }
    }

    val scanCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { b ->
            coroutineScope.launch {
                isScanning = true
                scanError = null
                try {
                    val result = GeminiService.analyzeMedicineImage(b)
                    if (result != null) {
                        name = result.name
                        formula = result.formula
                        stock = "10000"
                        if (!result.category.isNullOrBlank()) {
                            val matchedCat = categories.firstOrNull { cat -> cat.name.equals(result.category, ignoreCase = true) }
                            if (matchedCat != null) {
                                selectedCat = matchedCat.name
                            }
                        }
                        // Apply the local background-brightening threshold filter
                        val processedBitmap = com.example.data.LocalImageProcessor.processMedicineImage(b)
                        val tempPath = saveBitmapToLocalFile(context, processedBitmap)
                        if (tempPath != null) {
                            imageUriPath = tempPath
                            imageUrlField = ""
                        }
                    } else {
                        scanError = "Failed to extract medicine details. Please try again."
                    }
                } catch (e: Exception) {
                    scanError = "Error: ${e.message}"
                } finally {
                    isScanning = false
                }
            }
        }
    }

    val scanGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            coroutineScope.launch {
                isScanning = true
                scanError = null
                try {
                    val inputStream = context.contentResolver.openInputStream(selectedUri)
                    val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        val result = GeminiService.analyzeMedicineImage(bitmap)
                        if (result != null) {
                            name = result.name
                            formula = result.formula
                            stock = "10000"
                            if (!result.category.isNullOrBlank()) {
                                val matchedCat = categories.firstOrNull { cat -> cat.name.equals(result.category, ignoreCase = true) }
                                if (matchedCat != null) {
                                    selectedCat = matchedCat.name
                                }
                            }
                            val localPath = saveAndProcessUriToLocalFile(context, selectedUri)
                            if (localPath != null) {
                                imageUriPath = localPath
                                imageUrlField = ""
                            }
                        } else {
                            scanError = "Failed to extract medicine details. Please try again."
                        }
                    } else {
                        scanError = "Failed to load selected image."
                    }
                } catch (e: Exception) {
                    scanError = "Error: ${e.message}"
                } finally {
                    isScanning = false
                }
            }
        }
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val monthStr = String.format("%02d", month + 1)
            val dayStr = String.format("%02d", dayOfMonth)
            expiryDate = "$year-$monthStr-$dayStr"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Add New Medicine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // AI Scanning Banner / Options
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Scanner",
                                tint = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "AI Medicine Scanner",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        
                        Text(
                            text = "Take a picture of medicine packaging or choose from gallery to automatically fill the details!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF558B2F),
                            textAlign = TextAlign.Center
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "💡 Best Result Tips / Behtreen Tareeqa:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "1. Place the medicine on a flat surface and take a photo without holding it in your hand.\n2. Medicine ko niche rakh kar photo khainchein, haath mein mat pakrein.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF33691E)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "✨ Our smart filter will automatically clean and brighten the background locally!",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                        
                        if (isScanning) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Analyzing medicine packaging...",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = { scanCameraLauncher.launch(null) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32)),
                                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Take Photo", style = MaterialTheme.typography.labelMedium)
                                }
                                
                                Button(
                                    onClick = { scanGalleryLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("From Gallery", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                        
                        scanError?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Medicine Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = formula, onValueChange = { formula = it }, label = { Text("Formula") }, modifier = Modifier.fillMaxWidth())

                // Quick selector for category
                Text("Select Category", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCat == cat.name,
                            onClick = { selectedCat = cat.name },
                            label = { Text(cat.name) }
                        )
                    }
                }

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (YYYY-MM-DD)") },
                    placeholder = { Text("e.g. 2026-12-31 (Optional)") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.Event, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )

                // Medicine Image Section
                Text("Medicine Image (Web URL or Upload)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                
                OutlinedTextField(
                    value = imageUrlField,
                    onValueChange = { 
                        imageUrlField = it
                        imageUriPath = if (it.isNotBlank()) it else null
                    },
                    label = { Text("Web Image URL (Option A - Shared Globally)") },
                    placeholder = { Text("https://images.unsplash.com/...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Or Local Device Upload", style = MaterialTheme.typography.labelMedium, color = Color.Gray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (imageUriPath != null) {
                        MedicineImage(
                            imageUri = imageUriPath,
                            category = selectedCat,
                            contentDescription = "Preview",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(12.dp)),
                            iconSize = 24.dp,
                            circleSize = 48.dp
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change Local Image", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = { 
                                    imageUriPath = null 
                                    imageUrlField = ""
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(Color(0xFFF1F8F5), RoundedCornerShape(12.dp))
                                .clickable { galleryLauncher.launch("image/*") }
                                .border(BorderStroke(1.dp, Color(0xFFC8E6C9)), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Image, null, tint = Color(0xFF2E7D32))
                                Text("Upload Local Photo", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            val priceVal = price.toDoubleOrNull() ?: 0.0
                            val stockVal = stock.toIntOrNull() ?: 0
                            onAdd(name, formula, selectedCat, priceVal, stockVal, imageUriPath, expiryDate.ifBlank { null })
                        },
                        enabled = name.isNotBlank() && formula.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun EditMedicineDialog(
    medicine: MedicineEntity,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (MedicineEntity) -> Unit
) {
    var name by remember { mutableStateOf(medicine.name) }
    var formula by remember { mutableStateOf(medicine.formula) }
    var selectedCat by remember { mutableStateOf(medicine.category) }
    var price by remember { mutableStateOf(medicine.price.toString()) }
    var stock by remember { mutableStateOf(medicine.stock.toString()) }

    val context = LocalContext.current
    var imageUriPath by remember { mutableStateOf<String?>(medicine.imageUri) }
    var imageUrlField by remember { mutableStateOf(if (medicine.imageUri?.startsWith("http") == true) medicine.imageUri ?: "" else "") }
    var expiryDate by remember { mutableStateOf(medicine.expiryDate ?: "") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val localPath = saveAndProcessUriToLocalFile(context, it)
            if (localPath != null) {
                imageUriPath = localPath
                imageUrlField = "" // Clear URL field if we picked a local file
            }
        }
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val monthStr = String.format("%02d", month + 1)
            val dayStr = String.format("%02d", dayOfMonth)
            expiryDate = "$year-$monthStr-$dayStr"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Edit Medicine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Medicine Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = formula, onValueChange = { formula = it }, label = { Text("Formula") }, modifier = Modifier.fillMaxWidth())

                Text("Select Category", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCat == cat.name,
                            onClick = { selectedCat = cat.name },
                            label = { Text(cat.name) }
                        )
                    }
                }

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (YYYY-MM-DD)") },
                    placeholder = { Text("e.g. 2026-12-31 (Optional)") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.Event, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )

                // Medicine Image Section
                Text("Medicine Image (Web URL or Upload)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                
                OutlinedTextField(
                    value = imageUrlField,
                    onValueChange = { 
                        imageUrlField = it
                        imageUriPath = if (it.isNotBlank()) it else null
                    },
                    label = { Text("Web Image URL (Option A - Shared Globally)") },
                    placeholder = { Text("https://images.unsplash.com/...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Or Local Device Upload", style = MaterialTheme.typography.labelMedium, color = Color.Gray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (imageUriPath != null) {
                        MedicineImage(
                            imageUri = imageUriPath,
                            category = selectedCat,
                            contentDescription = "Preview",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(12.dp)),
                            iconSize = 24.dp,
                            circleSize = 48.dp
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change Local Image", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = { 
                                    imageUriPath = null 
                                    imageUrlField = ""
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(Color(0xFFF1F8F5), RoundedCornerShape(12.dp))
                                .clickable { galleryLauncher.launch("image/*") }
                                .border(BorderStroke(1.dp, Color(0xFFC8E6C9)), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Image, null, tint = Color(0xFF2E7D32))
                                Text("Upload Local Photo", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            val priceVal = price.toDoubleOrNull() ?: medicine.price
                            val stockVal = stock.toIntOrNull() ?: medicine.stock
                            onSave(medicine.copy(name = name, formula = formula, category = selectedCat, price = priceVal, stock = stockVal, imageUri = imageUriPath, expiryDate = expiryDate.ifBlank { null }))
                        },
                        enabled = name.isNotBlank() && formula.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// ==================== CATEGORIES SCREEN ====================

@Composable
fun AdminCategoriesScreen(
    categories: List<CategoryEntity>,
    viewModel: PharmacyViewModel
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (index, offset) ->
                if (categories.isNotEmpty() && (index > 0 || offset > 0 || listState.isScrollInProgress)) {
                    viewModel.savedAdminCategoriesIndex = index
                    viewModel.savedAdminCategoriesOffset = offset
                } else if (categories.isNotEmpty() && index == 0 && offset == 0) {
                    viewModel.savedAdminCategoriesIndex = 0
                    viewModel.savedAdminCategoriesOffset = 0
                }
            }
    }

    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && (viewModel.savedAdminCategoriesIndex > 0 || viewModel.savedAdminCategoriesOffset > 0)) {
            val targetIndex = viewModel.savedAdminCategoriesIndex.coerceAtMost(categories.lastIndex.coerceAtLeast(0))
            listState.scrollToItem(targetIndex, viewModel.savedAdminCategoriesOffset)
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Medicine Categories",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Category")
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(categories) { category ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(getCategoryIcon(category.name), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                            Text(category.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        }

                        IconButton(onClick = { viewModel.deleteCategory(category) }) {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Add New Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Category Name (e.g. Syrups, Injections)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { showAddDialog = false }, modifier = Modifier.weight(1f)) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (name.isNotBlank()) {
                                        viewModel.addCategory(name.trim(), "medication")
                                        name = ""
                                        showAddDialog = false
                                    }
                                },
                                enabled = name.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== DOCTOR ACCOUNT MANAGEMENT SCREEN ====================

@Composable
fun AdminDoctorsScreen(
    users: List<UserEntity>,
    viewModel: PharmacyViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newUserEmail by remember { mutableStateOf("") }
    var newUserPassword by remember { mutableStateOf("") }
    var newUserRole by remember { mutableStateOf("doctor") } // "doctor" | "admin"

    var showEditDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserEntity?>(null) }
    var editUserName by remember { mutableStateOf("") }
    var editUserEmail by remember { mutableStateOf("") }
    var editUserPassword by remember { mutableStateOf("") }
    var editUserRole by remember { mutableStateOf("doctor") }
    var editUserActive by remember { mutableStateOf(true) }

    var selectedFilter by remember { mutableStateOf("all") } // "all" | "doctor" | "admin"

    val filteredUsers = remember(users, selectedFilter) {
        when (selectedFilter) {
            "doctor" -> users.filter { it.role == "doctor" }
            "admin" -> users.filter { it.role == "admin" }
            else -> users
        }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) }
            .collect { (index, offset) ->
                if (filteredUsers.isNotEmpty() && (index > 0 || offset > 0 || listState.isScrollInProgress)) {
                    viewModel.savedAdminDoctorsIndex = index
                    viewModel.savedAdminDoctorsOffset = offset
                } else if (filteredUsers.isNotEmpty() && index == 0 && offset == 0) {
                    viewModel.savedAdminDoctorsIndex = 0
                    viewModel.savedAdminDoctorsOffset = 0
                }
            }
    }

    LaunchedEffect(filteredUsers) {
        if (filteredUsers.isNotEmpty() && (viewModel.savedAdminDoctorsIndex > 0 || viewModel.savedAdminDoctorsOffset > 0)) {
            val targetIndex = viewModel.savedAdminDoctorsIndex.coerceAtMost(filteredUsers.lastIndex.coerceAtLeast(0))
            listState.scrollToItem(targetIndex, viewModel.savedAdminDoctorsOffset)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Accounts Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    newUserName = ""
                    newUserEmail = ""
                    newUserPassword = ""
                    newUserRole = "doctor"
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.PersonAdd, null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add User")
            }
        }

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                "all" to "All Users (${users.size})",
                "doctor" to "Doctors (${users.count { it.role == "doctor" }})",
                "admin" to "Admins (${users.count { it.role == "admin" }})"
            )
            filters.forEach { (key, label) ->
                val isSelected = selectedFilter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = key },
                    label = { Text(label) }
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredUsers) { u ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            color = if (!u.isActive) {
                                                Color.LightGray.copy(alpha = 0.3f)
                                            } else if (u.role == "admin") {
                                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                                            } else {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            },
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (u.role == "admin") Icons.Default.SupervisorAccount else Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = if (!u.isActive) {
                                            Color.Gray
                                        } else if (u.role == "admin") {
                                            MaterialTheme.colorScheme.tertiary
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = u.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        // Role Badge
                                        val badgeColor = if (u.role == "admin") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer
                                        val badgeTextColor = if (u.role == "admin") MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                                        Box(
                                            modifier = Modifier
                                                .background(badgeColor, shape = RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = u.role.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeTextColor
                                            )
                                        }
                                    }
                                    Text(
                                        text = u.email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "Password: ${u.password}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        editingUser = u
                                        editUserName = u.name
                                        editUserEmail = u.email
                                        editUserPassword = u.password
                                        editUserRole = u.role
                                        editUserActive = u.isActive
                                        showEditDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit User",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (u.email != "admin@daim.com") {
                                    IconButton(
                                        onClick = { viewModel.deleteUser(u) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete User",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Dialog
        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Add New User Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = newUserName,
                            onValueChange = { newUserName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newUserEmail,
                            onValueChange = { newUserEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newUserPassword,
                            onValueChange = { newUserPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Role Selector
                        Text("Role", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = newUserRole == "doctor",
                                onClick = { newUserRole = "doctor" },
                                label = { Text("Doctor") },
                                leadingIcon = {
                                    if (newUserRole == "doctor") {
                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                            FilterChip(
                                selected = newUserRole == "admin",
                                onClick = { newUserRole = "admin" },
                                label = { Text("Admin") },
                                leadingIcon = {
                                    if (newUserRole == "admin") {
                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    newUserName = ""
                                    newUserEmail = ""
                                    newUserPassword = ""
                                    newUserRole = "doctor"
                                    showAddDialog = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    if (newUserName.isNotBlank() && newUserEmail.isNotBlank() && newUserPassword.isNotBlank()) {
                                        viewModel.createAccount(
                                            name = newUserName.trim(),
                                            email = newUserEmail.trim(),
                                            role = newUserRole,
                                            password = newUserPassword.trim()
                                        )
                                        newUserName = ""
                                        newUserEmail = ""
                                        newUserPassword = ""
                                        showAddDialog = false
                                    }
                                },
                                enabled = newUserName.isNotBlank() && newUserEmail.isNotBlank() && newUserPassword.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Create")
                            }
                        }
                    }
                }
            }
        }

        // Edit Dialog
        if (showEditDialog && editingUser != null) {
            Dialog(onDismissRequest = { showEditDialog = false }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Edit User Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = editUserName,
                            onValueChange = { editUserName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editUserEmail,
                            onValueChange = { editUserEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, null) },
                            singleLine = true,
                            enabled = false, // Email is the unique ID
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editUserPassword,
                            onValueChange = { editUserPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Role Selector
                        Text("Role", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = editUserRole == "doctor",
                                onClick = { editUserRole = "doctor" },
                                label = { Text("Doctor") },
                                leadingIcon = {
                                    if (editUserRole == "doctor") {
                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                            FilterChip(
                                selected = editUserRole == "admin",
                                onClick = { editUserRole = "admin" },
                                label = { Text("Admin") },
                                leadingIcon = {
                                    if (editUserRole == "admin") {
                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                        }

                        // Active Toggle in Dialog
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Account Status", fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(if (editUserActive) "Active" else "Deactivated", style = MaterialTheme.typography.bodySmall)
                                Switch(
                                    checked = editUserActive,
                                    onCheckedChange = { editUserActive = it }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    editingUser = null
                                    showEditDialog = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    val userToUpdate = editingUser?.copy(
                                        name = editUserName.trim(),
                                        password = editUserPassword.trim(),
                                        role = editUserRole,
                                        isActive = editUserActive
                                    )
                                    if (userToUpdate != null) {
                                        viewModel.updateAccount(userToUpdate)
                                        editingUser = null
                                        showEditDialog = false
                                    }
                                },
                                enabled = editUserName.isNotBlank() && editUserPassword.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Changes")
                            }
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun AdminWhatsAppConfigDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val sharedPrefs = remember(context) { context.getSharedPreferences("user_session_pref", Context.MODE_PRIVATE) }
    val initialNumber = remember(sharedPrefs) { sharedPrefs.getString("admin_whatsapp_number", ADMIN_WHATSAPP_NUMBER) ?: ADMIN_WHATSAPP_NUMBER }

    var number by remember { mutableStateOf(initialNumber) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "WhatsApp Help Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                Text(
                    "Update the WhatsApp support number that doctors will contact for support and orders.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    "وہ واٹس ایپ نمبر درج کریں جس پر ڈاکٹرز آپ سے رابطہ کر سکیں۔ نمبر میں کنٹری کوڈ شامل کریں (مثال کے طور پر 923109551558)۔ پلس (+) یا سپیس استعمال نہ کریں۔",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = { input ->
                        number = input.filter { it.isDigit() }
                    },
                    label = { Text("WhatsApp Contact Number") },
                    placeholder = { Text("e.g. 923109551558") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            sharedPrefs.edit().putString("admin_whatsapp_number", number).apply()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

fun getExpiryStatus(expiryDateStr: String?): String {
    if (expiryDateStr.isNullOrBlank()) return "active"
    try {
        val parts = expiryDateStr.split("-")
        if (parts.size < 3) return "active"
        val year = parts[0].toIntOrNull() ?: return "active"
        val month = parts[1].toIntOrNull() ?: return "active"
        val day = parts[2].toIntOrNull() ?: return "active"

        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH) + 1 // 1-12
        val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)

        // Compare year first
        if (year < currentYear) return "expired"
        if (year == currentYear) {
            if (month < currentMonth) return "expired"
            if (month == currentMonth) {
                if (day < currentDay) return "expired"
                return "expired_soon_current_month"
            }
        }

        // Check if next month
        var targetMonth = currentMonth + 1
        var targetYear = currentYear
        if (targetMonth > 12) {
            targetMonth = 1
            targetYear += 1
        }
        if (year == targetYear && month == targetMonth) {
            return "expiring_next_month"
        }

        return "active"
    } catch (e: Exception) {
        return "active"
    }
}
