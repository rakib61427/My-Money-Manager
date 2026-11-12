package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionType
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.WarningOrange
import com.example.ui.viewmodel.MainViewModel
import com.example.util.Localization

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val filteredTx by viewModel.filteredTransactions.collectAsState()
    val activeTypeFilter by viewModel.historyTypeFilter.collectAsState()
    val activeDateFilter by viewModel.historyDateFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val language = settings.language
    val symbol = settings.currencySymbol

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = Localization.get("history", language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text(Localization.get("search_hint", language), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Type Filter Chips: All | Income | Expense | Given | Received
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeTypeFilter == "ALL",
                onClick = { viewModel.setHistoryTypeFilter("ALL") },
                label = { Text(Localization.get("all", language)) }
            )
            FilterChip(
                selected = activeTypeFilter == TransactionType.INCOME,
                onClick = { viewModel.setHistoryTypeFilter(TransactionType.INCOME) },
                label = { Text(Localization.get("income", language)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                    selectedLabelColor = IncomeGreen
                )
            )
            FilterChip(
                selected = activeTypeFilter == TransactionType.EXPENSE,
                onClick = { viewModel.setHistoryTypeFilter(TransactionType.EXPENSE) },
                label = { Text(Localization.get("expenses", language)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                    selectedLabelColor = ExpenseRed
                )
            )
            FilterChip(
                selected = activeTypeFilter == TransactionType.GIVEN,
                onClick = { viewModel.setHistoryTypeFilter(TransactionType.GIVEN) },
                label = { Text(Localization.get("given", language)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WarningOrange.copy(alpha = 0.2f),
                    selectedLabelColor = WarningOrange
                )
            )
            FilterChip(
                selected = activeTypeFilter == TransactionType.RECEIVED,
                onClick = { viewModel.setHistoryTypeFilter(TransactionType.RECEIVED) },
                label = { Text(Localization.get("received", language)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = InfoBlue.copy(alpha = 0.2f),
                    selectedLabelColor = InfoBlue
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Date Filter Chips: All | Today | This Week | This Month
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeDateFilter == "ALL",
                onClick = { viewModel.setHistoryDateFilter("ALL") },
                label = { Text(Localization.get("all", language)) }
            )
            FilterChip(
                selected = activeDateFilter == "TODAY",
                onClick = { viewModel.setHistoryDateFilter("TODAY") },
                label = { Text(Localization.get("today", language)) }
            )
            FilterChip(
                selected = activeDateFilter == "WEEK",
                onClick = { viewModel.setHistoryDateFilter("WEEK") },
                label = { Text(Localization.get("this_week", language)) }
            )
            FilterChip(
                selected = activeDateFilter == "MONTH",
                onClick = { viewModel.setHistoryDateFilter("MONTH") },
                label = { Text(Localization.get("this_month", language)) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Transaction list
        if (filteredTx.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Localization.get("no_transactions", language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTx, key = { it.id }) { tx ->
                    TransactionItemCard(
                        transaction = tx,
                        currencySymbol = symbol,
                        language = language,
                        onEdit = { viewModel.openEditTransaction(tx) },
                        onDelete = { viewModel.requestDeleteTransaction(tx) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
