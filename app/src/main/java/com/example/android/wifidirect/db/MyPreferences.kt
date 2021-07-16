package com.example.android.wifidirect.db

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.aware.WifiAwareManager
import android.util.Log

open class MyPreferences(context: Context) {

    val PREFERENCE_TRUE_FALSE = "generated_data"
    var sharedPreferences: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    var editor = sharedPreferences.edit()

    //when to write the data and when to read it
    //needs to be done programmaically

    fun get(): Boolean {
        return sharedPreferences.getBoolean(PREFERENCE_TRUE_FALSE,false)
    }



    fun set(myPreferences: MyPreferences, value: Boolean) {
        myPreferences.editor.apply{
            putBoolean(PREFERENCE_TRUE_FALSE, value)
        }
    }






}