package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionType
import com.example.ui.components.CurrentBalanceCard
import com.example.ui.components.QuickActionButton
import com.example.ui.components.SummaryCard
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedBg
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenBg
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.InfoBlueBg
import com.example.ui.theme.WarningOrange
import com.example.ui.theme.WarningOrangeBg
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.Localization

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsState()
    val recentTx by viewModel.recentTransactions.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val language = settings.language
    val symbol = settings.currencySymbol

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Localization.get("app_name", language),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = Localization.get("app_subtitle", language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Currency chip button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.openCurrencyDialog() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${settings.currencyCode} ($symbol)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Language toggle button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                val nextLang = if (language == Localization.LANG_BN) Localization.LANG_EN else Localization.LANG_BN
                                viewModel.updateLanguage(nextLang)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (language == Localization.LANG_BN) "বাংলা" else "EN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }

        // Current Balance Card
        item {
            CurrentBalanceCard(
                balance = summary.currentBalance,
                currencySymbol = symbol,
                title = Localization.get("current_balance", language)
            )
        }

        // 4 Summary Cards Grid (Total Income, Total Expense, To Pay, To Receive)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = Localization.get("total_income", language),
                        amount = summary.totalIncome,
                        currencySymbol = symbol,
                        icon = Icons.Default.TrendingUp,
                        iconColor = IncomeGreen,
                        bgColor = IncomeGreenBg,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openAddTransaction(TransactionType.INCOME) }
                    )

                    SummaryCard(
                        title = Localization.get("total_expense", language),
                        amount = summary.totalExpense,
                        currencySymbol = symbol,
                        icon = Icons.Default.TrendingDown,
                        iconColor = ExpenseRed,
                        bgColor = ExpenseRedBg,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openAddTransaction(TransactionType.EXPENSE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = Localization.get("to_pay", language),
                        amount = summary.iHaveToPay,
                        currencySymbol = symbol,
                        icon = Icons.Default.CallMade,
                        iconColor = WarningOrange,
                        bgColor = WarningOrangeBg,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.PEOPLE) }
                    )

                    SummaryCard(
                        title = Localization.get("to_receive", language),
                        amount = summary.iWillReceive,
                        currencySymbol = symbol,
                        icon = Icons.Default.CallReceived,
                        iconColor = InfoBlue,
                        bgColor = InfoBlueBg,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.PEOPLE) }
                    )
                }
            }
        }

        // Quick Actions Section
        item {
            Text(
                text = Localization.get("quick_actions", language),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        label = Localization.get("add_income", language),
                        icon = Icons.Default.Add,
                        iconColor = IncomeGreen,
                        containerColor = IncomeGreenBg,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openAddTransaction(TransactionType.INCOME) }
                    )

                    QuickActionButton(
                        label = Localization.get("add_expense", language),
                        icon = Icons.Default.Remove,
                        iconColor = ExpenseRed,
                        containerColor = ExpenseRedBg,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openAddTransaction(TransactionType.EXPENSE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        label = Localization.get("give_money", language),
                        icon = Icons.Default.CallMade,
                        iconColor = WarningOrange,
                        containerColor = WarningOrangeBg,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.PEOPLE) }
                    )

                    QuickActionButton(
                        label = Localization.get("receive_money", language),
                        icon = Icons.Default.CallReceived,
                        iconColor = InfoBlue,
                        containerColor = InfoBlueBg,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.PEOPLE) }
                    )
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Localization.get("recent_transactions", language),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                TextButton(
                    onClick = { viewModel.navigateTo(Screen.HISTORY) },
                    modifier = Modifier.testTag("view_all_transactions_button")
                ) {
                    Text(
                        text = Localization.get("view_all", language),
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Recent Transactions List
        if (recentTx.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Localization.get("no_transactions", language),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(recentTx, key = { it.id }) { tx ->
                TransactionItemCard(
                    transaction = tx,
                    currencySymbol = symbol,
                    language = language,
                    onEdit = { viewModel.openEditTransaction(tx) },
                    onDelete = { viewModel.requestDeleteTransaction(tx) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB & Bottom Navigation
        }
    }
}
