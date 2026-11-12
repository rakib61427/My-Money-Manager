package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.PersonEntity
import com.example.data.model.PersonType
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserSettingsEntity
import com.example.data.repository.MoneyManagerRepository
import com.example.util.DateUtils
import com.example.util.Localization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    PEOPLE,
    PERSON_DETAIL,
    HISTORY,
    STATISTICS,
    BUDGET,
    SETTINGS
}

data class DashboardSummary(
    val currentBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val iHaveToPay: Double = 0.0,
    val iWillReceive: Double = 0.0,
    val totalGiven: Double = 0.0,
    val totalReceived: Double = 0.0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = MoneyManagerRepository(db)

    // Current navigation screen & stack
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedPersonId = MutableStateFlow<Long?>(null)
    val selectedPersonId: StateFlow<Long?> = _selectedPersonId.asStateFlow()

    // Data streams
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.recentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPeople: StateFlow<List<PersonEntity>> = repository.allPeople
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val peopleToPay: StateFlow<List<PersonEntity>> = repository.peopleToPay
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val peopleToReceive: StateFlow<List<PersonEntity>> = repository.peopleToReceive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettingsEntity> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettingsEntity())

    // Selected person & their payment history
    private val _selectedPersonPayments = MutableStateFlow<List<PaymentEntity>>(emptyList())
    val selectedPersonPayments: StateFlow<List<PaymentEntity>> = _selectedPersonPayments.asStateFlow()

    // Dashboard calculations
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        allTransactions,
        peopleToPay,
        peopleToReceive,
        userSettings
    ) { txList, toPayList, toReceiveList, settings ->
        var income = 0.0
        var expense = 0.0
        var given = 0.0
        var received = 0.0

        for (tx in txList) {
            when (tx.type) {
                TransactionType.INCOME -> income += tx.amount
                TransactionType.EXPENSE -> expense += tx.amount
                TransactionType.GIVEN -> given += tx.amount
                TransactionType.RECEIVED -> received += tx.amount
            }
        }

        val toPayTotal = toPayList.sumOf { it.remainingAmount }
        val toReceiveTotal = toReceiveList.sumOf { it.remainingAmount }
        val balance = settings.openingBalance + income + received - expense - given

        DashboardSummary(
            currentBalance = balance,
            totalIncome = income,
            totalExpense = expense,
            iHaveToPay = toPayTotal,
            iWillReceive = toReceiveTotal,
            totalGiven = given,
            totalReceived = received
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // History filter states
    private val _historyTypeFilter = MutableStateFlow("ALL") // ALL, INCOME, EXPENSE, GIVEN, RECEIVED
    val historyTypeFilter: StateFlow<String> = _historyTypeFilter.asStateFlow()

    private val _historyDateFilter = MutableStateFlow("ALL") // ALL, TODAY, WEEK, MONTH
    val historyDateFilter: StateFlow<String> = _historyDateFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered transactions for History screen
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _historyTypeFilter,
        _historyDateFilter,
        _searchQuery
    ) { list, typeFilter, dateFilter, query ->
        list.filter { tx ->
            // Type filter
            val matchesType = when (typeFilter) {
                "ALL" -> true
                else -> tx.type.equals(typeFilter, ignoreCase = true)
            }
            // Date filter
            val matchesDate = when (dateFilter) {
                "TODAY" -> DateUtils.isToday(tx.datetime)
                "WEEK" -> DateUtils.isThisWeek(tx.datetime)
                "MONTH" -> DateUtils.isThisMonth(tx.datetime)
                else -> true
            }
            // Search query filter
            val matchesQuery = if (query.isBlank()) true else {
                tx.category.contains(query, ignoreCase = true) ||
                        tx.source.contains(query, ignoreCase = true) ||
                        tx.note.contains(query, ignoreCase = true) ||
                        (tx.personName?.contains(query, ignoreCase = true) ?: false) ||
                        tx.amount.toString().contains(query)
            }
            matchesType && matchesDate && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog & Sheet States
    private val _showQuickAddSheet = MutableStateFlow(false)
    val showQuickAddSheet: StateFlow<Boolean> = _showQuickAddSheet.asStateFlow()

    private val _editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    val editingTransaction: StateFlow<TransactionEntity?> = _editingTransaction.asStateFlow()

    private val _showAddTransactionDialog = MutableStateFlow(false)
    val showAddTransactionDialog: StateFlow<Boolean> = _showAddTransactionDialog.asStateFlow()

    private val _transactionDialogType = MutableStateFlow(TransactionType.EXPENSE)
    val transactionDialogType: StateFlow<String> = _transactionDialogType.asStateFlow()

    private val _showAddPersonDialog = MutableStateFlow(false)
    val showAddPersonDialog: StateFlow<Boolean> = _showAddPersonDialog.asStateFlow()

    private val _editingPerson = MutableStateFlow<PersonEntity?>(null)
    val editingPerson: StateFlow<PersonEntity?> = _editingPerson.asStateFlow()

    private val _showPaymentDialog = MutableStateFlow(false)
    val showPaymentDialog: StateFlow<Boolean> = _showPaymentDialog.asStateFlow()

    private val _showAddBudgetDialog = MutableStateFlow(false)
    val showAddBudgetDialog: StateFlow<Boolean> = _showAddBudgetDialog.asStateFlow()

    private val _editingBudget = MutableStateFlow<BudgetEntity?>(null)
    val editingBudget: StateFlow<BudgetEntity?> = _editingBudget.asStateFlow()

    private val _showCurrencyDialog = MutableStateFlow(false)
    val showCurrencyDialog: StateFlow<Boolean> = _showCurrencyDialog.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Confirmation dialog states
    private val _confirmDeleteTx = MutableStateFlow<TransactionEntity?>(null)
    val confirmDeleteTx: StateFlow<TransactionEntity?> = _confirmDeleteTx.asStateFlow()

    private val _confirmDeletePerson = MutableStateFlow<PersonEntity?>(null)
    val confirmDeletePerson: StateFlow<PersonEntity?> = _confirmDeletePerson.asStateFlow()

    private val _confirmDeletePayment = MutableStateFlow<PaymentEntity?>(null)
    val confirmDeletePayment: StateFlow<PaymentEntity?> = _confirmDeletePayment.asStateFlow()

    private val _confirmDeleteBudget = MutableStateFlow<BudgetEntity?>(null)
    val confirmDeleteBudget: StateFlow<BudgetEntity?> = _confirmDeleteBudget.asStateFlow()

    private val _showBackupConfirm = MutableStateFlow(false)
    val showBackupConfirm: StateFlow<Boolean> = _showBackupConfirm.asStateFlow()

    private val _showRestoreConfirm = MutableStateFlow(false)
    val showRestoreConfirm: StateFlow<Boolean> = _showRestoreConfirm.asStateFlow()

    private val _showResetConfirm = MutableStateFlow(false)
    val showResetConfirm: StateFlow<Boolean> = _showResetConfirm.asStateFlow()

    // Pending restore JSON text
    private var pendingRestoreJson: String? = null

    // App lock state for biometric security
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()
    private var hasCheckedInitialLock = false

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
        viewModelScope.launch {
            repository.userSettings.collect { settings ->
                if (settings.isBiometricEnabled && !hasCheckedInitialLock) {
                    hasCheckedInitialLock = true
                    _isAppLocked.value = true
                }
            }
        }
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val updated = userSettings.value.copy(isBiometricEnabled = enabled)
            repository.updateSettings(updated)
            if (enabled) {
                _isAppLocked.value = false
                showMessage(Localization.get("biometric_enabled_msg", userSettings.value.language))
            } else {
                _isAppLocked.value = false
                showMessage(Localization.get("biometric_disabled_msg", userSettings.value.language))
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun openPersonDetail(personId: Long) {
        _selectedPersonId.value = personId
        _currentScreen.value = Screen.PERSON_DETAIL
        viewModelScope.launch {
            repository.getPaymentsForPerson(personId).collect {
                _selectedPersonPayments.value = it
            }
        }
    }

    fun setHistoryTypeFilter(type: String) {
        _historyTypeFilter.value = type
    }

    fun setHistoryDateFilter(dateFilter: String) {
        _historyDateFilter.value = dateFilter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openQuickAddSheet() {
        _showQuickAddSheet.value = true
    }

    fun closeQuickAddSheet() {
        _showQuickAddSheet.value = false
    }

    fun openAddTransaction(type: String) {
        _transactionDialogType.value = type
        _editingTransaction.value = null
        _showAddTransactionDialog.value = true
        _showQuickAddSheet.value = false
    }

    fun openEditTransaction(tx: TransactionEntity) {
        _transactionDialogType.value = tx.type
        _editingTransaction.value = tx
        _showAddTransactionDialog.value = true
    }

    fun closeAddTransactionDialog() {
        _showAddTransactionDialog.value = false
        _editingTransaction.value = null
    }

    fun openAddPerson(defaultType: String = PersonType.OWE) {
        _editingPerson.value = null
        _showAddPersonDialog.value = true
        _showQuickAddSheet.value = false
    }

    fun openEditPerson(person: PersonEntity) {
        _editingPerson.value = person
        _showAddPersonDialog.value = true
    }

    fun closeAddPersonDialog() {
        _showAddPersonDialog.value = false
        _editingPerson.value = null
    }

    fun openPaymentDialog() {
        _showPaymentDialog.value = true
    }

    fun closePaymentDialog() {
        _showPaymentDialog.value = false
    }

    fun openAddBudget() {
        _editingBudget.value = null
        _showAddBudgetDialog.value = true
        _showQuickAddSheet.value = false
    }

    fun openEditBudget(budget: BudgetEntity) {
        _editingBudget.value = budget
        _showAddBudgetDialog.value = true
    }

    fun closeAddBudgetDialog() {
        _showAddBudgetDialog.value = false
        _editingBudget.value = null
    }

    fun openCurrencyDialog() {
        _showCurrencyDialog.value = true
    }

    fun closeCurrencyDialog() {
        _showCurrencyDialog.value = false
    }

    fun saveTransaction(
        id: Long = 0L,
        type: String,
        amount: Double,
        category: String,
        source: String,
        note: String,
        datetime: Long
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                id = id,
                type = type,
                amount = amount,
                category = category,
                source = source,
                note = note,
                datetime = datetime
            )
            repository.saveTransaction(tx)
            closeAddTransactionDialog()
            showMessage(Localization.get("success_saved", userSettings.value.language))
        }
    }

    fun requestDeleteTransaction(tx: TransactionEntity) {
        _confirmDeleteTx.value = tx
    }

    fun confirmDeleteTransaction() {
        val tx = _confirmDeleteTx.value ?: return
        viewModelScope.launch {
            repository.deleteTransaction(tx.id)
            _confirmDeleteTx.value = null
            showMessage(Localization.get("success_deleted", userSettings.value.language))
        }
    }

    fun cancelDeleteTransaction() {
        _confirmDeleteTx.value = null
    }

    fun savePerson(
        id: Long = 0L,
        name: String,
        phone: String,
        type: String,
        totalAmount: Double,
        purpose: String,
        dueDate: Long?,
        note: String
    ) {
        viewModelScope.launch {
            val currentPaid = _editingPerson.value?.paidAmount ?: 0.0
            val person = PersonEntity(
                id = id,
                name = name,
                phone = phone,
                type = type,
                totalAmount = totalAmount,
                paidAmount = currentPaid,
                purpose = purpose,
                dueDate = dueDate,
                note = note
            )
            repository.savePerson(person)
            closeAddPersonDialog()
            showMessage(Localization.get("success_saved", userSettings.value.language))
        }
    }

    fun requestDeletePerson(person: PersonEntity) {
        _confirmDeletePerson.value = person
    }

    fun confirmDeletePerson() {
        val person = _confirmDeletePerson.value ?: return
        viewModelScope.launch {
            repository.deletePerson(person.id)
            _confirmDeletePerson.value = null
            if (_currentScreen.value == Screen.PERSON_DETAIL) {
                _currentScreen.value = Screen.PEOPLE
            }
            showMessage(Localization.get("success_deleted", userSettings.value.language))
        }
    }

    fun cancelDeletePerson() {
        _confirmDeletePerson.value = null
    }

    fun recordPayment(
        amount: Double,
        datetime: Long,
        method: String,
        note: String
    ) {
        val personId = _selectedPersonId.value ?: return
        viewModelScope.launch {
            repository.recordPayment(personId, amount, datetime, method, note)
            closePaymentDialog()
            showMessage(Localization.get("success_saved", userSettings.value.language))
        }
    }

    fun requestDeletePayment(payment: PaymentEntity) {
        _confirmDeletePayment.value = payment
    }

    fun confirmDeletePayment() {
        val payment = _confirmDeletePayment.value ?: return
        viewModelScope.launch {
            repository.deletePayment(payment.id)
            _confirmDeletePayment.value = null
            showMessage(Localization.get("success_deleted", userSettings.value.language))
        }
    }

    fun cancelDeletePayment() {
        _confirmDeletePayment.value = null
    }

    fun saveBudget(
        id: Long = 0L,
        category: String,
        amount: Double,
        monthYear: String = DateUtils.currentMonthKey()
    ) {
        viewModelScope.launch {
            val budget = BudgetEntity(
                id = id,
                category = category,
                monthYear = monthYear,
                amount = amount
            )
            repository.saveBudget(budget)
            closeAddBudgetDialog()
            showMessage(Localization.get("success_saved", userSettings.value.language))
        }
    }

    fun requestDeleteBudget(budget: BudgetEntity) {
        _confirmDeleteBudget.value = budget
    }

    fun confirmDeleteBudget() {
        val budget = _confirmDeleteBudget.value ?: return
        viewModelScope.launch {
            repository.deleteBudget(budget.id)
            _confirmDeleteBudget.value = null
            showMessage(Localization.get("success_deleted", userSettings.value.language))
        }
    }

    fun cancelDeleteBudget() {
        _confirmDeleteBudget.value = null
    }

    fun updateCurrency(code: String, symbol: String) {
        viewModelScope.launch {
            val updated = userSettings.value.copy(currencyCode = code, currencySymbol = symbol)
            repository.updateSettings(updated)
            closeCurrencyDialog()
        }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            val updated = userSettings.value.copy(language = lang)
            repository.updateSettings(updated)
        }
    }

    fun updateDarkMode(mode: Int) {
        viewModelScope.launch {
            val updated = userSettings.value.copy(darkMode = mode)
            repository.updateSettings(updated)
        }
    }

    // Backup & Restore Prompts
    fun requestBackup() {
        _showBackupConfirm.value = true
    }

    fun cancelBackup() {
        _showBackupConfirm.value = false
    }

    suspend fun getBackupJson(): String {
        _showBackupConfirm.value = false
        return repository.exportDataJson()
    }

    fun prepareRestore(jsonString: String) {
        pendingRestoreJson = jsonString
        _showRestoreConfirm.value = true
    }

    fun cancelRestore() {
        _showRestoreConfirm.value = false
        pendingRestoreJson = null
    }

    fun confirmRestore() {
        val json = pendingRestoreJson ?: return
        viewModelScope.launch {
            val success = repository.restoreDataJson(json)
            _showRestoreConfirm.value = false
            pendingRestoreJson = null
            if (success) {
                showMessage(Localization.get("restore_success", userSettings.value.language))
            } else {
                showMessage("Failed to restore backup")
            }
        }
    }

    fun requestReset() {
        _showResetConfirm.value = true
    }

    fun cancelReset() {
        _showResetConfirm.value = false
    }

    fun confirmReset() {
        viewModelScope.launch {
            repository.resetAllData()
            _showResetConfirm.value = false
            showMessage(Localization.get("success_saved", userSettings.value.language))
        }
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
