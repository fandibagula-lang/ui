package org.babetech.borastock.ui.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.search
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Classe de données pour les statistiques génériques.
 * @param title Le titre de la statistique (ex: "Total", "Actifs").
 * @param value La valeur de la statistique (ex: "100", "50").
 * @param iconRes La ressource drawable pour l'icône de la statistique.
 * @param color La couleur principale associée à la statistique.
 */
data class StockStat(
    val title: String,
    val value: String,
    val iconRes: DrawableResource,
    val color: Color
)

/**
 * Classe de données pour les résumés génériques.
 * @param label Le libellé du résumé (ex: "Valeur totale des commandes").
 * @param value La valeur du résumé (ex: "150K €").
 * @param iconRes La ressource drawable pour l'icône du résumé.
 * @param iconTint La couleur de l'icône du résumé.
 * @param backgroundColor La couleur de fond du résumé.
 * @param valueColor La couleur de la valeur du résumé.
 */
data class StockSummary(
    val label: String,
    val value: String,
    val iconRes: DrawableResource,
    val iconTint: Color,
    val backgroundColor: Color,
    val valueColor: Color
)

/**
 * Composable pour afficher une paire label-valeur.
 * Utilisé pour les informations détaillées.
 * @param label Le libellé de l'information.
 * @param value La valeur de l'information.
 * @param modifier Le modificateur à appliquer au composable.
 */
@Composable
fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Composable pour une carte de statistique individuelle.
 * @param title Le titre de la carte de statistique.
 * @param value La valeur affichée sur la carte.
 * @param icon La ressource drawable pour l'icône.
 * @param color La couleur principale de la carte.
 * @param modifier Le modificateur à appliquer au composable.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: DrawableResource,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxHeight() // Égalise la hauteur des cartes
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = color.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Composable générique pour l'en-tête de l'écran, affichant les statistiques globales.
 * @param title Le titre principal de l'en-tête.
 * @param subtitle Le sous-titre de l'en-tête.
 * @param icon La ressource drawable pour l'icône de l'en-tête.
 * @param iconColor La couleur de l'icône de l'en-tête.
 * @param stats La liste des statistiques à afficher.
 * @param summaries La liste des résumés à afficher.
 * @param modifier Le modificateur à appliquer au composable.
 * @param animateStats Indique si les statistiques doivent être animées.
 */
@Composable
fun GenericHeader(
    title: String,
    subtitle: String,
    icon: DrawableResource,
    iconColor: Color,
    stats: List<StockStat>,
    summaries: List<StockSummary>,
    modifier: Modifier = Modifier,
    animateStats: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Titre
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(iconColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Statistiques
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), // Égalise la hauteur des cartes
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stats.forEachIndexed { index, stat ->
                    var visible by remember { mutableStateOf(!animateStats) }

                    LaunchedEffect(Unit) {
                        if (animateStats) {
                            delay(index * 150L)
                            visible = true
                        }
                    }


                        StatCard(
                            title = stat.title,
                            value = stat.value,
                            icon = stat.iconRes,
                            color = stat.color,
                            modifier = Modifier.weight(1f)
                        )
                    
                }
            }

            // Résumés
            summaries.forEach { summary ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = summary.backgroundColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                painter = painterResource(summary.iconRes),
                                contentDescription = null,
                                tint = summary.iconTint,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = summary.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = summary.value,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = summary.valueColor
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable générique pour la section de recherche et de filtres.
 * @param searchQuery La requête de recherche actuelle.
 * @param onSearchQueryChange Le callback pour les changements de requête de recherche.
 * @param searchLabel Le libellé du champ de recherche.
 * @param selectedFilter Le filtre actuellement sélectionné.
 * @param onFilterChange Le callback pour les changements de filtre.
 * @param filterOptions Les options disponibles pour le filtre.
 * @param sortBy La méthode de tri actuellement sélectionnée.
 * @param onSortChange Le callback pour les changements de tri.
 * @param sortOptions Les options disponibles pour le tri.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericSearchAndFiltersSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchLabel: String,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    filterOptions: List<String>,
    sortBy: String,
    onSortChange: (String) -> Unit,
    sortOptions: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Barre de recherche
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text(searchLabel) },
                leadingIcon = {
                    Icon(
                       painterResource(Res.drawable.search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Filtres et tri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Filtre par statut
                var filterExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = filterExpanded,
                    onExpandedChange = { filterExpanded = !filterExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedFilter,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrer") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { filterExpanded = false }
                    ) {
                        filterOptions.forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    onFilterChange(filter)
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }

                // Tri
                var sortExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = sortExpanded,
                    onExpandedChange = { sortExpanded = !sortExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = sortBy,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Trier par") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false }
                    ) {
                        sortOptions.forEach { sort ->
                            DropdownMenuItem(
                                text = { Text(sort) },
                                onClick = {
                                    onSortChange(sort)
                                    sortExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StockHeader(
    title: String,
    subtitle: String,
    icon: DrawableResource, // Changé de Painter à ImageVector
    iconColor: Color,
    stats: List<org.babetech.borastock.data.models.StockStat>,
    summaries: List<org.babetech.borastock.data.models.StockSummary>,
    modifier: Modifier = Modifier,
    animateStats: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Titre
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(iconColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(icon), // Utilisation de ImageVector
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Statistiques
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), // Égalise la hauteur des cartes
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stats.forEachIndexed { index, stat ->
                    var visible by remember { mutableStateOf(!animateStats) }

                    LaunchedEffect(Unit) {
                        if (animateStats) {
                            delay(index * 150L)
                            visible = true
                        }
                    }

//
                    StatCard(
                        title = stat.title,
                        value = stat.value,
                        icon = stat.iconRes,
                        color = stat.color,
                        modifier = Modifier.weight(1f)
                    )

                }
            }

            // Résumés
            summaries.forEach { summary ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = summary.backgroundColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                painter = painterResource(summary.iconRes), // Utilisation de ImageVector
                                contentDescription = null,
                                tint = summary.iconTint,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = summary.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = summary.value,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = summary.valueColor
                        )
                    }
                }
            }
        }
    }
}
