package org.babetech.borastock.ui.screens.screennavigation.Settings


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.babetech.borastock.data.models.SettingCategory
import org.babetech.borastock.data.models.SettingItem
import org.babetech.borastock.data.models.SettingType
import org.jetbrains.compose.resources.painterResource





@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SettingsScreen() {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    var selectedSettingItem by remember { mutableStateOf<SettingItem?>(null) }

    // États pour les switches
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var autoBackupEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }

    // Catégories de paramètres
    val settingCategories = remember(notificationsEnabled, darkModeEnabled, autoBackupEnabled, soundEnabled) {
        listOf(
            SettingCategory(
                title = "Compte",
                items = listOf(
                    SettingItem(
                        title = "Profil utilisateur",
                        subtitle = "Gérer vos informations personnelles",
                        icon =Res.drawable.Person,
                        type = SettingType.NAVIGATION,
                        action = { /* Navigation vers profil */ }
                    ),
                    SettingItem(
                        title = "Sécurité",
                        subtitle = "Mot de passe et authentification",
                        icon = Res.drawable.Setting,
                        type = SettingType.NAVIGATION,
                        action = { /* Navigation vers sécurité */ }
                    )
                )
            ),
            SettingCategory(
                title = "Application",
                items = listOf(
                    SettingItem(
                        title = "Notifications",
                        subtitle = "Recevoir des alertes et notifications",
                        icon = Res.drawable.Setting,
                        type = SettingType.SWITCH,
                        switchState = notificationsEnabled,
                        onSwitchChanged = { notificationsEnabled = it }
                    ),
                    SettingItem(
                        title = "Mode sombre",
                        subtitle = "Activer le thème sombre",
                        icon = Res.drawable.DarkMode,
                        type = SettingType.SWITCH,
                        switchState = darkModeEnabled,
                        onSwitchChanged = { darkModeEnabled = it }
                    ),
                    SettingItem(
                        title = "Sons",
                        subtitle = "Sons de l'application",
                        icon =Res.drawable.Setting,
                        type = SettingType.SWITCH,
                        switchState = soundEnabled,
                        onSwitchChanged = { soundEnabled = it }
                    )
                )
            ),
            SettingCategory(
                title = "Données",
                items = listOf(
                    SettingItem(
                        title = "Sauvegarde automatique",
                        subtitle = "Sauvegarder automatiquement vos données",
                        icon =Res.drawable.Setting,
                        type = SettingType.SWITCH,
                        switchState = autoBackupEnabled,
                        onSwitchChanged = { autoBackupEnabled = it }
                    ),
                    SettingItem(
                        title = "Exporter les données",
                        subtitle = "Télécharger vos données",
                        icon = Res.drawable.Setting,
                        type = SettingType.ACTION,
                        action = { /* Action d'export */ }
                    ),
                    SettingItem(
                        title = "Importer les données",
                        subtitle = "Importer des données depuis un fichier",
                        icon =Res.drawable.Setting,
                        type = SettingType.ACTION,
                        action = { /* Action d'import */ }
                    )
                )
            ),
            SettingCategory(
                title = "Support",
                items = listOf(
                    SettingItem(
                        title = "Centre d'aide",
                        subtitle = "FAQ et documentation",
                        icon = Res.drawable.Setting,
                        type = SettingType.NAVIGATION,
                        action = { /* Navigation vers aide */ }
                    ),
                    SettingItem(
                        title = "Contacter le support",
                        subtitle = "Obtenir de l'aide",
                        icon = Res.drawable.Setting,
                        type = SettingType.ACTION,
                        action = { /* Action contact */ }
                    ),
                    SettingItem(
                        title = "À propos",
                        subtitle = "Version 1.0.0",
                        icon = Res.drawable.Setting,
                        type = SettingType.INFO
                    )
                )
            )
        )
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                                )
                            )
                        ),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // En-tête
                    item {
                        SettingsHeader()
                    }

                    // Catégories de paramètres
                    items(settingCategories) { category ->
                        SettingCategoryCard(
                            category = category,
                            onItemClick = { item ->
                                selectedSettingItem = item
                                if (item.type == SettingType.NAVIGATION) {
                                    scope.launch {
                                        navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                    }
                                } else {
                                    item.action?.invoke()
                                }
                            }
                        )
                    }

                    // Actions dangereuses
                    item {
                        DangerousActionsCard()
                    }
                }
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedSettingItem?.let { item ->
                    SettingDetailPane(
                        item = item,
                        onBackClick = {
                            scope.launch {
                                navigator.navigateBack()
                            }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded
                    )
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sélectionnez un paramètre pour voir les détails",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun SettingsHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .shadow(
//                elevation = 4.dp,
//                shape = RoundedCornerShape(16.dp)
//            ),
       , shape = RoundedCornerShape(16.dp),
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
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.Setting),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Paramètres",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Personnalisez votre expérience BoraStock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingCategoryCard(
    category: SettingCategory,
    onItemClick: (SettingItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .shadow(
//                elevation = 2.dp,
//                shape = RoundedCornerShape(12.dp)
//            ),
        ,shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            category.items.forEachIndexed { index, item ->
                SettingItemRow(
                    item = item,
                    onClick = { onItemClick(item) }
                )
                if (index < category.items.size - 1) {
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItemRow(
    item: SettingItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                item.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        when (item.type) {
            SettingType.SWITCH -> {
                Switch(
                    checked = item.switchState,
                    onCheckedChange = item.onSwitchChanged ?: {},
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
            SettingType.NAVIGATION -> {
                Icon(
                    painterResource(Res.drawable.ChevronRight),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            SettingType.ACTION -> {
                Icon(
                    painterResource(Res.drawable.Launch),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            SettingType.INFO -> {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }


        }
    }
}

@Composable
private fun DangerousActionsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Zone de danger",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.error
            )

            OutlinedButton(
                onClick = { /* Action de réinitialisation */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.error,
                            MaterialTheme.colorScheme.error
                        )
                    )
                )
            ) {
                Icon(
                    painterResource(Res.drawable.ic_delete),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Réinitialiser l'application")
            }

            OutlinedButton(
                onClick = { /* Action de suppression de compte */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.error,
                            MaterialTheme.colorScheme.error
                        )
                    )
                )
            ) {
                Icon(
                    painterResource(Res.drawable.PersonRemove),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Supprimer le compte")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingDetailPane(
    item: SettingItem,
    onBackClick: () -> Unit,
    showBackButton: Boolean
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.title) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icône et titre
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    item.subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Divider()

            // Contenu détaillé (placeholder)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Configuration détaillée",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Ici vous pouvez configurer les paramètres détaillés pour ${item.title.lowercase()}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Options spécifiques selon le type
                    when (item.type) {
                        SettingType.SWITCH -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Activer ${item.title.lowercase()}")
                                Switch(
                                    checked = item.switchState,
                                    onCheckedChange = item.onSwitchChanged ?: {}
                                )
                            }
                        }
                        SettingType.NAVIGATION -> {
                            repeat(3) { index ->
                                OutlinedButton(
                                    onClick = { /* Action spécifique */ },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Option ${index + 1}")
                                }
                            }
                        }
                        else -> {
                            Button(
                                onClick = { item.action?.invoke() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Exécuter l'action")
                            }
                        }
                    }
                }
            }
        }
    }
}