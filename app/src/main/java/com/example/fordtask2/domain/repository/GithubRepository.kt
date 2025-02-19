package com.example.fordtask2.domain.repository

import com.example.fordtask2.domain.model.GithubRepo
import com.example.fordtask2.util.mapper.Resource
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    fun getRepos(user: String, page: Int): Flow<Resource<List<GithubRepo>>>
}