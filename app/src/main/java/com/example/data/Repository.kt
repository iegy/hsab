package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class Repository(private val dao: HesabatyDao) {
    
    fun getClientsWithBalances(searchQuery: String = ""): Flow<List<ClientWithBalance>> {
        return dao.getClientsWithBalance(searchQuery)
    }

    fun getClientById(id: Int) = dao.getClientById(id)
    fun getTransactionsForClient(clientId: Int) = dao.getTransactionsForClient(clientId)
    fun getClientAggregates(clientId: Int) = dao.getClientAggregates(clientId)

    suspend fun insertClient(client: Client) = dao.insertClient(client)
    suspend fun updateClient(client: Client) = dao.updateClient(client)
    suspend fun deleteClient(client: Client) = dao.deleteClient(client)

    suspend fun insertTransaction(transaction: Transaction) = dao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = dao.deleteTransaction(transaction)
}
