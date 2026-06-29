package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personId: Int,
    val personName: String,
    val dateMillis: Long,
    val description: String,
    val amount: Double
)
