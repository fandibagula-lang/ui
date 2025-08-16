package org.babetech.borastock.ui.screens.screennavigation.suppliers


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.Block
import borastock.composeapp.generated.resources.Business
import borastock.composeapp.generated.resources.ContactPhone
import borastock.composeapp.generated.resources.Notes
import borastock.composeapp.generated.resources.Pause
import borastock.composeapp.generated.resources.Person
import borastock.composeapp.generated.resources.Remove
import borastock.composeapp.generated.resources.Res
import borastock.composeapp.generated.resources.Schedule
import borastock.composeapp.generated.resources.ShoppingCart
import borastock.composeapp.generated.resources.ThumbDown
import borastock.composeapp.generated.resources.ThumbUp
import borastock.composeapp.generated.resources.TrendingUp
import borastock.composeapp.generated.resources.ic_check_circle
import borastock.composeapp.generated.resources.ic_star_filled
import borastock.composeapp.generated.resources.inventory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.babetech.borastock.data.models.Supplier
import org.babetech.borastock.data.models.SupplierReliability
import org.babetech.borastock.data.models.SupplierStatus
import org.babetech.borastock.ui.screens.screennavigation.exits.formatAsDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Import des composants communs
import org.babetech.borastock.ui.components.GenericHeader
import org.babetech.borastock.ui.components.GenericSearchAndFiltersSection
import org.babetech.borastock.ui.components.InfoItem

import org.babetech.borastock.ui.components.StockStat
import org.babetech.borastock.ui.components.StockSummary


