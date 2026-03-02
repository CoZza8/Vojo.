package com.riki.vojo

import android.content.Context

class PrefManager(context: Context) {
    private val prefs = context.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE)

    fun setPro(isPro: Boolean) {
        prefs.edit().putBoolean("is_pro_user", isPro).apply()
    }

    fun isPro(): Boolean {
        return prefs.getBoolean("is_pro_user", false)
    }
}
