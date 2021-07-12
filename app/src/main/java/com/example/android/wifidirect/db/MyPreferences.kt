package com.example.android.wifidirect.db

import android.content.Context
import android.content.SharedPreferences

class MyPreferences(context: Context) {

    val PREFERENCE_TRUE_FALSE = "generated_data"
    var sharedPreferences: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    var editor = sharedPreferences.edit()

    //when to write the data and when to read it
    //needs to be done programmaically

    fun get(): Boolean {
        return sharedPreferences.getBoolean(PREFERENCE_TRUE_FALSE,false)
    }

    fun generatedDataTrueOrFalse(): Boolean {
        //if true set to true --> set to false and vice versa

        return if (sharedPreferences.getBoolean(PREFERENCE_TRUE_FALSE,true)){
            editor.apply {
                putBoolean(PREFERENCE_TRUE_FALSE, false)
                apply()

            }
            true
        }else{
            editor.apply {
                putBoolean(PREFERENCE_TRUE_FALSE,true)
            }
            false
        }

        return sharedPreferences.getBoolean(PREFERENCE_TRUE_FALSE,false)
    }



}