package com.example.fordtask2.domain.usecase

import com.example.fordtask2.domain.model.GithubRepo
import com.example.fordtask2.domain.repository.GithubRepository
import com.example.fordtask2.util.mapper.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReposUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    operator fun invoke(user: String, currentPage: Int): Flow<Resource<List<GithubRepo>>> =
        repository.getRepos(user, currentPage)
}