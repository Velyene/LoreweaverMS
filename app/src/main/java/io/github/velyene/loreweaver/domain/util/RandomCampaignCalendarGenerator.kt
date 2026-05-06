/*
 * FILE: RandomCampaignCalendarGenerator.kt
 *
 * TABLE OF CONTENTS:
 * 1. Constants and random-name pools
 * 2. Data classes: CampaignCalendarMonth and CampaignCalendar
 * 3. Generators: calendar, note, and note rendering
 * 4. Private helpers: calendar, weekday, month, and festival naming
 */

package io.github.velyene.loreweaver.domain.util

import kotlin.random.Random

private const val RANDOM_CALENDAR_WEEKDAY_COUNT = 7
private const val RANDOM_CALENDAR_MIN_MONTH_COUNT = 10
private const val RANDOM_CALENDAR_MAX_MONTH_COUNT = 14
private const val RANDOM_CALENDAR_MIN_MONTH_DAYS = 28
private const val RANDOM_CALENDAR_MAX_MONTH_DAYS = 36
private const val RANDOM_CALENDAR_MIN_FESTIVAL_DAYS = 2
private const val RANDOM_CALENDAR_MAX_FESTIVAL_DAYS = 5
private const val RANDOM_CALENDAR_MIN_YEAR = 101
private const val RANDOM_CALENDAR_MAX_YEAR_EXCLUSIVE = 1000

private val calendarPrefixPool = listOf(
	"Amber",
	"Astral",
	"Aurora",
	"Cinder",
	"Dawn",
	"Duskwind",
	"Ember",
	"Frost",
	"Gilded",
	"Iron",
	"Ivory",
	"Moon",
	"River",
	"Silver",
	"Star",
	"Storm",
	"Sun",
	"Thorn",
	"Verdant",
	"Wyrm"
)

private val calendarSuffixPool = listOf(
	"Cycle",
	"Ledger",
	"Reckoning",
	"Roll",
	"Spiral",
	"Tally",
	"Turning",
	"Calendar"
)

private val weekdayPool = listOf(
	"Ashday",
	"Dawnsreach",
	"Emberday",
	"Farsun",
	"Gloamday",
	"Goldwake",
	"Hearthday",
	"Highsun",
	"Lowmoon",
	"Mistday",
	"Moonday",
	"Riverday",
	"Skydawn",
	"Starfall",
	"Stormrest",
	"Sunreach",
	"Thundersday",
	"Wellsday",
	"Windcall"
)

private val monthPrefixPool = listOf(
	"Ash",
	"Bloom",
	"Briar",
	"Bright",
	"Cinder",
	"Dawn",
	"Deep",
	"Ember",
	"Frost",
	"Gold",
	"Harvest",
	"High",
	"Hollow",
	"Moon",
	"Raven",
	"River",
	"Shadow",
	"Snow",
	"Star",
	"Sun",
	"Thorn",
	"Twilight",
	"Verdant",
	"Winter"
)

private val monthSuffixPool = listOf(
	"bloom",
	"crest",
	"dawn",
	"fall",
	"glow",
	"march",
	"moon",
	"reach",
	"rest",
	"rise",
	"tide",
	"wane"
)

private val festivalPrefixPool = listOf(
	"Aurora",
	"Ember",
	"Founders'",
	"Harvest",
	"Lantern",
	"Moon",
	"River",
	"Silver",
	"Star",
	"Sun",
	"Thorn",
	"Victory",
	"Wardens'",
	"Winter"
)

private val festivalSuffixPool = listOf(
	"Carnival",
	"Convocation",
	"Eve",
	"Faire",
	"Feast",
	"Jubilee",
	"Market",
	"Moot",
	"Procession",
	"Revel",
	"Remembrance",
	"Vigil"
)

private val eraPool = listOf(
	"Brazen Accord",
	"Crystal Compact",
	"Dawnbound Charter",
	"Everburning Pact",
	"Lantern Accord",
	"Moonwake Concord",
	"Northern Oath",
	"Sable Crown",
	"Starforged Treaty",
	"Verdant Promise"
)

data class CampaignCalendarMonth(
	val name: String,
	val days: Int,
)

data class CampaignCalendar(
	val name: String,
	val weekdays: List<String>,
	val months: List<CampaignCalendarMonth>,
	val festivalDays: List<String>,
	val currentYear: Int,
	val eraName: String,
	val currentMonthIndex: Int,
	val currentDay: Int,
)

fun generateRandomCampaignCalendar(random: Random = Random.Default): CampaignCalendar {
	val monthCount = random.nextInt(RANDOM_CALENDAR_MIN_MONTH_COUNT, RANDOM_CALENDAR_MAX_MONTH_COUNT + 1)
	val months = monthNames(random)
		.take(monthCount)
		.map { monthName ->
			CampaignCalendarMonth(
				name = monthName,
				days = random.nextInt(RANDOM_CALENDAR_MIN_MONTH_DAYS, RANDOM_CALENDAR_MAX_MONTH_DAYS + 1),
			)
		}
	val currentMonthIndex = random.nextInt(months.size)
	return CampaignCalendar(
		name = calendarName(random),
		weekdays = weekdayNames(random),
		months = months,
		festivalDays = festivalNames(random),
		currentYear = random.nextInt(RANDOM_CALENDAR_MIN_YEAR, RANDOM_CALENDAR_MAX_YEAR_EXCLUSIVE),
		eraName = eraPool.random(random),
		currentMonthIndex = currentMonthIndex,
		currentDay = random.nextInt(1, months[currentMonthIndex].days + 1),
	)
}

fun generateRandomCampaignCalendarNote(random: Random = Random.Default): String {
	return generateRandomCampaignCalendar(random).toCampaignCalendarNote()
}

fun CampaignCalendar.toCampaignCalendarNote(): String {
	return buildString {
		appendLine("Calendar: $name")
		appendLine("Current Year: Year $currentYear of the $eraName")
		appendLine("Weekdays: ${weekdays.joinToString()}")
		appendLine()
		appendLine("Months:")
		months.forEachIndexed { index, month ->
			appendLine("${index + 1}. ${month.name} — ${month.days} days")
		}
		if (festivalDays.isNotEmpty()) {
			appendLine()
			appendLine("Festival Days:")
			festivalDays.forEach { festival ->
				appendLine("• $festival")
			}
		}
		appendLine()
		appendLine("Current Date: $currentDay ${months[currentMonthIndex].name}, Year $currentYear")
	}.trimEnd()
}

private fun calendarName(random: Random): String {
	return "${calendarPrefixPool.random(random)} ${calendarSuffixPool.random(random)}"
}

private fun weekdayNames(random: Random): List<String> {
	return weekdayPool.shuffled(random).take(RANDOM_CALENDAR_WEEKDAY_COUNT)
}

private fun monthNames(random: Random): List<String> {
	return monthPrefixPool
		.flatMap { prefix -> monthSuffixPool.map { suffix -> prefix + suffix } }
		.shuffled(random)
		.distinct()
}

private fun festivalNames(random: Random): List<String> {
	return festivalPrefixPool.shuffled(random)
		.zip(festivalSuffixPool.shuffled(random))
		.map { (prefix, suffix) -> "$prefix $suffix" }
		.distinct()
		.take(random.nextInt(RANDOM_CALENDAR_MIN_FESTIVAL_DAYS, RANDOM_CALENDAR_MAX_FESTIVAL_DAYS + 1))
}

