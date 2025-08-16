package org.babetech.borastock.ui.screens.screennavigation.exits

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.babetech.borastock.data.models.StockExit
import org.babetech.borastock.data.models.StockStat
import org.babetech.borastock.data.models.StockSummary
import org.babetech.borastock.data.models.ExitStatus // Import correct et unique de ExitStatus
import org.babetech.borastock.data.models.ExitUrgency // Import correct et unique de ExitUrgency
import org.babetech.borastock.ui.components.ExitCard // Import du composant extrait
import org.babetech.borastock.ui.components.ExitDetailPane // Import du composant extrait
import org.babetech.borastock.ui.components.LoadingAnimation // Import du composant extrait

import borastock.composeapp.generated.resources.Output
import borastock.composeapp.generated.resources.Receipt
import borastock.composeapp.generated.resources.Schedule
import borastock.composeapp.generated.resources.LocalShipping
import borastock.composeapp.generated.resources.ic_check_circle
import borastock.composeapp.generated.resources.TrendingDown
import borastock.composeapp.generated.resources.Warning
import borastock.composeapp.generated.resources.Res // Importez votre Res généré
import org.babetech.borastock.ui.components.GenericSearchAndFiltersSection
import org.babetech.borastock.ui.components.StockHeader

import kotlin.time.Clock
import kotlin.time.ExperimentalTime


