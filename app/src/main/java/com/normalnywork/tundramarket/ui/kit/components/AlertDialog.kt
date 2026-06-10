package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMShapes

@Composable
fun TMAlertDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    confirmAction: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title.uppercase(),
                style = typography.label,
                color = colors.textPrimary,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = body,
                    style = typography.bodySmall,
                    color = colors.textSecondary,
                )
                content()
            }
        },
        dismissButton = {
            TMAlertDialogTextButton(
                text = stringResource(R.string.trading_station_order_dialog_cancel_action),
                onClick = onDismiss,
            )
        },
        confirmButton = {
            confirmAction()
        },
        containerColor = colors.background,
    )
}

@Composable
fun TMAlertDialogTextButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val colors = LocalTMColors.current
    val contentColor by animateColorAsState(
        targetValue = if (enabled) colors.primary else colors.primary.copy(alpha = 0.45f),
        label = "DialogTextButtonColor",
    )

    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(TMShapes.Small)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.primary),
                onClick = onClick,
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.uppercase(),
            style = LocalTMTypography.current.buttonSmall,
            color = contentColor,
        )
    }
}