import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalTime::class)
@Composable
fun SuppliersScreen() {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    var selectedSupplier by remember { mutableStateOf<Supplier?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tous") }
    var sortBy by remember { mutableStateOf("Nom") }
    var isLoading by remember { mutableStateOf(true) }


    val timeZone = TimeZone.currentSystemDefault()

    val nowInstant = Clock.System.now()



    val now = Clock.System.now()

    fun nowMinus(days: Int = 0, hours: Int = 0): LocalDateTime {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val period = DateTimePeriod(days = days, hours = hours)
        return now.minus(period, timeZone).toLocalDateTime(timeZone)
    }



    val period = DateTimePeriod(days = 1, hours = 6)
    val exitDate = now.minus(period, timeZone).toLocalDateTime(timeZone)



    val instantNow = Clock.System.now()
    fun nowMinusHours(hours: Int): LocalDateTime {
        return Clock.System.now()
            .minus(hours, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    }


    // Animation de chargement initial
    LaunchedEffect(Unit) {
        delay(800)
        isLoading = false
    }

    // Données d'exemple
    val suppliers = remember {
        listOf(
            Supplier(
                id = "SUP001",
                name = "Apple Inc.",
                category = "Électronique",
                contactPerson = "Jean Dupont",
                email = "contact@apple-france.com",
                phone = "+33 1 23 45 67 89",
                address = "12 Rue de Rivoli",
                city = "Paris",
                country = "France",
                productsCount = 25,
                totalOrders = 156,
                totalValue = 2500000.0,
                rating = 4.9f,
                status = SupplierStatus.ACTIVE,
                reliability = SupplierReliability.EXCELLENT,
                lastOrderDate = nowMinus(days = 1, hours = 2),
                paymentTerms = "30 jours",
                notes = "Fournisseur premium avec excellent service client"
            ),
            Supplier(
                id = "SUP002",
                name = "Samsung Electronics",
                category = "Électronique",
                contactPerson = "Marie Martin",
                email = "pro@samsung.fr",
                phone = "+33 1 34 56 78 90",
                address = "45 Avenue des Champs-Élysées",
                city = "Paris",
                country = "France",
                productsCount = 32,
                totalOrders = 203,
                totalValue = 1800000.0,
                rating = 4.7f,
                status = SupplierStatus.ACTIVE,
                reliability = SupplierReliability.EXCELLENT,
                lastOrderDate = nowMinus(days = 1, hours = 5),
                paymentTerms = "45 jours",
                notes = "Partenaire stratégique depuis 5 ans"
            ),
            Supplier(
                id = "SUP003",
                name = "Dell Technologies",
                category = "Informatique",
                contactPerson = "Pierre Dubois",
                email = "business@dell.fr",
                phone = "+33 1 45 67 89 01",
                address = "78 Boulevard Haussmann",
                city = "Paris",
                country = "France",
                productsCount = 18,
                totalOrders = 89,
                totalValue = 950000.0,
                rating = 4.3f,
                status = SupplierStatus.ACTIVE,
                reliability = SupplierReliability.GOOD,
                lastOrderDate = nowMinus(days = 1, hours = 10),
                paymentTerms = "30 jours",
                notes = "Bon rapport qualité-prix"
            ),
            Supplier(
                id = "SUP004",
                name = "Sony Corporation",
                category = "Audio/Vidéo",
                contactPerson = "Sophie Leroy",
                email = "pro@sony.fr",
                phone = "+33 1 56 78 90 12",
                address = "23 Rue de la Paix",
                city = "Lyon",
                country = "France",
                productsCount = 15,
                totalOrders = 67,
                totalValue = 720000.0,
                rating = 4.5f,
                status = SupplierStatus.ACTIVE,
                reliability = SupplierReliability.GOOD,
                lastOrderDate = nowMinus(days = 1, hours = 7),
                paymentTerms = "60 jours",
                notes = "Spécialiste audio haut de gamme"
            ),
            Supplier(
                id = "SUP005",
                name = "Microsoft France",
                category = "Logiciels",
                contactPerson = "Thomas Bernard",
                email = "enterprise@microsoft.fr",
                phone = "+33 1 67 89 01 23",
                address = "39 Quai du Président Roosevelt",
                city = "Issy-les-Moulineaux",
                country = "France",
                productsCount = 12,
                totalOrders = 45,
                totalValue = 580000.0,
                rating = 4.1f,
                status = SupplierStatus.PENDING,
                reliability = SupplierReliability.AVERAGE,
                lastOrderDate = nowMinus(days = 1, hours = 20),
                paymentTerms = "30 jours",
                notes = "En cours de négociation contrat"
            ),
            Supplier(
                id = "SUP006",
                name = "Xiaomi France",
                category = "Électronique",
                contactPerson = "Amélie Rousseau",
                email = "b2b@xiaomi.fr",
                phone = "+33 1 78 90 12 34",
                address = "15 Rue du Commerce",
                city = "Marseille",
                country = "France",
                productsCount = 28,
                totalOrders = 134,
                totalValue = 650000.0,
                rating = 3.8f,
                status = SupplierStatus.ACTIVE,
                reliability = SupplierReliability.AVERAGE,
                lastOrderDate = nowMinus(days = 1, hours = 3),
                paymentTerms = "45 jours",
                notes = "Bon rapport qualité-prix, délais parfois longs"
            ),
            Supplier(
                id = "SUP007",
                name = "HP Enterprise",
                category = "Informatique",
                contactPerson = "Nicolas Moreau",
                email = "channel@hpe.fr",
                phone = "+33 1 89 01 23 45",
                address = "92 Avenue de la Grande Armée",
                city = "Paris",
                country = "France",
                productsCount = 8,
                totalOrders = 23,
                totalValue = 180000.0,
                rating = 3.2f,
                status = SupplierStatus.BLOCKED,
                reliability = SupplierReliability.POOR,
                lastOrderDate = nowMinus(days = 1, hours = 45),
                paymentTerms = "30 jours",
                notes = "Problèmes de qualité récurrents - En révision"
            )
        )
    }

    val filteredSuppliers = suppliers.filter { supplier ->
        val matchesSearch = supplier.name.contains(searchQuery, ignoreCase = true) ||
                supplier.category.contains(searchQuery, ignoreCase = true) ||
                supplier.contactPerson.contains(searchQuery, ignoreCase = true) ||
                supplier.city.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Tous" -> true
            "Actifs" -> supplier.status == SupplierStatus.ACTIVE
            "Inactifs" -> supplier.status == SupplierStatus.INACTIVE
            "En attente" -> supplier.status == SupplierStatus.PENDING
            "Bloqués" -> supplier.status == SupplierStatus.BLOCKED
            else -> true
        }

        matchesSearch && matchesFilter
    }.let { suppliers ->
        when (sortBy) {
            "Nom" -> suppliers.sortedBy { it.name }
            "Catégorie" -> suppliers.sortedBy { it.category }
            "Note" -> suppliers.sortedByDescending { it.rating }
            "Commandes" -> suppliers.sortedByDescending { it.totalOrders }
            "Valeur" -> suppliers.sortedByDescending { it.totalValue }
            "Statut" -> suppliers.sortedBy { it.status.label }
            else -> suppliers
        }
    }

    SupportingPaneScaffold(
        value = navigator.scaffoldValue,
        directive = navigator.scaffoldDirective,
        mainPane = {
            AnimatedPane {
                SuppliersMainPane(
                    suppliers = filteredSuppliers,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    sortBy = sortBy,
                    onSortChange = { sortBy = it },
                    onSupplierSelected = { supplier ->
                        selectedSupplier = supplier
                        scope.launch {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                        }
                    },
                    isLoading = isLoading
                )
            }
        },
        supportingPane = {
            AnimatedPane {
                selectedSupplier?.let { supplier ->
                    SupplierDetailPane(
                        supplier = supplier,
                        onBack = {
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden
                    )
                }
            }
        }
    )
}


@Composable
private fun SuppliersMainPane(
    suppliers: List<Supplier>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    onSupplierSelected: (Supplier) -> Unit,
    isLoading: Boolean
) {
    var headerVisible by remember { mutableStateOf(false) }
    var searchVisible by remember { mutableStateOf(false) }
    var listVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            headerVisible = true
            delay(200)
            searchVisible = true
            delay(200)
            listVisible = true
        }
    }

    Box( // Utiliser Box comme racine pour contrôler la mise en page globale et l'état de chargement
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
        if (isLoading) {
            // L'indicateur de chargement prend toute la taille et centre son contenu
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation()
            }
        } else {
            // Quand il ne charge pas, afficher la LazyColumn pour le contenu défilable
            LazyColumn(
                modifier = Modifier.fillMaxSize(), // LazyColumn remplit l'espace restant
                contentPadding = PaddingValues(vertical = 12.dp), // Rembourrage global du contenu
                verticalArrangement = Arrangement.spacedBy(10.dp) // Espacement cohérent entre les éléments
            ) {
                // En-tête avec statistiques
                item {

                        val totalSuppliers = suppliers.size
                        val activeSuppliers = suppliers.count { it.status == SupplierStatus.ACTIVE }
                        val pendingSuppliers = suppliers.count { it.status == SupplierStatus.PENDING }
                        val totalValue = suppliers.sumOf { it.totalValue }
                        val averageRating = if (suppliers.isNotEmpty()) suppliers.map { it.rating }.average() else 0.0

                        val stats = listOf(
                            StockStat("Total", totalSuppliers.toString(), Res.drawable.Business, MaterialTheme.colorScheme.primary),
                            StockStat("Actifs", activeSuppliers.toString(),  Res.drawable.ic_check_circle, Color(0xFF22c55e)),
                            StockStat("En attente", pendingSuppliers.toString(),  Res.drawable.Schedule, Color(0xFFf59e0b)),
                            StockStat("Note moy.", formatRating(averageRating), Res.drawable.ic_star_filled, Color(0xFF8b5cf6))
                        )

                        val summaries = listOf(
                            StockSummary(
                                label = "Valeur totale des commandes",
                                value = "${formatValue(totalValue / 1000)}K €",
                                iconRes = Res.drawable.TrendingUp,
                                iconTint = MaterialTheme.colorScheme.primary,
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                valueColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        GenericHeader(
                            title = "Gestion des Fournisseurs",
                            subtitle = "Partenaires et relations commerciales",
                            icon = Res.drawable.Person,
                            iconColor = MaterialTheme.colorScheme.primary,
                            stats = stats,
                            summaries = summaries,
                            animateStats = !isLoading // Animate only on initial load
                        )
                    }



                // Barre de recherche et filtres
                item {
                    AnimatedVisibility(
                        visible = searchVisible,
                        enter = slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { -it / 2 }
                        ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
                    ) {
                        GenericSearchAndFiltersSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = onSearchQueryChange,
                            searchLabel = "Rechercher un fournisseur...",
                            selectedFilter = selectedFilter,
                            onFilterChange = onFilterChange,
                            filterOptions = listOf("Tous", "Actifs", "Inactifs", "En attente", "Bloqués"),
                            sortBy = sortBy,
                            onSortChange = onSortChange,
                            sortOptions = listOf("Nom", "Catégorie", "Note", "Commandes", "Valeur", "Statut")
                        )
                    }
                }

                // Liste des fournisseurs
                itemsIndexed(suppliers) { index, supplier -> // Utiliser directement itemsIndexed ici
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 100L)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetX = { it }
                        ) + fadeIn(animationSpec = tween(400))
                    ) {
                        SupplierCard(
                            supplier = supplier,
                            onClick = { onSupplierSelected(supplier) }
                        )
                    }
                }

                // FAB animé
                item {
                    AnimatedVisibility(
                        visible = listVisible,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(animationSpec = tween(400, delayMillis = 600))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp), // Rembourrage ajusté pour le FAB
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            AnimatedFAB()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.Person),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .rotate(rotation),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Chargement des fournisseurs...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AnimatedFAB() {
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    FloatingActionButton(
        onClick = { /* TODO: Ajouter nouveau fournisseur */ },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Ajouter un fournisseur",
            modifier = Modifier.size(24.dp)
        )
    }
}


