package org.babetech.borastock.ui.screens.exits

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
import org.babetech.borastock.data.models.ExitStatus
import org.babetech.borastock.data.models.ExitUrgency
import org.babetech.borastock.data.models.StockExit
import org.babetech.borastock.ui.components.forms.FormDialog
import org.babetech.borastock.ui.components.forms.FormValidationMessage
import org.babetech.borastock.ui.components.forms.IconTextField
import kotlin.time.ExperimentalTime

/**
 * Dialogue pour ajouter une nouvelle sortie de stock
 */
@OptIn(ExperimentalTime::class)
@Composable
fun StockExitAddDialog(
    onDismiss: () -> Unit,
    onSave: (StockExit) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var customer by remember { mutableStateOf("") }
    var orderNumber by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val isValid = productName.isNotBlank() && 
                  category.isNotBlank() && 
                  customer.isNotBlank() &&
                  quantity.toIntOrNull()?.let { it > 0 } == true &&
                  unitPrice.toDoubleOrNull()?.let { it > 0 } == true

    FormDialog(
        title = "Ajouter une nouvelle sortie",
        onDismiss = onDismiss,
        onConfirm = {
            if (isValid) {
                val newExit = StockExit(
                    id = "", // Sera généré par le repository
                    productName = productName,
                    category = category,
                    quantity = quantity.toInt(),
                    unitPrice = unitPrice.toDouble(),
                    totalValue = 0.0, // Sera calculé par le repository
                    customer = customer,
                    exitDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    orderNumber = orderNumber.ifBlank { null },
                    deliveryAddress = deliveryAddress.ifBlank { null },
                    status = ExitStatus.PENDING,
                    notes = notes.ifBlank { null },
                    urgency = ExitUrgency.LOW
                )
                onSave(newExit)
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
                value = customer,
                onValueChange = { 
                    customer = it
                    errorMessage = ""
                },
                label = "Client *",
                icon = Res.drawable.Person
            )

            IconTextField(
                value = orderNumber,
                onValueChange = { orderNumber = it },
                label = "N° de commande (optionnel)",
                icon = Res.drawable.Receipt
            )

            IconTextField(
                value = deliveryAddress,
                onValueChange = { deliveryAddress = it },
                label = "Adresse de livraison (optionnel)",
                icon = Res.drawable.LocationOn,
                singleLine = false,
                minLines = 2
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
 * Dialogue pour éditer une sortie de stock existante
 */
@Composable
fun StockExitEditDialog(
    exit: StockExit,
    onDismiss: () -> Unit,
    onSave: (StockExit) -> Unit
) {
    var productName by remember { mutableStateOf(exit.productName) }
    var category by remember { mutableStateOf(exit.category) }
    var quantity by remember { mutableStateOf(exit.quantity.toString()) }
    var unitPrice by remember { mutableStateOf(exit.unitPrice.toString()) }
    var customer by remember { mutableStateOf(exit.customer) }
    var orderNumber by remember { mutableStateOf(exit.orderNumber ?: "") }
    var deliveryAddress by remember { mutableStateOf(exit.deliveryAddress ?: "") }
    var notes by remember { mutableStateOf(exit.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    val isValid = productName.isNotBlank() && 
                  category.isNotBlank() && 
                  customer.isNotBlank() &&
                  quantity.toIntOrNull()?.let { it > 0 } == true &&
                  unitPrice.toDoubleOrNull()?.let { it > 0 } == true

    FormDialog(
        title = "Modifier la sortie",
        onDismiss = onDismiss,
        onConfirm = {
            if (isValid) {
                val updatedExit = exit.copy(
                    productName = productName,
                    category = category,
                    quantity = quantity.toInt(),
                    unitPrice = unitPrice.toDouble(),
                    customer = customer,
                    orderNumber = orderNumber.ifBlank { null },
                    deliveryAddress = deliveryAddress.ifBlank { null },
                    notes = notes.ifBlank { null }
                )
                onSave(updatedExit)
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
                value = customer,
                onValueChange = { 
                    customer = it
                    errorMessage = ""
                },
                label = "Client *",
                icon = Res.drawable.Person
            )

            IconTextField(
                value = orderNumber,
                onValueChange = { orderNumber = it },
                label = "N° de commande (optionnel)",
                icon = Res.drawable.Receipt
            )

            IconTextField(
                value = deliveryAddress,
                onValueChange = { deliveryAddress = it },
                label = "Adresse de livraison (optionnel)",
                icon = Res.drawable.LocationOn,
                singleLine = false,
                minLines = 2
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