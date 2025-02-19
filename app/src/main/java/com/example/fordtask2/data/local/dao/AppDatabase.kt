package com.example.fordtask2.data.local.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fordtask2.data.local.model.RepoEntity

@Database(entities = [RepoEntity::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}
