package com.example.fordtask2.util.mapper

import com.example.fordtask2.data.local.model.RepoEntity
import com.example.fordtask2.data.remote.GithubRepoDto
import com.example.fordtask2.domain.model.GithubRepo

fun GithubRepoDto.toEntity(): RepoEntity = RepoEntity(
    id = id,
    name = name,
    ownerAvatar = owner?.avatar_url ?: "",
    htmlUrl = htmlUrl,
    description = description,
    stars = stargazers_count,
    language = language,
    create_at = created_at
)

fun RepoEntity.toDomain(): GithubRepo = GithubRepo(
    id = id,
    name = name,
    ownerAvatar = ownerAvatar,
    htmlUrl = htmlUrl,
    description = description,
    stars = stars,
    language = language,
    created_at = create_at
)