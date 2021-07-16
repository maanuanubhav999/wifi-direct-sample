package com.example.android.wifidirect.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(person: Person)

     @Query("SELECT * FROM Person_table ORDER BY id ASC")
     fun getAllPerson(): List<Person>
}
