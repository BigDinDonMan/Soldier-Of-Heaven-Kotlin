package com.soldierofheaven.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun formatWithThousandsSeparator(value: Int): String {
    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
    val symbols = formatter.decimalFormatSymbols
    symbols.groupingSeparator = ',';
    formatter.decimalFormatSymbols = symbols
    return formatter.format(value)
}
