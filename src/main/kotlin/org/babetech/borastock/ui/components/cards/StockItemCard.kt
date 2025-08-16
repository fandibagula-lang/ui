package org.babetech.borastock.ui.components.cards

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.babetech.borastock.data.models.StockItem
import org.babetech.borastock.ui.components.common.AnimatedProgressBar
import org.babetech.borastock.ui.components.common.InfoItem
import org.babetech.borastock.ui.components.common.StatusBadge

/**
 * Composant réutilisable pour afficher une carte de produit en stock
 */
@Composable
fun StockItemCard(
    item: StockItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = item.status.color.copy(alpha = 0.1f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // En-tête avec nom et statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(
                    label = item.status.label,
                    color = item.status.color,
                    icon = item.status.icon
                )
            }

            // Informations détaillées
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    label = "Stock actuel",
                    value = "${item.currentStock} unités",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Prix unitaire",
                    value = "${item.price} €",
                    modifier = Modifier.weight(1f)
                )
            }

            // Barre de progression du stock
            StockProgressSection(
                currentStock = item.currentStock,
                maxStock = item.maxStock,
                minStock = item.minStock,
                progress = item.stockPercentage
            )
        }
    }
}

/**
 * Composant pour la section de progression du stock
 */
@Composable
private fun StockProgressSection(
    currentStock: Int,
    maxStock: Int,
    minStock: Int,
    progress: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Niveau de stock",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$currentStock/$maxStock",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        AnimatedProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = when {
                currentStock <= minStock -> Color(0xFFef4444)
                currentStock >= maxStock * 0.8 -> Color(0xFF3b82f6)
                else -> Color(0xFF22c55e)
            }
        )
    }
}