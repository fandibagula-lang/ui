package org.babetech.borastock.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.babetech.borastock.data.models.*
import org.babetech.borastock.data.repository.StockRepository

class StockViewModel(
    private val repository: StockRepository
) : ViewModel() {

    // États observables
    val stockItems = repository.stockItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stockEntries = repository.stockEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stockExits = repository.stockExits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val suppliers = repository.suppliers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // États de l'interface utilisateur
    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    // États de recherche et filtrage
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Tous")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _sortBy = MutableStateFlow("Nom")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    // Statistiques calculées
    val stockStatistics = stockItems.map { items ->
        repository.getStockStatistics()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val entryStatistics = stockEntries.map { entries ->
        repository.getEntryStatistics()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val exitStatistics = stockExits.map { exits ->
        repository.getExitStatistics()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Données filtrées
    val filteredStockItems = combine(
        stockItems,
        searchQuery,
        selectedFilter,
        sortBy
    ) { items, query, filter, sort ->
        filterAndSortStockItems(items, query, filter, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredStockEntries = combine(
        stockEntries,
        searchQuery,
        selectedFilter,
        sortBy
    ) { entries, query, filter, sort ->
        filterAndSortStockEntries(entries, query, filter, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredStockExits = combine(
        stockExits,
        searchQuery,
        selectedFilter,
        sortBy
    ) { exits, query, filter, sort ->
        filterAndSortStockExits(exits, query, filter, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // ===== ACTIONS =====

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun updateSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setError(error: String?) {
        _uiState.value = _uiState.value.copy(error = error)
    }

    // ===== STOCK ITEMS =====
    fun addStockItem(item: StockItem) {
        viewModelScope.launch {
            try {
                setLoading(true)
                repository.addStockItem(item)
            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    fun updateStockItem(item: StockItem) {
        viewModelScope.launch {
            try {
                setLoading(true)
                repository.updateStockItem(item)
            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    fun deleteStockItem(itemId: String) {
        viewModelScope.launch {
            try {
                setLoading(true)
                repository.deleteStockItem(itemId)
            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    // ===== STOCK ENTRIES =====
    fun addStockEntry(entry: StockEntry) {
        viewModelScope.launch {
            try {
                setLoading(true)
                repository.addStockEntry(entry)
            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    fun updateStockEntry(entry: StockEntry) {
        viewModelScope.launch {
            try {
                setLoading(true)
                repository.updateStockEntry(entry)
            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    // ===== STOCK EXITS =====
    fun addStockExit(exit: StockExit) {
        viewModelScope.launch {
            try {
                setLoading(true)
                repository.addStockExit(exit)
            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    fun updateStockExit(exit: StockExit) {
        viewModelScope.launch {
            try {
                setLoading(true)
                repository.updateStockExit(exit)
            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    // ===== FILTERING AND SORTING =====
    private fun filterAndSortStockItems(
        items: List<StockItem>,
        query: String,
        filter: String,
        sort: String
    ): List<StockItem> {
        return items.filter { item ->
            val matchesSearch = item.name.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true) ||
                    item.supplier.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Tous" -> true
                "En stock" -> item.status == StockStatus.IN_STOCK
                "Stock faible" -> item.status == StockStatus.LOW_STOCK
                "Rupture" -> item.status == StockStatus.OUT_OF_STOCK
                "Surstock" -> item.status == StockStatus.OVERSTOCKED
                else -> true
            }
            matchesSearch && matchesFilter
        }.let { filteredItems ->
            when (sort) {
                "Nom" -> filteredItems.sortedBy { it.name }
                "Stock" -> filteredItems.sortedBy { it.currentStock }
                "Prix" -> filteredItems.sortedBy { it.price }
                "Statut" -> filteredItems.sortedBy { it.status.label }
                else -> filteredItems
            }
        }
    }

    private fun filterAndSortStockEntries(
        entries: List<StockEntry>,
        query: String,
        filter: String,
        sort: String
    ): List<StockEntry> {
        return entries.filter { entry ->
            val matchesSearch = entry.productName.contains(query, ignoreCase = true) ||
                    entry.category.contains(query, ignoreCase = true) ||
                    entry.supplier.contains(query, ignoreCase = true) ||
                    entry.batchNumber?.contains(query, ignoreCase = true) == true

            val matchesFilter = when (filter) {
                "Toutes" -> true
                "En attente" -> entry.status == EntryStatus.PENDING
                "Validées" -> entry.status == EntryStatus.VALIDATED
                "Reçues" -> entry.status == EntryStatus.RECEIVED
                "Annulées" -> entry.status == EntryStatus.CANCELLED
                else -> true
            }
            matchesSearch && matchesFilter
        }.let { filteredEntries ->
            when (sort) {
                "Date" -> filteredEntries.sortedByDescending { it.entryDate }
                "Produit" -> filteredEntries.sortedBy { it.productName }
                "Quantité" -> filteredEntries.sortedByDescending { it.quantity }
                "Valeur" -> filteredEntries.sortedByDescending { it.totalValue }
                "Statut" -> filteredEntries.sortedBy { it.status.label }
                else -> filteredEntries
            }
        }
    }

    private fun filterAndSortStockExits(
        exits: List<StockExit>,
        query: String,
        filter: String,
        sort: String
    ): List<StockExit> {
        return exits.filter { exit ->
            val matchesSearch = exit.productName.contains(query, ignoreCase = true) ||
                    exit.category.contains(query, ignoreCase = true) ||
                    exit.customer.contains(query, ignoreCase = true) ||
                    exit.orderNumber?.contains(query, ignoreCase = true) == true

            val matchesFilter = when (filter) {
                "Toutes" -> true
                "En préparation" -> exit.status == ExitStatus.PENDING
                "Préparées" -> exit.status == ExitStatus.PREPARED
                "Expédiées" -> exit.status == ExitStatus.SHIPPED
                "Livrées" -> exit.status == ExitStatus.DELIVERED
                "Annulées" -> exit.status == ExitStatus.CANCELLED
                else -> true
            }
            matchesSearch && matchesFilter
        }.let { filteredExits ->
            when (sort) {
                "Date" -> filteredExits.sortedByDescending { it.exitDate }
                "Produit" -> filteredExits.sortedBy { it.productName }
                "Client" -> filteredExits.sortedBy { it.customer }
                "Quantité" -> filteredExits.sortedByDescending { it.quantity }
                "Valeur" -> filteredExits.sortedByDescending { it.totalValue }
                "Statut" -> filteredExits.sortedBy { it.status.label }
                "Urgence" -> filteredExits.sortedByDescending { it.urgency.ordinal }
                else -> filteredExits
            }
        }
    }
}

data class StockUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedStockItem: StockItem? = null,
    val selectedStockEntry: StockEntry? = null,
    val selectedStockExit: StockExit? = null
)