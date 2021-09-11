package com.martysuzuki.uilogic.search

import com.github.marty_suzuki.unio.Computed
import com.github.marty_suzuki.unio.Dependency
import com.github.marty_suzuki.unio.OutputFactory
import com.github.marty_suzuki.unio.Unio
import com.github.marty_suzuki.unio.UnioFactory
import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.repositoryinterface.movie.MoviesResult
import com.martysuzuki.repositoryinterface.movie.Page
import com.martysuzuki.uilogic.DiffCalculator
import com.martysuzuki.uilogicinterface.search.MovieSearchInput
import com.martysuzuki.uilogicinterface.search.MovieSearchItem
import com.martysuzuki.uilogicinterface.search.MovieSearchOutput
import com.martysuzuki.uilogicinterface.search.MovieSearchUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class MovieSearchUnio @AssistedInject constructor(
    input: MovieSearchInput,
    state: State,
    defaultDispatcher: CoroutineDispatcher,
    movieRepository: MovieRepository,
    @Assisted viewModelScope: CoroutineScope,
    @Assisted onCleared: Flow<Unit>,
) : Unio<
        MovieSearchInput,
        MovieSearchOutput,
        MovieSearchUnio.Extra,
        MovieSearchUnio.State
        >(
    input,
    Extra(
        defaultDispatcher = defaultDispatcher,
        movieRepository = movieRepository,
        onCleared = onCleared
    ),
    state,
    MovieSearchUnio,
    viewModelScope
) {

    @AssistedFactory
    interface Factory : UnioFactory<MovieSearchInput, MovieSearchOutput> {
        override fun create(
            @Assisted viewModelScope: CoroutineScope,
            @Assisted onCleared: Flow<Unit>
        ): MovieSearchUnio
    }

    private sealed class SearchAction {

        data class Execute(val query: String, val page:  Int?) : SearchAction()

        object Stop : SearchAction()
    }

    class State : Unio.State {
        val items = MutableStateFlow<List<MovieSearchItem>>(emptyList())
        val navigateToMovieDetail = MutableSharedFlow<Int>(extraBufferCapacity = 1)
        val showUnauthorizedDialog = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        var isSearching = false
        var page: Page? = null
        var query: String? = null
    }

    class Extra(
        val defaultDispatcher: CoroutineDispatcher,
        val movieRepository: MovieRepository,
        val onCleared: Flow<Unit>,
    ) : Unio.Extra

    companion object : OutputFactory<
            MovieSearchInput,
            MovieSearchOutput,
            Extra,
            State
            > {
        override fun create(
            dependency: Dependency<MovieSearchInput, Extra, State>,
            viewModelScope: CoroutineScope
        ): MovieSearchOutput {
            val state = dependency.state
            val extra = dependency.extra

            val initialSearch: Flow<SearchAction.Execute> = dependency.getFlow(MovieSearchInput::search)
                .onEach {
                    state.isSearching = false
                    state.page = null
                    state.query = it
                    state.items.value =
                        listOf(MovieSearchItem.Loading(MovieSearchItem.Loading.Style.MATCH_PARENT))
                }
                .map { SearchAction.Execute(it, null) }

            val loadMore: Flow<SearchAction> = dependency.getFlow(MovieSearchInput::reachBottom)
                .flatMapLatest {
                    if (state.isSearching) {
                        return@flatMapLatest emptyFlow()
                    }
                    val query = state.query ?: return@flatMapLatest emptyFlow()
                    val page = state.page?.let {
                        when (it) {
                            is Page.Next -> it.page
                            is Page.Finish -> return@flatMapLatest emptyFlow()
                        }
                    }
                    flowOf(SearchAction.Execute(query, page))
                }

            listOf(
                initialSearch,
                loadMore,
                extra.onCleared.map { SearchAction.Stop }
            )
                .merge()
                .onEach {
                    state.isSearching = true
                }
                .flatMapLatest {
                    when (it) {
                        is SearchAction.Execute ->
                            flowOf(extra.movieRepository.fetchMovies(it.query, it.page ?: 1))
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

                            if (state.page == null && newItems.isEmpty()) {
                                state.items.value = listOf(MovieSearchItem.NoResults)
                            } else {
                                state.items.value = (state.items.value + newItems)
                                    .filter { it !is MovieSearchItem.Loading }
                                    .let {
                                        when (result.page) {
                                            is Page.Next ->
                                                it + listOf(
                                                    MovieSearchItem.Loading(
                                                        MovieSearchItem.Loading.Style.WRAP_CONTENT
                                                    )
                                                )
                                            is Page.Finish ->
                                                it
                                        }
                                    }
                            }
                            state.page = result.page
                        }
                        is MoviesResult.Failure -> {
                            state.items.value =
                                state.items.value.filter { it !is MovieSearchItem.Loading }

                            when (result.reason) {
                                is MoviesResult.Failure.Reason.Unauthorized -> {
                                    state.showUnauthorizedDialog.emit(Unit)
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
                    state.isSearching = false
                }
                .launchIn(viewModelScope)

            dependency.getFlow(MovieSearchInput::onItemClicked)
                .onEach { position ->
                    state.items.value.getOrNull(position)?.also {
                        if (it is MovieSearchItem.Movie) {
                            state.navigateToMovieDetail.emit(it.id)
                        }
                    }
                }
                .launchIn(viewModelScope)

            val update = state.items
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
                .flowOn(extra.defaultDispatcher)

            return MovieSearchOutput(
                update = update,
                query = Computed { state.query },
                items = Computed { state.items.value },
                navigateToMovieDetail = state.navigateToMovieDetail,
                showUnauthorizedDialog = state.showUnauthorizedDialog,
            )
        }
    }
}