@Composable
private fun SuppliersList(
    suppliers: List<Supplier>,
    onSupplierSelected: (Supplier) -> Unit,
    modifier: Modifier = Modifier
) {
    // Ce composable ne contient plus directement une LazyColumn.
    // Son contenu est maintenant rendu par la LazyColumn dans SuppliersMainPane.
    Column(
        modifier = modifier.padding(horizontal = 16.dp), // Rembourrage ajusté
        verticalArrangement = Arrangement.spacedBy(10.dp), // Espacement ajusté
        //contentPadding = PaddingValues(vertical = 12.dp) // Rembourrage ajusté
    ) {
        // Les éléments réels sont maintenant gérés directement dans la LazyColumn de SuppliersMainPane
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupplierCard(
    supplier: Supplier,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_scale"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 2.dp, // Élévation ajustée pour correspondre à EntriesScreen
                shape = RoundedCornerShape(12.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
                spotColor = supplier.status.color.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(12.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
            verticalArrangement = Arrangement.spacedBy(10.dp) // Espacement ajusté pour correspondre à EntriesScreen
        ) {
            // En-tête avec nom et statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = supplier.name,
                        style = MaterialTheme.typography.titleMedium.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = supplier.category,
                        style = MaterialTheme.typography.bodySmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Espacement ajusté pour correspondre à EntriesScreen
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
                        colors = CardDefaults.cardColors(
                            containerColor = supplier.status.color.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp) // Espacement ajusté pour correspondre à EntriesScreen
                        ) {
                            Icon(
                                painter = painterResource(supplier.status.icon),
                                contentDescription = null,
                                tint = supplier.status.color,
                                modifier = Modifier.size(12.dp) // Taille ajustée pour correspondre à EntriesScreen
                            )
                            Text(
                                text = supplier.status.label,
                                style = MaterialTheme.typography.labelSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                                    fontWeight = FontWeight.Bold
                                ),
                                color = supplier.status.color
                            )
                        }
                    }

                    // Note avec étoiles
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp) // Espacement ajusté pour correspondre à EntriesScreen
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFfbbf24),
                            modifier = Modifier.size(16.dp) // Taille ajustée pour correspondre à EntriesScreen
                        )
                        Text(
                            text = formatRating(supplier.rating.toDouble()), // Note formatée
                            style = MaterialTheme.typography.labelSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Informations de contact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacement ajusté pour correspondre à EntriesScreen
            ) {
                InfoItem(
                    label = "Contact",
                    value = supplier.contactPerson,
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Ville",
                    value = supplier.city,
                    modifier = Modifier.weight(1f)
                )
            }

            // Métriques business
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacement ajusté pour correspondre à EntriesScreen
            ) {
                InfoItem(
                    label = "Produits",
                    value = "${supplier.productsCount} réf.",
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Commandes",
                    value = supplier.totalOrders.toString(),
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    label = "Valeur totale",
                    value = "${formatValue(supplier.totalValue / 1000)}K €", // Valeur formatée
                    modifier = Modifier.weight(1f)
                )
            }

            // Badge de fiabilité
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
                    colors = CardDefaults.cardColors(
                        containerColor = supplier.reliability.color.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp) // Espacement ajusté pour correspondre à EntriesScreen
                    ) {
                        Icon(
                            painter =painterResource(supplier.reliability.icon),
                            contentDescription = null,
                            tint = supplier.reliability.color,
                            modifier = Modifier.size(12.dp) // Taille ajustée pour correspondre à EntriesScreen
                        )
                        Text(
                            text = "Fiabilité ${supplier.reliability.label}",
                            style = MaterialTheme.typography.labelSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                                fontWeight = FontWeight.Medium
                            ),
                            color = supplier.reliability.color
                        )
                    }
                }

                Text(
                    text = "Dernière commande: ${supplier.lastOrderDate.formatAsDateTime()}",
                    style = MaterialTheme.typography.labelSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    icon: Painter,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp, // Élévation ajustée pour correspondre à EntriesScreen
                shape = RoundedCornerShape(10.dp) // Rayon de coin ajusté pour correspondre à EntriesScreen
            ),
        shape = RoundedCornerShape(10.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espacement ajusté pour correspondre à EntriesScreen
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp) // Espacement ajusté pour correspondre à EntriesScreen
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp) // Taille ajustée pour correspondre à EntriesScreen
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
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

