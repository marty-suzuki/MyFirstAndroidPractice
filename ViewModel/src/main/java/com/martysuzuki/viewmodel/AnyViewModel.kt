package com.martysuzuki.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martysuzuki.router.Router
import com.martysuzuki.uilogicinterface.UiLogic
import com.martysuzuki.uilogicinterface.UiLogicFactory

abstract class AnyViewModel<out Logic : UiLogic, out R : Router, Dependency>(
    uiLogicFactory: UiLogicFactory<Logic, Dependency>,
    val router: R,
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