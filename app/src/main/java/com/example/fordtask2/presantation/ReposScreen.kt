package com.example.fordtask2.presantation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fordtask2.domain.model.GithubRepo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ViewComfy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.fordtask2.presantation.components.RepoGridItem
import com.example.fordtask2.presantation.components.RepoListItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReposScreen(viewModel: ReposViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.loadRepos(isInitialLoad = true)
    }
    val state by viewModel.uiState
    val viewType by viewModel.viewType.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GitHub Repositories") },
                actions = {
                    ViewTypeSelector(
                        currentType = viewType,
                        onTypeSelected = viewModel::updateViewType
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading && state.repos.isEmpty() -> LoadingIndicator()
                state.error != null -> {
                    ErrorMessage(
                        state.error!!,
                        onRetry = { viewModel.loadRepos(isInitialLoad = true) }
                    )
                }
                else -> RepoListContent(
                    repos = state.repos,
                    viewType = viewType,
                    onItemClick = { repo ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("CreatedDate: ${repo.created_at?.trim()} - ⭐ ${repo.stars}")
                        }
                        println("${repo.name} - ⭐ ${repo.stars}")
                    },
                    onLoadMore = {
                        viewModel.loadRepos(isInitialLoad = false)
                    },
                    isPaginating = state.isPaginating
                )
            }
        }
    }
}

@Composable
private fun RepoListContent(
    repos: List<GithubRepo>,
    viewType: Int,
    onItemClick: (GithubRepo) -> Unit,
    onLoadMore: () -> Unit,
    isPaginating: Boolean
) {
    when (viewType) {
        1 -> RepoListView(
            repos = repos,
            onItemClick = onItemClick,
            onLoadMore = onLoadMore,
            isPaginating = isPaginating
        )
        2 -> RepoGridView(
            repos = repos,
            columns = 2,
            onItemClick = onItemClick,
            onLoadMore = onLoadMore,
            isPaginating = isPaginating

        )
        3 -> RepoGridView(
            repos = repos,
            columns = 3,
            onItemClick = onItemClick,
            onLoadMore = onLoadMore,
            isPaginating = isPaginating

        )
        else -> RepoListView(
            repos = repos,
            onItemClick = onItemClick,
            onLoadMore = onLoadMore,
            isPaginating = isPaginating
        )
    }
}

@Composable
fun RepoListView(
    repos: List<GithubRepo>,
    onItemClick: (GithubRepo) -> Unit,
    onLoadMore: () -> Unit,
    isPaginating: Boolean
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(repos) { repo ->
            RepoListItem(repo = repo, onClick = onItemClick)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Pagination için yükleniyor göstergesi
        if (isPaginating) {
            item {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }

    // Pagination tetikleyicisini düzeltelim:
    LaunchedEffect(listState, isPaginating) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                if (!isPaginating && lastVisibleItemIndex >= totalItems - 1) {
                    onLoadMore()
                }
            }
    }
}


@Composable
fun RepoGridView(
    repos: List<GithubRepo>,
    columns: Int,
    onItemClick: (GithubRepo) -> Unit,
    onLoadMore: () -> Unit,
    isPaginating: Boolean
) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(repos.size) { index ->
            val repo = repos[index]
            RepoGridItem(repo = repo, onClick = onItemClick)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isPaginating) {
            item {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }

    LaunchedEffect(gridState, isPaginating) {
        snapshotFlow { gridState.layoutInfo }
            .collect { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                if (!isPaginating && lastVisibleItemIndex >= totalItems - 1) {
                    onLoadMore()
                }
            }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun ViewTypeSelector(
    currentType: Int,
    onTypeSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.ViewComfy,
            contentDescription = "View type selector"
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        listOf(1, 2, 3).forEach { type ->
            DropdownMenuItem(
                text = { Text("View $type") },
                onClick = {
                    onTypeSelected(type)
                    expanded = false
                }
            )
        }
    }
}