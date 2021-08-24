package com.martysuzuki.viewmodelinterface

import kotlinx.coroutines.CoroutineScope

interface UiLogicFactory<Logic: UiLogic, Dependency> {
    fun create(
        viewModelScope: CoroutineScope,
        dependency: Dependency
    ): Logic
}