package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

enum class TransactionType {
    INCOME,   // وارد
    EXPENSE,  // صادر (خامات)
    LABOR     // أجرة
}

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientId")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    val type: TransactionType,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val description: String
)

// A class to represent the client with their calculated balance
data class ClientWithBalance(
    val id: Int,
    val name: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val totalLabor: Double,
    val balance: Double // (Expense + Labor) - Income
)

@Dao
interface HesabatyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client): Long

    @Update
    suspend fun updateClient(client: Client)

    @Delete
    suspend fun deleteClient(client: Client)

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>

    @Query("SELECT * FROM clients WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun searchClients(searchQuery: String): Flow<List<Client>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    fun getClientById(id: Int): Flow<Client?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE clientId = :clientId ORDER BY date DESC")
    fun getTransactionsForClient(clientId: Int): Flow<List<Transaction>>

    @Query("""
        SELECT 
            c.id, 
            c.name, 
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0.0) as totalIncome,
            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0.0) as totalExpense,
            COALESCE(SUM(CASE WHEN t.type = 'LABOR' THEN t.amount ELSE 0 END), 0.0) as totalLabor,
            (COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0.0) + 
             COALESCE(SUM(CASE WHEN t.type = 'LABOR' THEN t.amount ELSE 0 END), 0.0) - 
             COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0.0)) as balance
        FROM clients c
        LEFT JOIN transactions t ON c.id = t.clientId
        WHERE c.name LIKE '%' || :searchQuery || '%'
        GROUP BY c.id, c.name
        ORDER BY c.name ASC
    """)
    fun getClientsWithBalance(searchQuery: String): Flow<List<ClientWithBalance>>
    
    // Get aggregated sums for a client
    @Query("""
        SELECT 
            SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as totalIncome,
            SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as totalExpense,
            SUM(CASE WHEN type = 'LABOR' THEN amount ELSE 0 END) as totalLabor
        FROM transactions WHERE clientId = :clientId
    """)
    fun getClientAggregates(clientId: Int): Flow<ClientAggregates?>
}

data class ClientAggregates(
    val totalIncome: Double?,
    val totalExpense: Double?,
    val totalLabor: Double?
)

@Database(entities = [Client::class, Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hesabatyDao(): HesabatyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hesabaty_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
