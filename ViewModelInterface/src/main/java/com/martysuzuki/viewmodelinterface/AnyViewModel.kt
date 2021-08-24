package com.martysuzuki.viewmodelinterface

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

abstract class AnyViewModel<out Logic : UiLogic, Dependency>(
    uiLogicFactory: UiLogicFactory<Logic, Dependency>,
    dependency: Dependency
) : ViewModel() {

    val uiLogic = uiLogicFactory.create(
        viewModelScope = viewModelScope,
        dependency = dependency
    )

    override fun onCleared() {
        super.onCleared()
        uiLogic.onCleared()
    }
}