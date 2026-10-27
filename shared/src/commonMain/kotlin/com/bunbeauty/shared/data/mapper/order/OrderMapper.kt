package com.bunbeauty.shared.data.mapper.order

import com.bunbeauty.core.domain.util.DateTimeUtil
import com.bunbeauty.core.model.order.CreatedOrder
import com.bunbeauty.core.model.order.LightOrder
import com.bunbeauty.core.model.order.Order
import com.bunbeauty.core.model.order.OrderAddress
import com.bunbeauty.core.model.order.OrderCode
import com.bunbeauty.core.model.order.OrderStatus
import com.bunbeauty.core.model.payment_method.PaymentMethodName
import com.bunbeauty.shared.data.mapper.order_product.IOrderProductMapper
import com.bunbeauty.shared.data.network.model.order.get.LightOrderServer
import com.bunbeauty.shared.data.network.model.order.get.OrderServer
import com.bunbeauty.shared.data.network.model.order.post.OrderAddressPostServer
import com.bunbeauty.shared.data.network.model.order.post.OrderV6PostServer
import com.bunbeauty.shared.db.LightOrderEntity
import com.bunbeauty.shared.db.OrderEntity
import com.bunbeauty.shared.db.OrderWithProductEntity

class OrderMapper(
    private val orderProductMapper: IOrderProductMapper,
    private val dateTimeUtil: DateTimeUtil,
) : IOrderMapper {
    override fun toLightOrder(orderEntity: OrderEntity): LightOrder =
        LightOrder(
            uuid = orderEntity.uuid,
            status = OrderStatus.valueOf(orderEntity.status),
            code = orderEntity.code,
            dateTime = dateTimeUtil.toDateTime(orderEntity.time, orderEntity.timeZone),
        )

    override fun toLightOrder(orderServer: OrderServer): LightOrder =
        LightOrder(
            uuid = orderServer.uuid,
            status = OrderStatus.valueOf(orderServer.status),
            code = orderServer.code,
            dateTime = dateTimeUtil.toDateTime(orderServer.time, orderServer.timeZone),
        )

    override fun toLightOrder(lightOrderServer: LightOrderServer): LightOrder =
        LightOrder(
            uuid = lightOrderServer.uuid,
            code = lightOrderServer.code,
            status = OrderStatus.valueOf(lightOrderServer.status),
            dateTime =
                dateTimeUtil.toDateTime(
                    millis = lightOrderServer.time,
                    timeZone = lightOrderServer.timeZone,
                ),
        )

    override fun toLightOrder(lightOrderEntity: LightOrderEntity): LightOrder =
        LightOrder(
            uuid = lightOrderEntity.uuid,
            code = lightOrderEntity.code,
            status = OrderStatus.valueOf(lightOrderEntity.status),
            dateTime =
                dateTimeUtil.toDateTime(
                    millis = lightOrderEntity.time,
                    timeZone = lightOrderEntity.timeZone,
                ),
        )

    override fun toLightOrderEntity(lightOrderServer: LightOrderServer): LightOrderEntity =
        LightOrderEntity(
            uuid = lightOrderServer.uuid,
            code = lightOrderServer.code,
            status = lightOrderServer.status,
            time = lightOrderServer.time,
            timeZone = lightOrderServer.timeZone,
        )

    override fun toOrderCode(orderServer: OrderServer): OrderCode =
        OrderCode(
            code = orderServer.code,
        )

    override fun toOrder(orderWithProductEntityList: List<OrderWithProductEntity>): Order? =
        orderWithProductEntityList
            .groupBy { orderWithProductEntity ->
                orderWithProductEntity.uuid
            }.map { (_, groupedOrderWithProductEntityList) ->
                val firstOrderWithProductEntity =
                    groupedOrderWithProductEntityList.first()

                Order(
                    uuid = firstOrderWithProductEntity.uuid,
                    code = firstOrderWithProductEntity.code,
                    status = OrderStatus.valueOf(firstOrderWithProductEntity.status),
                    dateTime =
                        dateTimeUtil.toDateTime(
                            millis = firstOrderWithProductEntity.time,
                            timeZone = firstOrderWithProductEntity.timeZone,
                        ),
                    isDelivery = firstOrderWithProductEntity.isDelivery,
                    deferredTime =
                        firstOrderWithProductEntity.deferredTime?.let { millis ->
                            dateTimeUtil.toTime(millis, firstOrderWithProductEntity.timeZone)
                        },
                    address =
                        OrderAddress(
                            description = firstOrderWithProductEntity.address,
                            street = firstOrderWithProductEntity.addressStreet,
                            house = firstOrderWithProductEntity.addressHouse,
                            flat = firstOrderWithProductEntity.addressFlat,
                            entrance = firstOrderWithProductEntity.addressEntrance,
                            floor = firstOrderWithProductEntity.addressFloor,
                            comment = firstOrderWithProductEntity.addressComment,
                        ),
                    comment = firstOrderWithProductEntity.comment,
                    deliveryCost = firstOrderWithProductEntity.deliveryCost,
                    orderProductList =
                        orderProductMapper.toOrderProduct(
                            groupedOrderWithProductEntityList,
                        ),
                    paymentMethod =
                        PaymentMethodName
                            .values()
                            .firstOrNull { it.name == firstOrderWithProductEntity.paymentMethod },
                    oldTotalCost = firstOrderWithProductEntity.oldTotalCost,
                    newTotalCost = firstOrderWithProductEntity.newTotalCost,
                    percentDiscount = firstOrderWithProductEntity.percentDiscount,
                )
            }.firstOrNull()

    override fun toOrder(orderServer: OrderServer): Order =
        Order(
            uuid = orderServer.uuid,
            code = orderServer.code,
            status = OrderStatus.valueOf(orderServer.status),
            dateTime = dateTimeUtil.toDateTime(orderServer.time, orderServer.timeZone),
            isDelivery = orderServer.isDelivery,
            deferredTime =
                orderServer.deferredTime?.let { millis ->
                    dateTimeUtil.toTime(millis, orderServer.timeZone)
                },
            address =
                OrderAddress(
                    description = orderServer.address.description,
                    street = orderServer.address.street,
                    house = orderServer.address.house,
                    flat = orderServer.address.flat,
                    entrance = orderServer.address.entrance,
                    floor = orderServer.address.floor,
                    comment = orderServer.address.comment,
                ),
            comment = orderServer.comment,
            deliveryCost = orderServer.deliveryCost,
            orderProductList = orderServer.oderProductList.map(orderProductMapper::toOrderProduct),
            paymentMethod =
                PaymentMethodName.entries
                    .firstOrNull { it.name == orderServer.paymentMethod },
            oldTotalCost = orderServer.oldTotalCost,
            newTotalCost = orderServer.newTotalCost,
            percentDiscount = orderServer.percentDiscount,
        )

    override fun toOrderEntity(orderServer: OrderServer): OrderEntity =
        OrderEntity(
            uuid = orderServer.uuid,
            status = orderServer.status,
            isDelivery = orderServer.isDelivery,
            time = orderServer.time,
            timeZone = orderServer.timeZone,
            code = orderServer.code,
            address = orderServer.address.description ?: "",
            addressStreet = orderServer.address.street,
            addressHouse = orderServer.address.house,
            addressFlat = orderServer.address.flat,
            addressEntrance = orderServer.address.entrance,
            addressFloor = orderServer.address.floor,
            addressComment = orderServer.address.comment,
            comment = orderServer.comment,
            deliveryCost = orderServer.deliveryCost,
            deferredTime = orderServer.deferredTime,
            userUuid = orderServer.clientUserUuid,
            paymentMethod = orderServer.paymentMethod,
            oldTotalCost = orderServer.oldTotalCost,
            newTotalCost = orderServer.newTotalCost,
            percentDiscount = orderServer.percentDiscount,
        )

    override fun toOrderPostServer(createdOrder: CreatedOrder): OrderV6PostServer =
        OrderV6PostServer(
            isDelivery = createdOrder.isDelivery,
            address =
                OrderAddressPostServer(
                    uuid = createdOrder.address.uuid,
                    description = createdOrder.address.description,
                    street = createdOrder.address.street,
                    house = createdOrder.address.house,
                    flat = createdOrder.address.flat,
                    entrance = createdOrder.address.entrance,
                    floor = createdOrder.address.floor,
                    comment = createdOrder.address.comment,
                ),
            comment = createdOrder.comment,
            deferredTime = createdOrder.deferredTime,
            orderProducts = createdOrder.orderProducts.map(orderProductMapper::toPostServerModel),
            paymentMethod = createdOrder.paymentMethod,
            pickupServing = createdOrder.pickupServing?.name,
        )
}