/**
 * Composable principal pour l'écran des sorties de stock.
 * Utilise `SupportingPaneScaffold` pour une mise en page adaptative.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)
@Composable
fun ExitsScreen() {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    var selectedExit by remember { mutableStateOf<StockExit?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Toutes") }
    var sortBy by remember { mutableStateOf("Date") }
    var isLoading by remember { mutableStateOf(true) }


    // Fonction utilitaire pour obtenir une LocalDateTime en soustrayant des heures
    fun nowMinusHours(hours: Int): LocalDateTime {
        return Clock.System.now()
            .minus(hours, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }

    // Fonction utilitaire pour obtenir une LocalDateTime en soustrayant des jours et des heures
    fun nowMinus(days: Int = 0, hours: Int = 0): LocalDateTime {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val period = DateTimePeriod(days = days, hours = hours)
        return now.minus(period, timeZone).toLocalDateTime(timeZone)
    }

    // Animation de chargement initial
    LaunchedEffect(Unit) {
        delay(800) // Simule un chargement de données
        isLoading = false
    }

    // Données d'exemple avec différents statuts et urgences
    val stockExits = remember {
        mutableStateListOf( // Rendre la liste mutable pour de futures modifications
            StockExit(
                id = "S001",
                productName = "iPhone 15 Pro Max",
                category = "Électronique",
                quantity = 2,
                unitPrice = 1199.99,
                totalValue = 2399.98,
                customer = "TechStore Paris",
                exitDate = nowMinusHours(1),
                orderNumber = "CMD2024001",
                deliveryAddress = "123 Rue de Rivoli, 75001 Paris",
                status = ExitStatus.SHIPPED, // Utilisation de l'enum importée
                notes = "Livraison express demandée",
                urgency = ExitUrgency.HIGH // Utilisation de l'enum importée
            ),
            StockExit(
                id = "S002",
                productName = "Samsung Galaxy S24 Ultra",
                category = "Électronique",
                quantity = 1,
                unitPrice = 1299.99,
                totalValue = 1299.99,
                customer = "Mobile World Lyon",
                exitDate = nowMinusHours(3),
                orderNumber = "CMD2024002",
                deliveryAddress = "45 Place Bellecour, 69002 Lyon",
                status = ExitStatus.DELIVERED, // Utilisation de l'enum importée
                notes = "Client satisfait, livraison réussie",
                urgency = ExitUrgency.LOW // Utilisation de l'enum importée
            ),
            StockExit(
                id = "S003",
                productName = "MacBook Air M3",
                category = "Informatique",
                quantity = 3,
                unitPrice = 1299.99,
                totalValue = 3899.97,
                customer = "Université de Bordeaux",
                exitDate = nowMinusHours(5),
                orderNumber = "CMD2024003",
                deliveryAddress = "351 Cours de la Libération, 33405 Talence",
                status = ExitStatus.PREPARED, // Utilisation de l'enum importée
                notes = "Commande institutionnelle, facture séparée",
                urgency = ExitUrgency.MEDIUM // Utilisation de l'enum importée
            ),
            StockExit(
                id = "S004",
                productName = "AirPods Pro 2",
                category = "Audio",
                quantity = 10,
                unitPrice = 279.99,
                totalValue = 2799.90,
                customer = "AudioMax Marseille",
                exitDate = nowMinusHours(8),
                orderNumber = "CMD2024004",
                deliveryAddress = "12 La Canebière, 13001 Marseille",
                status = ExitStatus.PENDING, // Utilisation de l'enum importée
                notes = "Vérifier stock avant expédition",
                urgency = ExitUrgency.LOW // Utilisation de l'enum importée
            ),
            StockExit(
                id = "S005",
                productName = "Dell XPS 13",
                category = "Informatique",
                quantity = 1,
                unitPrice = 999.99,
                totalValue = 999.99,
                customer = "StartupTech Lille",
                exitDate = nowMinusHours(12),
                orderNumber = "CMD2024005",
                deliveryAddress = "78 Rue Nationale, 59000 Lille",
                status = ExitStatus.CANCELLED, // Utilisation de l'enum importée
                notes = "Annulée - problème de paiement",
                urgency = ExitUrgency.LOW // Utilisation de l'enum importée
            ),
            StockExit(
                id = "S006",
                productName = "Sony WH-1000XM5",
                category = "Audio",
                quantity = 5,
                unitPrice = 399.99,
                totalValue = 1999.95,
                customer = "MusicStore Toulouse",
                exitDate = nowMinusHours(1),
                orderNumber = "CMD2024006",
                deliveryAddress = "25 Place du Capitole, 31000 Toulouse",
                status = ExitStatus.DELIVERED, // Utilisation de l'enum importée
                notes = "Livraison parfaite, client régulier",
                urgency = ExitUrgency.MEDIUM // Utilisation de l'enum importée
            ),
            StockExit(
                id = "S007",
                productName = "iPad Pro 12.9",
                category = "Tablettes",
                quantity = 2,
                unitPrice = 1099.99,
                totalValue = 2199.98,
                customer = "DesignStudio Nice",
                exitDate = nowMinus(days = 1, hours = 6),
                orderNumber = "CMD2024007",
                deliveryAddress = "10 Promenade des Anglais, 06000 Nice",
                status = ExitStatus.SHIPPED, // Utilisation de l'enum importée
                notes = "Matériel professionnel pour studio",
                urgency = ExitUrgency.HIGH // Utilisation de l'enum importée
            )
        )
    }

    // Filtrage et tri des sorties
    val filteredExits = stockExits.filter { exit ->
        val matchesSearch = exit.productName.contains(searchQuery, ignoreCase = true) ||
                exit.category.contains(searchQuery, ignoreCase = true) ||
                exit.customer.contains(searchQuery, ignoreCase = true) ||
                exit.orderNumber?.contains(searchQuery, ignoreCase = true) == true

        val matchesFilter = when (selectedFilter) {
            "Toutes" -> true
            "En préparation" -> exit.status == ExitStatus.PENDING // Utilisation de l'enum importée
            "Préparées" -> exit.status == ExitStatus.PREPARED // Utilisation de l'enum importée
            "Expédiées" -> exit.status == ExitStatus.SHIPPED // Utilisation de l'enum importée
            "Livrées" -> exit.status == ExitStatus.DELIVERED // Utilisation de l'enum importée
            "Annulées" -> exit.status == ExitStatus.CANCELLED // Utilisation de l'enum importée
            else -> true
        }

        matchesSearch && matchesFilter
    }.let { exits ->
        when (sortBy) {
            "Date" -> exits.sortedByDescending { it.exitDate }
            "Produit" -> exits.sortedBy { it.productName }
            "Client" -> exits.sortedBy { it.customer }
            "Quantité" -> exits.sortedByDescending { it.quantity }
            "Valeur" -> exits.sortedByDescending { it.totalValue }
            "Statut" -> exits.sortedBy { it.status.label }
            "Urgence" -> exits.sortedByDescending { it.urgency.ordinal }
            else -> exits
        }
    }

    // Effet pour sélectionner la première sortie si la liste filtrée n'est pas vide
    LaunchedEffect(filteredExits) {
        if (selectedExit == null && filteredExits.isNotEmpty()) {
            selectedExit = filteredExits.first()
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                ExitsMainPane(
                    exits = filteredExits,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    sortBy = sortBy,
                    onSortChange = { sortBy = it },
                    onExitSelected = { exit ->
                        selectedExit = exit
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }
                    },
                    isLoading = isLoading,
                    stockExits = stockExits // Passer la liste mutable
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedExit?.let { exit ->
                    ExitDetailPane(
                        exit = exit,
                        onBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden
                    )
                } ?: run {
                    // Message de placeholder si aucune sortie n'est sélectionnée
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sélectionnez une sortie pour voir les détails",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}

/**
 * Composable principal pour le volet de gauche (liste des sorties).
 * Contient l'en-tête, la barre de recherche/filtres et la liste des sorties.
 */
