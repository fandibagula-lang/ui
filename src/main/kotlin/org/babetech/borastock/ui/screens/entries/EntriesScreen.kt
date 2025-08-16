package org.babetech.borastock.ui.screens.entries

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.Euro
import borastock.composeapp.generated.resources.Input
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.TrendingUp
import kotlinx.coroutines.launch
import org.babetech.borastock.data.models.StockEntry
import org.babetech.borastock.data.models.StockSummary
import org.babetech.borastock.ui.components.cards.StockEntryCard
import org.babetech.borastock.ui.components.common.EmptyStateMessage
import org.babetech.borastock.ui.components.common.LoadingAnimation
import org.babetech.borastock.ui.components.common.SearchAndFiltersSection
import org.babetech.borastock.ui.components.common.StockHeader
import org.babetech.borastock.ui.viewmodel.StockViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Écran principal de gestion des entrées de stock
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EntriesScreen(
    viewModel: StockViewModel = koinViewModel()
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    // États observables
    val stockEntries by viewModel.filteredStockEntries.collectAsStateWithLifecycle()
    val entryStatistics by viewModel.entryStatistics.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    var selectedEntry by remember { mutableStateOf<StockEntry?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Sélectionner automatiquement la première entrée
    LaunchedEffect(stockEntries) {
        if (selectedEntry == null && stockEntries.isNotEmpty()) {
            selectedEntry = stockEntries.first()
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                EntriesMainPane(
                    stockEntries = stockEntries,
                    entryStatistics = entryStatistics,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    selectedFilter = selectedFilter,
                    onFilterChange = viewModel::updateSelectedFilter,
                    sortBy = sortBy,
                    onSortChange = viewModel::updateSortBy,
                    onEntrySelected = { entry ->
                        selectedEntry = entry
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }
                    },
                    onAddEntryClick = { showAddDialog = true },
                    isLoading = uiState.isLoading
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedEntry?.let { entry ->
                    StockEntryDetailPane(
                        entry = entry,
                        onBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden,
                        onEditEntry = { updatedEntry ->
                            viewModel.updateStockEntry(updatedEntry)
                            selectedEntry = updatedEntry
                        }
                    )
                } ?: EmptyStateMessage(
                    title = "Sélectionnez une entrée",
                    subtitle = "Choisissez une entrée pour voir ses détails",
                    icon = Res.drawable.Input
                )
            }
        }
    )

    // Dialogue d'ajout
    if (showAddDialog) {
        StockEntryAddDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newEntry ->
                viewModel.addStockEntry(newEntry)
                showAddDialog = false
            }
        )
    }
}

/**
 * Volet principal avec la liste des entrées
 */
@Composable
private fun EntriesMainPane(
    stockEntries: List<StockEntry>,
    entryStatistics: List<org.babetech.borastock.data.models.StockStat>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    onEntrySelected: (StockEntry) -> Unit,
    onAddEntryClick: () -> Unit,
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
            val totalValue = stockEntries.filter { it.status != org.babetech.borastock.data.models.EntryStatus.CANCELLED }
                .sumOf { it.totalValue }
            
            StockHeader(
                title = "Entrées de Stock",
                subtitle = "Gestion des réceptions et commandes",
                icon = Res.drawable.Input,
                iconColor = MaterialTheme.colorScheme.primary,
                stats = entryStatistics,
                summaries = listOf(
                    StockSummary(
                        "Valeur totale des entrées",
                        "${totalValue} €",
                        Res.drawable.TrendingUp,
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
                searchLabel = "Rechercher une entrée...",
                selectedFilter = selectedFilter,
                onFilterChange = onFilterChange,
                filterOptions = listOf("Toutes", "En attente", "Validées", "Reçues", "Annulées"),
                sortBy = sortBy,
                onSortChange = onSortChange,
                sortOptions = listOf("Date", "Produit", "Quantité", "Valeur", "Statut"),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Liste des entrées
        if (isLoading) {
            item {
                LoadingAnimation(
                    modifier = Modifier.fillParentMaxHeight(),
                    message = "Chargement des entrées..."
                )
            }
        } else if (stockEntries.isEmpty()) {
            item {
                EmptyStateMessage(
                    title = "Aucune entrée trouvée",
                    subtitle = "Ajoutez une nouvelle entrée ou modifiez vos critères de recherche",
                    icon = Res.drawable.Input,
                    modifier = Modifier.fillParentMaxHeight()
                )
            }
        } else {
            items(stockEntries, key = { it.id }) { entry ->
                StockEntryCard(
                    entry = entry,
                    onClick = { onEntrySelected(entry) },
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
                    onClick = onAddEntryClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Ajouter une entrée",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}