package org.babetech.borastock.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.DarkMode
import borastock.composeapp.generated.resources.LightMode
import borastock.composeapp.generated.resources.Res
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.babetech.borastock.data.models.PreferencesModel
import org.babetech.borastock.ui.navigation.AppDestinations
import org.babetech.borastock.ui.screens.auth.viewmodel.LoginViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Suppress("UnusedBoxWithConstraintsScope")
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

    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val currentUser = viewModel.currentUser.collectAsStateWithLifecycle()


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
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    },
                    navigationIcon = {
                        if (isExpanded) {
                            IconButton(onClick = { showNavigationDrawer = !showNavigationDrawer }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { isMenuExpanded = true }) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(currentUser.value?.profile_picture_uri.toString())
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profil",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            // Header with user and company info
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = currentUser.value?.name ?: "Utilisateur", fontWeight = FontWeight.Bold)
                                Text(text = currentUser.value?.email ?: "email@example.com", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = currentUser.value?.name ?: "Entreprise", fontWeight = FontWeight.Medium)
                            }
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Mon profil") },
                                onClick = {
                                    isMenuExpanded = false
                                    onProfileClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Déconnexion") },
                                onClick = {
                                    isMenuExpanded = false
                                    viewModel.logout()
                                }
                            )
                        }

                        ThemeSwitcher(
                            onThemeChange = onThemeChange,
                            currentTheme = currentTheme
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
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
                                Icon(
                                    painter = dest.icon(),
                                    contentDescription = dest.contentDescription,
                                    tint = if (currentDestination == dest)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            label = {
                                if (layoutType != NavigationSuiteType.NavigationBar) {
                                    Text(
                                        text = dest.label,
                                        color = if (currentDestination == dest)
                                            MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
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

@Composable
fun ThemeSwitcher(onThemeChange: (String) -> Unit, currentTheme: String) {
    Button(onClick = {
        if (currentTheme == "light") onThemeChange("dark") else onThemeChange("light")
    }) {
        Crossfade(targetState = currentTheme, animationSpec = tween(500)) { theme ->
            if (theme == "dark") {
                Icon(
                    painter = painterResource(Res.drawable.DarkMode),
                    contentDescription = "Thème sombre"
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.LightMode),
                    contentDescription = "Thème clair"
                )
            }
        }
    }
}