@Composable
private fun ExitsMainPane(
    exits: List<StockExit>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    onExitSelected: (StockExit) -> Unit,
    isLoading: Boolean,
    stockExits: SnapshotStateList<StockExit> // Recevoir la liste mutable
) {
    var headerVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val fabScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fab_scale"
    )

    LaunchedEffect(Unit) {
        delay(200)
        headerVisible = true
        delay(300)
        contentVisible = true
    }

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
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // En-tête avec animation d'entrée
        item {
            AnimatedVisibility(
                visible = headerVisible,
                enter = slideInVertically(
                    animationSpec = tween(600, easing = EaseOutCubic),
                    initialOffsetY = { -it }
                ) + fadeIn(animationSpec = tween(600))
            ) {
                val totalExits = stockExits.size
                val pendingExits = stockExits.count { it.status == ExitStatus.PENDING } // Utilisation de l'enum importée
                val shippedExits = stockExits.count { it.status == ExitStatus.SHIPPED } // Utilisation de l'enum importée
                val deliveredExits = stockExits.count { it.status == ExitStatus.DELIVERED } // Utilisation de l'enum importée
                val totalValueExits = stockExits.filter { it.status != ExitStatus.CANCELLED }.sumOf { it.totalValue } // Utilisation de l'enum importée
                val urgentExits = stockExits.count { it.urgency == ExitUrgency.HIGH } // Utilisation de l'enum importée

                StockHeader(
                    title = "Sorties de Stock",
                    subtitle = "Gestion des expéditions et livraisons",
                    icon = Res.drawable.Output,
                    iconColor = MaterialTheme.colorScheme.primary,
                    stats = listOf(
                        StockStat("Total Sorties", totalExits.toString(), Res.drawable.Receipt, MaterialTheme.colorScheme.primary),
                        StockStat("En Préparation", pendingExits.toString(), Res.drawable.Schedule, Color(0xFFf59e0b)),
                        StockStat("Expédiées", shippedExits.toString(), Res.drawable.LocalShipping, Color(0xFF8b5cf6)),
                        StockStat("Livrées", deliveredExits.toString(), Res.drawable.ic_check_circle, Color(0xFF22c55e))
                    ),
                    summaries = listOf(
                        StockSummary(
                            "Valeur totale des sorties",
                            "${totalValueExits} €",
                            Res.drawable.TrendingDown,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.primary
                        ),
                        StockSummary(
                            "Sorties urgentes",
                            urgentExits.toString(),
                            Res.drawable.Warning,
                            Color(0xFFef4444),
                            Color(0xFFef4444).copy(alpha = 0.1f),
                            Color(0xFFef4444)
                        )
                    )
                )

            }
        }

        // Section de recherche et filtres avec animation
        item {
            AnimatedVisibility(
                visible = contentVisible,
                enter = slideInVertically(
                    animationSpec = tween(700, delayMillis = 200, easing = EaseOutCubic),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
            ) {
                GenericSearchAndFiltersSection(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    searchLabel = "Rechercher une sortie...",
                    selectedFilter = selectedFilter,
                    onFilterChange = onFilterChange,
                    filterOptions = listOf("Toutes", "En préparation", "Préparées", "Expédiées", "Livrées", "Annulées"),
                    sortBy = sortBy,
                    onSortChange = onSortChange,
                    sortOptions = listOf("Date", "Produit", "Client", "Quantité", "Valeur", "Statut", "Urgence")
                )
            }
        }

        // Liste des sorties avec animation de chargement
        if (isLoading) {
            item {
                LoadingAnimation(modifier = Modifier.fillParentMaxHeight())
            }
        } else {
            items(exits) { exit -> // Utiliser directement les items ici
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(exit.id) {
                    delay(50)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(
                        animationSpec = tween(500, easing = EaseOutCubic),
                        initialOffsetX = { it }
                    ) + fadeIn(animationSpec = tween(500))
                ) {
                    ExitCard(
                        exit = exit,
                        onClick = { onExitSelected(exit) }
                    )
                }
            }
        }

        // FAB avec animation de rebond et de pulsation
        item {
            AnimatedVisibility(
                visible = contentVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialScale = 0f
                ) + fadeIn(animationSpec = tween(500, delayMillis = 600))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { /* TODO: Ajouter nouvelle sortie */ },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                            .graphicsLayer {
                                scaleX = fabScale
                                scaleY = fabScale
                            }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Ajouter une sortie",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fonction d'extension pour formater une LocalDateTime en chaîne de caractères.
 * Gardée ici car elle est spécifique au formatage des dates de sortie.
 */
fun LocalDateTime.formatAsDateTime(): String {
    return "${dayOfMonth.toString().padStart(2, '0')}/" +
            "${monthNumber.toString().padStart(2, '0')}/" +
            "$year " +
            "${hour.toString().padStart(2, '0')}:" +
            "${minute.toString().padStart(2, '0')}"
}
