/*
 * FILE: SharedScrollbars.kt
 *
 * TABLE OF CONTENTS:
 * 1. Scrollbar constants and draw parameter models
 * 2. ScrollState and LazyListState modifier extensions
 * 3. Scrollbar metric calculation helpers
 * 4. Drawing helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val ScrollbarThickness = 6.dp
private val ScrollbarEndPadding = 4.dp
private val ScrollbarMinThumbHeight = 48.dp

private data class ScrollbarDrawParams(
	val thumbColor: Color,
	val trackColor: Color,
	val thicknessPx: Float,
	val endPaddingPx: Float,
	val minThumbHeightPx: Float
)

internal data class ScrollbarMetrics(
	val thumbHeight: Float,
	val thumbOffsetY: Float,
	val viewportHeight: Float
)

@Composable
private fun rememberScrollbarDrawParams(
	thickness: Dp,
	endPadding: Dp,
	minThumbHeight: Dp
): ScrollbarDrawParams {
	val density = LocalDensity.current
	return ScrollbarDrawParams(
		thumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.82f),
		trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
		thicknessPx = with(density) { thickness.toPx() },
		endPaddingPx = with(density) { endPadding.toPx() },
		minThumbHeightPx = with(density) { minThumbHeight.toPx() }
	)
}

@Composable
internal fun Modifier.visibleVerticalScrollbar(
	scrollState: ScrollState,
	thickness: Dp = ScrollbarThickness,
	endPadding: Dp = ScrollbarEndPadding,
	minThumbHeight: Dp = ScrollbarMinThumbHeight
): Modifier {
	val params = rememberScrollbarDrawParams(
		thickness = thickness,
		endPadding = endPadding,
		minThumbHeight = minThumbHeight
	)

	return this.drawWithContent {
		drawContent()

		val viewportHeight = size.height
		val maxValue = scrollState.maxValue.toFloat()
		if (viewportHeight <= 0f || maxValue <= 0f) return@drawWithContent

		val totalContentHeight = viewportHeight + maxValue
		if (totalContentHeight <= viewportHeight) return@drawWithContent

		val thumbHeight = ((viewportHeight / totalContentHeight) * viewportHeight)
			.coerceAtLeast(params.minThumbHeightPx)
			.coerceAtMost(viewportHeight)
		val travel = (viewportHeight - thumbHeight).coerceAtLeast(0f)
		val thumbOffsetY = if (travel == 0f) 0f else (scrollState.value / maxValue) * travel

		drawScrollbarTrackAndThumb(
			trackColor = params.trackColor,
			thumbColor = params.thumbColor,
			thumbOffsetY = thumbOffsetY,
			thumbHeight = thumbHeight,
			thicknessPx = params.thicknessPx,
			endPaddingPx = params.endPaddingPx,
			viewportHeight = viewportHeight
		)
	}
}

@Composable
internal fun Modifier.visibleVerticalScrollbar(
	listState: LazyListState,
	thickness: Dp = ScrollbarThickness,
	endPadding: Dp = ScrollbarEndPadding,
	minThumbHeight: Dp = ScrollbarMinThumbHeight
): Modifier {
	val params = rememberScrollbarDrawParams(
		thickness = thickness,
		endPadding = endPadding,
		minThumbHeight = minThumbHeight
	)

	return this.drawWithContent {
		drawContent()

		val layoutInfo = listState.layoutInfo
		val viewportHeight = size.height
		val visibleItems = layoutInfo.visibleItemsInfo
		val totalItemsCount = layoutInfo.totalItemsCount
		if (viewportHeight <= 0f || visibleItems.isEmpty() || totalItemsCount <= 0) return@drawWithContent
		if (!listState.canScrollBackward && !listState.canScrollForward) return@drawWithContent

		val metrics = calculateLazyListScrollbarMetrics(
			viewportHeight = viewportHeight,
			visibleItemSizes = visibleItems.map { it.size },
			totalItemsCount = totalItemsCount,
			beforeContentPadding = layoutInfo.beforeContentPadding,
			afterContentPadding = layoutInfo.afterContentPadding,
			firstVisibleItemIndex = listState.firstVisibleItemIndex,
			firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
			minThumbHeightPx = params.minThumbHeightPx
		) ?: return@drawWithContent

		drawScrollbarTrackAndThumb(
			trackColor = params.trackColor,
			thumbColor = params.thumbColor,
			thumbOffsetY = metrics.thumbOffsetY,
			thumbHeight = metrics.thumbHeight,
			thicknessPx = params.thicknessPx,
			endPaddingPx = params.endPaddingPx,
			viewportHeight = metrics.viewportHeight
		)
	}
}

internal fun calculateLazyListScrollbarMetrics(
	viewportHeight: Float,
	visibleItemSizes: List<Int>,
	totalItemsCount: Int,
	beforeContentPadding: Int,
	afterContentPadding: Int,
	firstVisibleItemIndex: Int,
	firstVisibleItemScrollOffset: Int,
	minThumbHeightPx: Float
): ScrollbarMetrics? {
	if (viewportHeight <= 0f || visibleItemSizes.isEmpty() || totalItemsCount <= 0) return null

	val averageItemHeight = visibleItemSizes
		.sum()
		.toFloat()
		.div(visibleItemSizes.size)
		.coerceAtLeast(1f)
	val estimatedTotalContentHeight = (
		(averageItemHeight * totalItemsCount) +
			beforeContentPadding +
			afterContentPadding
		).coerceAtLeast(viewportHeight)
	val estimatedScrollOffset = (
		(averageItemHeight * firstVisibleItemIndex) +
			firstVisibleItemScrollOffset
		).coerceAtLeast(0f)
	val maxScrollOffset = (estimatedTotalContentHeight - viewportHeight).coerceAtLeast(1f)
	val thumbHeight = ((viewportHeight / estimatedTotalContentHeight) * viewportHeight)
		.coerceAtLeast(minThumbHeightPx)
		.coerceAtMost(viewportHeight)
	val travel = (viewportHeight - thumbHeight).coerceAtLeast(0f)
	val scrollFraction = (estimatedScrollOffset / maxScrollOffset).coerceIn(0f, 1f)

	return ScrollbarMetrics(
		thumbHeight = thumbHeight,
		thumbOffsetY = travel * scrollFraction,
		viewportHeight = viewportHeight
	)
}

private fun DrawScope.drawScrollbarTrackAndThumb(
	trackColor: Color,
	thumbColor: Color,
	thumbOffsetY: Float,
	thumbHeight: Float,
	thicknessPx: Float,
	endPaddingPx: Float,
	viewportHeight: Float
) {
	if (size.width <= thicknessPx + endPaddingPx) return

	val scrollbarX = size.width - thicknessPx - endPaddingPx
	val cornerRadius = CornerRadius(thicknessPx / 2f, thicknessPx / 2f)

	drawRoundRect(
		color = trackColor,
		topLeft = Offset(scrollbarX, 0f),
		size = Size(thicknessPx, viewportHeight),
		cornerRadius = cornerRadius
	)
	drawRoundRect(
		color = thumbColor,
		topLeft = Offset(scrollbarX, thumbOffsetY.coerceIn(0f, (viewportHeight - thumbHeight).coerceAtLeast(0f))),
		size = Size(thicknessPx, thumbHeight),
		cornerRadius = cornerRadius
	)
}

