package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HesabatyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).hesabatyDao()
        repository = Repository(dao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val clients: StateFlow<List<ClientWithBalance>> = _searchQuery
        .flatMapLatest { query ->
            repository.getClientsWithBalances(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addClient(name: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                repository.insertClient(Client(name = name))
            }
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }

    // Details functions
    fun getClient(id: Int): Flow<Client?> = repository.getClientById(id)
    fun getTransactions(clientId: Int): Flow<List<Transaction>> = repository.getTransactionsForClient(clientId)
    fun getAggregates(clientId: Int): Flow<ClientAggregates?> = repository.getClientAggregates(clientId)

    fun addTransaction(clientId: Int, type: TransactionType, amount: Double, description: String, date: Long) {
        if (amount > 0) {
            viewModelScope.launch {
                repository.insertTransaction(
                    Transaction(
                        clientId = clientId,
                        type = type,
                        amount = amount,
                        description = description,
                        date = date
                    )
                )
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}
