package com.bunbeauty.designsystem.ui.element.footer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bunbeauty.designsystem.isWeb

data class SiteFooterActions(
    val onUserAgreementClick: () -> Unit,
    val onPrivacyPolicyClick: () -> Unit,
    val onTermsOfServiceClick: () -> Unit,
)

val LocalSiteFooterActions = staticCompositionLocalOf<SiteFooterActions?> { null }

/**
 * Футер сайта. Рендерится только на вебе: на мобильных платформах вызов ничего не делает.
 * Экшены берутся из [LocalSiteFooterActions], который провайдится в MainScreen.
 *
 * Если к низу экрана прибит элемент, рисующийся поверх контента (actionButton,
 * BottomPanel, BottomAmountBar), под футером нужно зарезервировать место
 * через `Modifier.padding(bottom = FoodDeliveryTheme.dimensions.scrollScreenBottomSpace)`,
 * иначе он накроет ссылки.
 */
@Composable
fun WebSiteFooter(modifier: Modifier = Modifier) {
    val actions = LocalSiteFooterActions.current
    if (isWeb && actions != null) {
        SiteFooter(
            onUserAgreementClick = actions.onUserAgreementClick,
            onPrivacyPolicyClick = actions.onPrivacyPolicyClick,
            onTermsOfServiceClick = actions.onTermsOfServiceClick,
            modifier = modifier,
        )
    }
}

/**
 * Скроллируемая колонка со sticky-футером на вебе:
 * при коротком контенте футер прибит к низу экрана,
 * при длинном — уезжает вниз вместе со скроллом.
 *
 * На мобильных — обычный [verticalScroll] без футера.
 * Футер добавлять в [content] не нужно.
 */
@Composable
fun WebStickyFooterColumn(
    modifier: Modifier = Modifier,
    footerModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!isWeb) {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            content = content,
        )
        return
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content,
            )
            WebSiteFooter(modifier = footerModifier)
        }
    }
}

/**
 * [LazyColumn] со sticky-футером на вебе: список занимает оставшееся место,
 * футер прибит к низу экрана. На мобильных — обычный [LazyColumn] без футера.
 *
 * Футер в [content] добавлять не нужно.
 */
@Composable
fun WebStickyFooterLazyColumn(
    modifier: Modifier = Modifier,
    footerModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: LazyListScope.() -> Unit,
) {
    if (!isWeb) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            content = content,
        )
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            content = content,
        )
        WebSiteFooter(modifier = footerModifier)
    }
}

/**
 * Добавляет [WebSiteFooter] последним элементом списка.
 * На мобильных платформах элемент не создаётся.
 * Для коротких экранов предпочтительнее [WebStickyFooterLazyColumn].
 */
fun LazyListScope.webSiteFooterItem(modifier: Modifier = Modifier) {
    if (!isWeb) return

    item(key = SITE_FOOTER_ITEM_KEY) {
        WebSiteFooter(modifier = modifier)
    }
}

/**
 * Добавляет [WebSiteFooter] последним элементом сетки на всю ширину.
 * На мобильных платформах элемент не создаётся.
 */
fun LazyGridScope.webSiteFooterItem(modifier: Modifier = Modifier) {
    if (!isWeb) return

    item(
        key = SITE_FOOTER_ITEM_KEY,
        span = { GridItemSpan(maxLineSpan) },
    ) {
        WebSiteFooter(modifier = modifier)
    }
}

private const val SITE_FOOTER_ITEM_KEY = "SiteFooter"
