package com.grupo3.misterpastel.utils

import android.util.Patterns

/**
 * Validadores básicos reutilizables.
 */
object Validators {
    fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
