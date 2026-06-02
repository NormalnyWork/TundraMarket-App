package com.normalnywork.tundramarket.data.local.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_PRODUCT_COL_PRODUCT_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_PRODUCT_COL_ID
import com.normalnywork.tundramarket.data.local.db.entities.OrderProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.ProductEntity

data class OrderProductWithProduct(
    @Embedded
    val orderProduct: OrderProductEntity,
    @Relation(
        parentColumn = DB_ORDER_PRODUCT_COL_PRODUCT_ID,
        entityColumn = DB_PRODUCT_COL_ID,
    )
    val product: ProductEntity,
)
