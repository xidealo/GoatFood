package com.bunbeauty.shared.di

import com.bunbeauty.auth.domain.UpdateNotificationUseCase
import com.bunbeauty.core.OpenExternalSource
import com.bunbeauty.core.buildVersionQualifier
import com.bunbeauty.core.domain.link.GetLinkListUseCase
import com.bunbeauty.core.domain.review.AppReviewManager
import com.bunbeauty.core.flavorQualifier
import com.bunbeauty.core.isDebugQualifier
import com.bunbeauty.core.targetName
import com.bunbeauty.shared.DataStoreRepo
import com.bunbeauty.shared.NetworkUtil
import com.bunbeauty.shared.data.DataStoreRepository
import com.bunbeauty.shared.data.DatabaseDriverFactory
import com.bunbeauty.shared.data.UuidGenerator
import com.bunbeauty.shared.data.createFoodDeliveryDatabase
import com.bunbeauty.shared.data.network.logger.IosNetworkErrorLogger
import com.bunbeauty.shared.data.network.logger.NetworkErrorLogger
import com.bunbeauty.shared.domain.feature.notification.NotificationPermissionRequester
import com.bunbeauty.shared.domain.feature.review.NoOpAppReviewManager
import org.koin.dsl.module
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual fun platformModule() =
    module {
        single {
            createFoodDeliveryDatabase(DatabaseDriverFactory().createDriver())
        }
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
        single(flavorQualifier) { targetName }
        single { NetworkUtil() }
        factory { OpenExternalSource() }
        single(isDebugQualifier) { Platform.isDebugBinary }
        single(buildVersionQualifier) {
            platform.Foundation.NSBundle.mainBundle
                .infoDictionary
                ?.get("CFBundleVersion")
                .toString()
                .toLong()
        }
        factory {
            GetLinkListUseCase(
                linkRepo = get(),
            )
        }
        single<NetworkErrorLogger> {
            IosNetworkErrorLogger()
        }
    }
