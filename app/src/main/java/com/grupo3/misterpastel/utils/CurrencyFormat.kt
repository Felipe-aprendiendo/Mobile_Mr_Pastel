package com.grupo3.misterpastel.utils

/**
 * Utilidad para manejar conversiones entre "$45.000 CLP" y Double.
 * Nota: es intencionalmente simple (proyecto institucional).
 */
object CurrencyFormat {

    fun strToDouble(clp: String): Double {
        return clp.replace("[^\\d.,]".toRegex(), "")
            .replace(".", "")
            .replace(",", ".")
            .toDoubleOrNull() ?: 0.0
    }

    fun doubleToClp(value: Double): String {
        // Representación sin formato local (simple)
        return "$" + value.toInt().toString() + " CLP"
    }
}
