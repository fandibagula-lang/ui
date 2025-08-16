package org.babetech.borastock.ui.screens.entries

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import org.babetech.borastock.data.models.EntryStatus
import org.babetech.borastock.data.models.StockEntry
import org.babetech.borastock.ui.components.common.InfoItem
import org.babetech.borastock.ui.components.common.StatusBadge

/**
 * Volet de détails pour une entrée de stock
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockEntryDetailPane(
    entry: StockEntry,
    onBack: () -> Unit,
    showBackButton: Boolean,
    onEditEntry: (StockEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }

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
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Carte principale avec informations détaillées
            StockEntryDetailCard(entry = entry)

            // Actions
            StockEntryActions(
                entry = entry,
                onEditClick = { showEditDialog = true }
            )
        }
    }

    // Dialogue d'édition
    if (showEditDialog) {
        StockEntryEditDialog(
            entry = entry,
            onDismiss = { showEditDialog = false },
            onSave = { updatedEntry ->
                onEditEntry(updatedEntry)
                showEditDialog = false
            }
        )
    }
}

/**
 * Carte avec les détails complets de l'entrée
 */
@Composable
private fun StockEntryDetailCard(
    entry: StockEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // En-tête avec nom et statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                StatusBadge(
                    label = entry.status.label,
                    color = entry.status.color,
                    icon = entry.status.iconRes
                )
            }

            Divider()

            // Informations produit
            DetailSection(
                title = "Informations produit",
                items = listOf(
                    "Catégorie" to entry.category,
                    "Quantité" to "${entry.quantity} unités",
                    "Prix unitaire" to "${entry.unitPrice} €",
                    "Valeur totale" to "${entry.totalValue} €"
                )
            )

            // Informations fournisseur
            DetailSection(
                title = "Informations fournisseur",
                items = listOfNotNull(
                    "Fournisseur" to entry.supplier,
                    entry.batchNumber?.let { "N° de lot" to it },
                    "Date d'entrée" to entry.entryDate.toString(),
                    "Statut" to entry.status.label
                )
            )

            // Notes si présentes
            entry.notes?.let { notes ->
                DetailSection(
                    title = "Notes",
                    items = listOf("Commentaires" to notes)
                )
            }
        }
    }
}

/**
 * Section de détails réutilisable
 */
@Composable
private fun DetailSection(
    title: String,
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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
 * Actions disponibles pour l'entrée
 */
@Composable
private fun StockEntryActions(
    entry: StockEntry,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onEditClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Modifier")
        }

        Button(
            onClick = { /* TODO: Valider l'entrée */ },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            enabled = entry.status == EntryStatus.PENDING
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Valider")
        }
    }
}