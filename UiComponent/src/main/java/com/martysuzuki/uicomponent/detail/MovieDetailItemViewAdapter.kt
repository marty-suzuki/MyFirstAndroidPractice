package com.martysuzuki.uicomponent.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.martysuzuki.uicomponent.R
import com.martysuzuki.uilogicinterface.detail.MovieDetailItem
import com.martysuzuki.uilogicinterface.detail.MovieDetailUiState

class MovieDetailItemViewAdapter : RecyclerView.Adapter<MovieDetailItemViewHolder>() {

    private var items: List<MovieDetailItem> = emptyList()
    private var listener: OnItemClickListener? = null

    enum class ViewType(val rawValue: Int) {
        LOADING(1),
        SECTION_TITLE_HEADER(2),
        THUMBNAIL(3),
        TITLE(4),
        OVERVIEW(5),
        CAST(6),
        RECOMMENDATION(7)
    }

    fun apply(uiState: MovieDetailUiState) {
        items = uiState.items
        uiState.diffResult.dispatchUpdatesTo(this)
    }

    fun getViewType(position: Int) = when (items[position]) {
        is MovieDetailItem.Loading -> ViewType.LOADING
        is MovieDetailItem.SectionTitleHeader -> ViewType.SECTION_TITLE_HEADER
        is MovieDetailItem.Thumbnail -> ViewType.THUMBNAIL
        is MovieDetailItem.Title -> ViewType.TITLE
        is MovieDetailItem.Overview -> ViewType.OVERVIEW
        is MovieDetailItem.Cast -> ViewType.CAST
        is MovieDetailItem.Recommendation -> ViewType.RECOMMENDATION
    }

    override fun getItemViewType(position: Int) = getViewType(position).rawValue

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieDetailItemViewHolder {
        return when(ViewType.values().first { it.rawValue == viewType }) {
            ViewType.LOADING -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
                MovieDetailItemViewHolder.Loading(itemView)
            }
            ViewType.SECTION_TITLE_HEADER -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_movie_detail_section_title_header, parent, false)
                MovieDetailItemViewHolder.SectionTitleHeader(itemView)
            }
            ViewType.THUMBNAIL -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_movie_detail_thumbnail, parent, false)
                MovieDetailItemViewHolder.Thumbnail(itemView)
            }
            ViewType.TITLE -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_movie_detail_title, parent, false)
                MovieDetailItemViewHolder.Title(itemView)
            }
            ViewType.OVERVIEW -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_movie_detail_overview, parent, false)
                MovieDetailItemViewHolder.Overview(itemView)
            }
            ViewType.CAST -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_movie_detail_cast, parent, false)
                MovieDetailItemViewHolder.Cast(itemView)
            }
            ViewType.RECOMMENDATION -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
                MovieDetailItemViewHolder.Recommendation(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: MovieDetailItemViewHolder, position: Int) {
        when (val item = items[position]) {
            is MovieDetailItem.Loading ->
                (holder as? MovieDetailItemViewHolder.Loading)?.configure(item)
            is MovieDetailItem.SectionTitleHeader ->
                (holder as? MovieDetailItemViewHolder.SectionTitleHeader)?.configure(item)
            is MovieDetailItem.Thumbnail ->
                (holder as? MovieDetailItemViewHolder.Thumbnail)?.configure(item)
            is MovieDetailItem.Title ->
                (holder as? MovieDetailItemViewHolder.Title)?.configure(item)
            is MovieDetailItem.Overview -> {
                (holder as? MovieDetailItemViewHolder.Overview)?.configure(item)
                holder.itemView.setOnClickListener {
                    listener?.onItemClicked(position)
                }
            }
            is MovieDetailItem.Cast ->
                (holder as? MovieDetailItemViewHolder.Cast)?.configure(item)
            is MovieDetailItem.Recommendation -> {
                (holder as? MovieDetailItemViewHolder.Recommendation)?.configure(item)
                holder.itemView.setOnClickListener {
                    listener?.onItemClicked(position)
                }
            }
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