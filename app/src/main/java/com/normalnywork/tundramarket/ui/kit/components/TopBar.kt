package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.icons.ArrowBack
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TundraMarketTheme

@Composable
fun TMTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    showDivider: Boolean = false,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TopBarTokens.Height)
                .background(LocalTMColors.current.background)
                .padding(horizontal = TopBarTokens.PaddingHorizontal),
            horizontalArrangement = Arrangement.spacedBy(TopBarTokens.Spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                TMTopBarBackButton(onBack = onBack)
            }
            Text(
                text = title.uppercase(),
                style = LocalTMTypography.current.title,
                color = LocalTMColors.current.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        TMTopBarDivider(shown = showDivider)
    }
}

@Composable
private fun TMTopBarBackButton(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .size(TopBarTokens.IconContainerSize)
            .clip(CircleShape)
            .background(LocalTMColors.current.backgroundCard)
            .clickable(
                role = Role.Button,
                onClick = onBack,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = TMIcons.ArrowBack,
            contentDescription = stringResource(R.string.common_cd_back_action),
            tint = LocalTMColors.current.primary,
            modifier = Modifier.size(TopBarTokens.IconSize),
        )
    }
}

@Composable
private fun TMTopBarDivider(shown: Boolean) {
    val color = if (shown) LocalTMColors.current.stroke else Color.Transparent
    val colorState by animateColorAsState(color)

    HorizontalDivider(color = colorState)
}

private object TopBarTokens {

    val Height = 64.dp
    val IconSize = 24.dp
    val IconContainerSize = 40.dp
    val PaddingHorizontal = 16.dp
    val Spacing = 16.dp
}

@Preview
@Composable
private fun Preview() {
    TundraMarketTheme {
        TMTopBar(
            title = "Top bar",
            onBack = {},
        )
    }
}