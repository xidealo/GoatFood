package com.bunbeauty.shared.domain.feature.notification

import com.bunbeauty.core.domain.repo.UserRepo

class UpdateNotificationPermissionUseCase(
    private val userRepository: UserRepo,
) {
    suspend operator fun invoke(enabled: Boolean) {
        userRepository.updateNotificationPermissionSuspend(enabled)
    }
}
