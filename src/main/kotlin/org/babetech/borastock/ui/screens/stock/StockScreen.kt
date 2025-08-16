package org.babetech.borastock.ui.screens.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.Euro
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.inventory
import kotlinx.coroutines.launch
import org.babetech.borastock.data.models.StockItem
import org.babetech.borastock.data.models.StockSummary
import org.babetech.borastock.ui.components.cards.StockItemCard
import org.babetech.borastock.ui.components.common.EmptyStateMessage
import org.babetech.borastock.ui.components.common.LoadingAnimation
import org.babetech.borastock.ui.components.common.SearchAndFiltersSection
import org.babetech.borastock.ui.components.common.StockHeader
import org.babetech.borastock.ui.viewmodel.StockViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Écran principal de gestion des stocks
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun StockScreen(
    viewModel: StockViewModel = koinViewModel()
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    // États observables
    val stockItems by viewModel.filteredStockItems.collectAsStateWithLifecycle()
    val stockStatistics by viewModel.stockStatistics.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    var selectedStockItem by remember { mutableStateOf<StockItem?>(null) }

    // Sélectionner automatiquement le premier élément
    LaunchedEffect(stockItems) {
        if (selectedStockItem == null && stockItems.isNotEmpty()) {
            selectedStockItem = stockItems.first()
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                StockMainPane(
                    stockItems = stockItems,
                    stockStatistics = stockStatistics,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    selectedFilter = selectedFilter,
                    onFilterChange = viewModel::updateSelectedFilter,
                    sortBy = sortBy,
                    onSortChange = viewModel::updateSortBy,
                    onItemSelected = { item ->
                        selectedStockItem = item
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }
                    },
                    isLoading = uiState.isLoading
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedStockItem?.let { item ->
                    StockItemDetailPane(
                        item = item,
                        onBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden,
                        onEditItem = { updatedItem ->
                            viewModel.updateStockItem(updatedItem)
                            selectedStockItem = updatedItem
                        }
                    )
                } ?: EmptyStateMessage(
                    title = "Sélectionnez un produit",
                    subtitle = "Choisissez un produit pour voir ses détails",
                    icon = Res.drawable.inventory
                )
            }
        }
    )
}

/**
 * Volet principal avec la liste des produits
 */
@Composable
private fun StockMainPane(
    stockItems: List<StockItem>,
    stockStatistics: List<org.babetech.borastock.data.models.StockStat>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    onItemSelected: (StockItem) -> Unit,
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
            val totalValue = stockItems.sumOf { it.totalValue }
            
            StockHeader(
                title = "Gestion des Stocks",
                subtitle = "Vue d'ensemble de votre inventaire",
                icon = Res.drawable.inventory,
                iconColor = MaterialTheme.colorScheme.primary,
                stats = stockStatistics,
                summaries = listOf(
                    StockSummary(
                        "Valeur totale du stock",
                        "${totalValue} €",
                        Res.drawable.Euro,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
        }

        // Barre de recherche et filtres
        item {
            SearchAndFiltersSection(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                searchLabel = "Rechercher un produit...",
                selectedFilter = selectedFilter,
                onFilterChange = onFilterChange,
                filterOptions = listOf("Tous", "En stock", "Stock faible", "Rupture", "Surstock"),
                sortBy = sortBy,
                onSortChange = onSortChange,
                sortOptions = listOf("Nom", "Stock", "Prix", "Statut"),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Liste des produits
        if (isLoading) {
            item {
                LoadingAnimation(
                    modifier = Modifier.fillParentMaxHeight(),
                    message = "Chargement des produits..."
                )
            }
        } else if (stockItems.isEmpty()) {
            item {
                EmptyStateMessage(
                    title = "Aucun produit trouvé",
                    subtitle = "Essayez de modifier vos critères de recherche",
                    icon = Res.drawable.inventory,
                    modifier = Modifier.fillParentMaxHeight()
                )
            }
        } else {
            items(stockItems, key = { it.id }) { item ->
                StockItemCard(
                    item = item,
                    onClick = { onItemSelected(item) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}