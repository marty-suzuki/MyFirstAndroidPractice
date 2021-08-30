package com.martysuzuki.uilogicinterface

import kotlinx.coroutines.CoroutineScope

interface UiLogicFactory<Logic: UiLogic, Dependency> {
    fun create(
        viewModelScope: CoroutineScope,
        dependency: Dependency
    ): Logic
}