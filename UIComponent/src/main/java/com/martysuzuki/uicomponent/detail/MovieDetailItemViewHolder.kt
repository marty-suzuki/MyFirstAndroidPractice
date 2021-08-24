package com.martysuzuki.uicomponent.detail

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.martysuzuki.uicomponent.R
import com.martysuzuki.viewmodelinterface.detail.MovieDetailItem

sealed class MovieDetailItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    class Loading(itemView: View): MovieDetailItemViewHolder(itemView) {

        fun configure(item: MovieDetailItem.Loading) {
            when(item.style) {
                MovieDetailItem.Loading.Style.MATCH_PARENT ->
                    itemView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                MovieDetailItem.Loading.Style.WRAP_CONTENT ->
                    itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }

    class SectionTitleHeader(itemView: View) : MovieDetailItemViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.item_movie_detail_section_title_header_textView)

        fun configure(item: MovieDetailItem.SectionTitleHeader) {
            val id = when (item.type) {
                MovieDetailItem.SectionTitleHeader.SectionType.OVERVIEW -> R.string.overview
                MovieDetailItem.SectionTitleHeader.SectionType.RECOMMENDATIONS -> R.string.recommendations
                MovieDetailItem.SectionTitleHeader.SectionType.CASTS -> R.string.casts
            }
            textView.text = itemView.context.getString(id)
        }
    }

    class Thumbnail(itemView: View) : MovieDetailItemViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.item_movie_detail_thumbnail_imageView)
        private val textView: TextView = itemView.findViewById(R.id.item_movie_detail_thumbnail_textView)

        fun configure(item: MovieDetailItem.Thumbnail) {
            when (val innerItem = item.item) {
                is MovieDetailItem.Thumbnail.Item.Image -> {
                    textView.visibility = View.GONE
                    Glide.with(itemView)
                        .load(innerItem.imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView)
                }
                is MovieDetailItem.Thumbnail.Item.Empty -> {
                    textView.visibility = View.VISIBLE
                }
            }
        }
    }

    class Title(itemView: View) : MovieDetailItemViewHolder(itemView) {
        private val mainTextView: TextView = itemView.findViewById(R.id.item_movie_detail_title_main_textView)
        private val releaseDateTextView: TextView = itemView.findViewById(R.id.item_movie_detail_title_release_date_textView)

        fun configure(item: MovieDetailItem.Title) {
            mainTextView.text = item.text
            releaseDateTextView.text = item.releaseDate
        }
    }

    class Overview(itemView: View) : MovieDetailItemViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.item_movie_detail_overview_textView)
        private val imageView: ImageView = itemView.findViewById(R.id.item_movie_detail_overview_imageView)

        fun configure(item: MovieDetailItem.Overview) {
            textView.text = item.text
            imageView.visibility = if (item.isGradientVisible) View.VISIBLE else View.GONE
        }
    }

    class Cast(itemView: View) : MovieDetailItemViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.item_movie_detail_cast_textView)

        fun configure(item: MovieDetailItem.Cast) {
            textView.text = item.text
        }
    }

    class Recommendation(itemView: View) : MovieDetailItemViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.item_movie_textView)
        private val imageView: ImageView = itemView.findViewById(R.id.item_movie_imageView)

        fun configure(item: MovieDetailItem.Recommendation) {
            textView.text = item.title
            item.postPath?.also {
                Glide.with(itemView)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
            }
        }
    }
}