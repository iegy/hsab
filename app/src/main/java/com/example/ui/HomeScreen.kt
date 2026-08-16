package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.HesabatyViewModel
import com.example.data.ClientWithBalance
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HesabatyViewModel,
    onNavigateToClient: (Int) -> Unit
) {
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Slate50,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("حساباتي", fontWeight = FontWeight.Bold, color = Blue700, fontSize = 24.sp)
                        Text("إدارة ميزانية النجارة", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Blue700
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(44.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Blue50)
                            .clickable { /* Toggle Search, optional */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Blue600)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Blue600,
                contentColor = Color.White,
                shape = RoundedCornerShape(100)
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة عميل")
            }
        },
        bottomBar = {
            Text(
                text = "برمجة وتصميم محمد حسين iegy.net",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = Slate400
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val totalIncome = clients.sumOf { it.totalIncome }
            val totalExpenseAndLabor = clients.sumOf { it.totalExpense + it.totalLabor }
            val totalBalance = clients.sumOf { it.balance }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Divider(color = Blue50)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Bento Grid Dashboard - Row 1
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
                                Text("إجمالي الوارد", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                Column {
                                    Text(totalIncome.formatCurrency(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                                Text("إجمالي المنصرف", color = Slate500, fontSize = 12.sp)
                                Column {
                                    Text(totalExpenseAndLabor.formatCurrency(), color = Red500, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bento Grid Dashboard - Row 2
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
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Blue600)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("صافي السوق (رصيد لك)", color = Slate500, fontSize = 10.sp)
                                    Text(totalBalance.formatCurrency(), color = Slate800, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Box(modifier = Modifier.background(Blue600, RoundedCornerShape(100)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                                Text("ممتاز", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("العملاء", color = Slate700, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("ابحث عن عميل...", color = Slate400) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Blue600,
                            unfocusedBorderColor = Slate100
                        )
                    )
                }

                items(clients) { client ->
                    ClientCard(client = client, onClick = { onNavigateToClient(client.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة عميل جديد", color = Slate900, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم العميل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addClient(name)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600)
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء", color = Slate500)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun ClientCard(client: ClientWithBalance, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Slate500)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "رصيد العميل",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            val balanceColor = if (client.balance > 0) Red500 else if (client.balance < 0) Green600 else Slate400
            val balanceText = if (client.balance > 0) "+${client.balance.formatCurrency()}"
            else if (client.balance < 0) (-client.balance).formatCurrency()
            else "0"
            
            Text(
                text = balanceText,
                style = MaterialTheme.typography.bodyMedium,
                color = balanceColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
