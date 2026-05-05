package com.normalnywork.tundramarket.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.ui.kit.components.ExtendedInfoCard
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMButtonTertiary
import com.normalnywork.tundramarket.ui.kit.components.TMTextField
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.icons.Phone
import com.normalnywork.tundramarket.ui.kit.icons.Shop
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Tent
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.navigation.auth.AuthUserInfoComponent

@Composable
fun AuthUserInfoContent(component: AuthUserInfoComponent) {
    val colors = LocalTMColors.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            AuthTopBar(goBack = component::goBack)
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddings)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                RoleOverview(
                    role = component.role,
                    changeRole = component::goBack,
                )
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PhoneNumberInput(state = component.phoneNumber)

                    if (component.role == UserRole.TradingStation) {
                        val available by component.tradingStations.collectAsState()
                        val selected by component.selectedStation.collectAsState()

                        TradingStationSelection(
                            available = available,
                            selected = selected,
                            select = component::selectTradingStation,
                        )
                    }
                }
            }
        },
        bottomBar = {
            TMButtonPrimary(
                text = stringResource(R.string.auth_info_auth_action),
                onClick = {
                    keyboardController?.hide()
                    component.authorize()
                },
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .fillMaxWidth(),
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
    )
}

@Composable
private fun RoleOverview(
    role: UserRole,
    changeRole: () -> Unit,
) {
    ExtendedInfoCard(
        caption = stringResource(R.string.auth_info_role_title),
        title = stringResource(
            when (role) {
                UserRole.Nomad -> R.string.auth_role_nomad
                UserRole.TradingStation -> R.string.auth_role_trading_station
            },
        ),
        body = stringResource(
            when (role) {
                UserRole.Nomad -> R.string.auth_role_nomad_desc
                UserRole.TradingStation -> R.string.auth_role_trading_station_desc
            },
        ),
        icon = when (role) {
            UserRole.Nomad -> TMIcons.Tent
            UserRole.TradingStation -> TMIcons.Shop
        },
        action = {
            TMButtonTertiary(
                text = stringResource(R.string.auth_info_role_change_action),
                onClick = changeRole,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}

@Composable
private fun PhoneNumberInput(state: TextFieldState) {
    TMTextField(
        state = state,
        icon = TMIcons.Phone,
        label = stringResource(R.string.auth_info_phone_label),
        placeholder = "+7 (999) 123-45-67",
        inputTransformation = InputTransformation {
            if (length == 0) return@InputTransformation

            when (charAt(0)) {
                '7', '8' -> replace(0, 1, "7")
                '9' -> replace(0, 1, "79")
                else -> delete(0, 1)
            }
            if (length > 1 && charAt(1) != '9') delete(1, 2)

            if (length > 11) delete(11, length)
        },
        outputTransformation = OutputTransformation {
            var currentOffset = 0

            if (length != 0) insert(0, "+".also { currentOffset += it.length })

            if (length > 1 + currentOffset)
                insert(1 + currentOffset, " (".also { currentOffset += it.length })
            if (length > 4 + currentOffset)
                insert(4 + currentOffset, ") ".also { currentOffset += it.length })

            if (length > 7 + currentOffset)
                insert(7 + currentOffset, "-".also { currentOffset += it.length })
            if (length > 9 + currentOffset)
                insert(9 + currentOffset, "-")
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    )
}

@Composable
private fun TradingStationSelection(
    available: List<TradingStation>,
    selected: TradingStation?,
    select: (TradingStation) -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = TMIcons.Shop,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(R.string.auth_info_choose_trading_station),
                style = typography.subtitle,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        AnimatedContent(
            targetState = available,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colors.backgroundCard,
                    shape = TMShapes.Medium,
                )
                .border(
                    width = 1.dp,
                    color = colors.stroke,
                    shape = TMShapes.Medium,
                ),
        ) { stations ->
            if (stations.isEmpty()) {
                LoadingIndicator(
                    color = colors.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(28.dp),
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    stations.forEachIndexed { index, station ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable(role = Role.RadioButton) {
                                    select(station)
                                }
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = station.name,
                                style = typography.body,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            RadioButton(
                                selected = selected?.id == station.id,
                                onClick = { select(station) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colors.primary,
                                    unselectedColor = colors.textSecondary,
                                )
                            )
                        }

                        if (index < stations.size - 1)
                            HorizontalDivider(
                                color = colors.stroke,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthTopBar(goBack: () -> Unit) {
    TMTopBar(
        title = stringResource(R.string.auth_info_title),
        onBack = goBack,
    )
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    AuthUserInfoContent(
        component = MockAuthUserInfoComponent(
            role = UserRole.TradingStation,
        ),
    )
}

