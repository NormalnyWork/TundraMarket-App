package com.normalnywork.tundramarket.ui.screens.nomad.neworder

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.DistanceFilled
import com.normalnywork.tundramarket.ui.kit.icons.LatitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.LongitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.Radar
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.tools.toDisplayCoordinate
import com.normalnywork.tundramarket.utils.TMConst
import kotlin.math.roundToInt

@Composable
fun TradingStationPageContent(
    items: List<TradingStationDistance>,
    selectedTradingStation: TradingStation?,
    onTradingStationClick: (TradingStation) -> Unit,
) {
    val nearestReachable = remember(items) { items.firstOrNull { it.reachable }?.tradingStation }
    val reachable = remember(items) { items.filter { it.reachable }}
    val tooFar = remember(items) { items.filterNot { it.reachable } }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (reachable.isEmpty()) {
            TradingStationTooFarPlaceholder()
            HorizontalDivider(
                color = LocalTMColors.current.stroke,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            tooFar.forEach { item ->
                TradingStationCard(
                    item = item,
                    selected = false,
                    recommended = false,
                    onClick = {},
                )
            }
        }

        reachable.forEach { item ->
            TradingStationCard(
                item = item,
                selected = item.tradingStation == selectedTradingStation,
                recommended = item.tradingStation == nearestReachable,
                onClick = { onTradingStationClick(item.tradingStation) },
            )
        }
    }
}

@Composable
private fun TradingStationTooFarPlaceholder() {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = TMIcons.Radar,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(32.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.nomad_create_order_trading_station_too_far_title).uppercase(),
            style = typography.label,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.nomad_create_order_trading_station_too_far_body),
            style = typography.body,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TradingStationCard(
    item: TradingStationDistance,
    selected: Boolean,
    recommended: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current
    val reachable = item.reachable

    val strokeColor by animateColorAsState(if (selected) colors.primary else Color.Transparent)
    val strokeWidth by animateDpAsState(if (selected) 1.dp else 0.dp)
    val checkmarkAlpha by animateFloatAsState(if (selected) 1f else 0f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TMShapes.Medium)
            .background(colors.backgroundCard)
            .border(
                width = strokeWidth,
                color = strokeColor,
                shape = TMShapes.Medium,
            )
            .clickable(enabled = reachable, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = item.tradingStation.name.uppercase(),
                style = typography.label,
                color = colors.textPrimary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StationMeta(
                        icon = TMIcons.DistanceFilled,
                        text = item.distanceText(),
                    )
                    when {
                        recommended ->
                            StationFlag(text = stringResource(R.string.nomad_create_order_trading_station_recommended))
                        !reachable ->
                            StationFlag(text = stringResource(R.string.nomad_create_order_trading_station_too_far_flag))
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StationMeta(
                        icon = TMIcons.LatitudeFilled,
                        text = item.tradingStation.location.latitude.toDisplayCoordinate(),
                    )
                    StationMeta(
                        icon = TMIcons.LongitudeFilled,
                        text = item.tradingStation.location.longitude.toDisplayCoordinate(),
                    )
                }
            }
        }
        Icon(
            imageVector = TMIcons.Checkmark,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier
                .alpha(checkmarkAlpha)
                .size(24.dp),
        )
    }

}

@Composable
private fun StationMeta(
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
private fun StationFlag(text: String) {
    Text(
        text = text,
        style = LocalTMTypography.current.bodySmall,
        color = LocalTMColors.current.primary,
        modifier = Modifier
            .clip(CircleShape)
            .background(LocalTMColors.current.primaryVariant)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

data class TradingStationDistance(
    val tradingStation: TradingStation,
    val distanceKm: Float?,
) {

    val reachable = distanceKm != null && distanceKm <= TMConst.TRADING_STATION_REACH_KM
}

@Composable
private fun TradingStationDistance.distanceText(): String {
    return distanceKm?.roundToInt()?.let {
        stringResource(R.string.nomad_create_order_trading_station_distance_km, it)
    } ?: stringResource(R.string.nomad_create_order_trading_station_distance_unknown)
}
