package com.martysuzuki.uicomponent.search

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.martysuzuki.args.detail.MovieDetailArgs
import com.martysuzuki.router.search.MovieSearchRouter
import com.martysuzuki.uicomponent.databinding.FragmentMovieSearchBinding
import com.martysuzuki.viewmodelinterface.search.MovieSearchUiLogic
import com.martysuzuki.viewmodelinterface.search.MovieSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MovieSearchFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var binding: FragmentMovieSearchBinding? = null

    private val viewModel: MovieSearchViewModel by viewModels()
    private val uiLogic: MovieSearchUiLogic by lazy { viewModel.uiLogic }

    @Inject lateinit var router: MovieSearchRouter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMovieSearchBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            fragmentMovieSearchSearchView.apply {

                isIconifiedByDefault = false
                viewModel.uiLogic.query?.also {
                    setQuery(it, false)
                }

                val searchView = this
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            viewModel.uiLogic.search(query)
                            searchView.clearFocus()
                        }
                        return false
                    }

                    override fun onQueryTextChange(p0: String?) = false
                })
            }

            fragmentMovieSearchRecyclerView.apply {

                val searchItemViewAdapter = MovieSearchItemViewAdapter().apply {
                    setOnItemClickListener(object : MovieSearchItemViewAdapter.OnItemClickListener {
                        override fun onItemClicked(position: Int) {
                            uiLogic.onItemClicked(position)
                        }
                    })
                }

                viewModel.uiLogic.update
                    .onEach {
                        searchItemViewAdapter.apply(it)
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)

                viewModel.uiLogic.navigateToMovieDetail
                    .onEach {
                        router.routeMovieDetail(
                            fragment = this@MovieSearchFragment,
                            args = MovieDetailArgs(it)
                        )
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)

                viewModel.uiLogic.showUnauthorizedDialog
                    .onEach {
                        router.routeUnauthorized(this@MovieSearchFragment)
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)

                val spanCount = 3
                val gridLayoutManager = GridLayoutManager(context, spanCount).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (searchItemViewAdapter.getViewType(position)) {
                                MovieSearchItemViewAdapter.ViewType.MOVIE -> 1
                                MovieSearchItemViewAdapter.ViewType.LOADING,
                                MovieSearchItemViewAdapter.ViewType.NO_RESULTS -> spanCount
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
                })

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        if (!recyclerView.canScrollVertically(1)) {
                            viewModel.uiLogic.reachBottom()
                        }
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}