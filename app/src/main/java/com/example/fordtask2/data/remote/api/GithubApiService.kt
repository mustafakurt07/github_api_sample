package com.example.fordtask2.data.remote.api

import com.example.fordtask2.data.remote.GithubRepoDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("type") type: String = "owner",
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int = 10
    ): List<GithubRepoDto>
}