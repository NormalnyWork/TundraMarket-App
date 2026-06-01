package com.normalnywork.tundramarket.ui.screens.nomad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.components.InfoCard
import com.normalnywork.tundramarket.ui.kit.components.StageBar
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSecondary
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSlider
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.Comment
import com.normalnywork.tundramarket.ui.kit.icons.Location
import com.normalnywork.tundramarket.ui.kit.icons.Products
import com.normalnywork.tundramarket.ui.kit.icons.Shop
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.screens.nomad.NomadCreateOrderComponent.Page

@Composable
fun NomadCreateOrderContent(component: NomadCreateOrderComponent) {
    val colors = LocalTMColors.current
    val pages by component.childPages.subscribeAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TMTopBar(
                    title = stringResource(R.string.nomad_create_order_title),
                    onBack = component::onBackClicked,
                )
                StageBar(
                    range = 1..pages.items.size,
                    current = pages.selectedIndex + 1,
                    modifier = Modifier.padding(vertical = 12.dp),
                    updateStage = { component.onPageSelected(it - 1) },
                )
            }
        },
        content = { paddings ->
            ChildPages(
                pages = component.childPages,
                onPageSelected = component::onPageSelected,
                pager = { modifier, state, key, pageContent ->
                    HorizontalPager(
                        state = state,
                        modifier = modifier,
                        key = key,
                        userScrollEnabled = false,
                        pageContent = pageContent,
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings),
                scrollAnimation = PagesScrollAnimation.Default,
            ) { _, pageComponent ->
                NomadCreateOrderPageContent(
                    component = pageComponent,
                    onNext = component::onNextPageClicked,
                    onSkipComment = component::onSkipCommentClicked,
                    onCreateOrder = component::onCreateOrderClicked,
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
    )
}

@Composable
private fun NomadCreateOrderPageContent(
    component: NomadCreateOrderComponent.PageComponent,
    onNext: () -> Unit,
    onSkipComment: () -> Unit,
    onCreateOrder: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            InfoCard(
                title = component.page.infoTitle(),
                body = component.page.infoBody(),
                icon = component.page.icon(),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = component.page.title(),
                    style = LocalTMTypography.current.body,
                    color = LocalTMColors.current.textPrimary,
                )
            }
        }
        NomadCreateOrderPageActions(
            page = component.page,
            showDivider = scrollState.canScrollForward,
            onNext = onNext,
            onSkipComment = onSkipComment,
            onCreateOrder = onCreateOrder,
        )
    }
}

@Composable
private fun NomadCreateOrderPageActions(
    page: Page,
    showDivider: Boolean,
    onNext: () -> Unit,
    onSkipComment: () -> Unit,
    onCreateOrder: () -> Unit,
) {
    Column {
        val colors = LocalTMColors.current
        val dividerColor = if (showDivider) colors.stroke else Color.Transparent

        HorizontalDivider(color = dividerColor)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (page) {
                Page.Location,
                Page.TradingStation,
                Page.Products -> {
                    TMButtonPrimary(
                        text = stringResource(R.string.nomad_create_order_next_action),
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Page.Comment -> {
                    TMButtonSecondary(
                        text = stringResource(R.string.nomad_create_order_skip_action),
                        onClick = onSkipComment,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TMButtonPrimary(
                        text = stringResource(R.string.nomad_create_order_next_action),
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Page.Overview -> TMButtonSlider(
                    text = stringResource(R.string.nomad_create_order_create_action),
                    onClick = onCreateOrder,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun Page.title(): String {
    return stringResource(
        when (this) {
            Page.Location -> R.string.nomad_create_order_location_page
            Page.TradingStation -> R.string.nomad_create_order_trading_station_page
            Page.Products -> R.string.nomad_create_order_products_page
            Page.Comment -> R.string.nomad_create_order_comment_page
            Page.Overview -> R.string.nomad_create_order_overview_page
        },
    )
}

@Composable
private fun Page.infoTitle(): String {
    return stringResource(
        when (this) {
            Page.Location -> R.string.nomad_create_order_location_title
            Page.TradingStation -> R.string.nomad_create_order_trading_station_title
            Page.Products -> R.string.nomad_create_order_products_title
            Page.Comment -> R.string.nomad_create_order_comment_title
            Page.Overview -> R.string.nomad_create_order_overview_title
        },
    )
}

@Composable
private fun Page.infoBody(): String {
    return stringResource(
        when (this) {
            Page.Location -> R.string.nomad_create_order_location_body
            Page.TradingStation -> R.string.nomad_create_order_trading_station_body
            Page.Products -> R.string.nomad_create_order_products_body
            Page.Comment -> R.string.nomad_create_order_comment_body
            Page.Overview -> R.string.nomad_create_order_overview_body
        },
    )
}

private fun Page.icon(): ImageVector {
    return when (this) {
        Page.Location -> TMIcons.Location
        Page.TradingStation -> TMIcons.Shop
        Page.Products -> TMIcons.Products
        Page.Comment -> TMIcons.Comment
        Page.Overview -> TMIcons.Checkmark
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    NomadCreateOrderContent(
        component = NomadCreateOrderComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            onBack = {},
        ),
    )
}
