package com.martysuzuki.uilogic.detail

import androidx.lifecycle.SavedStateHandle
import com.github.marty_suzuki.unio.Dependency
import com.github.marty_suzuki.unio.OutputFactory
import com.github.marty_suzuki.unio.UnidirectionalIO
import com.github.marty_suzuki.unio.Unio
import com.github.marty_suzuki.unio.UnioFactory
import com.martysuzuki.repositoryinterface.movie.MovieDetailResult
import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.uilogic.DiffCalculator
import com.martysuzuki.uilogicinterface.detail.MovieDetailInput
import com.martysuzuki.uilogicinterface.detail.MovieDetailItem
import com.martysuzuki.uilogicinterface.detail.MovieDetailOutput
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MovieDetailUnio(
    input: MovieDetailInput,
    extra: Extra,
    state: State,
    viewModelScope: CoroutineScope
) : Unio<
        MovieDetailInput,
        MovieDetailOutput,
        MovieDetailUnio.Extra,
        MovieDetailUnio.State
        >(
    input = input,
    extra = extra,
    state = state,
    outputFactory = MovieDetailUnio,
    viewModelScope = viewModelScope
) {

    internal data class ThumbnailStatus(val index: Int, val images: List<String>)

    internal data class ExpandableOverview(val original: String, val truncated: String)

    class State : Unio.State {
        internal val navigateToMovieDetail = MutableSharedFlow<Int>(extraBufferCapacity = 1)
        internal val items =
            MutableStateFlow<List<MovieDetailItem>>(listOf(MovieDetailItem.Loading(MovieDetailItem.Loading.Style.MATCH_PARENT)))
        internal val thumbnailStatus = MutableSharedFlow<ThumbnailStatus>(replay = 1)
        internal val updateOverview = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        internal var overview: ExpandableOverview? = null
    }

    class Extra(
        internal val movieRepository: MovieRepository,
        internal val defaultDispatcher: CoroutineDispatcher,
        internal val onCleared: Flow<Unit>,
        internal val movieId: Int
    ) : Unio.Extra

    companion object : OutputFactory<
            MovieDetailInput,
            MovieDetailOutput,
            Extra,
            State
            > {
        override fun create(
            dependency: Dependency<MovieDetailInput, Extra, State>,
            viewModelScope: CoroutineScope
        ): MovieDetailOutput {
            val state = dependency.state
            val extra = dependency.extra

            viewModelScope.launch {
                when (val response = extra.movieRepository.fetchMovieDetail(extra.movieId)) {
                    is MovieDetailResult.Success -> {
                        val movie = response.movie
                        val commonItems = listOf(
                            MovieDetailItem.Thumbnail(
                                movie.backdrops.firstOrNull()
                                    ?.let { MovieDetailItem.Thumbnail.Item.Image(it) }
                                    ?: MovieDetailItem.Thumbnail.Item.Empty
                            ),
                            MovieDetailItem.Title(
                                text = movie.title,
                                releaseDate = movie.releaseDate
                            )
                        )

                        val overviewItems = movie.overview
                            ?.let {
                                if (it.isEmpty()) {
                                    emptyList()
                                } else {
                                    val type =
                                        MovieDetailItem.SectionTitleHeader.SectionType.OVERVIEW
                                    val truncateLength = 400
                                    val item = if (it.length > truncateLength) {
                                        val truncated = it.substring(0, truncateLength)
                                        state.overview = ExpandableOverview(
                                            original = it,
                                            truncated = truncated
                                        )
                                        MovieDetailItem.Overview(
                                            text = truncated,
                                            isGradientVisible = true
                                        )
                                    } else {
                                        MovieDetailItem.Overview(
                                            text = it,
                                            isGradientVisible = false
                                        )
                                    }
                                    listOf(
                                        MovieDetailItem.SectionTitleHeader(type),
                                        item
                                    )
                                }
                            }
                            ?: emptyList()

                        val castItems = movie.casts.let { casts ->
                            if (casts.isEmpty()) {
                                emptyList()
                            } else {
                                val viewableCastItems = casts.take(5).map {
                                    MovieDetailItem.Cast(it)
                                }
                                val remain = casts.size - viewableCastItems.size
                                val castItems = if (remain > 0) {
                                    viewableCastItems + listOf(MovieDetailItem.Cast("...and more $remain casts"))
                                } else {
                                    viewableCastItems
                                }
                                val type = MovieDetailItem.SectionTitleHeader.SectionType.CASTS
                                listOf(MovieDetailItem.SectionTitleHeader(type)) + castItems
                            }
                        }

                        val recommendationItems = movie.recommendations
                            .map {
                                MovieDetailItem.Recommendation(
                                    id = it.id,
                                    title = it.title,
                                    postPath = it.posterPath
                                )
                            }.let {
                                if (it.isEmpty()) {
                                    it
                                } else {
                                    val type =
                                        MovieDetailItem.SectionTitleHeader.SectionType.RECOMMENDATIONS
                                    listOf(MovieDetailItem.SectionTitleHeader(type)) + it
                                }
                            }

                        state.items.value =
                            commonItems + overviewItems + castItems + recommendationItems
                        state.thumbnailStatus.emit(
                            ThumbnailStatus(
                                index = 0,
                                images = movie.backdrops
                            )
                        )
                    }
                    is MovieDetailResult.Failure -> {
                        // This unhandled error is low priority at this time.
                    }
                }
            }

            dependency.getFlow(MovieDetailInput::onItemClicked)
                .onEach { position ->
                    state.items.value.getOrNull(position)?.also {
                        when (it) {
                            is MovieDetailItem.Recommendation -> state.navigateToMovieDetail.tryEmit(
                                it.id
                            )
                            is MovieDetailItem.Overview -> state.updateOverview.tryEmit(Unit)
                            else -> return@also
                        }
                    }
                }
                .launchIn(viewModelScope)

            listOf(
                dependency.getFlow(MovieDetailInput::onViewCreated).map { true },
                dependency.getFlow(MovieDetailInput::onDestroyView).map { false },
                extra.onCleared.map { false }
            )
                .merge()
                .flatMapLatest {
                    if (it)
                        state.thumbnailStatus.onEach { delay(5000L) }
                    else
                        emptyFlow()
                }
                .onEach { status ->
                    val images = status.images
                    if (images.size < 2) {
                        return@onEach
                    }
                    val nextIndex = if (images.size > status.index + 1) {
                        status.index + 1
                    } else {
                        0
                    }

                    val image = MovieDetailItem.Thumbnail.Item.Image(images[nextIndex])
                    val filteredItems =
                        state.items.value.filter { it !is MovieDetailItem.Thumbnail }
                    state.items.value =
                        listOf(MovieDetailItem.Thumbnail(image)) + filteredItems
                    state.thumbnailStatus.tryEmit(
                        ThumbnailStatus(
                            index = nextIndex,
                            images = images
                        )
                    )
                }
                .launchIn(viewModelScope)

            state.updateOverview
                .onEach {
                    val overview = state.overview ?: return@onEach

                    state.items.value = state.items.value.map {
                        if (it is MovieDetailItem.Overview) {
                            if (it.isGradientVisible) {
                                MovieDetailItem.Overview(
                                    text = overview.original,
                                    isGradientVisible = false
                                )
                            } else {
                                MovieDetailItem.Overview(
                                    text = overview.truncated,
                                    isGradientVisible = true
                                )
                            }
                        } else {
                            it
                        }
                    }
                }
                .launchIn(viewModelScope)

            val update = state.items
                .scan(
                    Pair<List<MovieDetailItem>, List<MovieDetailItem>>(
                        emptyList(),
                        emptyList()
                    )
                ) { result, items ->
                    Pair(result.second, items)
                }
                .map { pair ->
                    val old = pair.first
                    val new = pair.second
                    val diffResult = DiffCalculator(
                        newItems = new,
                        oldItems = old
                    ).calculateDiff()
                    MovieDetailUiState(new, diffResult)
                }
                .flowOn(extra.defaultDispatcher)

            return MovieDetailOutput(
                update = update,
                navigateToMovieDetail = state.navigateToMovieDetail,
            )
        }
    }
}