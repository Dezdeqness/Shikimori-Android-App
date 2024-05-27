package com.dezdeqness.presentation.features.routing

import com.dezdeqness.core.AppLogger
import com.dezdeqness.core.BaseViewModel
import com.dezdeqness.core.CoroutineDispatcherProvider
import com.dezdeqness.domain.repository.AccountRepository
import com.dezdeqness.domain.repository.GenreRepository
import com.dezdeqness.presentation.event.NavigateToMainFlow
import javax.inject.Inject

class RoutingViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val genreRepository: GenreRepository,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    appLogger: AppLogger,
) : BaseViewModel(
    coroutineDispatcherProvider = coroutineDispatcherProvider,
    appLogger = appLogger,
) {
    override val viewModelTag: String = TAG

    init {
        launchOnIo {
            genreRepository.fetchGenres()

            if (accountRepository.isAuthorized()) {
                accountRepository
                    .getProfileRemote()
                    .onSuccess {
                        accountRepository.saveProfileLocal(it)

                        onEventReceive(NavigateToMainFlow)
                    }
                    .onFailure {
                        // TODO: Retry
                    }
            } else {
                onEventReceive(NavigateToMainFlow)
            }
        }
    }

    companion object {
        private const val TAG = "RoutingViewModel"
    }

}
