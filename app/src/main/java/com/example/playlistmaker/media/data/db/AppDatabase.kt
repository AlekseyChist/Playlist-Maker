package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.FavoriteTracksDao
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.media.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity

@Database(
    entities = [FavoriteTrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class],
    version = 3,
    exportSchema = false
)

abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}