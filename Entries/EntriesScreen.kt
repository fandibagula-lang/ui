package org.babetech.borastock.ui.screens.screennavigation.Entries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import borastock.composeapp.generated.resources.CheckCircle
import borastock.composeapp.generated.resources.Input
import borastock.composeapp.generated.resources.Receipt
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.babetech.borastock.data.models.StockEntry
import org.babetech.borastock.data.models.StockStat
import org.babetech.borastock.data.models.StockSummary
import org.babetech.borastock.data.models.EntryStatus // Explicitly import EntryStatus

import borastock.composeapp.generated.resources.Res // Import your generated Res
import borastock.composeapp.generated.resources.Schedule
import borastock.composeapp.generated.resources.TrendingUp
import org.babetech.borastock.ui.components.GenericSearchAndFiltersSection
import org.babetech.borastock.ui.components.InfoItem
import org.babetech.borastock.ui.components.StockHeader

import org.jetbrains.compose.resources.painterResource

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Composable principal qui utilise `SupportingPaneScaffold` pour une mise en page adaptative.
 * Il affiche la liste des entrées à gauche (mainPane) et les détails de l'entrée sélectionnée à droite (supportingPane).
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)
@Composable
fun EntriesScreen() {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    var selectedEntry by remember { mutableStateOf<StockEntry?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Toutes") }
    var sortBy by remember { mutableStateOf("Date") }

    // État pour contrôler la visibilité du dialogue d'ajout
    var showAddEntryDialog by remember { mutableStateOf(false) }
    // Nouveaux états pour contrôler la visibilité du dialogue de modification
    var showEditEntryDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<StockEntry?>(null) }


    fun nowMinusHours(hours: Int): LocalDateTime {
        return Clock.System.now()
            .minus(hours, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }


    // Données d'exemple (mutable pour pouvoir ajouter de nouvelles entrées)
    val stockEntries = remember {
        mutableStateListOf( // Utilisation de mutableStateListOf pour permettre les modifications
            StockEntry(
                id = "E001",
                productName = "iPhone 15 Pro Max",
                category = "Électronique",
                quantity = 50,
                unitPrice = 1199.99,
                totalValue = 59999.50,
                supplier = "Apple Inc.",
                entryDate = nowMinusHours(2),
                batchNumber = "APL2024001",
                expiryDate = null,
                status = EntryStatus.RECEIVED, // Direct reference
                notes = "Livraison conforme, emballage parfait"
            ),
            StockEntry(
                id = "E002",
                productName = "Samsung Galaxy S24 Ultra",
                category = "Électronique",
                quantity = 30,
                unitPrice = 1299.99,
                totalValue = 38999.70,
                supplier = "Samsung Electronics",
                entryDate = nowMinusHours(4),
                batchNumber = "SAM2024002",
                expiryDate = null,
                status = EntryStatus.VALIDATED, // Direct reference
                notes = "En attente de réception"
            ),
            StockEntry(
                id = "E003",
                productName = "MacBook Air M3",
                category = "Informatique",
                quantity = 15,
                unitPrice = 1299.99,
                totalValue = 19499.85,
                supplier = "Apple Inc.",
                entryDate = nowMinusHours(6),
                batchNumber = "APL2024003",
                expiryDate = null,
                status = EntryStatus.PENDING, // Direct reference
                notes = "Commande passée, livraison prévue demain"
            ),
            StockEntry(
                id = "E004",
                productName = "AirPods Pro 2",
                category = "Audio",
                quantity = 100,
                unitPrice = 279.99,
                totalValue = 27999.00,
                supplier = "Apple Inc.",
                entryDate = nowMinusHours(28),
                batchNumber = "APL2024004",
                expiryDate = null,
                status = EntryStatus.RECEIVED, // Direct reference
                notes = "Stock complet reçu"
            ),
            StockEntry(
                id = "E005",
                productName = "Dell XPS 13",
                category = "Informatique",
                quantity = 20,
                unitPrice = 999.99,
                totalValue = 19999.80,
                supplier = "Dell Technologies",
                entryDate = nowMinusHours(12),
                batchNumber = "DELL2024001",
                expiryDate = null,
                status = EntryStatus.CANCELLED, // Direct reference
                notes = "Annulée - problème de qualité"
            ),
            StockEntry(
                id = "E006",
                productName = "Sony WH-1000XM5",
                category = "Audio",
                quantity = 40,
                unitPrice = 399.99,
                totalValue = 15999.60,
                supplier = "Sony Corporation",
                entryDate = nowMinusHours(1),
                batchNumber = "SONY2024001",
                expiryDate = null,
                status = EntryStatus.RECEIVED, // Direct reference
                notes = "Excellent état, emballage premium"
            )
        )
    }

    val filteredEntries = stockEntries.filter { entry ->
        val matchesSearch = entry.productName.contains(searchQuery, ignoreCase = true) ||
                entry.category.contains(searchQuery, ignoreCase = true) ||
                entry.supplier.contains(searchQuery, ignoreCase = true) ||
                entry.batchNumber?.contains(searchQuery, ignoreCase = true) == true

        val matchesFilter = when (selectedFilter) {
            "Toutes" -> true
            "En attente" -> entry.status == EntryStatus.PENDING // Direct reference
            "Validées" -> entry.status == EntryStatus.VALIDATED // Direct reference
            "Reçues" -> entry.status == EntryStatus.RECEIVED // Direct reference
            "Annulées" -> entry.status == EntryStatus.CANCELLED // Direct reference
            else -> true
        }

        matchesSearch && matchesFilter
    }.let { entries ->
        when (sortBy) {
            "Date" -> entries.sortedByDescending { it.entryDate }
            "Produit" -> entries.sortedBy { it.productName }
            "Quantité" -> entries.sortedByDescending { it.quantity }
            "Valeur" -> entries.sortedByDescending { it.totalValue }
            "Statut" -> entries.sortedBy { it.status.label }
            else -> entries
        }
    }

    // Effet pour sélectionner la première entrée si la liste filtrée n'est pas vide
    // et qu'aucune entrée n'est sélectionnée. Utile pour les grands écrans.
    LaunchedEffect(filteredEntries) {
        if (selectedEntry == null && filteredEntries.isNotEmpty()) {
            selectedEntry = filteredEntries.first()
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                EntriesMainPane(
                    entries = filteredEntries,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilter = selectedFilter,
                    onFilterChange = { newValue -> selectedFilter = newValue }, // Corrected line
                    sortBy = sortBy,
                    onSortChange = { sortBy = it },
                    onEntrySelected = { entry ->
                        selectedEntry = entry
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }
                    },
                    onAddEntryClick = {
                        showAddEntryDialog = true // Ouvre le dialogue d'ajout
                    },
                    stockEntries = stockEntries // Passez la liste mutable
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedEntry?.let { entry ->
                    EntryDetailPane(
                        entry = entry,
                        onBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden,
                        onEditClick = { entryToModify -> // Nouveau lambda pour le bouton Modifier
                            entryToEdit = entryToModify
                            showEditEntryDialog = true
                        }
                    )
                } ?: run {
                    // Message de placeholder si aucune entrée n'est sélectionnée
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sélectionnez une entrée pour voir les détails",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )

    // Dialogue d'ajout de nouvelle entrée
    if (showAddEntryDialog) {
        AddEntryFormDialog(
            onDismiss = { showAddEntryDialog = false },
            onAddEntry = { newEntry ->
                val newId = (stockEntries.maxOfOrNull { it.id.substring(1).toInt() } ?: 0) + 1
                val entryToAdd = newEntry.copy(
                    id = "E${newId}",
                    entryDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    totalValue = newEntry.quantity * newEntry.unitPrice,
                    status = EntryStatus.PENDING // Nouvelle entrée est par défaut en attente
                )
                stockEntries.add(0, entryToAdd) // Ajoute la nouvelle entrée au début de la liste
                selectedEntry = entryToAdd // Sélectionne la nouvelle entrée
                showAddEntryDialog = false // Ferme le dialogue
                scope.launch {
                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting) // Affiche les détails de la nouvelle entrée
                }
            }
        )
    }

    // Nouveau dialogue pour la modification d'une entrée
    if (showEditEntryDialog) {
        entryToEdit?.let { entry ->
            EditEntryFormDialog(
                initialEntry = entry,
                onDismiss = { showEditEntryDialog = false },
                onEditEntry = { updatedEntry ->
                    val index = stockEntries.indexOfFirst { it.id == updatedEntry.id }
                    if (index != -1) {
                        // Met à jour l'entrée existante avec les nouvelles valeurs, en recalculant totalValue
                        stockEntries[index] = updatedEntry.copy(totalValue = updatedEntry.quantity * updatedEntry.unitPrice)
                        selectedEntry = updatedEntry // Met à jour l'entrée sélectionnée si c'était celle qui était modifiée
                    }
                    showEditEntryDialog = false // Ferme le dialogue
                }
            )
        }
    }
}

/**
 * Composable principal pour le volet de gauche (liste des entrées).
 * Contient l'en-tête, la barre de recherche/filtres et la liste des entrées.
 */
@Composable
private fun EntriesMainPane(
    entries: List<StockEntry>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    onEntrySelected: (StockEntry) -> Unit,
    onAddEntryClick: () -> Unit,
    stockEntries: SnapshotStateList<StockEntry>
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
            )
    ) {
        // Header avec statistiques ajouté comme un item dans LazyColumn
        item {
            val totalEntries = stockEntries.size
            val pendingEntries = stockEntries.count { it.status == EntryStatus.PENDING } // Direct reference
            val receivedEntries = stockEntries.count { it.status == EntryStatus.RECEIVED } // Direct reference
            val totalValue = stockEntries.filter { it.status != EntryStatus.CANCELLED }.sumOf { it.totalValue } // Direct reference

            StockHeader(
                title = "Entrées de Stock",
                subtitle = "Gestion des réceptions et commandes",
                icon = Res.drawable.Input, // Utilisation de Res.drawable
                iconColor = MaterialTheme.colorScheme.primary,
                stats = listOf(
                    StockStat(
                        "Total Entrées",
                        totalEntries.toString(),
                        Res.drawable.Receipt, // Utilisation de Res.drawable
                        MaterialTheme.colorScheme.primary
                    ),
                    StockStat(
                        "En Attente",
                        pendingEntries.toString(),
                        Res.drawable.Schedule, // Utilisation de Res.drawable
                        Color(0xFFf59e0b)
                    ),
                    StockStat(
                        "Reçues",
                        receivedEntries.toString(),
                        Res.drawable.CheckCircle,
                        Color(0xFF22c55e)
                    )
                ),
                summaries = listOf(
                    StockSummary(
                        "Valeur totale des entrées",
                        "${totalValue} €",
                        Res.drawable.TrendingUp, // Utilisation de Res.drawable
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
        }

        // Barre de recherche et filtres
        item {
            GenericSearchAndFiltersSection(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                searchLabel = "Rechercher une entrée...",
                selectedFilter = selectedFilter,
                onFilterChange = onFilterChange,
                filterOptions = listOf("Toutes", "En attente", "Validées", "Reçues", "Annulées"),
                sortBy = sortBy,
                onSortChange = onSortChange,
                sortOptions = listOf("Date", "Produit", "Quantité", "Valeur", "Statut")
            )
        }

        // Liste des entrées
        items(entries) { entry ->
            EntryCard(
                entry = entry,
                onClick = { onEntrySelected(entry) }
            )
        }

        // Bouton d'ajout flottant
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

/**
 * Composable pour une carte d'entrée individuelle.
 * Modifié pour être cliquable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryCard(
    entry: StockEntry,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = entry.status.color.copy(alpha = 0.1f) // Corrected and uncommented
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // En-tête avec nom et statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.productName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = entry.status.color.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            painter = painterResource(resource = entry.status.iconRes),
                            contentDescription = null,
                            tint = entry.status.color,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = entry.status.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = entry.status.color
                        )
                    }
                }
            }

            // Informations détaillées
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    label = "Quantité",
                    value = "${entry.quantity} unités",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Prix unitaire",
                    value = "${entry.unitPrice} €",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    label = "Valeur totale",
                    value = "${entry.totalValue} €",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Date d'entrée",
                    value = entry.entryDate.toString(), // Formatage de la date
                    modifier = Modifier.weight(1f)
                )
            }

            // Fournisseur et lot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    label = "Fournisseur",
                    value = entry.supplier,
                    modifier = Modifier.weight(1f)
                )
                entry.batchNumber?.let { batch ->
                    InfoItem(
                        label = "N° de lot",
                        value = batch,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Composable pour le volet de détails d'une entrée de stock.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryDetailPane(
    entry: StockEntry,
    onBack: () -> Unit,
    showBackButton: Boolean,
    onEditClick: (StockEntry) -> Unit
) {
    val scrollState = rememberScrollState()

    fun LocalDateTime.formatToString(): String {
        return "${dayOfMonth.toString().padStart(2, '0')}/" +
                "${monthNumber.toString().padStart(2, '0')}/" +
                "$year à ${hour.toString().padStart(2, '0')}:" +
                "${minute.toString().padStart(2, '0')}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entry.productName) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
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
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // En-tête de la carte de détails
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(entry.status.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(resource = entry.status.iconRes),
                                contentDescription = null,
                                tint = entry.status.color,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = entry.productName,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Entrée ${entry.id}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Détails complets
                    DetailSection(
                        title = "Informations produit",
                        items = listOf(
                            "Catégorie" to entry.category,
                            "Quantité" to "${entry.quantity} unités",
                            "Prix unitaire" to "${entry.unitPrice} €",
                            "Valeur totale" to "${entry.totalValue} €"
                        )
                    )

                    DetailSection(
                        title = "Informations fournisseur",
                        items = listOf(
                            "Fournisseur" to entry.supplier,
                            "N° de lot" to (entry.batchNumber ?: "Non spécifié"),
                            "Date d'entrée" to entry.entryDate.formatToString(),
                            "Statut" to entry.status.label
                        )
                    )

                    entry.notes?.let { notes ->
                        DetailSection(
                            title = "Notes",
                            items = listOf("Commentaires" to notes)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onEditClick(entry) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Modifier", style = MaterialTheme.typography.labelLarge)
                }

                Button(
                    onClick = { /* TODO: Valider l'entrée */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(10.dp),
                    enabled = entry.status == EntryStatus.PENDING
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Valider", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

/**
 * Composable pour une section de détails avec un titre et une liste d'éléments.
 */
@Composable
private fun DetailSection(
    title: String,
    items: List<Pair<String, String>>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        items.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Nouveau composable pour le formulaire d'ajout d'une nouvelle entrée.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AddEntryFormDialog(
    onDismiss: () -> Unit,
    onAddEntry: (StockEntry) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var unitPrice by remember { mutableStateOf("0.0") }
    var supplier by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ajouter une nouvelle entrée",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        errorMessage = ""
                    },
                    label = { Text("Nom du produit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        errorMessage = ""
                    },
                    label = { Text("Catégorie") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*"))) quantity = it
                        errorMessage = ""
                    },
                    label = { Text("Quantité") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*"))) unitPrice = it
                        errorMessage = ""
                    },
                    label = { Text("Prix unitaire (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = supplier,
                    onValueChange = {
                        supplier = it
                        errorMessage = ""
                    },
                    label = { Text("Fournisseur") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            val parsedQuantity = quantity.toIntOrNull() ?: 0
                            val parsedUnitPrice = unitPrice.toDoubleOrNull() ?: 0.0
                            if (productName.isNotBlank() && category.isNotBlank() && supplier.isNotBlank() && parsedQuantity > 0 && parsedUnitPrice > 0) {
                                val newEntry = StockEntry(
                                    id = "",
                                    productName = productName,
                                    category = category,
                                    quantity = parsedQuantity,
                                    unitPrice = parsedUnitPrice,
                                    totalValue = 0.0,
                                    supplier = supplier,
                                    entryDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                    batchNumber = null,
                                    expiryDate = null,
                                    status = EntryStatus.PENDING,
                                    notes = notes.ifBlank { null }
                                )
                                onAddEntry(newEntry)
                            } else {
                                errorMessage = "Veuillez remplir tous les champs obligatoires (Nom, Catégorie, Quantité, Prix Unitaire, Fournisseur) et s'assurer que Quantité et Prix Unitaire sont > 0."
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}

/**
 * Nouveau composable pour le formulaire de modification d'une entrée existante.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryFormDialog(
    initialEntry: StockEntry,
    onDismiss: () -> Unit,
    onEditEntry: (StockEntry) -> Unit
) {
    var productName by remember { mutableStateOf(initialEntry.productName) }
    var category by remember { mutableStateOf(initialEntry.category) }
    var quantity by remember { mutableStateOf(initialEntry.quantity.toString()) }
    var unitPrice by remember { mutableStateOf(initialEntry.unitPrice.toString()) }
    var supplier by remember { mutableStateOf(initialEntry.supplier) }
    var batchNumber by remember { mutableStateOf(initialEntry.batchNumber ?: "") }
    var notes by remember { mutableStateOf(initialEntry.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Modifier l'entrée",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = productName,
                    onValueChange = {
                        productName = it
                        errorMessage = ""
                    },
                    label = { Text("Nom du produit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        errorMessage = ""
                    },
                    label = { Text("Catégorie") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*"))) quantity = it
                        errorMessage = ""
                    },
                    label = { Text("Quantité") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*"))) unitPrice = it
                        errorMessage = ""
                    },
                    label = { Text("Prix unitaire (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = supplier,
                    onValueChange = {
                        supplier = it
                        errorMessage = ""
                    },
                    label = { Text("Fournisseur") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = batchNumber,
                    onValueChange = { batchNumber = it },
                    label = { Text("N° de lot (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            val parsedQuantity = quantity.toIntOrNull() ?: 0
                            val parsedUnitPrice = unitPrice.toDoubleOrNull() ?: 0.0
                            if (productName.isNotBlank() && category.isNotBlank() && supplier.isNotBlank() && parsedQuantity > 0 && parsedUnitPrice > 0) {
                                val updatedEntry = initialEntry.copy(
                                    productName = productName,
                                    category = category,
                                    quantity = parsedQuantity,
                                    unitPrice = parsedUnitPrice,
                                    supplier = supplier,
                                    batchNumber = batchNumber.ifBlank { null },
                                    notes = notes.ifBlank { null }
                                )
                                onEditEntry(updatedEntry)
                            } else {
                                errorMessage = "Veuillez remplir tous les champs obligatoires (Nom, Catégorie, Quantité, Prix Unitaire, Fournisseur) et s'assurer que Quantité et Prix Unitaire sont > 0."
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Modifier")
                    }
                }
            }
        }
    }
}
