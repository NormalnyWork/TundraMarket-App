package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.serialization.Serializable

class NomadCreateOrderComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
) : ComponentContext by componentContext {

    private val navigation = PagesNavigation<Page>()

    val childPages: Value<ChildPages<Page, PageComponent>> = childPages(
        source = navigation,
        serializer = Page.serializer(),
        initialPages = {
            Pages(
                items = listOf(
                    Page.Location,
                    Page.TradingStation,
                    Page.Products,
                    Page.Comment,
                    Page.Overview,
                ),
                selectedIndex = 0,
            )
        },
        childFactory = ::child,
    )

    init {
        backHandler.register(
            BackCallback {
                goBack()
            }
        )
    }

    fun onBackClicked() {
        goBack()
    }

    fun onNextPageClicked() {
        val pages = childPages.value
        if (pages.selectedIndex < pages.items.lastIndex) {
            navigation.select(pages.selectedIndex + 1)
        }
    }

    fun onPreviousPageClicked() {
        val pages = childPages.value
        if (pages.selectedIndex > 0) {
            navigation.select(pages.selectedIndex - 1)
        }
    }

    fun onSkipCommentClicked() {
        onNextPageClicked()
    }

    fun onCreateOrderClicked() = Unit

    fun onPageSelected(index: Int) {
        navigation.select(index)
    }

    private fun goBack() {
        val pages = childPages.value
        if (pages.selectedIndex > 0) {
            navigation.select(pages.selectedIndex - 1)
        } else {
            onBack.invoke()
        }
    }

    private fun child(
        page: Page,
        componentContext: ComponentContext,
    ): PageComponent = PageComponent(
        componentContext = componentContext,
        page = page,
    )

    @Serializable
    enum class Page {
        Location,
        TradingStation,
        Products,
        Comment,
        Overview,
    }

    class PageComponent(
        componentContext: ComponentContext,
        val page: Page,
    ) : ComponentContext by componentContext
}
