package io.github.velyene.loreweaver.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SharedScrollbarsTest {

	@Test
	fun calculateScrollStateScrollbarMetrics_mapsCurrentScrollOffsetToThumbPosition() {
		val metrics = calculateScrollStateScrollbarMetrics(
			viewportHeight = 200f,
			currentScrollOffset = 150f,
			maxScrollOffset = 300f,
			minThumbHeightPx = 10f
		)

		requireNotNull(metrics)
		assertEquals(80f, metrics.thumbHeight, 0.0001f)
		assertEquals(60f, metrics.thumbOffsetY, 0.0001f)
		assertEquals(300f, metrics.maxScrollOffset, 0.0001f)
	}

	@Test
	fun calculateContentDeltaForThumbDrag_scalesThumbTravelToContentTravel() {
		assertEquals(
			25f,
			calculateContentDeltaForThumbDrag(
				dragDeltaY = 10f,
				viewportHeight = 200f,
				thumbHeight = 80f,
				maxScrollOffset = 300f
			),
			0.0001f
		)
	}

	@Test
	fun calculateScrollbarTouchBounds_expandsThumbHitAreaNearTrailingEdge() {
		val bounds = calculateScrollbarTouchBounds(
			viewportWidth = 300f,
			metrics = ScrollbarMetrics(
				thumbHeight = 20f,
				thumbOffsetY = 50f,
				viewportHeight = 200f,
				maxScrollOffset = 300f
			),
			thicknessPx = 6f,
			endPaddingPx = 4f,
			touchTargetWidthPx = 28f,
			touchTargetHeightPx = 48f
		)

		assertEquals(268f, bounds.left, 0.0001f)
		assertEquals(36f, bounds.top, 0.0001f)
		assertEquals(296f, bounds.right, 0.0001f)
		assertEquals(84f, bounds.bottom, 0.0001f)
	}

	@Test
	fun calculateLazyListScrollbarMetrics_usesDrawViewportHeightForTrackAndThumbSizing() {
		val metrics = calculateLazyListScrollbarMetrics(
			viewportHeight = 200f,
			visibleItemSizes = listOf(50, 50, 50),
			totalItemsCount = 20,
			beforeContentPadding = 16,
			afterContentPadding = 16,
			firstVisibleItemIndex = 0,
			firstVisibleItemScrollOffset = 0,
			minThumbHeightPx = 10f
		)

		requireNotNull(metrics)
		assertEquals(200f, metrics.viewportHeight, 0.0001f)
		assertEquals(38.75969f, metrics.thumbHeight, 0.0001f)
		assertEquals(0f, metrics.thumbOffsetY, 0.0001f)
	}

	@Test
	fun calculateLazyListScrollbarMetrics_keepsPaddingInContentEstimateWhileUsingDrawSpaceTravel() {
		val metrics = calculateLazyListScrollbarMetrics(
			viewportHeight = 200f,
			visibleItemSizes = listOf(50, 50, 50),
			totalItemsCount = 20,
			beforeContentPadding = 16,
			afterContentPadding = 16,
			firstVisibleItemIndex = 10,
			firstVisibleItemScrollOffset = 25,
			minThumbHeightPx = 10f
		)

		requireNotNull(metrics)
		assertEquals(38.75969f, metrics.thumbHeight, 0.0001f)
		assertEquals(101.74419f, metrics.thumbOffsetY, 0.0001f)
	}

	@Test
	fun calculateLazyListScrollbarMetrics_returnsNullWithoutViewportOrVisibleItems() {
		assertNull(
			calculateLazyListScrollbarMetrics(
				viewportHeight = 0f,
				visibleItemSizes = listOf(50),
				totalItemsCount = 1,
				beforeContentPadding = 0,
				afterContentPadding = 0,
				firstVisibleItemIndex = 0,
				firstVisibleItemScrollOffset = 0,
				minThumbHeightPx = 10f
			)
		)
		assertNull(
			calculateLazyListScrollbarMetrics(
				viewportHeight = 200f,
				visibleItemSizes = emptyList(),
				totalItemsCount = 1,
				beforeContentPadding = 0,
				afterContentPadding = 0,
				firstVisibleItemIndex = 0,
				firstVisibleItemScrollOffset = 0,
				minThumbHeightPx = 10f
			)
		)
	}
}

