package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionType
import com.example.ui.components.BarChartItem
import com.example.ui.components.BarComparisonChart
import com.example.ui.components.CategoryShare
import com.example.ui.components.DonutCategoryChart
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.WarningOrange
import com.example.ui.viewmodel.MainViewModel
import com.example.util.CategoryUtils
import com.example.util.CurrencyHelper
import com.example.util.Localization

@Composable
fun StatisticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val peopleToPay by viewModel.peopleToPay.collectAsState()
    val peopleToReceive by viewModel.peopleToReceive.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val language = settings.language
    val symbol = settings.currencySymbol

    // Compute category expenses
    val expenseTransactions = allTransactions.filter { it.type == TransactionType.EXPENSE }
    val categoryTotals = expenseTransactions.groupBy { it.category }
        .map { (cat, list) ->
            val info = CategoryUtils.getCategoryInfo(cat, isIncome = false)
            CategoryShare(
                category = cat,
                amount = list.sumOf { it.amount },
                color = info.color
            )
        }
        .sortedByDescending { it.amount }

    // Bar chart items: Income, Expense, Given, Received
    val barItems = listOf(
        BarChartItem(Localization.get("income", language), summary.totalIncome, IncomeGreen),
        BarChartItem(Localization.get("expenses", language), summary.totalExpense, ExpenseRed),
        BarChartItem(Localization.get("given", language), summary.totalGiven, WarningOrange),
        BarChartItem(Localization.get("received", language), summary.totalReceived, InfoBlue)
    )

    // People debt & receivable sums
    val totalOwedAmount = peopleToPay.sumOf { it.totalAmount }
    val totalPaidDebt = peopleToPay.sumOf { it.paidAmount }
    val remainingDebt = peopleToPay.sumOf { it.remainingAmount }

    val totalReceivableAmount = peopleToReceive.sumOf { it.totalAmount }
    val totalReceivedAmount = peopleToReceive.sumOf { it.paidAmount }
    val remainingReceivable = peopleToReceive.sumOf { it.remainingAmount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Localization.get("statistics", language),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Section 1: Income vs Expense Comparison Bar Chart
        item {
            Text(
                text = Localization.get("income_vs_expense", language),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            BarComparisonChart(
                items = barItems,
                currencySymbol = symbol
            )
        }

        // Section 2: Expense Categories Donut/Pie Chart
        item {
            Text(
                text = Localization.get("expense_categories", language),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            DonutCategoryChart(
                categories = categoryTotals,
                currencySymbol = symbol,
                language = language
            )
        }

        // Section 3: People Payment Summary
        item {
            Text(
                text = Localization.get("people_summary", language),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Debt section
                    Text(
                        text = "${Localization.get("to_pay", language)} (${peopleToPay.size} people)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningOrange
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Debt: ${CurrencyHelper.format(totalOwedAmount, symbol)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Paid: ${CurrencyHelper.format(totalPaidDebt, symbol)}", fontSize = 12.sp, color = IncomeGreen)
                        Text(text = "Remaining: ${CurrencyHelper.format(remainingDebt, symbol)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarningOrange)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Receivable section
                    Text(
                        text = "${Localization.get("to_receive", language)} (${peopleToReceive.size} people)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = InfoBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Credit: ${CurrencyHelper.format(totalReceivableAmount, symbol)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Received: ${CurrencyHelper.format(totalReceivedAmount, symbol)}", fontSize = 12.sp, color = IncomeGreen)
                        Text(text = "Remaining: ${CurrencyHelper.format(remainingReceivable, symbol)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InfoBlue)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
