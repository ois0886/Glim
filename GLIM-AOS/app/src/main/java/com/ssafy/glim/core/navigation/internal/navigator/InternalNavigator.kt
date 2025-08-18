package com.ssafy.glim.core.navigation.internal.navigator

import kotlinx.coroutines.channels.Channel

internal interface InternalNavigator {
    val channel: Channel<InternalRoute>
}
