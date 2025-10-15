package com.example.playlistmaker.compose

import java.util.Locale

fun formatTrackTimeMillis(millis: Long): String {
    val min = millis / 60000
    val sec = (millis % 60000) / 1000
    return String.format(Locale.getDefault(), "%d:%02d", min, sec)
}