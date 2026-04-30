package io.github.velyene.loreweaver.domain.util

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RandomCampaignCalendarGeneratorTest {

	@Test
	fun generateRandomCampaignCalendar_createsReadableStructuredCalendar() {
		val calendar = generateRandomCampaignCalendar(Random(42))

		assertTrue(calendar.name.isNotBlank())
		assertEquals(7, calendar.weekdays.size)
		assertEquals(calendar.weekdays.size, calendar.weekdays.distinct().size)
		assertTrue(calendar.months.size in 10..14)
		assertEquals(calendar.months.size, calendar.months.map { it.name }.distinct().size)
		assertTrue(calendar.months.all { it.days in 28..36 })
		assertTrue(calendar.festivalDays.size in 2..5)
		assertEquals(calendar.festivalDays.size, calendar.festivalDays.distinct().size)
		assertTrue(calendar.currentMonthIndex in calendar.months.indices)
		assertTrue(calendar.currentDay in 1..calendar.months[calendar.currentMonthIndex].days)
	}

	@Test
	fun toCampaignCalendarNote_formatsSectionsForCampaignNotes() {
		val calendar = generateRandomCampaignCalendar(Random(7))
		val note = calendar.toCampaignCalendarNote()

		assertTrue(note.startsWith("Calendar: ${calendar.name}"))
		assertTrue(note.contains("Current Year: Year ${calendar.currentYear} of the ${calendar.eraName}"))
		assertTrue(note.contains("Weekdays: ${calendar.weekdays.joinToString()}"))
		assertTrue(note.contains("Months:"))
		calendar.months.forEachIndexed { index, month ->
			assertTrue(note.contains("${index + 1}. ${month.name} — ${month.days} days"))
		}
		calendar.festivalDays.forEach { festival ->
			assertTrue(note.contains("• $festival"))
		}
		assertTrue(
			note.contains(
				"Current Date: ${calendar.currentDay} ${calendar.months[calendar.currentMonthIndex].name}, Year ${calendar.currentYear}"
			)
		)
	}
}

