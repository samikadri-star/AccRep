package com.example.data

import java.util.UUID

data class SpreadsheetRow(
    val id: String = UUID.randomUUID().toString(),
    val person: Person? = null,
    val dateMillis: Long = System.currentTimeMillis(),
    val description: String = "",
    val amount: String = "",
    val personError: Boolean = false,
    val amountError: Boolean = false
)
