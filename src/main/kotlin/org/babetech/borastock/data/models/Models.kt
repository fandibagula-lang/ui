package org.babetech.borastock.data.models

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import borastock.composeapp.generated.resources.*

// ===== ENUMS =====

enum class StockStatus(val label: String, val color: Color, val icon: DrawableResource) {
    IN_STOCK("En stock", Color(0xFF22c55e), Res.drawable.CheckCircle),
    LOW_STOCK("Stock faible", Color(0xFFf59e0b), Res.drawable.Warning),
    OUT_OF_STOCK("Rupture", Color(0xFFef4444), Res.drawable.Error),
    OVERSTOCKED("Surstock", Color(0xFF3b82f6), Res.drawable.TrendingUp)
}

enum class EntryStatus(val label: String, val color: Color, val iconRes: DrawableResource) {
    PENDING("En attente", Color(0xFFf59e0b), Res.drawable.Schedule),
    VALIDATED("Validée", Color(0xFF22c55e), Res.drawable.CheckCircle),
    RECEIVED("Reçue", Color(0xFF3b82f6), Res.drawable.inventory),
    CANCELLED("Annulée", Color(0xFFef4444), Res.drawable.ic_close)
}

enum class ExitStatus(val label: String, val color: Color, val iconRes: DrawableResource) {
    PENDING("En préparation", Color(0xFFf59e0b), Res.drawable.Schedule),
    PREPARED("Préparée", Color(0xFF3b82f6), Res.drawable.inventory),
    SHIPPED("Expédiée", Color(0xFF8b5cf6), Res.drawable.LocalShipping),
    DELIVERED("Livrée", Color(0xFF22c55e), Res.drawable.ic_check_circle),
    CANCELLED("Annulée", Color(0xFFef4444), Res.drawable.ic_cancel_filled)
}

enum class ExitUrgency(val label: String, val color: Color, val iconRes: DrawableResource) {
    LOW("Normale", Color(0xFF6b7280), Res.drawable.Remove),
    MEDIUM("Prioritaire", Color(0xFFf59e0b), Res.drawable.PriorityHigh),
    HIGH("Urgente", Color(0xFFef4444), Res.drawable.Warning)
}

enum class SupplierStatus(val label: String, val color: Color, val icon: DrawableResource) {
    ACTIVE("Actif", Color(0xFF22c55e), Res.drawable.ic_check_circle),
    INACTIVE("Inactif", Color(0xFF6b7280), Res.drawable.Pause),
    PENDING("En attente", Color(0xFFf59e0b), Res.drawable.Schedule),
    BLOCKED("Bloqué", Color(0xFFef4444), Res.drawable.Block)
}

enum class SupplierReliability(val label: String, val color: Color, val icon: DrawableResource) {
    EXCELLENT("Excellent", Color(0xFF22c55e), Res.drawable.ic_star_filled),
    GOOD("Bon", Color(0xFF3b82f6), Res.drawable.ThumbUp),
    AVERAGE("Moyen", Color(0xFFf59e0b), Res.drawable.Remove),
    POOR("Faible", Color(0xFFef4444), Res.drawable.ThumbDown)
}

enum class SettingType {
    NAVIGATION,
    SWITCH,
    ACTION,
    INFO
}

// ===== DATA CLASSES =====

data class StockItem(
    val id: String,
    val name: String,
    val category: String,
    val currentStock: Int,
    val minStock: Int,
    val maxStock: Int,
    val price: Double,
    val supplier: String,
    val lastUpdate: String,
    val status: StockStatus
) {
    val stockPercentage: Float
        get() = if (maxStock > 0) (currentStock.toFloat() / maxStock.toFloat()).coerceIn(0f, 1f) else 0f
    
    val totalValue: Double
        get() = currentStock * price
}

data class StockEntry(
    val id: String,
    val productName: String,
    val category: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalValue: Double,
    val supplier: String,
    val entryDate: LocalDateTime,
    val batchNumber: String?,
    val expiryDate: LocalDateTime?,
    val status: EntryStatus,
    val notes: String?
)

data class StockExit(
    val id: String,
    val productName: String,
    val category: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalValue: Double,
    val customer: String,
    val exitDate: LocalDateTime,
    val orderNumber: String?,
    val deliveryAddress: String?,
    val status: ExitStatus,
    val notes: String?,
    val urgency: ExitUrgency
)

data class Supplier(
    val id: String,
    val name: String,
    val category: String,
    val contactPerson: String,
    val email: String,
    val phone: String,
    val address: String,
    val city: String,
    val country: String,
    val productsCount: Int,
    val totalOrders: Int,
    val totalValue: Double,
    val rating: Float,
    val status: SupplierStatus,
    val reliability: SupplierReliability,
    val lastOrderDate: LocalDateTime,
    val paymentTerms: String,
    val notes: String?
)

// ===== UI MODELS =====

data class StockStat(
    val title: String,
    val value: String,
    val iconRes: DrawableResource,
    val color: Color
)

data class StockSummary(
    val label: String,
    val value: String,
    val iconRes: DrawableResource,
    val iconTint: Color,
    val backgroundColor: Color,
    val valueColor: Color
)

data class MetricData(
    val title: String,
    val value: String,
    val trend: String,
    val trendUp: Boolean,
    val icon: DrawableResource,
    val color: Color
)

data class Movement(
    val description: String,
    val time: String,
    val isIncoming: Boolean
)

data class StatisticCard(
    val title: String,
    val value: String,
    val change: String,
    val isPositive: Boolean,
    val icon: DrawableResource,
    val color: Color
)

data class ChartPeriod(
    val label: String,
    val value: String
)

data class TopProduct(
    val name: String,
    val category: String,
    val sales: Int,
    val revenue: Double
)

data class SettingCategory(
    val title: String,
    val items: List<SettingItem>
)

data class SettingItem(
    val title: String,
    val subtitle: String? = null,
    val icon: DrawableResource,
    val type: SettingType,
    val switchState: Boolean = false,
    val onSwitchChanged: ((Boolean) -> Unit)? = null,
    val action: (() -> Unit)? = null
)

data class CompanyInfoSection(
    val id: String,
    val title: String,
    val iconResId: DrawableResource
)