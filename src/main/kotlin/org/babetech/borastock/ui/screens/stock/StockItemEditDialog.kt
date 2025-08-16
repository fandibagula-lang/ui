package org.babetech.borastock.ui.screens.stock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import borastock.composeapp.generated.resources.*
import org.babetech.borastock.data.models.StockItem
import org.babetech.borastock.data.models.StockStatus
import org.babetech.borastock.ui.components.forms.FormDialog
import org.babetech.borastock.ui.components.forms.FormValidationMessage
import org.babetech.borastock.ui.components.forms.IconTextField

/**
 * Dialogue pour éditer un produit en stock
 */
@Composable
fun StockItemEditDialog(
    item: StockItem,
    onDismiss: () -> Unit,
    onSave: (StockItem) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var category by remember { mutableStateOf(item.category) }
    var currentStock by remember { mutableStateOf(item.currentStock.toString()) }
    var minStock by remember { mutableStateOf(item.minStock.toString()) }
    var maxStock by remember { mutableStateOf(item.maxStock.toString()) }
    var price by remember { mutableStateOf(item.price.toString()) }
    var supplier by remember { mutableStateOf(item.supplier) }
    var errorMessage by remember { mutableStateOf("") }

    val isValid = name.isNotBlank() && 
                  category.isNotBlank() && 
                  supplier.isNotBlank() &&
                  currentStock.toIntOrNull() != null &&
                  minStock.toIntOrNull() != null &&
                  maxStock.toIntOrNull() != null &&
                  price.toDoubleOrNull() != null

    FormDialog(
        title = "Modifier le produit",
        onDismiss = onDismiss,
        onConfirm = {
            if (isValid) {
                val updatedItem = item.copy(
                    name = name,
                    category = category,
                    currentStock = currentStock.toInt(),
                    minStock = minStock.toInt(),
                    maxStock = maxStock.toInt(),
                    price = price.toDouble(),
                    supplier = supplier
                )
                onSave(updatedItem)
            } else {
                errorMessage = "Veuillez remplir tous les champs correctement"
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
                value = name,
                onValueChange = { 
                    name = it
                    errorMessage = ""
                },
                label = "Nom du produit",
                icon = Res.drawable.inventory
            )

            IconTextField(
                value = category,
                onValueChange = { 
                    category = it
                    errorMessage = ""
                },
                label = "Catégorie",
                icon = Res.drawable.Category
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconTextField(
                    value = currentStock,
                    onValueChange = { 
                        if (it.matches(Regex("^\\d*"))) {
                            currentStock = it
                            errorMessage = ""
                        }
                    },
                    label = "Stock actuel",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                IconTextField(
                    value = minStock,
                    onValueChange = { 
                        if (it.matches(Regex("^\\d*"))) {
                            minStock = it
                            errorMessage = ""
                        }
                    },
                    label = "Stock min",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconTextField(
                    value = maxStock,
                    onValueChange = { 
                        if (it.matches(Regex("^\\d*"))) {
                            maxStock = it
                            errorMessage = ""
                        }
                    },
                    label = "Stock max",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                IconTextField(
                    value = price,
                    onValueChange = { 
                        if (it.matches(Regex("^\\d*\\.?\\d*"))) {
                            price = it
                            errorMessage = ""
                        }
                    },
                    label = "Prix (€)",
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
                label = "Fournisseur",
                icon = Res.drawable.Business
            )

            FormValidationMessage(
                message = errorMessage,
                isError = true
            )
        }
    }
}