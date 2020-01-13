package dev.sunnyday.postcreator.postcreator.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

internal class PostCreatorViewModelFactory @Inject constructor(
    private val viewModelProvider: Provider<PostCreatorOperationsViewModel>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when(modelClass) {
        PostCreatorOperationsViewModel::class.java -> viewModelProvider.get() as T
        else -> error("Unexpected class: $modelClass")
    }

}