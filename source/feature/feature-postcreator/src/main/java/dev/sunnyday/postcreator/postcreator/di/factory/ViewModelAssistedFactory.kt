package dev.sunnyday.postcreator.postcreator.di.factory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

internal interface ViewModelAssistedFactory<T : ViewModel> {

    fun create(savedStateHandle: SavedStateHandle): T

}