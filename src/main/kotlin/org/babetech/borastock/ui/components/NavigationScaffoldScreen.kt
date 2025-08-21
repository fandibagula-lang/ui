package org.babetech.borastock.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.babetech.borastock.ui.navigation.AppDestinations
import org.babetech.borastock.ui.screens.auth.viewmodel.LoginViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Écran principal avec système de navigation adaptatif et design moderne
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScaffoldScreen(
    title: String,
    currentDestination: AppDestinations,
    onDestinationChanged: (AppDestinations) -> Unit,
    onThemeChange: (String) -> Unit,
    currentTheme: String,
    onProfileClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val viewModel = koinViewModel<LoginViewModel>()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var showNavigationDrawer by rememberSaveable { mutableStateOf(true) }

    BoxWithConstraints {
        val isCompact = maxWidth < 600.dp
        val isMedium = maxWidth in 600.dp..839.dp
        val isExpanded = maxWidth >= 840.dp

        val layoutType = when {
            isExpanded && showNavigationDrawer -> NavigationSuiteType.NavigationDrawer
            isExpanded && !showNavigationDrawer -> NavigationSuiteType.NavigationRail
            isMedium -> NavigationSuiteType.NavigationRail
            else -> NavigationSuiteType.NavigationBar
        }

        val destinationsToShow = if (layoutType == NavigationSuiteType.NavigationBar)
            AppDestinations.entries.take(5)
        else AppDestinations.entries

        Scaffold(
            topBar = {
                ModernTopAppBar(
                    title = title,
                    currentUser = currentUser,
                    isExpanded = isExpanded,
                    showNavigationDrawer = showNavigationDrawer,
                    onMenuClick = { showNavigationDrawer = !showNavigationDrawer },
                    isMenuExpanded = isMenuExpanded,
                    onMenuExpandedChange = { isMenuExpanded = it },
                    onProfileClick = onProfileClick,
                    onLogout = { viewModel.logout() },
                    currentTheme = currentTheme,
                    onThemeChange = onThemeChange
                )
            }
        ) { innerPadding ->
            NavigationSuiteScaffold(
                modifier = Modifier.padding(innerPadding),
                layoutType = layoutType,
                navigationSuiteItems = {
                    destinationsToShow.forEach { dest ->
                        item(
                            selected = currentDestination == dest,
                            onClick = { onDestinationChanged(dest) },
                            icon = {
                                NavigationIcon(
                                    destination = dest,
                                    isSelected = currentDestination == dest
                                )
                            },
                            label = {
                                if (layoutType != NavigationSuiteType.NavigationBar) {
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn() + slideInHorizontally()
                                    ) {
                                        Text(
                                            text = dest.label,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (currentDestination == dest) 
                                                    FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (currentDestination == dest)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            ) {
                content()
            }
        }
    }
}

/**
 * TopAppBar moderne avec design amélioré
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopAppBar(
    title: String,
    currentUser: Any?,
    isExpanded: Boolean,
    showNavigationDrawer: Boolean,
    onMenuClick: () -> Unit,
    isMenuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    currentTheme: String,
    onThemeChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigation et titre
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isExpanded) {
                        ModernMenuButton(
                            isOpen = showNavigationDrawer,
                            onClick = onMenuClick
                        )
                    }
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModernThemeSwitcher(
                        currentTheme = currentTheme,
                        onThemeChange = onThemeChange
                    )
                    
                    ModernProfileButton(
                        currentUser = currentUser,
                        isMenuExpanded = isMenuExpanded,
                        onMenuExpandedChange = onMenuExpandedChange,
                        onProfileClick = onProfileClick,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

/**
 * Bouton de menu animé moderne
 */
@Composable
private fun ModernMenuButton(
    isOpen: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isOpen) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "menu_rotation"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = if (isOpen) "Fermer le menu" else "Ouvrir le menu",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer { rotationZ = rotation }
        )
    }
}

/**
 * Commutateur de thème moderne avec animation
 */
@Composable
private fun ModernThemeSwitcher(
    currentTheme: String,
    onThemeChange: (String) -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }
    
    IconButton(
        onClick = {
            if (!isAnimating) {
                isAnimating = true
                onThemeChange(if (currentTheme == "light") "dark" else "light")
            }
        },
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        LaunchedEffect(currentTheme) {
            if (isAnimating) {
                delay(500)
                isAnimating = false
            }
        }

        val scale by animateFloatAsState(
            targetValue = if (isAnimating) 0.8f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh
            ),
            label = "theme_scale"
        )

        val rotation by animateFloatAsState(
            targetValue = if (isAnimating) 360f else 0f,
            animationSpec = tween(500, easing = EaseInOutCubic),
            label = "theme_rotation"
        )

        Crossfade(
            targetState = currentTheme,
            animationSpec = tween(300),
            label = "theme_crossfade"
        ) { theme ->
            Icon(
                painter = painterResource(
                    if (theme == "dark") Res.drawable.DarkMode else Res.drawable.LightMode
                ),
                contentDescription = if (theme == "dark") "Mode sombre" else "Mode clair",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationZ = rotation
                    }
            )
        }
    }
}

/**
 * Bouton de profil moderne avec menu déroulant stylisé
 */
@Composable
private fun ModernProfileButton(
    currentUser: Any?,
    isMenuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    Box {
        IconButton(
            onClick = { onMenuExpandedChange(true) },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data("https://via.placeholder.com/150") // currentUser?.profile_picture_uri
                    .crossfade(true)
                    .build(),
                contentDescription = "Photo de profil",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        ModernDropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { onMenuExpandedChange(false) },
            currentUser = currentUser,
            onProfileClick = {
                onMenuExpandedChange(false)
                onProfileClick()
            },
            onLogout = {
                onMenuExpandedChange(false)
                onLogout()
            }
        )
    }
}

/**
 * Menu déroulant moderne avec design amélioré
 */
@Composable
private fun ModernDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    currentUser: Any?,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // En-tête utilisateur
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data("https://via.placeholder.com/150") // currentUser?.profile_picture_uri
                        .crossfade(true)
                        .build(),
                    contentDescription = "Photo de profil",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
                
                Column {
                    Text(
                        text = "Utilisateur", // currentUser?.name ?: "Utilisateur"
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "email@example.com", // currentUser?.email ?: "email@example.com"
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Mon Entreprise", // currentUser?.company ?: "Entreprise"
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        // Options du menu
        ModernDropdownMenuItem(
            text = "Mon profil",
            icon = Icons.Default.Person,
            onClick = onProfileClick
        )
        
        ModernDropdownMenuItem(
            text = "Paramètres",
            icon = Icons.Default.Settings,
            onClick = { /* TODO */ }
        )
        
        ModernDropdownMenuItem(
            text = "Aide",
            icon = Icons.Default.Help,
            onClick = { /* TODO */ }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        ModernDropdownMenuItem(
            text = "Déconnexion",
            icon = Icons.Default.ExitToApp,
            onClick = onLogout,
            isDestructive = true
        )
    }
}

/**
 * Élément de menu déroulant moderne
 */
@Composable
private fun ModernDropdownMenuItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isDestructive) 
                    MaterialTheme.colorScheme.error 
                else MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) 
                    MaterialTheme.colorScheme.error 
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        },
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

/**
 * Icône de navigation avec animation
 */
@Composable
private fun NavigationIcon(
    destination: AppDestinations,
    isSelected: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (isSelected) 
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = destination.icon(),
            contentDescription = destination.contentDescription,
            tint = if (isSelected)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}