package com.martysuzuki.uilogic.detail

import com.martysuzuki.repositoryinterface.movie.MovieDetailResult
import com.martysuzuki.repositoryinterface.movie.MovieRepository
import com.martysuzuki.uilogic.DiffCalculator
import com.martysuzuki.uilogicinterface.detail.MovieDetailItem
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiLogic
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MovieDetailUiLogicImpl(
    private val movieRepository: MovieRepository,
    private val defaultDispatcher: CoroutineDispatcher,
    private val viewModelScope: CoroutineScope,
    private val movieId: Int
) : MovieDetailUiLogic {

    override val update: Flow<MovieDetailUiState>
        get() = _items
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
            .flowOn(defaultDispatcher)

    private val _items =
        MutableStateFlow<List<MovieDetailItem>>(listOf(MovieDetailItem.Loading(MovieDetailItem.Loading.Style.MATCH_PARENT)))

    override val navigateToMovieDetail: Flow<Int>
        get() = _navigateToMovieDetail
    private val _navigateToMovieDetail = MutableSharedFlow<Int>(extraBufferCapacity = 1)

    private val _thumbnailStatus = MutableSharedFlow<ThumbnailStatus>(replay = 1)
    private var timerJob: Job? = null

    private data class ThumbnailStatus(val index: Int, val images: List<String>)

    private var _overview: ExpandableOverview? = null

    private data class ExpandableOverview(val original: String, val truncated: String)

    init {
        viewModelScope.launch {
            when (val response = movieRepository.fetchMovieDetail(movieId)) {
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
                                val type = MovieDetailItem.SectionTitleHeader.SectionType.OVERVIEW
                                val truncateLength = 400
                                val item = if (it.length > truncateLength) {
                                    val truncated = it.substring(0, truncateLength)
                                    _overview = ExpandableOverview(
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

                    _items.value = commonItems + overviewItems + castItems + recommendationItems
                    _thumbnailStatus.emit(
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
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onDestroyView() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onViewCreated() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _thumbnailStatus
                .onEach { delay(5000L) }
                .collect { status ->
                    val images = status.images
                    if (images.size < 2) {
                        return@collect
                    }
                    val nextIndex = if (images.size > status.index + 1) {
                        status.index + 1
                    } else {
                        0
                    }

                    val image = MovieDetailItem.Thumbnail.Item.Image(images[nextIndex])
                    val filteredItems = _items.value.filter { it !is MovieDetailItem.Thumbnail }
                    _items.value = listOf(MovieDetailItem.Thumbnail(image)) + filteredItems
                    _thumbnailStatus.tryEmit(
                        ThumbnailStatus(
                            index = nextIndex,
                            images = images
                        )
                    )
                }
        }
    }

    private fun updateOverview(overview: ExpandableOverview?) {
        if (overview == null) return

        _items.value = _items.value.map {
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

    override fun onItemClicked(position: Int) {
        _items.value.getOrNull(position)?.also {
            when (it) {
                is MovieDetailItem.Recommendation -> _navigateToMovieDetail.tryEmit(it.id)
                is MovieDetailItem.Overview -> updateOverview(_overview)
                else -> return
            }
        }
    }
}
