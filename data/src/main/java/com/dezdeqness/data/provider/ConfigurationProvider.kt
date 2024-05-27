package com.dezdeqness.data.provider

import android.content.res.AssetManager
import com.dezdeqness.data.mapper.FilterMapper
import com.dezdeqness.data.mapper.GenreMapper
import com.dezdeqness.data.model.FilterItem
import com.dezdeqness.data.model.GenreRemote
import com.dezdeqness.domain.model.FilterEntity
import com.dezdeqness.domain.model.GenreEntity
import com.dezdeqness.domain.repository.GenreRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.buffer
import okio.source

class ConfigurationProvider(
    private val assetManager: AssetManager,
    private val genreMapper: GenreMapper,
    private val filterMapper: FilterMapper,
    private val moshi: Moshi,
    private val genreRepository: GenreRepository,
) {

    fun getListGenre(): List<GenreEntity> {
        val localGenres = genreRepository.getGenresLocal()

        if (localGenres.isNotEmpty()) {
            return localGenres
        }

        val inputStream = assetManager.open(FILENAME_GENRE_JSON)
        val type = Types.newParameterizedType(List::class.java, GenreRemote::class.java)
        val adapter = moshi.adapter<List<GenreRemote>>(type)
        val list = adapter.fromJson(inputStream.source().buffer()).orEmpty()
        return list.map { item -> genreMapper.fromResponse(item) }
    }

    fun getFilters(): List<FilterEntity> {
        val inputStream = assetManager.open(FILENAME_FILTER_JSON)
        val type = Types.newParameterizedType(List::class.java, FilterItem::class.java)
        val adapter = moshi.adapter<List<FilterItem>>(type)
        val list = adapter.fromJson(inputStream.source().buffer()).orEmpty()
        return list.map { item -> filterMapper.fromResponse(item) }
    }

    companion object {
        private const val FILENAME_GENRE_JSON = "genre.json"
        private const val FILENAME_FILTER_JSON = "filter.json"
    }

}
