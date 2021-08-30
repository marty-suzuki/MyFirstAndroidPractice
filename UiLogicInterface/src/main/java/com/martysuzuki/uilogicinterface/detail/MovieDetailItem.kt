package com.martysuzuki.uilogicinterface.detail

import com.martysuzuki.uilogicinterface.DiffableItem

sealed class MovieDetailItem : DiffableItem {

    data class Loading(val style: Style) : MovieDetailItem() {
        enum class Style {
            MATCH_PARENT, WRAP_CONTENT
        }

        override val identifier = "LOADING_IDENTIFIER-$style"
    }

    data class SectionTitleHeader(val type: SectionType) : MovieDetailItem() {
        enum class SectionType {
            OVERVIEW, CASTS, RECOMMENDATIONS
        }

        override val identifier = "SECTION_TITLE_HEADER_IDENTIFIER-$type"
    }

    data class Thumbnail(val item: Item) : MovieDetailItem() {
        sealed class Item : DiffableItem {
            object Empty : Item() {
                override val identifier = "EMPTY"
            }

            data class Image(val imageUrl: String) : Item() {
                override val identifier = "IMAGE"
            }
        }

        override val identifier = "THUMBNAIL_IDENTIFIER-${item.identifier}"
    }

    data class Title(val text: String, val releaseDate: String) : MovieDetailItem() {
        override val identifier = "TITLE_IDENTIFIER-$text-$releaseDate"
    }

    data class Overview(val text: String, val isGradientVisible: Boolean) : MovieDetailItem() {
        override val identifier = "OVERVIEW_IDENTIFIER"
    }

    data class Cast(val text: String) : MovieDetailItem() {
        override val identifier = "CAST_IDENTIFIER-$text"
    }

    data class Recommendation(val id: Int, val title: String, val postPath: String?) :
        MovieDetailItem() {
        override val identifier = "RECOMMENDATION_IDENTIFIER-$id"
    }
}