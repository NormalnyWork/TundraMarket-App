package com.normalnywork.tundramarket.ui.screens.nomad.neworder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.ui.kit.icons.Comment
import com.normalnywork.tundramarket.ui.kit.icons.Latitude
import com.normalnywork.tundramarket.ui.kit.icons.Location
import com.normalnywork.tundramarket.ui.kit.icons.Longitude
import com.normalnywork.tundramarket.ui.kit.icons.Products
import com.normalnywork.tundramarket.ui.kit.icons.Shop
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun OverviewPageContent(
    location: Location?,
    tradingStation: TradingStation?,
    tradingStationDistanceKm: Float?,
    products: List<Product>,
    selectedQuantities: Map<Int, Int>,
    comment: TextFieldState,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OverviewSection(
            title = stringResource(R.string.nomad_create_order_overview_location_section),
            icon = TMIcons.Location,
        ) {
            CoordinateRow(
                icon = TMIcons.Latitude,
                text = location?.latitude.toDisplayCoordinate(
                    positiveHemisphere = 'N',
                    negativeHemisphere = 'S',
                ),
            )
            CoordinateRow(
                icon = TMIcons.Longitude,
                text = location?.longitude.toDisplayCoordinate(
                    positiveHemisphere = 'E',
                    negativeHemisphere = 'W',
                ),
            )
        }
        OverviewSection(
            title = stringResource(R.string.nomad_create_order_overview_trading_station_section),
            icon = TMIcons.Shop,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OverviewText(
                    text = tradingStation?.name.orEmpty(),
                    modifier = Modifier.weight(1f, fill = false),
                )
                OverviewText(
                    text = tradingStationDistanceKm.distanceText(),
                    colorSecondary = true,
                )
            }
        }
        OverviewSection(
            title = stringResource(R.string.nomad_create_order_overview_products_section),
            icon = TMIcons.Products,
        ) {
            products
                .filter { (selectedQuantities[it.id] ?: 0) > 0 }
                .forEach { product ->
                    ProductRow(
                        product = product,
                        quantity = selectedQuantities[product.id] ?: 0,
                    )
                }
        }
        OverviewSection(
            title = stringResource(R.string.nomad_create_order_overview_comment_section),
            icon = TMIcons.Comment,
        ) {
            val commentText = comment.text.toString().trim()

            OverviewText(
                text = commentText.ifEmpty { stringResource(R.string.nomad_create_order_overview_no_comment) },
                colorSecondary = commentText.isEmpty(),
            )
        }
    }
}

@Composable
private fun OverviewSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val colors = LocalTMColors.current
        val typography = LocalTMTypography.current

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = title,
                style = typography.subtitle,
                color = colors.textPrimary,
            )
        }
        OverviewCard(content = content)
    }
}

@Composable
private fun OverviewCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TMShapes.Medium)
            .background(LocalTMColors.current.backgroundCard)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun CoordinateRow(
    icon: ImageVector,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocalTMColors.current.textSecondary,
            modifier = Modifier.size(18.dp),
        )
        OverviewText(text = text)
    }
}

@Composable
private fun ProductRow(
    product: Product,
    quantity: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OverviewText(
            text = product.name,
            modifier = Modifier.weight(1f),
        )
        OverviewText(
            text = stringResource(R.string.nomad_create_order_overview_product_quantity, quantity),
            colorSecondary = true,
        )
    }
}

@Composable
private fun OverviewText(
    text: String,
    modifier: Modifier = Modifier,
    colorSecondary: Boolean = false,
) {
    val colors = LocalTMColors.current

    Text(
        text = text,
        style = LocalTMTypography.current.body,
        color = if (colorSecondary) colors.textSecondary else colors.textPrimary,
        modifier = modifier,
    )
}

@Composable
private fun Float?.distanceText(): String {
    return this?.roundToInt()?.let {
        stringResource(R.string.nomad_create_order_trading_station_distance_km, it)
    } ?: stringResource(R.string.nomad_create_order_trading_station_distance_unknown)
}

private fun Float?.toDisplayCoordinate(
    positiveHemisphere: Char,
    negativeHemisphere: Char,
): String {
    if (this == null) return ""

    val absolute = abs(this)
    val degrees = absolute.toInt()
    val minutesFloat = (absolute - degrees) * 60
    val minutes = minutesFloat.toInt()
    val seconds = ((minutesFloat - minutes) * 60).roundToInt()
    val hemisphere = if (this >= 0f) positiveHemisphere else negativeHemisphere

    return "$degrees° $minutes' $seconds\" $hemisphere"
}
