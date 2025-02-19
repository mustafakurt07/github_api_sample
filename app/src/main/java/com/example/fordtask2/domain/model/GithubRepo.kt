package com.example.fordtask2.domain.model

data class GithubRepo(
    val id: Long,
    val name: String?,
    val ownerAvatar: String?,
    val htmlUrl: String?,
    val description: String?,
    val stars: Int?,
    val language: String?,
    val created_at: String?
)
