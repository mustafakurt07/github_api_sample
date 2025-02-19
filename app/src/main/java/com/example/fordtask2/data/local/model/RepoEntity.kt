package com.example.fordtask2.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey val id: Long,
    val name: String?,
    val ownerAvatar: String?,
    val htmlUrl: String?,
    val description: String?,
    val stars: Int?,
    val lastUpdated: Long? = System.currentTimeMillis(),
    val language: String?,
    val create_at: String?
)
