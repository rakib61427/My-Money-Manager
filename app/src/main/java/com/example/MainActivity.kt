package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PersonType
import com.example.data.model.TransactionType
import com.example.ui.components.AddEditBudgetDialog
import com.example.ui.components.AddEditPersonDialog
import com.example.ui.components.AddEditTransactionDialog
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.CurrencySelectorDialog
import com.example.ui.components.MakePaymentDialog
import com.example.ui.components.QuickAddBottomSheet
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PeopleScreen
import com.example.ui.screens.PersonDetailScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.Localization
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.AppLockScreen

class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.userSettings.collectAsState()
            val isDarkTheme = when (settings.darkMode) {
                2 -> true
                1 -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                MoneyManagerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MoneyManagerApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val language = settings.language
    val symbol = settings.currencySymbol
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val isAppLocked by viewModel.isAppLocked.collectAsState()
    if (isAppLocked) {
        AppLockScreen(
            language = language,
            onUnlockSuccess = { viewModel.unlockApp() }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.HOME,
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.PEOPLE || currentScreen == Screen.PERSON_DETAIL,
                    onClick = { viewModel.navigateTo(Screen.PEOPLE) },
                    icon = { Icon(Icons.Default.People, contentDescription = "People") },
                    label = { Text(Localization.get("people", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_people")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.HISTORY,
                    onClick = { viewModel.navigateTo(Screen.HISTORY) },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text(Localization.get("history", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_history")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.STATISTICS,
                    onClick = { viewModel.navigateTo(Screen.STATISTICS) },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Statistics") },
                    label = { Text(Localization.get("statistics", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_statistics")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.BUDGET,
                    onClick = { viewModel.navigateTo(Screen.BUDGET) },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Budget") },
                    label = { Text(Localization.get("budget", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_budget")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.SETTINGS,
                    onClick = { viewModel.navigateTo(Screen.SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text(Localization.get("settings", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        },
        floatingActionButton = {
            if (currentScreen != Screen.SETTINGS && currentScreen != Screen.PERSON_DETAIL) {
                FloatingActionButton(
                    onClick = { viewModel.openQuickAddSheet() },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("floating_quick_add_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Quick Add")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(viewModel = viewModel)
                Screen.PEOPLE -> PeopleScreen(viewModel = viewModel)
                Screen.PERSON_DETAIL -> PersonDetailScreen(viewModel = viewModel)
                Screen.HISTORY -> HistoryScreen(viewModel = viewModel)
                Screen.STATISTICS -> StatisticsScreen(viewModel = viewModel)
                Screen.BUDGET -> BudgetScreen(viewModel = viewModel)
                Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    // Quick Add Bottom Sheet
    val showQuickAddSheet by viewModel.showQuickAddSheet.collectAsState()
    if (showQuickAddSheet) {
        QuickAddBottomSheet(
            language = language,
            onDismiss = { viewModel.closeQuickAddSheet() },
            onAddIncome = { viewModel.openAddTransaction(TransactionType.INCOME) },
            onAddExpense = { viewModel.openAddTransaction(TransactionType.EXPENSE) },
            onGiveMoney = {
                viewModel.closeQuickAddSheet()
                viewModel.navigateTo(Screen.PEOPLE)
            },
            onReceiveMoney = {
                viewModel.closeQuickAddSheet()
                viewModel.navigateTo(Screen.PEOPLE)
            },
            onAddPerson = { viewModel.openAddPerson(PersonType.OWE) },
            onAddBudget = { viewModel.openAddBudget() }
        )
    }

    // Add / Edit Transaction Dialog
    val showAddTx by viewModel.showAddTransactionDialog.collectAsState()
    val editingTx by viewModel.editingTransaction.collectAsState()
    val txType by viewModel.transactionDialogType.collectAsState()
    if (showAddTx) {
        AddEditTransactionDialog(
            initialType = txType,
            editingTx = editingTx,
            currencySymbol = symbol,
            language = language,
            onDismiss = { viewModel.closeAddTransactionDialog() },
            onSave = { id, type, amount, category, source, note, datetime ->
                viewModel.saveTransaction(id, type, amount, category, source, note, datetime)
            }
        )
    }

    // Add / Edit Person Dialog
    val showAddPerson by viewModel.showAddPersonDialog.collectAsState()
    val editingPerson by viewModel.editingPerson.collectAsState()
    if (showAddPerson) {
        AddEditPersonDialog(
            editingPerson = editingPerson,
            currencySymbol = symbol,
            language = language,
            onDismiss = { viewModel.closeAddPersonDialog() },
            onSave = { id, name, phone, type, totalAmount, purpose, dueDate, note ->
                viewModel.savePerson(id, name, phone, type, totalAmount, purpose, dueDate, note)
            }
        )
    }

    // Make Payment / Receive Money Dialog
    val showPaymentDialog by viewModel.showPaymentDialog.collectAsState()
    val selectedPersonId by viewModel.selectedPersonId.collectAsState()
    val allPeople by viewModel.allPeople.collectAsState()
    val currentPerson = allPeople.find { it.id == selectedPersonId }
    if (showPaymentDialog && currentPerson != null) {
        MakePaymentDialog(
            person = currentPerson,
            currencySymbol = symbol,
            language = language,
            onDismiss = { viewModel.closePaymentDialog() },
            onSavePayment = { amount, datetime, method, note ->
                viewModel.recordPayment(amount, datetime, method, note)
            }
        )
    }

    // Add / Edit Budget Dialog
    val showAddBudget by viewModel.showAddBudgetDialog.collectAsState()
    val editingBudget by viewModel.editingBudget.collectAsState()
    if (showAddBudget) {
        AddEditBudgetDialog(
            editingBudget = editingBudget,
            currencySymbol = symbol,
            language = language,
            onDismiss = { viewModel.closeAddBudgetDialog() },
            onSave = { id, category, amount ->
                viewModel.saveBudget(id, category, amount)
            }
        )
    }

    // Currency Selector Dialog
    val showCurrencyDialog by viewModel.showCurrencyDialog.collectAsState()
    if (showCurrencyDialog) {
        CurrencySelectorDialog(
            currentCode = settings.currencyCode,
            language = language,
            onDismiss = { viewModel.closeCurrencyDialog() },
            onSelect = { code, sym ->
                viewModel.updateCurrency(code, sym)
            }
        )
    }

    // Confirm Delete Transaction
    val confirmDeleteTx by viewModel.confirmDeleteTx.collectAsState()
    if (confirmDeleteTx != null) {
        ConfirmationDialog(
            title = Localization.get("delete_confirm_title", language),
            message = Localization.get("delete_confirm_msg", language),
            confirmText = Localization.get("delete", language),
            cancelText = Localization.get("cancel", language),
            isDestructive = true,
            onConfirm = { viewModel.confirmDeleteTransaction() },
            onDismiss = { viewModel.cancelDeleteTransaction() }
        )
    }

    // Confirm Delete Person
    val confirmDeletePerson by viewModel.confirmDeletePerson.collectAsState()
    if (confirmDeletePerson != null) {
        ConfirmationDialog(
            title = Localization.get("delete_person", language),
            message = "${confirmDeletePerson?.name} ${Localization.get("delete_confirm_msg", language)}",
            confirmText = Localization.get("delete", language),
            cancelText = Localization.get("cancel", language),
            isDestructive = true,
            onConfirm = { viewModel.confirmDeletePerson() },
            onDismiss = { viewModel.cancelDeletePerson() }
        )
    }

    // Confirm Delete Payment
    val confirmDeletePayment by viewModel.confirmDeletePayment.collectAsState()
    if (confirmDeletePayment != null) {
        ConfirmationDialog(
            title = "Delete Payment Record?",
            message = "This will deduct this payment from the person's paid total and remove its transaction.",
            confirmText = Localization.get("delete", language),
            cancelText = Localization.get("cancel", language),
            isDestructive = true,
            onConfirm = { viewModel.confirmDeletePayment() },
            onDismiss = { viewModel.cancelDeletePayment() }
        )
    }

    // Confirm Delete Budget
    val confirmDeleteBudget by viewModel.confirmDeleteBudget.collectAsState()
    if (confirmDeleteBudget != null) {
        ConfirmationDialog(
            title = "Delete Budget?",
            message = "Are you sure you want to delete this category budget?",
            confirmText = Localization.get("delete", language),
            cancelText = Localization.get("cancel", language),
            isDestructive = true,
            onConfirm = { viewModel.confirmDeleteBudget() },
            onDismiss = { viewModel.cancelDeleteBudget() }
        )
    }

    // Confirm Restore
    val showRestoreConfirm by viewModel.showRestoreConfirm.collectAsState()
    if (showRestoreConfirm) {
        ConfirmationDialog(
            title = Localization.get("restore_confirm_title", language),
            message = Localization.get("restore_confirm_msg", language),
            confirmText = Localization.get("restore", language),
            cancelText = Localization.get("cancel", language),
            isDestructive = true,
            onConfirm = { viewModel.confirmRestore() },
            onDismiss = { viewModel.cancelRestore() }
        )
    }

    // Confirm Reset Data
    val showResetConfirm by viewModel.showResetConfirm.collectAsState()
    if (showResetConfirm) {
        ConfirmationDialog(
            title = Localization.get("reset_data", language),
            message = Localization.get("reset_confirm", language),
            confirmText = Localization.get("confirm", language),
            cancelText = Localization.get("cancel", language),
            isDestructive = true,
            onConfirm = { viewModel.confirmReset() },
            onDismiss = { viewModel.cancelReset() }
        )
    }
}
