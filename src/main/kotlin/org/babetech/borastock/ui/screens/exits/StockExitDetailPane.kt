package org.babetech.borastock.ui.screens.exits

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
import org.babetech.borastock.data.models.ExitStatus
import org.babetech.borastock.data.models.StockExit
import org.babetech.borastock.ui.components.common.InfoItem
import org.babetech.borastock.ui.components.common.StatusBadge

/**
 * Volet de détails pour une sortie de stock
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockExitDetailPane(
    exit: StockExit,
    onBack: () -> Unit,
    showBackButton: Boolean,
    onEditExit: (StockExit) -> Unit,
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
                title = { Text(exit.productName) },
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
            StockExitDetailCard(exit = exit)

            // Actions
            StockExitActions(
                exit = exit,
                onEditClick = { showEditDialog = true }
            )
        }
    }

    // Dialogue d'édition
    if (showEditDialog) {
        StockExitEditDialog(
            exit = exit,
            onDismiss = { showEditDialog = false },
            onSave = { updatedExit ->
                onEditExit(updatedExit)
                showEditDialog = false
            }
        )
    }
}

/**
 * Carte avec les détails complets de la sortie
 */
@Composable
private fun StockExitDetailCard(
    exit: StockExit,
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
            // En-tête avec nom et statuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exit.productName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sortie ${exit.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(
                        label = exit.status.label,
                        color = exit.status.color,
                        icon = exit.status.iconRes
                    )
                    
                    StatusBadge(
                        label = exit.urgency.label,
                        color = exit.urgency.color,
                        icon = exit.urgency.iconRes
                    )
                }
            }

            Divider()

            // Informations produit
            DetailSection(
                title = "Informations produit",
                items = listOf(
                    "Catégorie" to exit.category,
                    "Quantité" to "${exit.quantity} unités",
                    "Prix unitaire" to "${exit.unitPrice} €",
                    "Valeur totale" to "${exit.totalValue} €"
                )
            )

            // Informations client et livraison
            DetailSection(
                title = "Informations client",
                items = listOfNotNull(
                    "Client" to exit.customer,
                    exit.orderNumber?.let { "N° commande" to it },
                    exit.deliveryAddress?.let { "Adresse de livraison" to it },
                    "Date de sortie" to exit.exitDate.toString(),
                    "Statut" to exit.status.label,
                    "Urgence" to exit.urgency.label
                )
            )

            // Notes si présentes
            exit.notes?.let { notes ->
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
 * Actions disponibles pour la sortie
 */
@Composable
private fun StockExitActions(
    exit: StockExit,
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
            onClick = { /* TODO: Marquer comme expédiée */ },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            enabled = exit.status == ExitStatus.PREPARED
        ) {
            Icon(
                Icons.Default.LocalShipping,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Expédier")
        }
    }
}