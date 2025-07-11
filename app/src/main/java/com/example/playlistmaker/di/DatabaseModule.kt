package com.example.playlistmaker.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.DatabaseMigrations
import com.example.playlistmaker.media.data.db.converter.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.converter.TrackDbConverter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "playlist_maker_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    single { TrackDbConverter() }
    single { PlaylistDbConverter() }
}

// Миграция
private val MIGRATION_1_2 = object : Migration(1, 2) {
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
