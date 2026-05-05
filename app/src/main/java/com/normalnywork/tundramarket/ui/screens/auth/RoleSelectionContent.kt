package com.normalnywork.tundramarket.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.BuildConfig
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.icons.IllustrationDroneTent
import com.normalnywork.tundramarket.ui.kit.icons.Shop
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Tent
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.navigation.auth.RoleSelectionComponent
import com.normalnywork.tundramarket.ui.tools.RequireLightSystemBars

@Composable
fun RoleSelectionContent(component: RoleSelectionComponent) {
    val colors = LocalTMColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primary)
            .systemBarsPadding(),
    ) {
        MainContent(
            onNomadSelected = component::onNomadSelected,
            onTradingStationSelected = component::onTradingStationSelected,
        )
        AppVersionPreview()
    }

    RequireLightSystemBars()
}

@Composable
private fun ColumnScope.MainContent(
    onNomadSelected: () -> Unit,
    onTradingStationSelected: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
    ) {
        AppInfo()
        RoleSelectionCard(
            onNomadSelected = onNomadSelected,
            onTradingStationSelected = onTradingStationSelected,
        )
    }
}

@Composable
private fun RoleSelectionCard(
    onNomadSelected: () -> Unit,
    onTradingStationSelected: () -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TMShapes.Big)
            .background(colors.background)
            .padding(8.dp)
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.auth_role_subtitle),
            style = typography.subtitle,
            color = colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        RoleSelection(
            onNomadSelected = onNomadSelected,
            onTradingStationSelected = onTradingStationSelected,
        )
    }
}

@Composable
private fun AppInfo() {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = TMIcons.IllustrationDroneTent,
            contentDescription = null,
            tint = colors.secondary,
            modifier = Modifier.size(width = 128.dp, height = 125.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = typography.title,
                color = colors.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.auth_role_app_desc),
                style = typography.body,
                color = colors.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RoleSelection(
    onNomadSelected: () -> Unit,
    onTradingStationSelected: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        RoleSelectionItem(
            icon = TMIcons.Tent,
            title = stringResource(R.string.auth_role_nomad),
            description = stringResource(R.string.auth_role_nomad_desc),
            onClick = onNomadSelected,
            position = RoleSelectionCardPosition.Top,
        )
        RoleSelectionItem(
            icon = TMIcons.Shop,
            title = stringResource(R.string.auth_role_trading_station),
            description = stringResource(R.string.auth_role_trading_station_desc),
            onClick = onTradingStationSelected,
            position = RoleSelectionCardPosition.Bottom,
        )
    }
}

@Composable
private fun AppVersionPreview() {
    val typography = LocalTMTypography.current
    val colors = LocalTMColors.current

    val versionName = remember { BuildConfig.VERSION_NAME }
    val versionCode = remember { BuildConfig.VERSION_CODE }

    Text(
        text = "v $versionName ($versionCode)",
        style = typography.body,
        color = colors.onPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    )
}

private enum class RoleSelectionCardPosition {
    Top,
    Bottom,
}

@Composable
private fun RoleSelectionItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    position: RoleSelectionCardPosition,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    val shape = remember(position) {
        RoundedCornerShape(
            topStart = if (position == RoleSelectionCardPosition.Top) {
                TMShapes.Tokens.MEDIUM_RADIUS
            } else {
                TMShapes.Tokens.SMALL_RADIUS
            },
            topEnd = if (position == RoleSelectionCardPosition.Top) {
                TMShapes.Tokens.MEDIUM_RADIUS
            } else {
                TMShapes.Tokens.SMALL_RADIUS
            },
            bottomStart = if (position == RoleSelectionCardPosition.Bottom) {
                TMShapes.Tokens.MEDIUM_RADIUS
            } else {
                TMShapes.Tokens.SMALL_RADIUS
            },
            bottomEnd = if (position == RoleSelectionCardPosition.Bottom) {
                TMShapes.Tokens.MEDIUM_RADIUS
            } else {
                TMShapes.Tokens.SMALL_RADIUS
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = colors.stroke,
                shape = shape,
            )
            .clip(shape)
            .background(color = colors.backgroundCard)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(24.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title.uppercase(),
                style = typography.button,
                color = colors.textPrimary,
            )
            Text(
                text = description,
                style = typography.body,
                color = colors.textSecondary,
            )
        }
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    RoleSelectionContent(component = MockRoleSelectionComponent())
}
