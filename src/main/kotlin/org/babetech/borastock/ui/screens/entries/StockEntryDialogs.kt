package org.babetech.borastock.ui.screens.entries

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.babetech.borastock.data.models.EntryStatus
import org.babetech.borastock.data.models.StockEntry
import org.babetech.borastock.ui.components.forms.FormDialog
import org.babetech.borastock.ui.components.forms.FormValidationMessage
import org.babetech.borastock.ui.components.forms.IconTextField
import kotlin.time.ExperimentalTime

/**
 * Dialogue pour ajouter une nouvelle entrée de stock
 */
@OptIn(ExperimentalTime::class)
@Composable
fun StockEntryAddDialog(
    onDismiss: () -> Unit,
    onSave: (StockEntry) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var batchNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val isValid = productName.isNotBlank() && 
                  category.isNotBlank() && 
                  supplier.isNotBlank() &&
                  quantity.toIntOrNull()?.let { it > 0 } == true &&
                  unitPrice.toDoubleOrNull()?.let { it > 0 } == true

    FormDialog(
        title = "Ajouter une nouvelle entrée",
        onDismiss = onDismiss,
        onConfirm = {
            if (isValid) {
                val newEntry = StockEntry(
                    id = "", // Sera généré par le repository
                    productName = productName,
                    category = category,
                    quantity = quantity.toInt(),
                    unitPrice = unitPrice.toDouble(),
                    totalValue = 0.0, // Sera calculé par le repository
                    supplier = supplier,
                    entryDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    batchNumber = batchNumber.ifBlank { null },
                    expiryDate = null,
                    status = EntryStatus.PENDING,
                    notes = notes.ifBlank { null }
                )
                onSave(newEntry)
            } else {
                errorMessage = "Veuillez remplir tous les champs obligatoires correctement"
            }
        },
        confirmText = "Ajouter",
        isConfirmEnabled = isValid
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTextField(
                value = productName,
                onValueChange = { 
                    productName = it
                    errorMessage = ""
                },
                label = "Nom du produit *",
                icon = Res.drawable.inventory
            )

            IconTextField(
                value = category,
                onValueChange = { 
                    category = it
                    errorMessage = ""
                },
                label = "Catégorie *",
                icon = Res.drawable.Category
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconTextField(
                    value = quantity,
                    onValueChange = { 
                        if (it.matches(Regex("^\\d*"))) {
                            quantity = it
                            errorMessage = ""
                        }
                    },
                    label = "Quantité *",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                IconTextField(
                    value = unitPrice,
                    onValueChange = { 
                        if (it.matches(Regex("^\\d*\\.?\\d*"))) {
                            unitPrice = it
                            errorMessage = ""
                        }
                    },
                    label = "Prix unitaire (€) *",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            IconTextField(
                value = supplier,
                onValueChange = { 
                    supplier = it
                    errorMessage = ""
                },
                label = "Fournisseur *",
                icon = Res.drawable.Business
            )

            IconTextField(
                value = batchNumber,
                onValueChange = { batchNumber = it },
                label = "N° de lot (optionnel)",
                icon = Res.drawable.QrCode
            )

            IconTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes (optionnel)",
                icon = Res.drawable.Note,
                singleLine = false,
                minLines = 3
            )

            FormValidationMessage(
                message = errorMessage,
                isError = true
            )
        }
    }
}

/**
 * Dialogue pour éditer une entrée de stock existante
 */
@Composable
fun StockEntryEditDialog(
    entry: StockEntry,
    onDismiss: () -> Unit,
    onSave: (StockEntry) -> Unit
) {
    var productName by remember { mutableStateOf(entry.productName) }
    var category by remember { mutableStateOf(entry.category) }
    var quantity by remember { mutableStateOf(entry.quantity.toString()) }
    var unitPrice by remember { mutableStateOf(entry.unitPrice.toString()) }
    var supplier by remember { mutableStateOf(entry.supplier) }
    var batchNumber by remember { mutableStateOf(entry.batchNumber ?: "") }
    var notes by remember { mutableStateOf(entry.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    val isValid = productName.isNotBlank() && 
                  category.isNotBlank() && 
                  supplier.isNotBlank() &&
                  quantity.toIntOrNull()?.let { it > 0 } == true &&
                  unitPrice.toDoubleOrNull()?.let { it > 0 } == true

    FormDialog(
        title = "Modifier l'entrée",
        onDismiss = onDismiss,
        onConfirm = {
            if (isValid) {
                val updatedEntry = entry.copy(
                    productName = productName,
                    category = category,
                    quantity = quantity.toInt(),
                    unitPrice = unitPrice.toDouble(),
                    supplier = supplier,
                    batchNumber = batchNumber.ifBlank { null },
                    notes = notes.ifBlank { null }
                )
                onSave(updatedEntry)
            } else {
                errorMessage = "Veuillez remplir tous les champs obligatoires correctement"
            }
        },
        confirmText = "Enregistrer",
        isConfirmEnabled = isValid
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTextField(
                value = productName,
                onValueChange = { 
                    productName = it
                    errorMessage = ""
                },
                label = "Nom du produit *",
                icon = Res.drawable.inventory
            )

            IconTextField(
                value = category,
                onValueChange = { 
                    category = it
                    errorMessage = ""
                },
                label = "Catégorie *",
                icon = Res.drawable.Category
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconTextField(
                    value = quantity,
                    onValueChange = { 
                        if (it.matches(Regex("^\\d*"))) {
                            quantity = it
                            errorMessage = ""
                        }
                    },
                    label = "Quantité *",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                IconTextField(
                    value = unitPrice,
                    onValueChange = { 
                        if (it.matches(Regex("^\\d*\\.?\\d*"))) {
                            unitPrice = it
                            errorMessage = ""
                        }
                    },
                    label = "Prix unitaire (€) *",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            IconTextField(
                value = supplier,
                onValueChange = { 
                    supplier = it
                    errorMessage = ""
                },
                label = "Fournisseur *",
                icon = Res.drawable.Business
            )

            IconTextField(
                value = batchNumber,
                onValueChange = { batchNumber = it },
                label = "N° de lot (optionnel)",
                icon = Res.drawable.QrCode
            )

            IconTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes (optionnel)",
                icon = Res.drawable.Note,
                singleLine = false,
                minLines = 3
            )

            FormValidationMessage(
                message = errorMessage,
                isError = true
            )
        }
    }
}