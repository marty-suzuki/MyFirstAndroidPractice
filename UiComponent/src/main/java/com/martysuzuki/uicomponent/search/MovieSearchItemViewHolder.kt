package com.martysuzuki.uicomponent.search

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.martysuzuki.uicomponent.R
import com.martysuzuki.uilogicinterface.search.MovieSearchItem

sealed class MovieSearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class Default(itemView: View) : MovieSearchItemViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.item_movie_textView)
        private val imageView: ImageView = itemView.findViewById(R.id.item_movie_imageView)

        fun configure(item: MovieSearchItem.Movie) {
            textView.text = item.title
            imageView.setImageDrawable(null)
            item.postPath?.also {
                Glide.with(itemView)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
            }
        }
    }

    class NoResults(itemView: View) : MovieSearchItemViewHolder(itemView)

    class Loading(itemView: View) : MovieSearchItemViewHolder(itemView) {

        fun configure(item: MovieSearchItem.Loading) {
            when (item.style) {
                MovieSearchItem.Loading.Style.MATCH_PARENT ->
                    itemView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                MovieSearchItem.Loading.Style.WRAP_CONTENT ->
                    itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }
}