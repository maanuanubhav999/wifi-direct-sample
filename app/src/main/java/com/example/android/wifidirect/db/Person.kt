package com.example.android.wifidirect.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Person_table")
data class Person (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val dob: Long,
    val gender: String,
    val telephone: Long

        )