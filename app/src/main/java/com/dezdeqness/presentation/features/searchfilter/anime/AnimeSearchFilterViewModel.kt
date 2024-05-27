package com.dezdeqness.presentation.features.searchfilter.anime

import com.dezdeqness.core.AppLogger
import com.dezdeqness.core.BaseViewModel
import com.dezdeqness.core.CoroutineDispatcherProvider
import com.dezdeqness.domain.repository.SearchFilterRepository
import com.dezdeqness.presentation.event.ApplyFilter
import com.dezdeqness.presentation.models.AnimeCell
import com.dezdeqness.presentation.models.AnimeSearchFilter
import com.dezdeqness.presentation.models.CellState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Named

class AnimeSearchFilterViewModel @Inject constructor(
    animeSearchFilterComposer: AnimeSearchFilterComposer,
    @Named("searchFiltersList") private val list: List<AnimeSearchFilter>,
    searchFilterRepository: SearchFilterRepository,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    appLogger: AppLogger,
) : BaseViewModel(
    coroutineDispatcherProvider = coroutineDispatcherProvider,
    appLogger = appLogger,
) {

    private val _animeSearchFilterStateFlow: MutableStateFlow<AnimeSearchFilterState> =
        MutableStateFlow(AnimeSearchFilterState())
    val animeSearchFilterStateFlow: StateFlow<AnimeSearchFilterState> get() = _animeSearchFilterStateFlow

    private val selectedCells: MutableList<AnimeCell> = mutableListOf()

    init {
        launchOnIo {
            if (list.isEmpty()) {
                val list = searchFilterRepository.getFilterConfiguration()
                _animeSearchFilterStateFlow.value =
                    AnimeSearchFilterState(items = animeSearchFilterComposer.compose(list))
            } else {
                _animeSearchFilterStateFlow.value =
                    AnimeSearchFilterState(items = list)
            }
        }
    }

    override val viewModelTag = "AnimeSearchFilterViewModel"

    fun onCellClicked(item: AnimeCell) {
        when (item.state) {
            CellState.NONE -> {
                item.state = CellState.INCLUDE
                selectedCells.add(item)
            }
            CellState.EXCLUDE -> {
                item.state = CellState.INCLUDE
            }
            else -> {
                item.state = CellState.NONE
                selectedCells.remove(item)
            }
        }
        _animeSearchFilterStateFlow.value =
            _animeSearchFilterStateFlow.value.copy(currentCellUpdate = item)

    }

    fun onCellLongClicked(item: AnimeCell) {
        when (item.state) {
            CellState.NONE -> {
                item.state = CellState.EXCLUDE
                selectedCells.add(item)
            }

            CellState.INCLUDE -> {
                item.state = CellState.EXCLUDE
            }

            CellState.EXCLUDE -> {
                item.state = CellState.INCLUDE
            }
        }
        _animeSearchFilterStateFlow.value =
            _animeSearchFilterStateFlow.value.copy(currentCellUpdate = item)
    }

    fun nullifyCurrentCell() {
        if (_animeSearchFilterStateFlow.value.currentCellUpdate == null) {
            return
        }
        _animeSearchFilterStateFlow.value =
            _animeSearchFilterStateFlow.value.copy(currentCellUpdate = null)
    }

    fun onApplyButtonClicked() {
        val animeSearchFilters = _animeSearchFilterStateFlow.value.items

        applyFilter(animeSearchFilters)
    }

    fun onResetButtonClicked() {
        applyFilter(listOf())
    }

    private fun applyFilter(animeSearchFilters: List<AnimeSearchFilter>) {
        onEventReceive(ApplyFilter(filters = animeSearchFilters))
    }

}
