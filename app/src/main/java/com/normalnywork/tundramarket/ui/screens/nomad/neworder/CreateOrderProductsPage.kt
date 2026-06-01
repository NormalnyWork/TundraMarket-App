package com.normalnywork.tundramarket.ui.screens.nomad.neworder

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.ui.kit.icons.Minus
import com.normalnywork.tundramarket.ui.kit.icons.Plus
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.VolumeFilled
import com.normalnywork.tundramarket.ui.kit.icons.WeightFilled
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import kotlin.math.roundToInt

@Composable
fun ProductsPageContent(
    products: List<Product>,
    selectedQuantities: Map<Int, Int>,
    onIncrement: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        products.forEach { product ->
            ProductCard(
                product = product,
                quantity = selectedQuantities[product.id] ?: 0,
                onIncrement = { onIncrement(product) },
                onDecrement = { onDecrement(product) },
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    val colors = LocalTMColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TMShapes.Medium)
            .background(colors.backgroundCard)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProductTitle(product = product)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProductMeta(
                icon = TMIcons.WeightFilled,
                text = stringResource(
                    R.string.nomad_create_order_product_weight,
                    product.weight.toMetricText(),
                ),
            )
            ProductMeta(
                icon = TMIcons.VolumeFilled,
                text = stringResource(
                    R.string.nomad_create_order_product_volume,
                    product.volume.toMetricText(),
                ),
            )
        }
        QuantitySelector(
            quantity = quantity,
            onIncrement = onIncrement,
            onDecrement = onDecrement,
        )
    }
}

@Composable
private fun ProductTitle(product: Product) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current
    val details = product.details?.let { stringResource(R.string.nomad_create_order_product_details, it) }

    Text(
        text = buildAnnotatedString {
            withStyle(
                typography.label
                    .toSpanStyle()
                    .copy(color = colors.textPrimary),
            ) {
                append(product.name.uppercase())
            }
            details?.let {
                append(" ")
                withStyle(
                    typography.caption
                        .toSpanStyle()
                        .copy(
                            color = colors.textSecondary,
                            baselineShift = BaselineShift(DETAILS_BASELINE_SHIFT),
                        ),
                ) {
                    append(it)
                }
            }
        },
    )
}

@Composable
private fun ProductMeta(
    icon: ImageVector,
    text: String,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            style = typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuantityButton(
            icon = TMIcons.Minus,
            enabled = quantity > 0,
            onClick = onDecrement,
        )
        AnimatedContent(
            targetState = quantity,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                slideInVertically { direction * it } togetherWith slideOutVertically { -direction * it }
            },
            label = "ProductQuantity",
        ) { animatedQuantity ->
            Text(
                text = animatedQuantity.toString(),
                style = typography.label,
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        QuantityButton(
            icon = TMIcons.Plus,
            enabled = true,
            onClick = onIncrement,
        )
    }
}

@Composable
private fun QuantityButton(
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTMColors.current

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = colors.primary,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(colors.primaryVariant)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.primary),
                onClick = onClick,
            )
            .padding(8.dp),
    )
}

private const val DETAILS_BASELINE_SHIFT = 0.15f

private fun Float.toMetricText(): String {
    val normalized = this / 1000f
    return if (normalized % 1f == 0f) {
        normalized.roundToInt().toString()
    } else {
        "%.1f".format(normalized).replace('.', ',')
    }
}
