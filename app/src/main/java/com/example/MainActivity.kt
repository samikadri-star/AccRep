package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.ExpenseRepository
import com.example.ui.ExpenseViewModel
import com.example.ui.ExpenseViewModelFactory
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize database & repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(database.personDao(), database.expenseDao())

        // 2. Set up ViewModel via Factory
        val viewModel = ViewModelProvider(
            this,
            ExpenseViewModelFactory(repository)
        )[ExpenseViewModel::class.java]

        // 3. Set content
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
