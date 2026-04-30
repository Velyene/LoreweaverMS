/*
 * FILE: SharedScrollbars.kt
 *
 * TABLE OF CONTENTS:
 * 1. Scrollbar constants and draw parameter models
 * 2. ScrollState and LazyListState modifier extensions
 * 3. Scrollbar metric calculation
 * 4. Drawing support
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.max

private val ScrollbarThickness = 6.dp
private val ScrollbarEndPadding = 4.dp
private val ScrollbarMinThumbHeight = 48.dp
private val ScrollbarTouchTargetWidth = 28.dp
private val ScrollbarTouchTargetHeight = 48.dp

private data class ScrollbarDrawParams(
	val thumbColor: Color,
	val trackColor: Color,
	val thicknessPx: Float,
	val endPaddingPx: Float,
	val minThumbHeightPx: Float,
	val touchTargetWidthPx: Float,
	val touchTargetHeightPx: Float
)

internal data class ScrollbarMetrics(
	val thumbHeight: Float,
	val thumbOffsetY: Float,
	val viewportHeight: Float,
	val maxScrollOffset: Float
)

internal data class ScrollbarTouchBounds(
	val left: Float,
	val top: Float,
	val right: Float,
	val bottom: Float
) {
	fun contains(x: Float, y: Float): Boolean = x in left..right && y in top..bottom
}

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
		minThumbHeightPx = with(density) { minThumbHeight.toPx() },
		touchTargetWidthPx = with(density) { ScrollbarTouchTargetWidth.toPx() },
		touchTargetHeightPx = with(density) { ScrollbarTouchTargetHeight.toPx() }
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
	var layoutSize by remember { mutableStateOf(IntSize.Zero) }
	var isDraggingThumb by remember(scrollState) { mutableStateOf(false) }
	var lastDragY by remember(scrollState) { mutableFloatStateOf(0f) }

	return this
		.onSizeChanged { layoutSize = it }
		.pointerInteropFilter { event ->
			val metrics = calculateScrollStateScrollbarMetrics(
				viewportHeight = layoutSize.height.toFloat(),
				currentScrollOffset = scrollState.value.toFloat(),
				maxScrollOffset = scrollState.maxValue.toFloat(),
				minThumbHeightPx = params.minThumbHeightPx
			)
			val bounds = metrics?.let {
				calculateScrollbarTouchBounds(
					viewportWidth = layoutSize.width.toFloat(),
					metrics = it,
					thicknessPx = params.thicknessPx,
					endPaddingPx = params.endPaddingPx,
					touchTargetWidthPx = params.touchTargetWidthPx,
					touchTargetHeightPx = params.touchTargetHeightPx
				)
			}

			when (event.actionMasked) {
				android.view.MotionEvent.ACTION_DOWN -> {
					val hitThumb = bounds?.contains(event.x, event.y) == true
					isDraggingThumb = hitThumb
					lastDragY = event.y
					hitThumb
				}

				android.view.MotionEvent.ACTION_MOVE -> {
					if (!isDraggingThumb || metrics == null) {
						false
					} else {
						val dragDeltaY = event.y - lastDragY
						if (dragDeltaY != 0f) {
							scrollState.dispatchRawDelta(
								calculateContentDeltaForThumbDrag(
									dragDeltaY = dragDeltaY,
									viewportHeight = metrics.viewportHeight,
									thumbHeight = metrics.thumbHeight,
									maxScrollOffset = metrics.maxScrollOffset
								)
							)
							lastDragY = event.y
						}
						true
					}
				}

				android.view.MotionEvent.ACTION_UP,
				android.view.MotionEvent.ACTION_CANCEL -> {
					val consumed = isDraggingThumb
					isDraggingThumb = false
					consumed
				}

				else -> false
			}
		}
		.drawWithContent {
		drawContent()

		val metrics = calculateScrollStateScrollbarMetrics(
			viewportHeight = size.height,
			currentScrollOffset = scrollState.value.toFloat(),
			maxScrollOffset = scrollState.maxValue.toFloat(),
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
	var layoutSize by remember { mutableStateOf(IntSize.Zero) }
	var isDraggingThumb by remember(listState) { mutableStateOf(false) }
	var lastDragY by remember(listState) { mutableFloatStateOf(0f) }

	return this
		.onSizeChanged { layoutSize = it }
		.pointerInteropFilter { event ->
			val metrics = calculateLazyListScrollbarMetrics(
				viewportHeight = layoutSize.height.toFloat(),
				visibleItemSizes = listState.layoutInfo.visibleItemsInfo.map { it.size },
				totalItemsCount = listState.layoutInfo.totalItemsCount,
				beforeContentPadding = listState.layoutInfo.beforeContentPadding,
				afterContentPadding = listState.layoutInfo.afterContentPadding,
				firstVisibleItemIndex = listState.firstVisibleItemIndex,
				firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
				minThumbHeightPx = params.minThumbHeightPx
			)
			val bounds = metrics?.let {
				calculateScrollbarTouchBounds(
					viewportWidth = layoutSize.width.toFloat(),
					metrics = it,
					thicknessPx = params.thicknessPx,
					endPaddingPx = params.endPaddingPx,
					touchTargetWidthPx = params.touchTargetWidthPx,
					touchTargetHeightPx = params.touchTargetHeightPx
				)
			}

			when (event.actionMasked) {
				android.view.MotionEvent.ACTION_DOWN -> {
					val hitThumb = bounds?.contains(event.x, event.y) == true
					isDraggingThumb = hitThumb
					lastDragY = event.y
					hitThumb
				}

				android.view.MotionEvent.ACTION_MOVE -> {
					if (!isDraggingThumb || metrics == null) {
						false
					} else {
						val dragDeltaY = event.y - lastDragY
						if (dragDeltaY != 0f) {
							listState.dispatchRawDelta(
								calculateContentDeltaForThumbDrag(
									dragDeltaY = dragDeltaY,
									viewportHeight = metrics.viewportHeight,
									thumbHeight = metrics.thumbHeight,
									maxScrollOffset = metrics.maxScrollOffset
								)
							)
							lastDragY = event.y
						}
						true
					}
				}

				android.view.MotionEvent.ACTION_UP,
				android.view.MotionEvent.ACTION_CANCEL -> {
					val consumed = isDraggingThumb
					isDraggingThumb = false
					consumed
				}

				else -> false
			}
		}
		.drawWithContent {
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
		viewportHeight = viewportHeight,
		maxScrollOffset = maxScrollOffset
	)
}

internal fun calculateScrollStateScrollbarMetrics(
	viewportHeight: Float,
	currentScrollOffset: Float,
	maxScrollOffset: Float,
	minThumbHeightPx: Float
): ScrollbarMetrics? {
	if (viewportHeight <= 0f || maxScrollOffset <= 0f) return null

	val totalContentHeight = viewportHeight + maxScrollOffset
	if (totalContentHeight <= viewportHeight) return null

	val thumbHeight = ((viewportHeight / totalContentHeight) * viewportHeight)
		.coerceAtLeast(minThumbHeightPx)
		.coerceAtMost(viewportHeight)
	val travel = (viewportHeight - thumbHeight).coerceAtLeast(0f)
	val thumbOffsetY = if (travel == 0f) 0f else {
		(currentScrollOffset / maxScrollOffset).coerceIn(0f, 1f) * travel
	}

	return ScrollbarMetrics(
		thumbHeight = thumbHeight,
		thumbOffsetY = thumbOffsetY,
		viewportHeight = viewportHeight,
		maxScrollOffset = maxScrollOffset
	)
}


internal fun calculateContentDeltaForThumbDrag(
	dragDeltaY: Float,
	viewportHeight: Float,
	thumbHeight: Float,
	maxScrollOffset: Float
): Float {
	if (dragDeltaY == 0f || viewportHeight <= 0f || maxScrollOffset <= 0f) return 0f
	val travel = (viewportHeight - thumbHeight).coerceAtLeast(0f)
	if (travel == 0f) return 0f
	return dragDeltaY * (maxScrollOffset / travel)
}

internal fun calculateScrollbarTouchBounds(
	viewportWidth: Float,
	metrics: ScrollbarMetrics,
	thicknessPx: Float,
	endPaddingPx: Float,
	touchTargetWidthPx: Float,
	touchTargetHeightPx: Float
): ScrollbarTouchBounds {
	val touchWidth = max(thicknessPx, touchTargetWidthPx)
	val touchHeight = max(metrics.thumbHeight, touchTargetHeightPx)
	val left = (viewportWidth - endPaddingPx - touchWidth).coerceAtLeast(0f)
	val top = (metrics.thumbOffsetY - ((touchHeight - metrics.thumbHeight) / 2f))
		.coerceIn(0f, (metrics.viewportHeight - touchHeight).coerceAtLeast(0f))

	return ScrollbarTouchBounds(
		left = left,
		top = top,
		right = left + touchWidth,
		bottom = top + touchHeight
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

