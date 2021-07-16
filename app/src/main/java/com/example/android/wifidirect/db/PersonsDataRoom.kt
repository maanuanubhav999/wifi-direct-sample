package com.example.android.wifidirect.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Person::class), version = 1, exportSchema = false)
public abstract class PersonsDataRoom : RoomDatabase() {

  abstract fun personDao(): PersonDao

  companion object {
      //singleton
      @Volatile
      private var INSTANCE : PersonsDataRoom? = null

      fun getDatabase(context: Context): PersonsDataRoom {
          //instance is not null return
          //else create db
          return  INSTANCE ?: synchronized(this) {
              val instance = Room.databaseBuilder(
                  context.applicationContext,
                  PersonsDataRoom::class.java,
                  "person_database"
              ).build()
              INSTANCE = instance
              instance
          }
      }
  }
}
