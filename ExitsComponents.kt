package org.babetech.borastock.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import org.babetech.borastock.data.models.StockExit
import org.babetech.borastock.data.models.ExitStatus // Import correct depuis data.models
import org.babetech.borastock.data.models.ExitUrgency // Import correct depuis data.models
import borastock.composeapp.generated.resources.LocalShipping
import borastock.composeapp.generated.resources.PlayArrow
import borastock.composeapp.generated.resources.Refresh
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.ic_check_circle
import org.jetbrains.compose.resources.painterResource

/**
 * Composable pour l'animation de chargement.
 */
@Composable
fun LoadingAnimation(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "loading")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ), label = "loading_rotation"
            )

            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer { rotationZ = rotation },
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                "Chargement des sorties...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Composable pour une carte de sortie individuelle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExitCard(
    exit: StockExit,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isPressed) 4.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = exit.status.color.copy(alpha = 0.15f)
            )
            .graphicsLayer {
                scaleX = if (isPressed) 0.98f else 1f
                scaleY = if (isPressed) 0.98f else 1f
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100)
                isPressed = false
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // En-tête avec nom, statut et urgence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exit.productName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = exit.category,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Badge de statut
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = exit.status.color.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                painter = painterResource(exit.status.iconRes),
                                contentDescription = null,
                                tint = exit.status.color,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = exit.status.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = exit.status.color
                            )
                        }
                    }

                    // Badge d'urgence si nécessaire
                    if (exit.urgency != ExitUrgency.LOW) { // Comparaison avec l'enum importée
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = exit.urgency.color.copy(alpha = 0.15f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    painter = painterResource(exit.urgency.iconRes),
                                    contentDescription = null,
                                    tint = exit.urgency.color,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = exit.urgency.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = exit.urgency.color
                                )
                            }
                        }
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
                    value = "${exit.quantity} unités",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Valeur totale",
                    value = "${exit.totalValue} €",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    label = "Client",
                    value = exit.customer,
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Date de sortie",
                    value = exit.exitDate.formatAsDateTime(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Commande et adresse
            exit.orderNumber?.let { orderNumber ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoItem(
                        label = "N° de commande",
                        value = orderNumber,
                        modifier = Modifier.weight(1f)
                    )
                    exit.deliveryAddress?.let { address ->
                        InfoItem(
                            label = "Adresse de livraison",
                            value = address.split(",").first(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable pour le volet de détails d'une sortie de stock.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExitDetailPane(
    exit: StockExit,
    onBack: () -> Unit,
    showBackButton: Boolean
) {
    val scrollState = rememberScrollState()
    var detailVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        detailVisible = true
    }

    // Animation pour la barre de progression de la quantité (si applicable)
    val maxQuantityDemo = 100 // Valeur maximale arbitraire pour la démo
    val animatedQuantityProgress by animateFloatAsState(
        targetValue = (exit.quantity.toFloat() / maxQuantityDemo.toFloat()).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000) // Durée de l'animation en ms
    )

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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnimatedVisibility(
                visible = detailVisible,
                enter = slideInVertically(
                    animationSpec = tween(600, easing = EaseOutCubic),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(animationSpec = tween(600))
            ) {
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
                        // En-tête avec animation
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                exit.status.color,
                                                exit.status.color.copy(alpha = 0.7f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(exit.status.iconRes),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = exit.productName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Sortie ${exit.id}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            thickness = 1.dp
                        )

                        // Détails complets avec sections
                        DetailSection(
                            title = "Informations produit",
                            items = listOf(
                                "Catégorie" to exit.category,
                                "Quantité" to "${exit.quantity} unités",
                                "Prix unitaire" to "${exit.unitPrice} €",
                                "Valeur totale" to "${exit.totalValue} €"
                            )
                        )

                        // Barre de progression pour la quantité
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Quantité de sortie (sur max. ${maxQuantityDemo})",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LinearProgressIndicator(
                                progress = { animatedQuantityProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                            Text(
                                text = "${exit.quantity} unités",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }


                        DetailSection(
                            title = "Informations client",
                            items = listOfNotNull(
                                "Client" to exit.customer,
                                exit.orderNumber?.let { "N° de commande" to it },
                                exit.deliveryAddress?.let { "Adresse de livraison" to it },
                                "Date de sortie" to exit.exitDate.formatAsDateTime()
                            )
                        )



                        DetailSection(
                            title = "Statut et priorité",
                            items = listOf(
                                "Statut" to exit.status.label,
                                "Urgence" to exit.urgency.label
                            )
                        )

                        exit.notes?.let { notes ->
                            DetailSection(
                                title = "Notes",
                                items = listOf("Commentaires" to notes)
                            )
                        }

                        // Actions avec animations
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* TODO: Modifier la sortie */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Modifier", style = MaterialTheme.typography.labelLarge)
                            }

                            Button(
                                onClick = { /* TODO: Changer le statut */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = exit.status.color
                                )
                            ) {
                                Icon(
                                    when (exit.status) {
                                        ExitStatus.PENDING -> painterResource(Res.drawable.PlayArrow) // Utilisation de l'enum importée
                                        ExitStatus.PREPARED ->painterResource(Res.drawable.LocalShipping) // Utilisation de l'enum importée
                                        ExitStatus.SHIPPED -> painterResource(Res.drawable.ic_check_circle) // Utilisation de l'enum importée
                                        else ->painterResource(Res.drawable.Refresh)
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    when (exit.status) { // Utilisation de l'enum importée
                                        ExitStatus.PENDING -> "Préparer"
                                        ExitStatus.PREPARED -> "Expédier"
                                        ExitStatus.SHIPPED -> "Confirmer"
                                        else -> "Réactiver"
                                    },
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable pour une section de détails avec un titre et une liste d'éléments.
 */
@Composable
fun DetailSection( // Made public for reuse
    title: String,
    items: List<Pair<String, String>>
) {
    var sectionVisible by remember { mutableStateOf(false) }

    LaunchedEffect(title) {
        delay(100)
        sectionVisible = true
    }

    AnimatedVisibility(
        visible = sectionVisible,
        enter = slideInVertically(
            animationSpec = tween(400, easing = EaseOutCubic),
            initialOffsetY = { it / 4 }
        ) + fadeIn(animationSpec = tween(400))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}

/**
 * Fonction d'extension pour formater une LocalDateTime en chaîne de caractères.
 * Gardée ici car elle est spécifique au formatage des dates de sortie.
 */
fun LocalDateTime.formatAsDateTime(): String {
    return "${dayOfMonth.toString().padStart(2, '0')}/" +
            "${monthNumber.toString().padStart(2, '0')}/" +
            "$year " +
            "${hour.toString().padStart(2, '0')}:" +
            "${minute.toString().padStart(2, '0')}"
}
