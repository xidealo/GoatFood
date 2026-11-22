package com.bunbeauty.shared.di

import com.bunbeauty.auth.domain.UpdateNotificationUseCase
import com.bunbeauty.core.OpenExternalSource
import com.bunbeauty.core.buildVersionQualifier
import com.bunbeauty.core.domain.link.GetLinkListUseCase
import com.bunbeauty.core.domain.review.AppReviewManager
import com.bunbeauty.core.flavorQualifier
import com.bunbeauty.core.isDebugQualifier
import com.bunbeauty.shared.DataStoreRepo
import com.bunbeauty.shared.NetworkUtil
import com.bunbeauty.shared.data.DataStoreRepository
import com.bunbeauty.shared.data.UuidGenerator
import com.bunbeauty.shared.data.network.logger.JsNetworkErrorLogger
import com.bunbeauty.shared.data.network.logger.NetworkErrorLogger
import com.bunbeauty.shared.domain.feature.notification.NotificationPermissionRequester
import com.bunbeauty.shared.domain.feature.review.NoOpAppReviewManager
import com.bunbeauty.shared.resolveWebFlavor
import org.koin.dsl.module

actual fun platformModule() =
    module {
        single<DataStoreRepo> {
            DataStoreRepository()
        }
        single {
            UuidGenerator()
        }
        factory {
            UpdateNotificationUseCase(
                userRepository = get(),
            )
        }
        single { NotificationPermissionRequester() }
        single<AppReviewManager> { NoOpAppReviewManager() }
        single(flavorQualifier) { resolveWebFlavor() }
        single { NetworkUtil() }
        single<NetworkErrorLogger> { JsNetworkErrorLogger() }
        factory { OpenExternalSource() }
        single(isDebugQualifier) { false }
        single(buildVersionQualifier) { 312L }
        factory {
            GetLinkListUseCase(
                linkRepo = get(),
            )
        }
    }
