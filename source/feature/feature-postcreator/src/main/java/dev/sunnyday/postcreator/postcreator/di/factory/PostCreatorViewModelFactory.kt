package dev.sunnyday.postcreator.postcreator.di.factory

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

internal class PostCreatorViewModelFactory @AssistedInject constructor(
    @Assisted savedStateRegistryOwner: SavedStateRegistryOwner,
    private val factories: MutableMap<Class<out ViewModel>, ViewModelAssistedFactory<out ViewModel>>
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val factory = factories[modelClass]
            ?: throw IllegalStateException("Unexpected viewModel class: $modelClass")

        @Suppress("UNCHECKED_CAST")
        return factory.create(handle) as T
    }

    @AssistedInject.Factory
    interface Factory {

        fun create(savedStateRegistryOwner: SavedStateRegistryOwner): PostCreatorViewModelFactory

    }

}