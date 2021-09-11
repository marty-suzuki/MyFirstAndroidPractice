package com.martysuzuki.uilogic.search

import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.repositoryinterface.movie.MoviesResult
import com.martysuzuki.repositoryinterface.movie.Page
import com.martysuzuki.uilogic.DiffCalculator
import com.martysuzuki.uilogicinterface.UiLogicFactory
import com.martysuzuki.uilogicinterface.search.MovieSearchItem
import com.martysuzuki.uilogicinterface.search.MovieSearchUiLogic
import com.martysuzuki.uilogicinterface.search.MovieSearchUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class MovieSearchUiLogicImpl @AssistedInject constructor(
    private val movieRepository: MovieRepository,
    private val defaultDispatcher: CoroutineDispatcher,
    @Assisted viewModelScope: CoroutineScope,
    @Assisted dependency: Unit
) : MovieSearchUiLogic {

    @AssistedFactory
    interface Factory : UiLogicFactory<MovieSearchUiLogic, Unit> {
        override fun create(viewModelScope: CoroutineScope, dependency: Unit): MovieSearchUiLogicImpl
    }

    private sealed class SearchAction {

        data class Execute(val query: String, val page:  Int?) : SearchAction()

        object Stop : SearchAction()
    }

    override val update: Flow<MovieSearchUiState>
        get() = _items
            .scan(
                Pair<List<MovieSearchItem>, List<MovieSearchItem>>(
                    emptyList(),
                    emptyList()
                )
            ) { result, items ->
                Pair(result.second, items)
            }
            .map { pair ->
                val oldItems = pair.first
                val newItems = pair.second
                val diffResult = DiffCalculator(
                    newItems = newItems,
                    oldItems = oldItems
                ).calculateDiff()
                MovieSearchUiState(newItems, diffResult)
            }
            .flowOn(defaultDispatcher)

    override val query: String?
        get() = _query

    override val items: List<MovieSearchItem>
        get() = _items.value
    private val _items = MutableStateFlow<List<MovieSearchItem>>(emptyList())

    override val navigateToMovieDetail: Flow<Int>
        get() = _navigateToMovieDetail
    private val _navigateToMovieDetail = MutableSharedFlow<Int>(extraBufferCapacity = 1)

    override val showUnauthorizedDialog: Flow<Unit>
        get() = _showUnauthorizedDialog
    private val _showUnauthorizedDialog = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val searchAction = MutableSharedFlow<SearchAction>(extraBufferCapacity = 1)
    private var isSearching = false
    private var _page: Page? = null
    private var _query: String? = null

    init {
        searchAction
            .flatMapLatest {
                when (it) {
                    is SearchAction.Execute -> {
                        isSearching = true
                        flowOf(movieRepository.fetchMovies(it.query, it.page ?: 1))
                    }
                    SearchAction.Stop ->
                        emptyFlow()
                }
            }
            .onEach { result ->
                when (result) {
                    is MoviesResult.Success -> {

                        val newItems = result.items.map {
                            MovieSearchItem.Movie(
                                id = it.id,
                                title = it.title,
                                postPath = it.posterPath
                            )
                        }

                        if (_page == null && newItems.isEmpty()) {
                            _items.value = listOf(MovieSearchItem.NoResults)
                        } else {
                            _items.value = (_items.value + newItems)
                                .filter { it !is MovieSearchItem.Loading }
                                .let {
                                    when (result.page) {
                                        is Page.Next ->
                                            it + listOf(MovieSearchItem.Loading(MovieSearchItem.Loading.Style.WRAP_CONTENT))
                                        is Page.Finish ->
                                            it
                                    }
                                }
                        }
                        _page = result.page
                    }
                    is MoviesResult.Failure -> {
                        _items.value = _items.value.filter { it !is MovieSearchItem.Loading }

                        when (result.reason) {
                            is MoviesResult.Failure.Reason.Unauthorized -> {
                                _showUnauthorizedDialog.emit(Unit)
                            }
                            is MoviesResult.Failure.Reason.EmptyQuery,
                            is MoviesResult.Failure.Reason.InvalidPage,
                            is MoviesResult.Failure.Reason.NotFound,
                            is MoviesResult.Failure.Reason.Unknown -> {
                                // These are low priority unhandled errors at this time.
                            }
                        }
                    }
                }
                isSearching = false
            }
            .launchIn(viewModelScope)
    }

    override fun search(text: String) {
        isSearching = false
        _page = null
        _query = text
        _items.value = listOf(MovieSearchItem.Loading(MovieSearchItem.Loading.Style.MATCH_PARENT))
        searchAction.tryEmit(SearchAction.Execute(text, null))
    }

    override fun reachBottom() {
        if (isSearching) {
            return
        }
        val query = _query ?: return
        val page = _page?.let {
            when (it) {
                is Page.Next -> it.page
                is Page.Finish -> return
            }
        }
        searchAction.tryEmit(SearchAction.Execute(query, page))
    }

    override fun onCleared() {
        searchAction.tryEmit(SearchAction.Stop)
    }

    override fun onItemClicked(position: Int) {
        _items.value.getOrNull(position)?.also {
            if (it is MovieSearchItem.Movie) {
                _navigateToMovieDetail.tryEmit(it.id)
            }
        }
    }
}

