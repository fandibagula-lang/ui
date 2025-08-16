package org.babetech.borastock.ui.screens.screennavigation.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import kotlinx.coroutines.launch
import borastock.composeapp.generated.resources.* // Assuming your generated resources are here
import org.babetech.borastock.ui.screens.setup.viewmodel.CompanySetupViewModel // Import the ViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource // Import for painterResource

import coil3.ImageLoader
import coil3.compose.AsyncImage

import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.getPath
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.coil.KmpFileFetcher
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import org.babetech.borastock.ui.screens.auth.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.toArgb
import org.babetech.borastock.ui.theme.AppThemeType // Importation de l'enum

// Data class to represent a section/category of company information
data class CompanyInfoSection(
    val id: String,
    val title: String,
    val iconResId: DrawableResource // Icon for the section, now an Int resource ID
)

// Extension function to convert a hex string to a Compose Color
fun String.toColor(): Color {
    return try {
        Color(this.removePrefix("#").toLong(16).toInt() or 0xFF000000.toInt())
    } catch (e: Exception) {
        Color.Black // Fallback color
    }
}

// Fonction utilitaire pour mapper une couleur hexadécimale à un nom de type de thème
fun getThemeNameFromColorHex(colorHex: String): String {
    return when (colorHex) {
        "#2196F3", "#0A3B5C" -> "BLEU_MARINE"
        "#9C27B0", "#F44336" -> "VIOLET_MYSTERE"
        "#FF9800" -> "ORANGE_TANGELO"
        else -> "DEFAULT"
    }
}

