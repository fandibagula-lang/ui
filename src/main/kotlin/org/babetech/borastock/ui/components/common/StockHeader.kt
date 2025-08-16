package org.babetech.borastock.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.babetech.borastock.data.models.StockStat
import org.babetech.borastock.data.models.StockSummary
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Composant réutilisable pour l'en-tête des écrans avec statistiques
 */
@Composable
fun StockHeader(
    title: String,
    subtitle: String,
    icon: DrawableResource,
    iconColor: Color,
    stats: List<StockStat>,
    summaries: List<StockSummary>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // En-tête principal
        SectionHeader(
            title = title,
            subtitle = subtitle,
            icon = icon,
            iconColor = iconColor
        )

        // Statistiques en ligne
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(stats) { stat ->
                StatCard(
                    stat = stat,
                    modifier = Modifier.width(160.dp)
                )
            }
        }

        // Résumés
        summaries.forEach { summary ->
            SummaryCard(summary = summary)
        }
    }
}