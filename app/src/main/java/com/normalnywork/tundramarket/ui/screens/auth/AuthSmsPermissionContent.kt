package com.normalnywork.tundramarket.ui.screens.auth

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.components.InfoCard
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMTimeline
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.Comment
import com.normalnywork.tundramarket.ui.kit.icons.Internet
import com.normalnywork.tundramarket.ui.kit.icons.Shield
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.navigation.auth.AuthSmsPermissionComponent

@Composable
fun AuthSmsPermissionContent(component: AuthSmsPermissionComponent) {
    val context = LocalContext.current
    val colors = LocalTMColors.current

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        component.onPermissionResult(isGranted)
    }

    fun requestSmsPermission() {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECEIVE_SMS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            component.onPermissionResult(isGranted = true)
        } else {
            smsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
        }
    }

    LaunchedEffect(Unit) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECEIVE_SMS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            component.onPermissionResult(isGranted = true)
        }
    }

    Scaffold(
        topBar = {
            TMTopBar(title = stringResource(R.string.auth_init_title))
        },
        bottomBar = {
            TMButtonPrimary(
                text = stringResource(R.string.auth_sms_permission_action),
                onClick = ::requestSmsPermission,
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth(),
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            InfoCard(
                title = stringResource(R.string.auth_sms_permission_info_title),
                body = stringResource(R.string.auth_sms_permission_info_body),
                icon = TMIcons.Comment,
            )
            HowItWorksCard()
            SmsPrivacyCard()
        }
    }
}

@Composable
private fun HowItWorksCard() {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current
    val items = listOf(
        HowItWorksItem(
            title = stringResource(R.string.auth_sms_permission_how_step_send_title),
            body = stringResource(R.string.auth_sms_permission_how_step_send_body),
            icon = TMIcons.Internet,
        ),
        HowItWorksItem(
            title = stringResource(R.string.auth_sms_permission_how_step_receive_title),
            body = stringResource(R.string.auth_sms_permission_how_step_receive_body),
            icon = TMIcons.Comment,
        ),
        HowItWorksItem(
            title = stringResource(R.string.auth_sms_permission_how_step_system_title),
            body = stringResource(R.string.auth_sms_permission_how_step_system_body),
            icon = TMIcons.Checkmark,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = TMShapes.Medium,
                color = colors.stroke,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.auth_sms_permission_how_title).uppercase(),
            style = typography.label,
            color = colors.textPrimary,
        )
        TMTimeline(
            itemsCount = items.size,
            itemSpacing = 10.dp,
            marker = { index ->
                HowItWorksMarker(icon = items[index].icon)
            },
            content = { index ->
                HowItWorksText(item = items[index])
            },
        )
    }
}

@Composable
private fun HowItWorksMarker(icon: ImageVector) {
    val colors = LocalTMColors.current

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = colors.primaryVariant,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun HowItWorksText(item: HowItWorksItem) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = item.title,
            style = typography.subtitle,
            color = colors.textPrimary,
        )
        Text(
            text = item.body,
            style = typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun SmsPrivacyCard() {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = TMShapes.Medium,
                color = colors.stroke,
            )
            .background(
                color = colors.backgroundCard,
                shape = TMShapes.Medium,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = TMIcons.Shield,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = stringResource(R.string.auth_sms_permission_privacy_body),
            style = typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

private data class HowItWorksItem(
    val title: String,
    val body: String,
    val icon: ImageVector,
)

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    AuthSmsPermissionContent(
        component = MockAuthSmsPermissionComponent(),
    )
}