// Définition du Composable pour l'écran de configuration des informations de l'entreprise
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoScreen(
    viewModel: CompanySetupViewModel = koinViewModel()
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()
    val paneState = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
    val showSupporting = paneState != PaneAdaptedValue.Hidden

    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Liste des sections pour le volet principal
    val sections = remember {
        listOf(
            CompanyInfoSection("general", "1. Informations générales", Res.drawable.ic_info), // Assuming ic_info drawable exists
            CompanyInfoSection("address", "2. Adresse & localisation", Res.drawable.ic_location_on), // Assuming ic_location_on drawable exists
            CompanyInfoSection("contact", "3. Coordonnées", Res.drawable.ic_phone), // Assuming ic_phone drawable exists
            CompanyInfoSection("billing", "4. Paramètres de facturation", Res.drawable.ic_receipt), // Assuming ic_receipt drawable exists
            CompanyInfoSection("visual", "5. Identité visuelle", Res.drawable.palette_24px), // Assuming ic_palette drawable exists
            CompanyInfoSection("advanced", "6. Options avancées", Res.drawable.ic_settings) // Assuming ic_settings drawable exists
        )
    }

    val viewModels = koinViewModel<LoginViewModel>()


    val isLoading = viewModels.isLoading.collectAsStateWithLifecycle()
    val currentUser = viewModels.currentUser.collectAsStateWithLifecycle()

    // État pour suivre la section actuellement sélectionnée
    var selectedSectionId by remember { mutableStateOf(sections.first().id) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                // Volet principal: liste des sections
                AnimatedPane(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        topBar = {
                            LargeTopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            "Paramètres de l'entreprise ${currentUser.value?.confirme}",
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            "Configurez les informations de votre entreprise",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.largeTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    ) { paddingValues ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp) // Reduced spacing
                        ) {
                            items(sections) { section ->
                                SectionListItem(
                                    section = section,
                                    isSelected = section.id == selectedSectionId,
                                    onClick = {
                                        selectedSectionId = section.id
                                        scope.launch {
                                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                        }
                                    }
                                )
                            }
                            item {
                                Spacer(Modifier.height(16.dp)) // Space at the bottom
                            }
                        }
                    }
                }
            },
            supportingPane = {
                // Volet de support: détails de la section sélectionnée
                if (showSupporting) {
                    AnimatedPane(modifier = Modifier.fillMaxSize()) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(
                                            sections.find { it.id == selectedSectionId }?.title
                                                ?: ""
                                        )
                                    },
                                    navigationIcon = {
                                        if (navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded) {
                                            IconButton(onClick = { scope.launch { navigator.navigateBack() } }) {
                                                Icon(
                                                    painterResource(Res.drawable.ic_arrow_back),
                                                    contentDescription = "Retour"
                                                ) // Assuming ic_arrow_back drawable exists
                                            }
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                        ) { paddingValues ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp) // Adjusted spacing
                            ) {
                                // Afficher l'indicateur de chargement si les données sont en cours de chargement
                                if (uiState.isLoading) {
                                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                }

                                // Afficher le message d'erreur le cas échéant
                                uiState.errorMessage?.let { message ->
                                    Text(
                                        text = "Erreur: $message",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                LazyColumn(
                                    modifier = Modifier.weight(1f), // Make LazyColumn fill remaining space
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    item {
                                        // Rendre le contenu en fonction de selectedSectionId
                                        when (selectedSectionId) {
                                            "general" -> GeneralInfoSection(
                                                companyName = uiState.companyName, onCompanyNameChange = viewModel::updateCompanyName,
                                                mainActivity = uiState.mainActivity, onMainActivityChange = viewModel::updateMainActivity,
                                                slogan = "6600 VOLTS – Électricité & Bâtiment", onSloganChange = { /* Not handled by ViewModel */ },
                                                idNat = uiState.idNat, onIdNatChange = viewModel::updateIdNat,
                                                rccmNrc = uiState.rccmNrc, onRccmNrcChange = viewModel::updateRccmNrc
                                            )

                                            "address" -> AddressLocationSection(
                                                address = uiState.address, onAddressChange = viewModel::updateAddress,
                                                city = uiState.city, onCityChange = viewModel::updateCity,
                                                province = uiState.province, onProvinceChange = viewModel::updateProvince,
                                                country = uiState.country, onCountryChange = viewModel::updateCountry,
                                                postalCode = "", onPostalCodeChange = { /* Not handled by ViewModel */ }
                                            )

                                            "contact" -> ContactInfoSection(
                                                phone1 = uiState.phone1, onPhone1Change = viewModel::updatePhone1,
                                                phone2 = "+243 853513481", onPhone2Change = { /* Not handled by ViewModel */ },
                                                email = "contact@entreprise.cd", onEmailChange = { /* Not handled by ViewModel */ },
                                                website = "www.entreprise-materiaux.cd", onWebsiteChange = { /* Not handled by ViewModel */ }
                                            )

                                            "billing" -> BillingSettingsSection(
                                                invoicePrefix = "FACTURE N°", onInvoicePrefixChange = { /* Not handled by ViewModel */ },
                                                startingNumber = "1182", onStartingNumberChange = { /* Not handled by ViewModel */ },
                                                salesConditions = "Les marchandises vendues ne sont ni reprises ni échangées", onSalesConditionsChange = { /* Not handled by ViewModel */ },
                                                invoiceFooter = "Merci pour votre achat chez nous !", onInvoiceFooterChange = { /* Not handled by ViewModel */ }
                                            )

                                            "visual" -> VisualIdentitySection(
                                                logoUrl = uiState.logoUri ?: "",
                                                onLogoUrlChange = viewModel::updateLogoUri,
                                                mainColor = uiState.mainColor,
                                                onMainColorChange = viewModel::updateMainColor,
                                                // Passage de la nouvelle fonction pour mettre à jour le type de thème
                                                onAppThemeTypeChange = viewModel::updateAppThemeType,
                                                customFont = "Roboto",
                                                onCustomFontChange = { /* Not handled by ViewModel */ }
                                            )

                                            "advanced" -> AdvancedOptionsSection(
                                                managerName = "M. Jean Mukonzi", onManagerNameChange = { /* Not handled by ViewModel */ },
                                                managerFunction = "Directeur Général", onManagerFunctionChange = { /* Not handled by ViewModel */ },
                                                openingHours = "Lundi à Samedi : 8h00 - 17h00", onOpeningHoursChange = { /* Not handled by ViewModel */ },
                                                taxNumber = uiState.taxNumber, onTaxNumberChange = viewModel::updateTaxNumber,
                                                vatRate = uiState.vatRate, onVatRateChange = viewModel::updateVatRate,
                                                creationYear = "2010", onCreateYearChange = { /* Not handled by ViewModel */ },
                                                employeesCount = "15", onEmployeesCountChange = { /* Not handled by ViewModel */ }
                                            )
                                        }
                                    }
                                }
                                Button(
                                    onClick = { viewModel.saveCompanyInfo() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    enabled = !uiState.isLoading // Désactiver le bouton pendant le chargement
                                ) {
                                    Text(
                                        "Enregistrer les informations",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                                Spacer(Modifier.height(16.dp)) // Espace en bas du bouton
                            }
                        }
                    }
                }
            }
        )
    }
}

