package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.PersonEntity
import com.example.data.model.PersonType
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserSettingsEntity
import com.example.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class MoneyManagerRepository(private val db: AppDatabase) {

    private val transactionDao = db.transactionDao()
    private val personDao = db.personDao()
    private val paymentDao = db.paymentDao()
    private val budgetDao = db.budgetDao()
    private val userSettingsDao = db.userSettingsDao()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val recentTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions(10)
    val allPeople: Flow<List<PersonEntity>> = personDao.getAllPeople()
    val peopleToPay: Flow<List<PersonEntity>> = personDao.getPeopleByType(PersonType.OWE)
    val peopleToReceive: Flow<List<PersonEntity>> = personDao.getPeopleByType(PersonType.RECEIVABLE)
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
    val userSettings: Flow<UserSettingsEntity> = userSettingsDao.getSettings().map {
        it ?: UserSettingsEntity()
    }

    fun getPaymentsForPerson(personId: Long): Flow<List<PaymentEntity>> {
        return paymentDao.getPaymentsForPerson(personId)
    }

    fun getPerson(personId: Long): Flow<PersonEntity?> {
        return personDao.getPersonById(personId)
    }

    fun getTransaction(txId: Long): Flow<TransactionEntity?> {
        return transactionDao.getTransactionById(txId)
    }

    fun getBudgetsForMonth(monthKey: String): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForMonth(monthKey)
    }

    suspend fun saveTransaction(tx: TransactionEntity): Long = withContext(Dispatchers.IO) {
        if (tx.id == 0L) {
            transactionDao.insertTransaction(tx)
        } else {
            transactionDao.updateTransaction(tx)
            tx.id
        }
    }

    suspend fun deleteTransaction(txId: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransactionById(txId)
    }

    suspend fun savePerson(person: PersonEntity): Long = withContext(Dispatchers.IO) {
        if (person.id == 0L) {
            personDao.insertPerson(person)
        } else {
            personDao.updatePerson(person)
            person.id
        }
    }

    suspend fun deletePerson(personId: Long) = withContext(Dispatchers.IO) {
        personDao.deletePersonById(personId)
        paymentDao.deletePaymentsByPersonId(personId)
        transactionDao.deleteByPersonId(personId)
    }

    suspend fun recordPayment(
        personId: Long,
        amount: Double,
        datetime: Long,
        method: String,
        note: String
    ): Long = withContext(Dispatchers.IO) {
        val person = personDao.getPersonByIdSync(personId) ?: return@withContext -1L
        val isOwe = person.type == PersonType.OWE
        val txType = if (isOwe) TransactionType.GIVEN else TransactionType.RECEIVED
        val paymentType = if (isOwe) "GIVEN" else "RECEIVED"
        val categoryName = if (isOwe) "Person Payment" else "Money Received"

        // Create transaction record
        val tx = TransactionEntity(
            type = txType,
            amount = amount,
            category = categoryName,
            source = method,
            personId = personId,
            personName = person.name,
            note = note.ifEmpty { "${person.name} payment" },
            datetime = datetime
        )
        val txId = transactionDao.insertTransaction(tx)

        // Create payment record
        val payment = PaymentEntity(
            personId = personId,
            amount = amount,
            type = paymentType,
            datetime = datetime,
            method = method,
            note = note,
            transactionId = txId
        )
        val paymentId = paymentDao.insertPayment(payment)

        // Update person's paid amount
        val newPaid = person.paidAmount + amount
        personDao.updatePerson(person.copy(paidAmount = newPaid))

        paymentId
    }

    suspend fun deletePayment(paymentId: Long) = withContext(Dispatchers.IO) {
        val payment = paymentDao.getPaymentByIdSync(paymentId) ?: return@withContext
        val person = personDao.getPersonByIdSync(payment.personId)
        if (person != null) {
            val updatedPaid = (person.paidAmount - payment.amount).coerceAtLeast(0.0)
            personDao.updatePerson(person.copy(paidAmount = updatedPaid))
        }
        if (payment.transactionId != null) {
            transactionDao.deleteTransactionById(payment.transactionId)
        }
        paymentDao.deletePaymentById(paymentId)
    }

    suspend fun saveBudget(budget: BudgetEntity): Long = withContext(Dispatchers.IO) {
        if (budget.id == 0L) {
            budgetDao.insertBudget(budget)
        } else {
            budgetDao.updateBudget(budget)
            budget.id
        }
    }

    suspend fun deleteBudget(budgetId: Long) = withContext(Dispatchers.IO) {
        budgetDao.deleteBudgetById(budgetId)
    }

    suspend fun updateSettings(settings: UserSettingsEntity) = withContext(Dispatchers.IO) {
        userSettingsDao.saveSettings(settings)
    }

    // Export all data to JSON string for backup
    suspend fun exportDataJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "My Money Manager")
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        // Settings
        val settings = userSettingsDao.getSettingsSync() ?: UserSettingsEntity()
        val settingsObj = JSONObject().apply {
            put("currencyCode", settings.currencyCode)
            put("currencySymbol", settings.currencySymbol)
            put("language", settings.language)
            put("darkMode", settings.darkMode)
            put("openingBalance", settings.openingBalance)
            put("isBiometricEnabled", settings.isBiometricEnabled)
        }
        root.put("settings", settingsObj)

        // Transactions
        val transactions = transactionDao.getAllTransactionsSync()
        val txArray = JSONArray()
        for (tx in transactions) {
            val obj = JSONObject().apply {
                put("id", tx.id)
                put("type", tx.type)
                put("amount", tx.amount)
                put("category", tx.category)
                put("source", tx.source)
                put("personId", tx.personId ?: JSONObject.NULL)
                put("personName", tx.personName ?: "")
                put("note", tx.note)
                put("datetime", tx.datetime)
                put("createdAt", tx.createdAt)
            }
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        // People
        val people = personDao.getAllPeopleSync()
        val peopleArray = JSONArray()
        for (p in people) {
            val obj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("phone", p.phone)
                put("type", p.type)
                put("totalAmount", p.totalAmount)
                put("paidAmount", p.paidAmount)
                put("purpose", p.purpose)
                put("dueDate", p.dueDate ?: JSONObject.NULL)
                put("note", p.note)
                put("createdAt", p.createdAt)
            }
            peopleArray.put(obj)
        }
        root.put("people", peopleArray)

        // Payments
        val payments = paymentDao.getAllPaymentsSync()
        val paymentsArray = JSONArray()
        for (pm in payments) {
            val obj = JSONObject().apply {
                put("id", pm.id)
                put("personId", pm.personId)
                put("amount", pm.amount)
                put("type", pm.type)
                put("datetime", pm.datetime)
                put("method", pm.method)
                put("note", pm.note)
                put("transactionId", pm.transactionId ?: JSONObject.NULL)
                put("createdAt", pm.createdAt)
            }
            paymentsArray.put(obj)
        }
        root.put("payments", paymentsArray)

        // Budgets
        val budgets = budgetDao.getAllBudgetsSync()
        val budgetsArray = JSONArray()
        for (b in budgets) {
            val obj = JSONObject().apply {
                put("id", b.id)
                put("category", b.category)
                put("monthYear", b.monthYear)
                put("amount", b.amount)
                put("createdAt", b.createdAt)
            }
            budgetsArray.put(obj)
        }
        root.put("budgets", budgetsArray)

        root.toString(2)
    }

    // Restore data from JSON string
    suspend fun restoreDataJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("transactions") && !root.has("people")) {
                return@withContext false
            }

            // Clear current data
            transactionDao.deleteAll()
            personDao.deleteAll()
            paymentDao.deleteAll()
            budgetDao.deleteAll()

            // Restore settings if present
            if (root.has("settings")) {
                val sObj = root.getJSONObject("settings")
                val restoredSettings = UserSettingsEntity(
                    currencyCode = sObj.optString("currencyCode", "BDT"),
                    currencySymbol = sObj.optString("currencySymbol", "৳"),
                    language = sObj.optString("language", "bn"),
                    darkMode = sObj.optInt("darkMode", 0),
                    openingBalance = sObj.optDouble("openingBalance", 0.0),
                    isBiometricEnabled = sObj.optBoolean("isBiometricEnabled", false)
                )
                userSettingsDao.saveSettings(restoredSettings)
            }

            // Restore people
            if (root.has("people")) {
                val pArray = root.getJSONArray("people")
                val peopleList = mutableListOf<PersonEntity>()
                for (i in 0 until pArray.length()) {
                    val p = pArray.getJSONObject(i)
                    peopleList.add(
                        PersonEntity(
                            id = p.optLong("id", 0L),
                            name = p.getString("name"),
                            phone = p.optString("phone", ""),
                            type = p.getString("type"),
                            totalAmount = p.getDouble("totalAmount"),
                            paidAmount = p.optDouble("paidAmount", 0.0),
                            purpose = p.optString("purpose", ""),
                            dueDate = if (p.isNull("dueDate")) null else p.optLong("dueDate"),
                            note = p.optString("note", ""),
                            createdAt = p.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                personDao.insertAll(peopleList)
            }

            // Restore transactions
            if (root.has("transactions")) {
                val tArray = root.getJSONArray("transactions")
                val txList = mutableListOf<TransactionEntity>()
                for (i in 0 until tArray.length()) {
                    val t = tArray.getJSONObject(i)
                    txList.add(
                        TransactionEntity(
                            id = t.optLong("id", 0L),
                            type = t.getString("type"),
                            amount = t.getDouble("amount"),
                            category = t.getString("category"),
                            source = t.optString("source", ""),
                            personId = if (t.isNull("personId")) null else t.optLong("personId"),
                            personName = t.optString("personName", ""),
                            note = t.optString("note", ""),
                            datetime = t.optLong("datetime", System.currentTimeMillis()),
                            createdAt = t.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                transactionDao.insertAll(txList)
            }

            // Restore payments
            if (root.has("payments")) {
                val pmArray = root.getJSONArray("payments")
                val pmList = mutableListOf<PaymentEntity>()
                for (i in 0 until pmArray.length()) {
                    val pm = pmArray.getJSONObject(i)
                    pmList.add(
                        PaymentEntity(
                            id = pm.optLong("id", 0L),
                            personId = pm.getLong("personId"),
                            amount = pm.getDouble("amount"),
                            type = pm.getString("type"),
                            datetime = pm.optLong("datetime", System.currentTimeMillis()),
                            method = pm.optString("method", "Cash"),
                            note = pm.optString("note", ""),
                            transactionId = if (pm.isNull("transactionId")) null else pm.optLong("transactionId"),
                            createdAt = pm.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                paymentDao.insertAll(pmList)
            }

            // Restore budgets
            if (root.has("budgets")) {
                val bArray = root.getJSONArray("budgets")
                val bList = mutableListOf<BudgetEntity>()
                for (i in 0 until bArray.length()) {
                    val b = bArray.getJSONObject(i)
                    bList.add(
                        BudgetEntity(
                            id = b.optLong("id", 0L),
                            category = b.getString("category"),
                            monthYear = b.getString("monthYear"),
                            amount = b.getDouble("amount"),
                            createdAt = b.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                budgetDao.insertAll(bList)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        transactionDao.deleteAll()
        personDao.deleteAll()
        paymentDao.deleteAll()
        budgetDao.deleteAll()
        populateSampleData()
    }

    // Pre-populate demo records if database is empty (Requirement #26)
    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val count = transactionDao.getAllTransactionsSync().size
        if (count == 0) {
            populateSampleData()
        }
    }

    private suspend fun populateSampleData() {
        // Sample people
        val rahimId = personDao.insertPerson(
            PersonEntity(
                name = "Rahim",
                phone = "01711223344",
                type = PersonType.OWE,
                totalAmount = 10000.0,
                paidAmount = 4000.0,
                purpose = "Grocery & Borrowed",
                note = "Personal loan for home renovation"
            )
        )
        val karimId = personDao.insertPerson(
            PersonEntity(
                name = "Karim",
                phone = "01811556677",
                type = PersonType.OWE,
                totalAmount = 5000.0,
                paidAmount = 2000.0,
                purpose = "Electronics purchase",
                note = "To be settled end of month"
            )
        )
        val hasanId = personDao.insertPerson(
            PersonEntity(
                name = "Hasan",
                phone = "01911889900",
                type = PersonType.RECEIVABLE,
                totalAmount = 8000.0,
                paidAmount = 3000.0,
                purpose = "Freelance website design",
                note = "Remaining ৳5,000 upon delivery"
            )
        )

        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        // Payments for Rahim
        val p1 = paymentDao.insertPayment(
            PaymentEntity(
                personId = rahimId,
                amount = 2000.0,
                type = "GIVEN",
                datetime = now - (2 * oneDay),
                method = "bKash",
                note = "Grocery payment"
            )
        )
        val p2 = paymentDao.insertPayment(
            PaymentEntity(
                personId = rahimId,
                amount = 2000.0,
                type = "GIVEN",
                datetime = now - (7 * oneDay),
                method = "Cash",
                note = "First installment"
            )
        )

        // Payment for Karim
        paymentDao.insertPayment(
            PaymentEntity(
                personId = karimId,
                amount = 2000.0,
                type = "GIVEN",
                datetime = now - (3 * oneDay),
                method = "Bank",
                note = "Partial payment"
            )
        )

        // Payment for Hasan
        paymentDao.insertPayment(
            PaymentEntity(
                personId = hasanId,
                amount = 3000.0,
                type = "RECEIVED",
                datetime = now - (4 * oneDay),
                method = "Nagad",
                note = "Advance received"
            )
        )

        // Sample Income Transactions
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.INCOME,
                amount = 30000.0,
                category = "Salary",
                source = "Bank",
                note = "Monthly salary deposit",
                datetime = now - (10 * oneDay)
            )
        )
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.INCOME,
                amount = 10000.0,
                category = "Business",
                source = "bKash",
                note = "Product sales revenue",
                datetime = now - (8 * oneDay)
            )
        )
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.INCOME,
                amount = 5000.0,
                category = "Freelance",
                source = "Bank",
                note = "UI Design project",
                datetime = now - (5 * oneDay)
            )
        )

        // Sample Expense Transactions
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = 3000.0,
                category = "Food",
                source = "Cash",
                note = "Family dinner & daily meals",
                datetime = now - (1 * oneDay)
            )
        )
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = 1500.0,
                category = "Transport",
                source = "bKash",
                note = "Uber & CNG ride fare",
                datetime = now - (3 * oneDay)
            )
        )
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = 2500.0,
                category = "Shopping",
                source = "Card",
                note = "Clothing and accessories",
                datetime = now - (4 * oneDay)
            )
        )
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = 2000.0,
                category = "Bills",
                source = "bKash",
                note = "Electricity and internet bills",
                datetime = now - (6 * oneDay)
            )
        )

        // Payments as Transactions
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.GIVEN,
                amount = 2000.0,
                category = "Person Payment",
                source = "bKash",
                personId = rahimId,
                personName = "Rahim",
                note = "Rahim Payment - Grocery",
                datetime = now - (2 * oneDay)
            )
        )
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.GIVEN,
                amount = 2000.0,
                category = "Person Payment",
                source = "Cash",
                personId = rahimId,
                personName = "Rahim",
                note = "Rahim Payment - Installment",
                datetime = now - (7 * oneDay)
            )
        )
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.GIVEN,
                amount = 2000.0,
                category = "Person Payment",
                source = "Bank",
                personId = karimId,
                personName = "Karim",
                note = "Karim Payment",
                datetime = now - (3 * oneDay)
            )
        )
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.RECEIVED,
                amount = 3000.0,
                category = "Money Received",
                source = "Nagad",
                personId = hasanId,
                personName = "Hasan",
                note = "Hasan Received",
                datetime = now - (4 * oneDay)
            )
        )

        // Sample Budgets
        val currentMonth = DateUtils.currentMonthKey()
        budgetDao.insertBudget(BudgetEntity(category = "Food", monthYear = currentMonth, amount = 5000.0))
        budgetDao.insertBudget(BudgetEntity(category = "Transport", monthYear = currentMonth, amount = 3000.0))
        budgetDao.insertBudget(BudgetEntity(category = "Shopping", monthYear = currentMonth, amount = 5000.0))
        budgetDao.insertBudget(BudgetEntity(category = "Bills", monthYear = currentMonth, amount = 4000.0))
        budgetDao.insertBudget(BudgetEntity(category = "Education", monthYear = currentMonth, amount = 3000.0))
        budgetDao.insertBudget(BudgetEntity(category = "Other", monthYear = currentMonth, amount = 10000.0))
    }
}
