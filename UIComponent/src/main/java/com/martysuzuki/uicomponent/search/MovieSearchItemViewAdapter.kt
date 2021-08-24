package com.martysuzuki.uicomponent.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.martysuzuki.uicomponent.R
import com.martysuzuki.viewmodelinterface.search.MovieSearchItem
import com.martysuzuki.viewmodelinterface.search.MovieSearchUiState

class MovieSearchItemViewAdapter : RecyclerView.Adapter<MovieSearchItemViewHolder>() {

    private var items: List<MovieSearchItem> = emptyList()
    private var listener: OnItemClickListener? = null

    enum class ViewType(val rawValue: Int) {
        LOADING(1),
        NO_RESULTS(2),
        MOVIE(3)
    }

    fun apply(uiState: MovieSearchUiState) {
        items = uiState.items
        uiState.diffResult.dispatchUpdatesTo(this)
    }

    fun getViewType(position: Int) = when (items[position]) {
        is MovieSearchItem.Movie -> ViewType.MOVIE
        is MovieSearchItem.Loading -> ViewType.LOADING
        is MovieSearchItem.NoResults -> ViewType.NO_RESULTS
    }

    override fun getItemViewType(position: Int) = getViewType(position).rawValue

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSearchItemViewHolder {
        return when(ViewType.values().first { it.rawValue == viewType }) {
            ViewType.LOADING -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
                MovieSearchItemViewHolder.Loading(itemView)
            }
            ViewType.NO_RESULTS -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_no_results, parent, false)
                MovieSearchItemViewHolder.NoResults(itemView)
            }
            ViewType.MOVIE -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
                MovieSearchItemViewHolder.Default(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: MovieSearchItemViewHolder, position: Int) {
        when (val item = items[position]) {
            is MovieSearchItem.Movie ->
                (holder as? MovieSearchItemViewHolder.Default)?.configure(item)
            is MovieSearchItem.Loading ->
                (holder as? MovieSearchItemViewHolder.Loading)?.configure(item)
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClicked(position)
        }
    }

    override fun getItemCount() = items.size

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }
}