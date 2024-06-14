package com.sanryoo.shopping.feature.presentation.using.my_shop_purchases

import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product

sealed class MSPUiEvent {
    object BackToMyShopScreen: MSPUiEvent()
}
