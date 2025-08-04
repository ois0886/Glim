package com.ssafy.glim.core.data.authmanager

import androidx.annotation.StringRes
import com.ssafy.glim.R

sealed class LogoutReason(@StringRes val messageRes: Int) {
    object MaxRetryExceeded : LogoutReason(R.string.logout_network_error)
    object TokenNotFound : LogoutReason(R.string.logout_auth_info_missing)
    object RefreshTokenExpired : LogoutReason(R.string.logout_session_expired)
    object UnknownError : LogoutReason(R.string.logout_unknown_error)
    object UserLogout : LogoutReason(R.string.logout_user_logout)
    object  UserWithDrawl : LogoutReason(R.string.logout_user_withdrawl)
}
