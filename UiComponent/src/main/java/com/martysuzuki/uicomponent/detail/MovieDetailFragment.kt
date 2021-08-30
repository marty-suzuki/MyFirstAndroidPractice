package com.martysuzuki.uicomponent.detail

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.router.detail.MovieDetailRouter
import com.martysuzuki.uicomponent.databinding.FragmentMovieDetailBinding
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiLogic
import com.martysuzuki.viewmodel.detail.MovieDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var binding: FragmentMovieDetailBinding? = null

    private val viewModel: MovieDetailViewModel by viewModels()
    private val uiLogic: MovieDetailUiLogic by lazy { viewModel.uiLogic }
    private val router: MovieDetailRouter by lazy { viewModel.router }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMovieDetailBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            fragmentMovieDetailRecyclerView.apply {

                val searchItemViewAdapter = MovieDetailItemViewAdapter().apply {
                    setOnItemClickListener(object : MovieDetailItemViewAdapter.OnItemClickListener {
                        override fun onItemClicked(position: Int) {
                            uiLogic.onItemClicked(position)
                        }
                    })
                }

                uiLogic.update
                    .onEach {
                        searchItemViewAdapter.apply(it)
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)

                uiLogic.navigateToMovieDetail
                    .onEach {
                        router.routeMovieDetail(
                            fragment = this@MovieDetailFragment,
                            args = MovieDetailArgs(it)
                        )
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)

                val spanCount = 3
                val gridLayoutManager = GridLayoutManager(context, spanCount).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (searchItemViewAdapter.getViewType(position)) {
                                MovieDetailItemViewAdapter.ViewType.LOADING,
                                MovieDetailItemViewAdapter.ViewType.SECTION_TITLE_HEADER,
                                MovieDetailItemViewAdapter.ViewType.THUMBNAIL,
                                MovieDetailItemViewAdapter.ViewType.TITLE,
                                MovieDetailItemViewAdapter.ViewType.OVERVIEW,
                                MovieDetailItemViewAdapter.ViewType.CAST -> spanCount
                                MovieDetailItemViewAdapter.ViewType.RECOMMENDATION -> 1
                            }
                        }
                    }
                }

                setHasFixedSize(true)
                layoutManager = gridLayoutManager
                adapter = searchItemViewAdapter

                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        super.getItemOffsets(outRect, view, parent, state)

                        (parent.getChildViewHolder(view) as? MovieDetailItemViewHolder)?.also {
                            when (it) {
                                is MovieDetailItemViewHolder.Loading,
                                is MovieDetailItemViewHolder.SectionTitleHeader,
                                is MovieDetailItemViewHolder.Thumbnail,
                                is MovieDetailItemViewHolder.Title,
                                is MovieDetailItemViewHolder.Overview,
                                is MovieDetailItemViewHolder.Cast -> return@also
                                is MovieDetailItemViewHolder.Recommendation -> {
                                    val position = parent.getChildAdapterPosition(view)
                                    val column = position % spanCount
                                    val spacing = 8
                                    outRect.set(
                                        spacing - column * spacing / spanCount,
                                        if (position < spanCount) spacing else 0,
                                        (column + 1) * spacing / spanCount,
                                        spacing
                                    )
                                }
                            }
                        }
                    }
                })
            }
        }

        uiLogic.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiLogic.onDestroyView()
        binding = null
    }
}