// Composable pour un élément de la liste de sections
@Composable
fun SectionListItem(section: CompanyInfoSection, isSelected: Boolean, onClick: () -> Unit) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "animatedElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
//            .shadow(
//                elevation = animatedElevation,
//                shape = RoundedCornerShape(16.dp)
//
        ,shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                   .background(MaterialTheme.colorScheme.surfaceVariant), // Static background color
                contentAlignment = Alignment.Center
            ) {
                // L'icône utilisera désormais la LocalContentColor par défaut ou un tint explicitement défini
                Icon(
                    painter = painterResource(section.iconResId),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // Définition explicite du tint
                )
            }

            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.weight(1f))
            if (isSelected) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check_circle), // Assumes ic_check_circle drawable exists
                    contentDescription = "Sélectionné",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// Composables de section individuelle (extraits de l'écran principal pour plus de clarté)
@Composable
fun GeneralInfoSection(
    companyName: String, onCompanyNameChange: (String) -> Unit,
    mainActivity: String, onMainActivityChange: (String) -> Unit,
    slogan: String, onSloganChange: (String) -> Unit, // Keep for now, not in ViewModel
    idNat: String, onIdNatChange: (String) -> Unit,
    rccmNrc: String, onRccmNrcChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = companyName,
            onValueChange = onCompanyNameChange,
            label = { Text("Nom de l'entreprise") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_business), contentDescription = null) }, // Assuming ic_business drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = mainActivity,
            onValueChange = onMainActivityChange,
            label = { Text("Activité principale") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_work), contentDescription = null) }, // Assuming ic_work drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = slogan,
            onValueChange = onSloganChange,
            label = { Text("Slogan / Spécialité") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_star), contentDescription = null) }, // Assuming ic_star drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = idNat,
            onValueChange = onIdNatChange,
            label = { Text("ID.NAT") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_badge), contentDescription = null) }, // Assuming ic_badge drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = rccmNrc,
            onValueChange = onRccmNrcChange,
            label = { Text("Numéro RCCM / NRC") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_article), contentDescription = null) }, // Assuming ic_article drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun AddressLocationSection(
    address: String, onAddressChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit,
    province: String, onProvinceChange: (String) -> Unit,
    country: String, onCountryChange: (String) -> Unit,
    postalCode: String, onPostalCodeChange: (String) -> Unit // Keep for now, not in ViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Adresse physique") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_home), contentDescription = null) }, // Assuming ic_home drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = { Text("Ville") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_location_city), contentDescription = null) }, // Assuming ic_location_city drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = province,
            onValueChange = onProvinceChange,
            label = { Text("Province / Région") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_public), contentDescription = null) }, // Assuming ic_public drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = country,
            onValueChange = onCountryChange,
            label = { Text("Pays") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_flag), contentDescription = null) }, // Assuming ic_flag drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = postalCode,
            onValueChange = onPostalCodeChange,
            label = { Text("Code postal (optionnel)") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_local_post_office), contentDescription = null) }, // Assuming ic_local_post_office drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun ContactInfoSection(
    phone1: String, onPhone1Change: (String) -> Unit,
    phone2: String, onPhone2Change: (String) -> Unit, // Keep for now, not in ViewModel
    email: String, onEmailChange: (String) -> Unit, // Keep for now, not in ViewModel
    website: String, onWebsiteChange: (String) -> Unit // Keep for now, not in ViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = phone1,
            onValueChange = onPhone1Change,
            label = { Text("Téléphone 1") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_phone), contentDescription = null) }, // Assuming ic_phone drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = phone2,
            onValueChange = onPhone2Change,
            label = { Text("Téléphone 2") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_phone), contentDescription = null) }, // Assuming ic_phone_android drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email (optionnel)") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_email), contentDescription = null) }, // Assuming ic_email drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = website,
            onValueChange = onWebsiteChange,
            label = { Text("Site Web (optionnel)") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_link), contentDescription = null) }, // Assuming ic_link drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun BillingSettingsSection(
    invoicePrefix: String, onInvoicePrefixChange: (String) -> Unit, // Keep for now, not in ViewModel
    startingNumber: String, onStartingNumberChange: (String) -> Unit, // Keep for now, not in ViewModel
    salesConditions: String, onSalesConditionsChange: (String) -> Unit, // Keep for now, not in ViewModel
    invoiceFooter: String, onInvoiceFooterChange: (String) -> Unit // Keep for now, not in ViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = invoicePrefix,
            onValueChange = onInvoicePrefixChange,
            label = { Text("Préfixe de facture") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_numbers), contentDescription = null) }, // Assuming ic_numbers drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = startingNumber,
            onValueChange = onStartingNumberChange,
            label = { Text("Numéro de départ") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_looks_one), contentDescription = null) }, // Assuming ic_looks_one drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = salesConditions,
            onValueChange = onSalesConditionsChange,
            label = { Text("Conditions de vente") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_description), contentDescription = null) }, // Assuming ic_description drawable exists
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = invoiceFooter,
            onValueChange = onInvoiceFooterChange,
            label = { Text("Pied de page de facture (optionnel)") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_note), contentDescription = null) }, // Assuming ic_note drawable exists
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun VisualIdentitySection(
    logoUrl: String,
    onLogoUrlChange: (String) -> Unit,
    mainColor: String?, // Now nullable to match ViewModel state
    onMainColorChange: (String) -> Unit,
    // Nouveau paramètre pour la mise à jour du thème
    onAppThemeTypeChange: (String) -> Unit,
    customFont: String,
    onCustomFontChange: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    var platformSpecificFilePath by remember { mutableStateOf("") }
    var platformSpecificFile by remember { mutableStateOf<KmpFile?>(null) }

    val imageLoader =
        ImageLoader.Builder(coil3.compose.LocalPlatformContext.current).components {
            add(KmpFileFetcher.Factory())
        }.build()

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            scope.launch {
                files.firstOrNull()?.let { kmpFile ->
                    val filePath = kmpFile.getPath(context)
                    if (filePath != null) {
                        onLogoUrlChange(filePath)
                    }
                }
            }
        }
    )

    // Palette de couleurs par défaut
    val colorPalette = remember {
        listOf(
            "#0A3B5C", // Deep Blue -> BLEU_MARINE
            "#4CAF50", // Green -> DEFAULT
            "#FF9800", // Orange -> ORANGE_TANGELO
            //"#F44336", // Red -> VIOLET_MYSTERE (fallback)
            "#9C27B0", // Purple -> VIOLET_MYSTERE
          //  "#2196F3"  // Blue -> BLEU_MARINE (fallback)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logique corrigée pour afficher soit l'image, soit un espace réservé
        if (logoUrl.isNotEmpty()) {
            AsyncImage(
                imageLoader = imageLoader,
                model = logoUrl,
                contentDescription = "Logo de l’entreprise",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.ic_image),
                contentDescription = "Placeholder pour le logo de l’entreprise",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            )
        }

        Button(
            onClick = { pickerLauncher.launch() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Téléverser un logo")
        }

        // Ajout du sélecteur de palette de couleurs
        Text(
            text = "Couleur principale",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        ColorPaletteSelector(
            colors = colorPalette,
            selectedColorHex = mainColor,
            onColorSelected = { colorHex ->
                // Mise à jour de la couleur principale
                onMainColorChange(colorHex)
                // Mise à jour du type de thème en fonction de la couleur sélectionnée
                val newThemeName = getThemeNameFromColorHex(colorHex)
                onAppThemeTypeChange(newThemeName)
            }
        )

        OutlinedTextField(
            value = customFont,
            onValueChange = onCustomFontChange,
            label = { Text("Police personnalisée") },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_font_download),
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// Composable pour le sélecteur de palette de couleurs
@Composable
fun ColorPaletteSelector(
    colors: List<String>,
    selectedColorHex: String?,
    onColorSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colors) { colorHex ->
            val color = colorHex.toColor()
            val isSelected = selectedColorHex == colorHex
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .clickable { onColorSelected(colorHex) }
//                    .then(
//                        if (isSelected) {
//                            Modifier.shadow(8.dp, RoundedCornerShape(12.dp))
//                        } else {
//                            Modifier
//                        }
//                    ),
               , contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_check), // Assumes ic_check drawable exists
                        contentDescription = "Couleur sélectionnée",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun AdvancedOptionsSection(
    managerName: String, onManagerNameChange: (String) -> Unit, // Keep for now, not in ViewModel
    managerFunction: String, onManagerFunctionChange: (String) -> Unit, // Keep for now, not in ViewModel
    openingHours: String, onOpeningHoursChange: (String) -> Unit, // Keep for now, not in ViewModel
    taxNumber: String, onTaxNumberChange: (String) -> Unit,
    vatRate: String, onVatRateChange: (String) -> Unit,
    creationYear: String, onCreateYearChange: (String) -> Unit, // Keep for now, not in ViewModel
    employeesCount: String, onEmployeesCountChange: (String) -> Unit // Keep for now, not in ViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = managerName,
            onValueChange = onManagerNameChange,
            label = { Text("Nom du gérant / responsable") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_person), contentDescription = null) }, // Assuming ic_person drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = managerFunction,
            onValueChange = onManagerFunctionChange,
            label = { Text("Fonction du gérant (optionnel)") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_assignment_ind), contentDescription = null) }, // Assuming ic_assignment_ind drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = openingHours,
            onValueChange = onOpeningHoursChange,
            label = { Text("Horaires d'ouverture") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_access_time), contentDescription = null) }, // Assuming ic_access_time drawable exists
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = taxNumber,
            onValueChange = onTaxNumberChange,
            label = { Text("N° d'impôt / TVA") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_attach_money), contentDescription = null) }, // Assuming ic_attach_money drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = vatRate,
            onValueChange = onVatRateChange,
            label = { Text("Taux de TVA (%)") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_percent), contentDescription = null) }, // Assuming ic_percent drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = creationYear,
            onValueChange = onCreateYearChange,
            label = { Text("Année de création (optionnel)") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_calendar_today), contentDescription = null) }, // Assuming ic_calendar_today drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = employeesCount,
            onValueChange = onEmployeesCountChange,
            label = { Text("Nombre d'employés (optionnel)") },
            leadingIcon = { Icon(painterResource(Res.drawable.ic_group), contentDescription = null) }, // Assuming ic_group drawable exists
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
