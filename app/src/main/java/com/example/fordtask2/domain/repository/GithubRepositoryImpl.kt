package com.example.fordtask2.domain.repository

import com.example.fordtask2.data.local.dao.RepoDao
import com.example.fordtask2.data.remote.api.GithubApiService
import com.example.fordtask2.domain.model.GithubRepo
import com.example.fordtask2.util.mapper.NetworkHelper
import com.example.fordtask2.util.mapper.Resource
import com.example.fordtask2.util.mapper.toDomain
import com.example.fordtask2.util.mapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
private val api: GithubApiService,
private val repoDao: RepoDao,
private val networkHelper: NetworkHelper
) : GithubRepository {

    override fun getRepos(user: String, page: Int): Flow<Resource<List<GithubRepo>>> = flow {
        emit(Resource.Loading)

        if (networkHelper.isConnected()) {
            try {
                val remoteRepos = api.getUserRepos(user, page = page)

                // İlk sayfa için veritabanını temizle
                if (page == 1) {
                    repoDao.clearRepos()
                }

                // Yeni repoları kaydet
                repoDao.insertRepos(remoteRepos.map { it.toEntity() })

                emit(Resource.Success(remoteRepos.map { it.toEntity().toDomain() }))
            } catch (e: Exception) {
                handleOfflineScenario(page)
            }
        } else {
            handleOfflineScenario(page)
        }
    }
        .catch { e ->
            handleOfflineScenario(page)
        }
        .flowOn(Dispatchers.IO) // Tüm flow işlemlerini Dispatchers.IO üzerinde çalıştır

    private suspend fun FlowCollector<Resource<List<GithubRepo>>>.handleOfflineScenario(page: Int) {
        if (page == 1) {
            val localRepos = repoDao.getAllRepos().first().map { it.toDomain() }
            if (localRepos.isNotEmpty()) {
                emit(Resource.Success(localRepos))
            } else {
                emit(Resource.Error("No internet connection and no local data please check internet connection"))
            }
        } else {
            // Sayfalama için offline modda boş liste dön
            emit(Resource.Success(emptyList()))
        }
    }
}