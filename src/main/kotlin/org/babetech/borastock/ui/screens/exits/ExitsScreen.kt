package org.babetech.borastock.ui.screens.exits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.babetech.borastock.data.models.ExitStatus
import org.babetech.borastock.data.models.ExitUrgency
import org.babetech.borastock.data.models.StockExit
import org.babetech.borastock.data.models.StockSummary
import org.babetech.borastock.ui.components.cards.StockExitCard
import org.babetech.borastock.ui.components.common.EmptyStateMessage
import org.babetech.borastock.ui.components.common.LoadingAnimation
import org.babetech.borastock.ui.components.common.SearchAndFiltersSection
import org.babetech.borastock.ui.components.common.StockHeader
import org.babetech.borastock.ui.viewmodel.StockViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Écran principal de gestion des sorties de stock
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ExitsScreen(
    viewModel: StockViewModel = koinViewModel()
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    // États observables
    val stockExits by viewModel.filteredStockExits.collectAsStateWithLifecycle()
    val exitStatistics by viewModel.exitStatistics.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    var selectedExit by remember { mutableStateOf<StockExit?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Sélectionner automatiquement la première sortie
    LaunchedEffect(stockExits) {
        if (selectedExit == null && stockExits.isNotEmpty()) {
            selectedExit = stockExits.first()
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                ExitsMainPane(
                    stockExits = stockExits,
                    exitStatistics = exitStatistics,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    selectedFilter = selectedFilter,
                    onFilterChange = viewModel::updateSelectedFilter,
                    sortBy = sortBy,
                    onSortChange = viewModel::updateSortBy,
                    onExitSelected = { exit ->
                        selectedExit = exit
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }
                    },
                    onAddExitClick = { showAddDialog = true },
                    isLoading = uiState.isLoading
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedExit?.let { exit ->
                    StockExitDetailPane(
                        exit = exit,
                        onBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden,
                        onEditExit = { updatedExit ->
                            viewModel.updateStockExit(updatedExit)
                            selectedExit = updatedExit
                        }
                    )
                } ?: EmptyStateMessage(
                    title = "Sélectionnez une sortie",
                    subtitle = "Choisissez une sortie pour voir ses détails",
                    icon = Res.drawable.Output
                )
            }
        }
    )

    // Dialogue d'ajout
    if (showAddDialog) {
        StockExitAddDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newExit ->
                viewModel.addStockExit(newExit)
                showAddDialog = false
            }
        )
    }
}

/**
 * Volet principal avec la liste des sorties
 */
@Composable
private fun ExitsMainPane(
    stockExits: List<StockExit>,
    exitStatistics: List<org.babetech.borastock.data.models.StockStat>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    onExitSelected: (StockExit) -> Unit,
    onAddExitClick: () -> Unit,
    isLoading: Boolean
) {
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
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // En-tête avec statistiques
        item {
            val totalValueExits = stockExits.filter { it.status != ExitStatus.CANCELLED }
                .sumOf { it.totalValue }
            val urgentExits = stockExits.count { it.urgency == ExitUrgency.HIGH }
            
            StockHeader(
                title = "Sorties de Stock",
                subtitle = "Gestion des expéditions et livraisons",
                icon = Res.drawable.Output,
                iconColor = MaterialTheme.colorScheme.primary,
                stats = exitStatistics,
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

        // Barre de recherche et filtres
        item {
            SearchAndFiltersSection(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                searchLabel = "Rechercher une sortie...",
                selectedFilter = selectedFilter,
                onFilterChange = onFilterChange,
                filterOptions = listOf("Toutes", "En préparation", "Préparées", "Expédiées", "Livrées", "Annulées"),
                sortBy = sortBy,
                onSortChange = onSortChange,
                sortOptions = listOf("Date", "Produit", "Client", "Quantité", "Valeur", "Statut", "Urgence"),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Liste des sorties
        if (isLoading) {
            item {
                LoadingAnimation(
                    modifier = Modifier.fillParentMaxHeight(),
                    message = "Chargement des sorties..."
                )
            }
        } else if (stockExits.isEmpty()) {
            item {
                EmptyStateMessage(
                    title = "Aucune sortie trouvée",
                    subtitle = "Ajoutez une nouvelle sortie ou modifiez vos critères de recherche",
                    icon = Res.drawable.Output,
                    modifier = Modifier.fillParentMaxHeight()
                )
            }
        } else {
            items(stockExits, key = { it.id }) { exit ->
                StockExitCard(
                    exit = exit,
                    onClick = { onExitSelected(exit) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // FAB
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = onAddExitClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
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