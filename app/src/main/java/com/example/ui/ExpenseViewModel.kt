package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Expense
import com.example.data.ExpenseRepository
import com.example.data.Person
import com.example.data.SpreadsheetRow
import com.example.utils.DetailedReportRow
import com.example.utils.PeriodReportRow
import com.example.utils.PdfPrintUtility
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // Persons basic data
    val allPersons: StateFlow<List<Person>> = repository.allPersons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Person Input State
    private val _personInputName = MutableStateFlow("")
    val personInputName = _personInputName.asStateFlow()

    private val _personInputError = MutableStateFlow<String?>(null)
    val personInputError = _personInputError.asStateFlow()

    // Unsaved Spreadsheet rows
    private val _spreadsheetRows = MutableStateFlow<List<SpreadsheetRow>>(listOf(SpreadsheetRow()))
    val spreadsheetRows = _spreadsheetRows.asStateFlow()

    // Toast/Alert notification flow
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    // --- Reports state ---
    // 1. Detailed Report Filter
    private val _selectedDetailedPerson = MutableStateFlow<Person?>(null)
    val selectedDetailedPerson = _selectedDetailedPerson.asStateFlow()

    // Detailed expenses based on selected person
    val detailedExpenses: StateFlow<List<Expense>> = _selectedDetailedPerson
        .combine(repository.allExpenses) { person, expenses ->
            if (person == null) emptyList()
            else expenses.filter { it.personId == person.id }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. Period Report Filters (default: start of month to today)
    private val _periodStartDate = MutableStateFlow<Long>(getStartOfMonthMillis())
    val periodStartDate = _periodStartDate.asStateFlow()

    private val _periodEndDate = MutableStateFlow<Long>(getEndOfDayMillis())
    val periodEndDate = _periodEndDate.asStateFlow()

    // Period expenses based on date range
    val periodExpenses: StateFlow<List<Expense>> = combine(
        _periodStartDate,
        _periodEndDate,
        repository.allExpenses
    ) { start, end, expenses ->
        expenses.filter { it.dateMillis in start..end }.sortedBy { it.dateMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Person modification (delete)
    fun deletePerson(person: Person) {
        viewModelScope.launch {
            repository.deletePerson(person)
            _uiEvent.emit(UiEvent.ShowToast("تم حذف الاسم بنجاح"))
            if (_selectedDetailedPerson.value?.id == person.id) {
                _selectedDetailedPerson.value = null
            }
        }
    }

    // Insert new person
    fun setPersonInputName(name: String) {
        _personInputName.value = name
        _personInputError.value = null
    }

    fun addPerson() {
        val name = _personInputName.value.trim()
        if (name.isEmpty()) {
            _personInputError.value = "الرجاء إدخال اسم صحيح"
            return
        }

        // Check duplicates
        val exists = allPersons.value.any { it.name.equals(name, ignoreCase = true) }
        if (exists) {
            _personInputError.value = "هذا الاسم موجود بالفعل"
            return
        }

        viewModelScope.launch {
            repository.insertPerson(Person(name = name))
            _personInputName.value = ""
            _personInputError.value = null
            _uiEvent.emit(UiEvent.ShowToast("تم حفظ الاسم بنجاح"))
        }
    }

    // --- Spreadsheet Row Actions ---
    fun addSpreadsheetRow() {
        _spreadsheetRows.value = _spreadsheetRows.value + SpreadsheetRow()
    }

    fun removeSpreadsheetRow(id: String) {
        val current = _spreadsheetRows.value
        if (current.size > 1) {
            _spreadsheetRows.value = current.filter { it.id != id }
        } else {
            // Reset the only row
            _spreadsheetRows.value = listOf(SpreadsheetRow())
        }
    }

    fun updateRowPerson(id: String, person: Person) {
        _spreadsheetRows.value = _spreadsheetRows.value.map {
            if (it.id == id) it.copy(person = person, personError = false) else it
        }
    }

    fun updateRowDate(id: String, dateMillis: Long) {
        _spreadsheetRows.value = _spreadsheetRows.value.map {
            if (it.id == id) it.copy(dateMillis = dateMillis) else it
        }
    }

    fun updateRowDescription(id: String, description: String) {
        _spreadsheetRows.value = _spreadsheetRows.value.map {
            if (it.id == id) it.copy(description = description) else it
        }
    }

    fun updateRowAmount(id: String, amount: String) {
        _spreadsheetRows.value = _spreadsheetRows.value.map {
            if (it.id == id) {
                // Ensure text is decimal-friendly
                val filtered = amount.filter { char -> char.isDigit() || char == '.' }
                it.copy(amount = filtered, amountError = false)
            } else it
        }
    }

    // Post / "ترحيل" all spreadsheet rows to Room Database
    fun postSpreadsheetRows() {
        val currentRows = _spreadsheetRows.value
        var hasErrors = false

        val validatedRows = currentRows.map { row ->
            val personErr = row.person == null
            val amtVal = row.amount.toDoubleOrNull()
            val amtErr = amtVal == null || amtVal <= 0.0

            if (personErr || amtErr) {
                hasErrors = true
            }
            row.copy(personError = personErr, amountError = amtErr)
        }

        if (hasErrors) {
            _spreadsheetRows.value = validatedRows
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("الرجاء تصحيح الحقول الحمراء قبل الترحيل"))
            }
            return
        }

        // Convert and post
        viewModelScope.launch {
            try {
                val expenses = currentRows.map { row ->
                    Expense(
                        personId = row.person!!.id,
                        personName = row.person.name,
                        dateMillis = row.dateMillis,
                        description = row.description,
                        amount = row.amount.toDouble()
                    )
                }
                repository.insertExpenses(expenses)
                // Clear and reset spreadsheet rows with a single blank row
                _spreadsheetRows.value = listOf(SpreadsheetRow())
                _uiEvent.emit(UiEvent.ShowToast("تم ترحيل البيانات بنجاح إلى قاعدة البيانات"))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowToast("حدث خطأ أثناء الترحيل: ${e.localizedMessage}"))
            }
        }
    }

    // --- Report Action ---
    fun selectDetailedPerson(person: Person?) {
        _selectedDetailedPerson.value = person
    }

    fun setPeriodStartDate(millis: Long) {
        _periodStartDate.value = getStartOfDayMillis(millis)
    }

    fun setPeriodEndDate(millis: Long) {
        _periodEndDate.value = getEndOfDayMillis(millis)
    }

    // --- Printing ---
    fun printDetailedReport(context: Context) {
        val person = _selectedDetailedPerson.value
        val expenses = detailedExpenses.value
        if (person == null || expenses.isEmpty()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("لا توجد بيانات للطباعة"))
            }
            return
        }

        val totalAmount = expenses.sumOf { it.amount }
        val expensesList = expenses.map {
            DetailedReportRow(
                date = formatDate(it.dateMillis),
                day = getDayName(it.dateMillis),
                amount = it.amount,
                description = it.description
            )
        }

        val htmlContent = PdfPrintUtility.generateDetailedReportHtml(
            personName = person.name,
            expensesList = expensesList,
            totalAmount = totalAmount
        )

        PdfPrintUtility.printHtml(
            context = context,
            htmlContent = htmlContent,
            jobName = "تقرير تفصيلي - ${person.name}"
        )
    }

    fun printPeriodReport(context: Context) {
        val expenses = periodExpenses.value
        if (expenses.isEmpty()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowToast("لا توجد بيانات للطباعة"))
            }
            return
        }

        val totalAmount = expenses.sumOf { it.amount }
        val expensesList = expenses.map {
            PeriodReportRow(
                personName = it.personName,
                date = formatDate(it.dateMillis),
                day = getDayName(it.dateMillis),
                description = it.description,
                amount = it.amount
            )
        }

        val startStr = formatDate(_periodStartDate.value)
        val endStr = formatDate(_periodEndDate.value)

        val htmlContent = PdfPrintUtility.generatePeriodReportHtml(
            startDate = startStr,
            endDate = endStr,
            expensesList = expensesList,
            totalAmount = totalAmount
        )

        PdfPrintUtility.printHtml(
            context = context,
            htmlContent = htmlContent,
            jobName = "تقرير الفترة من $startStr إلى $endStr"
        )
    }

    // --- Date Utilities ---
    fun formatDate(millis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale("ar")).format(Date(millis))
    }

    fun getDayName(millis: Long): String {
        return SimpleDateFormat("EEEE", Locale("ar")).format(Date(millis))
    }

    private fun getStartOfMonthMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfDayMillis(millis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDayMillis(millis: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    sealed interface UiEvent {
        data class ShowToast(val message: String) : UiEvent
    }
}

class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