@Composable
private fun MetricCard( // Renamed from StatCard to avoid conflict and for clarity in this context
    title: String,
    value: String,
    subtitle: String,
    icon: Painter,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 2.dp, // Élévation ajustée pour correspondre à EntriesScreen
                shape = RoundedCornerShape(10.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
                spotColor = color.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(10.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp) // Espacement ajusté pour correspondre à EntriesScreen
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp) // Taille ajustée pour correspondre à EntriesScreen
                    .clip(RoundedCornerShape(6.dp)) // Rayon de coin ajusté pour correspondre à EntriesScreen
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp) // Taille ajustée pour correspondre à EntriesScreen
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun SupplierDetailPane(
    supplier: Supplier,
    onBack: () -> Unit,
    showBackButton: Boolean
) {
    var detailsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        detailsVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
        verticalArrangement = Arrangement.spacedBy(10.dp) // Espacement ajusté pour correspondre à EntriesScreen
    ) {
        if (showBackButton) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp)) // Rayon de coin ajusté pour correspondre à EntriesScreen
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
        }

        AnimatedVisibility(
            visible = detailsVisible,
            enter = slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                initialOffsetY = { it / 2 }
            ) + fadeIn(animationSpec = tween(600))
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacement ajusté pour correspondre à EntriesScreen
            ) {
                item {
                    // En-tête du fournisseur
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp, // Élévation ajustée pour correspondre à EntriesScreen
                                shape = RoundedCornerShape(12.dp) // Rayon de coin ajusté pour correspondre à EntriesScreen
                            ),
                        shape = RoundedCornerShape(12.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
                            verticalArrangement = Arrangement.spacedBy(10.dp) // Espacement ajusté pour correspondre à EntriesScreen
                        ) {
                            // En-tête avec logo et statut
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp) // Espacement ajusté pour correspondre à EntriesScreen
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp) // Taille ajustée pour correspondre à EntriesScreen
                                        .clip(RoundedCornerShape(10.dp)) // Rayon de coin ajusté pour correspondre à EntriesScreen
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    supplier.status.color.copy(alpha = 0.2f),
                                                    supplier.status.color.copy(alpha = 0.1f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = supplier.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = supplier.status.color
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = supplier.name,
                                        style = MaterialTheme.typography.titleLarge.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = supplier.category,
                                        style = MaterialTheme.typography.bodyMedium.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Note avec étoiles
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp), // Espacement ajusté pour correspondre à EntriesScreen
                                        modifier = Modifier.padding(top = 2.dp) // Rembourrage ajusté
                                    ) {
                                        repeat(5) { index ->
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (index < supplier.rating.toInt())
                                                    Color(0xFFfbbf24)
                                                else
                                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                                modifier = Modifier.size(14.dp) // Taille ajustée pour correspondre à EntriesScreen
                                            )
                                        }
                                        Text(
                                            text = "${formatRating(supplier.rating.toDouble())}", // Note formatée
                                            style = MaterialTheme.typography.labelSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Card(
                                    shape = RoundedCornerShape(16.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
                                    colors = CardDefaults.cardColors(
                                        containerColor = supplier.status.color.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp) // Espacement ajusté pour correspondre à EntriesScreen
                                    ) {
                                        Icon(
                                            painter = painterResource(supplier.status.icon),
                                            contentDescription = null,
                                            tint = supplier.status.color,
                                            modifier = Modifier.size(14.dp) // Taille ajustée pour correspondre à EntriesScreen
                                        )
                                        Text(
                                            text = supplier.status.label,
                                            style = MaterialTheme.typography.labelSmall.copy( // Taille de police ajustée pour correspondre à EntriesScreen
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = supplier.status.color
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 1.dp) // Épaisseur ajustée

                            // Métriques principales
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacement ajusté pour correspondre à EntriesScreen
                            ) {
                                MetricCard(
                                    title = "Produits",
                                    value = supplier.productsCount.toString(),
                                    subtitle = "références",
                                    icon =  painterResource(Res.drawable.inventory),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Commandes",
                                    value = supplier.totalOrders.toString(),
                                    subtitle = "total",
                                    icon =  painterResource(Res.drawable.ShoppingCart),
                                    color = Color(0xFF3b82f6),
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Valeur",
                                    value = "${formatRating(supplier.totalValue / 1000)}K", // Valeur formatée
                                    subtitle = "euros",
                                    icon =  painterResource(Res.drawable.TrendingUp),
                                    color = Color(0xFF22c55e),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    // Informations de contact
                    DetailSection(
                        title = "Informations de contact",
                        icon =  painterResource(Res.drawable.ContactPhone),
                        items = listOf(
                            "Personne de contact" to supplier.contactPerson,
                            "Email" to supplier.email,
                            "Téléphone" to supplier.phone,
                            "Adresse" to supplier.address,
                            "Ville" to "${supplier.city}, ${supplier.country}"
                        )
                    )
                }

                item {
                    // Informations commerciales
                    DetailSection(
                        title = "Informations commerciales",
                        icon = painterResource(Res.drawable.Business),
                        items = listOf(
                            "Conditions de paiement" to supplier.paymentTerms,
                            "Fiabilité" to supplier.reliability.label,
                            "Dernière commande" to supplier.lastOrderDate.formatAsDateTime(),
                            "Statut" to supplier.status.label
                        )
                    )
                }

                item {
                    // Notes
                    supplier.notes?.let { notes ->
                        DetailSection(
                            title = "Notes et commentaires",
                            icon =  painterResource(Res.drawable.Notes),
                            items = listOf("Commentaires" to notes)
                        )
                    }
                }

                item {
                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacement ajusté pour correspondre à EntriesScreen
                    ) {
                        OutlinedButton(
                            onClick = { /* TODO: Modifier le fournisseur */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
                            contentPadding = PaddingValues(10.dp) // Rembourrage ajusté pour correspondre à EntriesScreen
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp) // Taille ajustée
                            )
                            Spacer(modifier = Modifier.width(6.dp)) // Espacement ajusté
                            Text("Modifier", style = MaterialTheme.typography.labelLarge) // Taille de police ajustée
                        }

                        Button(
                            onClick = { /* TODO: Nouvelle commande */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp), // Rayon de coin ajusté pour correspondre à EntriesScreen
                            contentPadding = PaddingValues(10.dp), // Rembourrage ajusté pour correspondre à EntriesScreen
                            enabled = supplier.status == SupplierStatus.ACTIVE
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp) // Taille ajustée
                            )
                            Spacer(modifier = Modifier.width(6.dp)) // Espacement ajusté
                            Text("Commander", style = MaterialTheme.typography.labelLarge) // Taille de police ajustée
                        }
                    }
                }
            }
        }
    }
}


fun formatValue(value: Double): String {
    val rounded = (value * 10).toInt() / 10.0
    return "$rounded"
}


fun formatRating(rating: Double): String {
    val rounded = (rating * 10).toInt() / 10.0
    return "$rounded"
}
