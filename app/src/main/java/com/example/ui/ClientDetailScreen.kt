package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.HesabatyViewModel
import com.example.data.Transaction
import com.example.data.TransactionType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    clientId: Int,
    viewModel: HesabatyViewModel,
    onNavigateBack: () -> Unit
) {
    val client by viewModel.getClient(clientId).collectAsStateWithLifecycle(initialValue = null)
    val transactions by viewModel.getTransactions(clientId).collectAsStateWithLifecycle(initialValue = emptyList())
    val aggregates by viewModel.getAggregates(clientId).collectAsStateWithLifecycle(initialValue = null)

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(client?.name ?: "تفاصيل الحساب", fontWeight = FontWeight.Bold, color = Blue700, fontSize = 20.sp)
                        Text("ميزانية العميل", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Blue600)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Blue600,
                contentColor = Color.White,
                shape = RoundedCornerShape(100)
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة عملية")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Divider(color = Blue50)
                    Spacer(modifier = Modifier.height(16.dp))

                    aggregates?.let { agg ->
                        val income = agg.totalIncome ?: 0.0
                        val expense = agg.totalExpense ?: 0.0
                        val labor = agg.totalLabor ?: 0.0
                        val balance = (expense + labor) - income

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Blue600),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("الوارد (واصلني)", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                    Column {
                                        Text(income.formatCurrency(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Card(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Blue100),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("الصادر (خامات)", color = Slate500, fontSize = 12.sp)
                                    Column {
                                        Text(expense.formatCurrency(), color = Red500, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("أجرة: ${labor.formatCurrency()}", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Blue50),
                            border = BorderStroke(1.dp, Blue100),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(100))
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Blue600)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("الرصيد النهائي", color = Slate500, fontSize = 10.sp)
                                        
                                        val balanceText = if (balance > 0) "+${balance.formatCurrency()}"
                                        else if (balance < 0) (-balance).formatCurrency()
                                        else "0"
                                        val balanceColor = if (balance > 0) Red500 else if (balance < 0) Green600 else Slate800
                                        Text(balanceText, color = balanceColor, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                                val statusText = if (balance > 0) "عليه" else if (balance < 0) "له" else "خالص"
                                Box(modifier = Modifier.background(Blue600, RoundedCornerShape(100)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                                    Text(statusText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("كشف الحساب", color = Slate700, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { type, amount, desc ->
                viewModel.addTransaction(
                    clientId = clientId,
                    type = type,
                    amount = amount,
                    description = desc,
                    date = System.currentTimeMillis()
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val (color, typeText) = when (transaction.type) {
        TransactionType.INCOME -> Green600 to "وارد"
        TransactionType.EXPENSE -> Red500 to "صادر خامات"
        TransactionType.LABOR -> Blue500 to "أجرة"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(typeText.first().toString(), color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.description.ifEmpty { typeText }, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(transaction.date.formatDate(), style = MaterialTheme.typography.bodySmall, color = Slate400, fontSize = 10.sp)
            }
            Text(
                text = (if(transaction.type == TransactionType.INCOME) "+" else "-") + transaction.amount.formatCurrency(),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (TransactionType, Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.INCOME) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة عملية", color = Slate900, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TransactionType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = {
                                Text(
                                    when(type) {
                                        TransactionType.INCOME -> "وارد"
                                        TransactionType.EXPENSE -> "صادر"
                                        TransactionType.LABOR -> "أجرة"
                                    }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Blue50,
                                selectedLabelColor = Blue700
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("المبلغ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue600,
                        unfocusedBorderColor = Slate100
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("البيان / التفاصيل") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue600,
                        unfocusedBorderColor = Slate100
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirm(selectedType, amt, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue600)
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Slate500)
            }
        },
        containerColor = Color.White
    )
}
