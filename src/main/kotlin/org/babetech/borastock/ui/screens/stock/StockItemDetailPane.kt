package org.babetech.borastock.ui.screens.stock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.babetech.borastock.data.models.StockItem
import org.babetech.borastock.ui.components.common.AnimatedProgressBar
import org.babetech.borastock.ui.components.common.InfoItem
import org.babetech.borastock.ui.components.common.StatusBadge
import org.jetbrains.compose.resources.painterResource

/**
 * Volet de détails pour un produit en stock
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockItemDetailPane(
    item: StockItem,
    onBack: () -> Unit,
    showBackButton: Boolean,
    onEditItem: (StockItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name) },
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
            StockItemDetailCard(item = item)

            // Actions
            StockItemActions(
                item = item,
                onEditClick = { showEditDialog = true }
            )
        }
    }

    // Dialogue d'édition
    if (showEditDialog) {
        StockItemEditDialog(
            item = item,
            onDismiss = { showEditDialog = false },
            onSave = { updatedItem ->
                onEditItem(updatedItem)
                showEditDialog = false
            }
        )
    }
}

/**
 * Carte avec les détails complets du produit
 */
@Composable
private fun StockItemDetailCard(
    item: StockItem,
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
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ID: ${item.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(
                    label = item.status.label,
                    color = item.status.color,
                    icon = item.status.icon
                )
            }

            Divider()

            // Informations détaillées
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoItem(
                        label = "Catégorie",
                        value = item.category,
                        modifier = Modifier.weight(1f)
                    )
                    InfoItem(
                        label = "Fournisseur",
                        value = item.supplier,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoItem(
                        label = "Prix unitaire",
                        value = "${item.price} €",
                        modifier = Modifier.weight(1f)
                    )
                    InfoItem(
                        label = "Valeur totale",
                        value = "${item.totalValue} €",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoItem(
                        label = "Stock minimum",
                        value = "${item.minStock} unités",
                        modifier = Modifier.weight(1f)
                    )
                    InfoItem(
                        label = "Stock maximum",
                        value = "${item.maxStock} unités",
                        modifier = Modifier.weight(1f)
                    )
                }

                InfoItem(
                    label = "Dernière mise à jour",
                    value = item.lastUpdate
                )
            }

            Divider()

            // Progression du stock
            StockProgressSection(item = item)
        }
    }
}

/**
 * Section de progression du stock avec animation
 */
@Composable
private fun StockProgressSection(
    item: StockItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Niveau de stock",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${item.currentStock}/${item.maxStock}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedProgressBar(
            progress = item.stockPercentage,
            modifier = Modifier.fillMaxWidth(),
            color = when {
                item.currentStock <= item.minStock -> Color(0xFFef4444)
                item.currentStock >= item.maxStock * 0.8 -> Color(0xFF3b82f6)
                else -> Color(0xFF22c55e)
            }
        )

        Text(
            text = when {
                item.currentStock <= item.minStock -> "⚠️ Stock critique - Réapprovisionnement nécessaire"
                item.currentStock <= item.minStock * 1.5 -> "⚡ Stock faible - Surveiller de près"
                item.currentStock >= item.maxStock * 0.9 -> "📦 Stock élevé - Considérer les promotions"
                else -> "✅ Niveau de stock optimal"
            },
            style = MaterialTheme.typography.bodySmall,
            color = when {
                item.currentStock <= item.minStock -> Color(0xFFef4444)
                item.currentStock <= item.minStock * 1.5 -> Color(0xFFf59e0b)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/**
 * Actions disponibles pour le produit
 */
@Composable
private fun StockItemActions(
    item: StockItem,
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
            onClick = { /* TODO: Historique */ },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Historique")
        }
    }
}