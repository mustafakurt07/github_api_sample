package com.example.fordtask2.data.remote

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class GithubRepoDto(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "html_url") val htmlUrl: String?,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "stargazers_count") val stargazers_count: Int?,
    @field:Json(name = "owner") val owner: OwnerDto?,
    @field:Json(name = "language") val language: String?,
    @field:Json(name = "created_at") val created_at: String?,
)

data class OwnerDto(
    @field:Json(name = "login") val login: String?,
    @SerializedName("avatar_url") val avatar_url: String?
)