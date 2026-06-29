package com.example.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val personDao: PersonDao,
    private val expenseDao: ExpenseDao
) {
    val allPersons: Flow<List<Person>> = personDao.getAllPersons()
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insertPerson(person: Person): Long = personDao.insertPerson(person)
    suspend fun updatePerson(person: Person) = personDao.updatePerson(person)
    suspend fun deletePerson(person: Person) = personDao.deletePerson(person)

    fun getExpensesForPerson(personId: Int): Flow<List<Expense>> = 
        expenseDao.getExpensesForPerson(personId)

    fun getExpensesInPeriod(startMillis: Long, endMillis: Long): Flow<List<Expense>> =
        expenseDao.getExpensesInPeriod(startMillis, endMillis)

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    suspend fun insertExpenses(expenses: List<Expense>) = expenseDao.insertExpenses(expenses)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
}
