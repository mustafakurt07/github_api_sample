package com.example.fordtask2.presantation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fordtask2.domain.model.GithubRepo
import com.example.fordtask2.domain.usecase.GetReposUseCase
import com.example.fordtask2.util.mapper.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import com.example.fordtask2.data.local.datastore.PreferencesDataStore
import com.example.fordtask2.util.mapper.NetworkHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val getReposUseCase: GetReposUseCase,
    private val dataStore: PreferencesDataStore,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _uiState = mutableStateOf(ReposUiState())
    val uiState: State<ReposUiState> get() = _uiState

    private var currentPage = 1

    private val _viewType = MutableStateFlow(1)
    val viewType: StateFlow<Int> = _viewType.asStateFlow()

    init {
        // Uygulama açıldığında kayıtlı view type'ı yükle
        viewModelScope.launch {
            dataStore.viewTypeFlow.collect { type ->
                _viewType.value = type
            }
        }
        observeNetworkChanges()
    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            networkHelper.observeNetworkState()
                .distinctUntilChanged() // Sadece durum değiştiğinde tetikle
                .collect { isConnected ->
                    if (isConnected) {
                        // İnternet bağlantısı geri geldiğinde, sayfa 1’den yeniden yükleyerek güncel veriyi getir
                        loadRepos(isInitialLoad = true)
                    } else {
                        // İnternet bağlantısı yoksa, yükleme durumlarını kapat
                        withContext(Dispatchers.Main) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isPaginating = false
                            )
                        }
                    }
                }
        }
    }

    fun loadRepos(isInitialLoad: Boolean = false, user: String = "JakeWharton") {
        // İlk sayfa yüklemesinde sayfa sayısını sıfırla ve state'i güncelle
        if (isInitialLoad) {
            currentPage = 1
            _uiState.value = _uiState.value.copy(hasMore = true)
        }

        // Eğer daha fazla veri yoksa veya zaten sayfalama yapılıyorsa, tekrar yüklemeyi engelle
        if (!_uiState.value.hasMore || _uiState.value.isPaginating) return

        viewModelScope.launch {
            getReposUseCase(user, currentPage)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            withContext(Dispatchers.Main) {
                                _uiState.value = if (isInitialLoad) {
                                    ReposUiState(isLoading = true)
                                } else {
                                    _uiState.value.copy(isPaginating = true)
                                }
                            }
                        }
                        is Resource.Success -> {
                            val newRepos = result.data
                            // Yeni veri 10 kayıt içeriyorsa daha fazla veri var demektir
                            val moreDataAvailable = newRepos.size == 10
                            withContext(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(
                                    repos = if (isInitialLoad) newRepos else (_uiState.value.repos + newRepos).distinctBy { it.id },
                                    isLoading = false,
                                    isPaginating = false,
                                    hasMore = moreDataAvailable
                                )
                            }
                            if (moreDataAvailable) {
                                currentPage++
                            }
                        }
                        is Resource.Error -> {
                            withContext(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(
                                    error = result.message,
                                    isLoading = false,
                                    isPaginating = false
                                )
                            }
                        }
                    }
                }
        }
    }

    fun updateViewType(type: Int) {
        _viewType.value = type
        viewModelScope.launch {
            dataStore.saveViewType(type)
        }
    }
}

data class ReposUiState(
    val repos: List<GithubRepo> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true
)
