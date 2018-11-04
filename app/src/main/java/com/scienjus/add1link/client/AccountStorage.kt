package com.scienjus.add1link.client

import android.content.Context
import android.content.SharedPreferences

object AccountStorage {

    private const val KEY_SHARED_PREFERENCES_NAME = "data"

    private const val KEY_EMAIL = "email"

    private const val KEY_TOKEN = "token"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        this.sharedPreferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun put(account: Account) {
        val editor = this.sharedPreferences.edit()
        editor.putString(KEY_EMAIL, account.email)
        editor.putString(KEY_TOKEN, account.token)
        editor.apply()
    }

    fun get() : Account? {
        val email = this.sharedPreferences.getString(KEY_EMAIL, null)
        val token = this.sharedPreferences.getString(KEY_TOKEN, null)
        if (email != null && token != null) {
            return Account(email, token)
        }
        return null
    }

    data class Account(val email: String, val token: String)
}