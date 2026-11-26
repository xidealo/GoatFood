package com.bunbeauty.shared.di.usecase

import com.bunbeauty.core.domain.order.CreateOrderUseCase
import com.bunbeauty.core.domain.order.GetExtendedCommentUseCase
import com.bunbeauty.core.domain.order.GetLastDeliveredOrderUseCase
import com.bunbeauty.core.domain.order.GetLastOrderUseCase
import com.bunbeauty.core.domain.order.GetWithoutUtensilsUseCase
import com.bunbeauty.core.domain.order.ObserveLastOrderUseCase
import com.bunbeauty.core.domain.order.ObserveOrderListUseCase
import com.bunbeauty.core.domain.order.ObserveOrderUseCase
import com.bunbeauty.core.domain.order.SaveWithoutUtensilsUseCase
import com.bunbeauty.core.domain.order.StopObserveOrdersUseCase
import com.bunbeauty.core.domain.order.TakeInProgressLightOrderUseCase
import com.bunbeauty.core.domain.order.TakeInProgressLightOrderUseCaseImpl
import com.bunbeauty.core.domain.orderavailable.IsOrderAvailableUseCase
import org.koin.dsl.module

internal fun orderUseCaseModule() =
    module {
        factory {
            CreateOrderUseCase(
                cartProductRepo = get(),
                dateTimeUtil = get(),
                orderRepo = get(),
                settingsRepo = get(),
            )
        }
        factory<TakeInProgressLightOrderUseCase> {
            TakeInProgressLightOrderUseCaseImpl()
        }
        factory {
            ObserveLastOrderUseCase(
                orderRepo = get(),
                lightOrderMapper = get(),
                takeInProgressLightOrderUseCase = get(),
            )
        }
        factory {
            StopObserveOrdersUseCase(
                orderRepo = get(),
            )
        }
        factory {
            ObserveOrderListUseCase(
                orderRepo = get(),
            )
        }
        factory {
            ObserveOrderUseCase(
                orderRepo = get(),
            )
        }
        factory {
            GetLastOrderUseCase(
                orderRepo = get(),
                takeInProgressLightOrderUseCase = get(),
            )
        }
        factory {
            GetLastDeliveredOrderUseCase(
                orderRepo = get(),
            )
        }
        factory {
            IsOrderAvailableUseCase(
                companyRepo = get(),
            )
        }
        factory {
            GetExtendedCommentUseCase()
        }
        factory {
            GetWithoutUtensilsUseCase(
                createOrderSettingsRepo = get(),
            )
        }
        factory {
            SaveWithoutUtensilsUseCase(
                createOrderSettingsRepo = get(),
            )
        }
    }
