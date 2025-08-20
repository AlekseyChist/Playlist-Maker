// app/src/main/java/com/example/playlistmaker/media/data/db/DatabaseMigrations.kt
package com.example.playlistmaker.media.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS playlists (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    coverPath TEXT,
                    trackIds TEXT NOT NULL,
                    trackCount INTEGER NOT NULL DEFAULT 0
                )
            """)
        }
    }
}