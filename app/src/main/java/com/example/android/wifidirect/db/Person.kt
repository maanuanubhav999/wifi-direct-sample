package com.example.android.wifidirect.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Person_table")
data class Person (
    @ColumnInfo(name = "person")
    val name: String,
    val dob: Long,
    val gender: String,
    val telephone: Long
    ){
    @PrimaryKey(autoGenerate = true) var id =0
}