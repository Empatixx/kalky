package cz.krokviak.kalky.core.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DeepLinkBus {
    private val _foodDetail = MutableSharedFlow<Long>(extraBufferCapacity = 4)
    val foodDetail: SharedFlow<Long> = _foodDetail.asSharedFlow()

    fun openFoodDetail(id: Long) {
        _foodDetail.tryEmit(id)
    }
}
