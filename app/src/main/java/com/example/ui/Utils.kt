package com.example.ui

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Double.formatCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ar", "EG"))
    return format.format(this)
}

fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("ar", "EG"))
    return sdf.format(Date(this))
}
