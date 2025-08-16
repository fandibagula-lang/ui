package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.babetech.borastock.data.models.MetricData
import org.babetech.borastock.data.models.Movement
import org.jetbrains.compose.resources.painterResource







// Sample Data
private val sampleMetrics = listOf(
    MetricData(
        title = "Produits",
        value = "128",
        trend = "+12%",
        trendUp = true,
        icon =Res.drawable.inventory,
        color = Color(0xFF3B82F6) // Blue
    ),
    MetricData(
        title = "Fournisseurs",
        value = "24",
        trend = "+3%",
        trendUp = true,
        icon = Res.drawable.group,
        color = Color(0xFF10B981) // Green
    ),
    MetricData(
        title = "Stock Total",
        value = "2,350",
        trend = "-5%",
        trendUp = false,
        icon = Res.drawable.Warehouse,
        color = Color(0xFFF59E0B) // Amber
    ),
    MetricData(
        title = "Commandes",
        value = "89",
        trend = "+18%",
        trendUp = true,
        icon = Res.drawable.ShoppingCart,
        color = Color(0xFFEF4444) // Red
    ),
    MetricData(
        title = "Clients",
        value = "1,250",
        trend = "+6%",
        trendUp = true,
        icon =Res.drawable.Person,
        color = Color(0xFF8B5CF6) // Violet
    ),
    MetricData(
        title = "Ventes du mois",
        value = "€15,800",
        trend = "+9%",
        trendUp = true,
        icon =Res.drawable.TrendingUp,
        color = Color(0xFF06B6D4) // Cyan
    )

)


private val sampleMovements = listOf(
    Movement("Ajout 20 unités - Produit A", "Il y a 2h", true),
    Movement("Sortie 5 unités - Produit B", "Il y a 4h", false),
    Movement("Ajout 50 unités - Produit C", "Il y a 6h", true),
    Movement("Sortie 12 unités - Produit D", "Il y a 8h", false)
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AccueilScreen() {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()
    val paneState = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
    val showSupporting = paneState != PaneAdaptedValue.Hidden

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                with(this) {
                    val paneModifier = Modifier
                        .width(if (showSupporting) maxWidth * 0.2f else maxWidth)
                        .fillMaxHeight()

                    AnimatedPane(modifier = paneModifier) {  // <-- applique ici
                        MainDashboardPane(
                            showChartButton = !showSupporting,
                            onToggleChart = {
                                scope.launch {
                                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                }
                            }
                        )
                    }
                }
            },
            supportingPane = {
                if (showSupporting) {
                    with(this) {
                        val paneModifier = Modifier
                            .width(maxWidth * 0.8f)
                            .fillMaxHeight()

                        AnimatedPane(modifier = paneModifier) {  // <-- et ici aussi
                            SupportingChartPane(
                                onBack = {
                                    scope.launch { navigator.navigateBack() }
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}

// --- Main Dashboard Pane Composable ---
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ThreePaneScaffoldScope.MainDashboardPane(
    showChartButton: Boolean,
    onToggleChart: () -> Unit,
    // ✨ MODIFICATION : Le Modifier est maintenant un paramètre de la fonction
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // ✨ MODIFICATION : Le Modifier reçu est appliqué ici
    AnimatedPane(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Text(
                                "Tableau de bord",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "Aperçu de votre activité",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { DashboardMetricsGrid() }
                item { RecentMovementsList() }
                item { QuickActionsSection() }
                if (showChartButton) {
                    item {
                        Button(
                            onClick = onToggleChart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    "Afficher les graphiques",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Supporting Chart Pane Composable ---
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldScope.SupportingChartPane(
    onBack: () -> Unit
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()

    AnimatedPane(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = if (navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] == PaneAdaptedValue.Expanded) 60.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Analyse des performances",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                GraphicSwitcherScreen() // Assurez-vous que ce composant existe
            }
        }
    }
}

// --- Dashboard Metrics Grid Composable ---
@Composable
fun DashboardMetricsGrid() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(350.dp)
    ) {
        items(sampleMetrics.withIndex().toList()) { (index, metric) ->
            StatCard(
                title = metric.title,
                value = metric.value.toIntOrNull() ?: 0,
                icon = { painterResource(metric.icon) },
                trend = metric.trend,
                trendUp = metric.trendUp,
                color = metric.color,
                delay = index * 100L
            )
        }
    }
}


@Composable
fun StatCard(
    title: String,
    value: Int,
    icon: @Composable (() -> Painter),
    trend: String,
    trendUp: Boolean,
    color: Color,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    val animatedValue by animateIntAsState(
        targetValue = if (visible) value else 0,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "animatedValue"
    )

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = color.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = icon(),
                            contentDescription = null,
                            tint = color
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = if (trendUp) painterResource(Res.drawable.TrendingUp) else painterResource(Res.drawable.TrendingDown),
                            contentDescription = null,
                            tint = if (trendUp) Color(0xFF22c55e) else Color(0xFFef4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = trend,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (trendUp) Color(0xFF22c55e) else Color(0xFFef4444)
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = animatedValue.toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- Recent Movements List Composable ---
@Composable
fun RecentMovementsList() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Mouvements récents",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            sampleMovements.forEach { movement ->
                MovementItem(
                    movement = movement.description,
                    time = movement.time,
                    isIncoming = movement.isIncoming
                )
            }
        }
    }
}

// --- Movement Item Composable ---
@Composable
private fun MovementItem(
    movement: String,
    time: String,
    isIncoming: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isIncoming) Color(0xFF22c55e) else Color(0xFFef4444)
                    )
            )
            Column {
                Text(
                    text = movement,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- Quick Actions Section Composable ---
@Composable
private fun QuickActionsSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Actions rapides",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                title = "Ajouter produit",
                icon = painterResource(Res.drawable.ic_add),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            ) { /* Action */ }

            QuickActionButton(
                title = "Scanner code",
                icon = painterResource(Res.drawable.qr_code),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            ) { /* Action */ }
        }

        QuickActionButton(
            title = "Voir l'analyse détaillée",
            icon = painterResource(Res.drawable.analytics),
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        ) { /* Action */ }
    }
}

// --- Quick Action Button Composable ---
@Composable
private fun QuickActionButton(
    title: String,
    icon: Painter,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}