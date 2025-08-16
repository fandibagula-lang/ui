package org.babetech.borastock.ui.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.babetech.borastock.data.models.StockExit
import org.babetech.borastock.ui.components.common.InfoItem
import org.babetech.borastock.ui.components.common.StatusBadge

/**
 * Composant réutilisable pour afficher une carte de sortie de stock
 */
@Composable
fun StockExitCard(
    exit: StockExit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = exit.status.color.copy(alpha = 0.1f)
            )
            .clickable { onClick() },
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
                        text = exit.productName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = exit.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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

            // Informations détaillées
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    label = "Quantité",
                    value = "${exit.quantity} unités",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Client",
                    value = exit.customer,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    label = "Valeur totale",
                    value = "${exit.totalValue} €",
                    modifier = Modifier.weight(1f)
                )
                exit.orderNumber?.let { orderNum ->
                    InfoItem(
                        label = "N° commande",
                        value = orderNum,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}