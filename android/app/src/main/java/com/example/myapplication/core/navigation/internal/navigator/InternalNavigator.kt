package com.example.myapplication.core.navigation.internal.navigator

import kotlinx.coroutines.channels.Channel

internal interface InternalNavigator {
    val channel: Channel<InternalRoute>
}
