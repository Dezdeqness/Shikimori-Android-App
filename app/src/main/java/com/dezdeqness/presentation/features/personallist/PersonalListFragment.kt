package com.dezdeqness.presentation.features.personallist

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dezdeqness.core.BaseComposeFragment
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.di.AppComponent
import com.dezdeqness.presentation.action.Action
import com.dezdeqness.presentation.event.AnimeDetails
import com.dezdeqness.presentation.event.ConsumableEvent
import com.dezdeqness.presentation.event.Event
import com.dezdeqness.presentation.event.EventConsumer
import com.dezdeqness.presentation.event.NavigateToEditRate
import com.dezdeqness.presentation.features.userrate.UserRateActivity
import kotlinx.coroutines.launch


class PersonalListFragment : BaseComposeFragment() {

    private val eventConsumer: EventConsumer by lazy {
        EventConsumer(
            fragment = this,
            context = this.requireContext(),
        )
    }

    private val editRateResult = registerForActivityResult(UserRateActivity.UserRate()) { userRate ->
        viewModel.onUserRateChanged(userRate)
    }

    private val viewModel: PersonalListViewModel by viewModels(factoryProducer = { viewModelFactory })

    @Composable
    override fun FragmentContent() {
        AppTheme {
            val state by viewModel.personalListStateFlow.collectAsState()

            PersonalListPage(
                state = state,
                actions = object : PersonalListActions {
                    override fun onPullDownRefreshed() {
                        viewModel.onPullDownRefreshed()
                    }

                    override fun onScrolled() {
                        viewModel.onScrolled()
                    }

                    override fun onInitialLoad() {
                        viewModel.onInitialLoad()
                    }

                    override fun onActionReceived(action: Action) {
                        viewModel.onActionReceive(action)
                    }

                    override fun onQueryChanged(query: String) {
                        viewModel.onQueryChanged(query)
                    }

                    override fun onOrderChanged(order: String) {
                        viewModel.onSortChanged(order)
                    }

                    override fun onRibbonItemSelected(id: String) {
                        viewModel.onRibbonItemSelected(id)
                    }

                }
            )
        }
    }

    override fun setupScreenComponent(component: AppComponent) =
        component
            .personalListComponent()
            .create()
            .inject(this)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    onEvent(event)
                }
            }
        }
    }

    private fun onEvent(event: Event) {
        when (event) {
            is NavigateToEditRate -> {
                editRateResult.launch(
                    UserRateActivity.UserRateParams(
                        userRateId = event.rateId,
                        title = event.title,
                        overallEpisodes = event.overallEpisodes,
                    ),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity()),
                )
            }

            is AnimeDetails -> {
                findNavController().navigate(
                        PersonalListFragmentDirections.navigateToAnimeDetails(event.animeId)
                    )
            }

            is ConsumableEvent -> {
                eventConsumer.consume(event)
            }

            else -> {}
        }
    }
}
