package com.example.playlistmaker.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import com.example.playlistmaker.R

/**
 * Конвертирует dp в пиксели
 */
fun Context.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}

/**
 * Форматирует количество треков с правильным окончанием
 */
fun Context.formatTracksCount(count: Int): String {
    return resources.getQuantityString(R.plurals.tracks_count, count, count)
}