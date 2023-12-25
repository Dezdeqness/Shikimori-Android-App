package com.dezdeqness.di.modules

import androidx.lifecycle.ViewModel
import com.dezdeqness.di.ViewModelKey
import com.dezdeqness.presentation.features.genericlistscreen.GenericListableViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class GenericListableViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(GenericListableViewModel::class)
    abstract fun bindAnimeSimilarViewModel(viewModel: GenericListableViewModel): ViewModel

}
