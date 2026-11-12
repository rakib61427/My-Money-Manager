package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetEntity
import com.example.data.model.PersonEntity
import com.example.data.model.PersonType
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.WarningOrange
import com.example.util.CategoryUtils
import com.example.util.CurrencyHelper
import com.example.util.CurrencyItem
import com.example.util.DateUtils
import com.example.util.Localization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddBottomSheet(
    language: String,
    onDismiss: () -> Unit,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onGiveMoney: () -> Unit,
    onReceiveMoney: () -> Unit,
    onAddPerson: () -> Unit,
    onAddBudget: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = Localization.get("quick_actions", language),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Action Items
            QuickAddActionRow(
                icon = Icons.Default.Add,
                iconColor = IncomeGreen,
                title = Localization.get("add_income", language),
                subtitle = "Salary, Business, Freelance, Gift",
                onClick = onAddIncome
            )

            QuickAddActionRow(
                icon = Icons.Default.Remove,
                iconColor = ExpenseRed,
                title = Localization.get("add_expense", language),
                subtitle = "Food, Grocery, Shopping, Bills",
                onClick = onAddExpense
            )

            QuickAddActionRow(
                icon = Icons.Default.CallMade,
                iconColor = WarningOrange,
                title = Localization.get("give_money", language),
                subtitle = "Pay debts or give money to someone",
                onClick = onGiveMoney
            )

            QuickAddActionRow(
                icon = Icons.Default.CallReceived,
                iconColor = InfoBlue,
                title = Localization.get("receive_money", language),
                subtitle = "Collect money from someone who owes you",
                onClick = onReceiveMoney
            )

            QuickAddActionRow(
                icon = Icons.Default.PersonAdd,
                iconColor = Color(0xFF8B5CF6),
                title = Localization.get("add_person", language),
                subtitle = "Track debts and receivables per contact",
                onClick = onAddPerson
            )

            QuickAddActionRow(
                icon = Icons.Default.PieChart,
                iconColor = Color(0xFF0D9488),
                title = Localization.get("add_budget", language),
                subtitle = "Set monthly spending limit per category",
                onClick = onAddBudget
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun QuickAddActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionDialog(
    initialType: String,
    editingTx: TransactionEntity?,
    currencySymbol: String,
    language: String,
    onDismiss: () -> Unit,
    onSave: (id: Long, type: String, amount: Double, category: String, source: String, note: String, datetime: Long) -> Unit
) {
    var type by remember { mutableStateOf(editingTx?.type ?: initialType) }
    var amountText by remember { mutableStateOf(editingTx?.amount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "") }
    var selectedCategory by remember { mutableStateOf(editingTx?.category ?: if (type == TransactionType.INCOME) "Salary" else "Food") }
    var customCategoryText by remember { mutableStateOf("") }
    var showCustomCategory by remember { mutableStateOf(false) }
    var selectedSource by remember { mutableStateOf(editingTx?.source ?: "Cash") }
    var noteText by remember { mutableStateOf(editingTx?.note ?: "") }
    var datetime by remember { mutableStateOf(editingTx?.datetime ?: System.currentTimeMillis()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val categories = if (type == TransactionType.INCOME) CategoryUtils.incomeCategories else CategoryUtils.expenseCategories

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (editingTx != null) Localization.get("edit", language) else {
                    if (type == TransactionType.INCOME) Localization.get("add_income", language) else Localization.get("add_expense", language)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Type Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            type = TransactionType.INCOME
                            selectedCategory = "Salary"
                            showCustomCategory = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == TransactionType.INCOME) IncomeGreen else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (type == TransactionType.INCOME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(Localization.get("income", language))
                    }

                    Button(
                        onClick = {
                            type = TransactionType.EXPENSE
                            selectedCategory = "Food"
                            showCustomCategory = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == TransactionType.EXPENSE) ExpenseRed else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (type == TransactionType.EXPENSE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(Localization.get("expenses", language))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorText = null
                    },
                    label = { Text("${Localization.get("amount", language)} ($currencySymbol)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    isError = errorText != null
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = ExpenseRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Categories Flow
                Text(
                    text = Localization.get("category", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = !showCustomCategory && selectedCategory.equals(cat.id, ignoreCase = true),
                            onClick = {
                                selectedCategory = cat.id
                                showCustomCategory = false
                            },
                            label = { Text(if (language == Localization.LANG_BN) cat.nameBn else cat.nameEn) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (type == TransactionType.INCOME) IncomeGreen.copy(alpha = 0.2f) else ExpenseRed.copy(alpha = 0.2f),
                                selectedLabelColor = if (type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                            )
                        )
                    }

                    FilterChip(
                        selected = showCustomCategory,
                        onClick = { showCustomCategory = true },
                        label = { Text("+ ${Localization.get("custom_category", language)}") }
                    )
                }

                if (showCustomCategory) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customCategoryText,
                        onValueChange = { customCategoryText = it },
                        label = { Text(Localization.get("custom_category", language)) },
                        placeholder = { Text("e.g. Gym, Pet, Gadgets") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Method / Source
                Text(
                    text = Localization.get("payment_method", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryUtils.paymentMethods.forEach { method ->
                        FilterChip(
                            selected = selectedSource.equals(method, ignoreCase = true),
                            onClick = { selectedSource = method },
                            label = { Text(method) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Note
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(Localization.get("note", language)) },
                    placeholder = { Text("Optional note or description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${Localization.get("date_time", language)}: ${DateUtils.formatDateTime(datetime)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorText = "Please enter a valid amount"
                        return@Button
                    }
                    val finalCategory = if (showCustomCategory && customCategoryText.isNotBlank()) {
                        customCategoryText.trim()
                    } else {
                        selectedCategory
                    }
                    onSave(
                        editingTx?.id ?: 0L,
                        type,
                        amount,
                        finalCategory,
                        selectedSource,
                        noteText.trim(),
                        datetime
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_transaction_button")
            ) {
                Text(Localization.get("save", language))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Localization.get("cancel", language))
            }
        }
    )
}

@Composable
fun AddEditPersonDialog(
    editingPerson: PersonEntity?,
    currencySymbol: String,
    language: String,
    onDismiss: () -> Unit,
    onSave: (id: Long, name: String, phone: String, type: String, totalAmount: Double, purpose: String, dueDate: Long?, note: String) -> Unit
) {
    var nameText by remember { mutableStateOf(editingPerson?.name ?: "") }
    var phoneText by remember { mutableStateOf(editingPerson?.phone ?: "") }
    var type by remember { mutableStateOf(editingPerson?.type ?: PersonType.OWE) }
    var amountText by remember { mutableStateOf(editingPerson?.totalAmount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "") }
    var purposeText by remember { mutableStateOf(editingPerson?.purpose ?: "") }
    var noteText by remember { mutableStateOf(editingPerson?.note ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (editingPerson != null) Localization.get("edit_person", language) else Localization.get("add_person", language),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Type selector: I Owe vs They Owe
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { type = PersonType.OWE },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == PersonType.OWE) WarningOrange else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (type == PersonType.OWE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(Localization.get("i_owe", language))
                    }

                    Button(
                        onClick = { type = PersonType.RECEIVABLE },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == PersonType.RECEIVABLE) InfoBlue else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (type == PersonType.RECEIVABLE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(Localization.get("they_owe", language))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = nameText,
                    onValueChange = {
                        nameText = it
                        errorText = null
                    },
                    label = { Text(Localization.get("name", language)) },
                    placeholder = { Text("Rahim / Hasan / Karim") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("person_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phoneText,
                    onValueChange = { phoneText = it },
                    label = { Text(Localization.get("phone", language)) },
                    placeholder = { Text("017XXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorText = null
                    },
                    label = { Text("${Localization.get("total_amount", language)} ($currencySymbol)") },
                    placeholder = { Text("10000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("person_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    isError = errorText != null
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = ExpenseRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = purposeText,
                    onValueChange = { purposeText = it },
                    label = { Text(Localization.get("purpose", language)) },
                    placeholder = { Text("e.g. Loan, Grocery, Freelance") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(Localization.get("note", language)) },
                    placeholder = { Text("Additional notes...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameText.isBlank()) {
                        errorText = "Please enter person's name"
                        return@Button
                    }
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorText = "Please enter valid amount"
                        return@Button
                    }
                    onSave(
                        editingPerson?.id ?: 0L,
                        nameText.trim(),
                        phoneText.trim(),
                        type,
                        amount,
                        purposeText.trim(),
                        null,
                        noteText.trim()
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == PersonType.OWE) WarningOrange else InfoBlue
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_person_button")
            ) {
                Text(Localization.get("save", language))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Localization.get("cancel", language))
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MakePaymentDialog(
    person: PersonEntity,
    currencySymbol: String,
    language: String,
    onDismiss: () -> Unit,
    onSavePayment: (amount: Double, datetime: Long, method: String, note: String) -> Unit
) {
    val isOwe = person.type == PersonType.OWE
    var amountText by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("Cash") }
    var noteText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isOwe) Localization.get("make_payment", language) else Localization.get("receive_payment", language),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "${person.name} • ${Localization.get("remaining", language)}: ${CurrencyHelper.format(person.remainingAmount, currencySymbol)}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorText = null
                    },
                    label = { Text("${Localization.get("amount", language)} ($currencySymbol)") },
                    placeholder = { Text("e.g. 2000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    isError = errorText != null
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = ExpenseRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = Localization.get("payment_method", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryUtils.paymentMethods.forEach { method ->
                        FilterChip(
                            selected = selectedMethod.equals(method, ignoreCase = true),
                            onClick = { selectedMethod = method },
                            label = { Text(method) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(Localization.get("note", language)) },
                    placeholder = { Text("e.g. Installment, Grocery payment") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorText = "Please enter valid payment amount"
                        return@Button
                    }
                    onSavePayment(
                        amount,
                        System.currentTimeMillis(),
                        selectedMethod,
                        noteText.trim()
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOwe) WarningOrange else InfoBlue
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_payment_button")
            ) {
                Text(Localization.get("save", language))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Localization.get("cancel", language))
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditBudgetDialog(
    editingBudget: BudgetEntity?,
    currencySymbol: String,
    language: String,
    onDismiss: () -> Unit,
    onSave: (id: Long, category: String, amount: Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(editingBudget?.category ?: "Food") }
    var amountText by remember { mutableStateOf(editingBudget?.amount?.let { if (it % 1 == 0.0) it.toLong().toString() else it.toString() } ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (editingBudget != null) Localization.get("edit_budget", language) else Localization.get("add_budget", language),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = Localization.get("category", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryUtils.expenseCategories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory.equals(cat.id, ignoreCase = true),
                            onClick = { selectedCategory = cat.id },
                            label = { Text(if (language == Localization.LANG_BN) cat.nameBn else cat.nameEn) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorText = null
                    },
                    label = { Text("${Localization.get("amount", language)} ($currencySymbol)") },
                    placeholder = { Text("5000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    isError = errorText != null
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = ExpenseRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorText = "Please enter valid budget amount"
                        return@Button
                    }
                    onSave(editingBudget?.id ?: 0L, selectedCategory, amount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_budget_button")
            ) {
                Text(Localization.get("save", language))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Localization.get("cancel", language))
            }
        }
    )
}

@Composable
fun CurrencySelectorDialog(
    currentCode: String,
    language: String,
    onDismiss: () -> Unit,
    onSelect: (code: String, symbol: String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Localization.get("currency", language),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(CurrencyHelper.supportedCurrencies) { item ->
                    val isSelected = item.code.equals(currentCode, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(item.code, item.symbol) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.flag, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${item.code} — ${if (language == Localization.LANG_BN) item.nameBn else item.nameEn}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Symbol: ${item.symbol}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(Localization.get("cancel", language))
            }
        }
    )
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = { Text(text = message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) ExpenseRed else EmeraldPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(cancelText)
            }
        }
    )
}
