
package io.github.velyene.loreweaver.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StatusEffectChipsTest {

	@Test
	fun normalizedStatusLabels_trimsCanonicalizesDedupesAndSorts() {
		val labels = listOf(" poisoned ", "Exhaustion", "Blessed", "poisoned", "blessed")

		assertEquals(
			listOf("Blessed", "Exhaustion", "Poisoned"),
			normalizedStatusLabels(labels)
		)
	}

	@Test
	fun persistentStatusChipModels_marksAllResultsPersistentUsingCanonicalLabels() {
		val chips = persistentStatusChipModels(listOf("cursed", " Exhaustion "))

		assertEquals(listOf("Cursed", "Exhaustion"), chips.map(StatusChipModel::name))
		assertTrue(chips.all(StatusChipModel::isPersistent))
	}

	@Test
	fun statusChipModel_marksOnlyMappedLabelsInteractive() {
		val officialChip = statusChipModel("Poisoned")
		val builtInChip = statusChipModel("Dodging")
		val homebrewChip = statusChipModel("Bleeding")

		assertTrue(officialChip.isInteractive)
		assertTrue(builtInChip.isInteractive)
		assertTrue(!homebrewChip.isInteractive)
	}

	@Test
	fun statusChipAnnouncement_describesReferenceAvailability() {
		val interactiveAnnouncement = statusChipAnnouncement(
			status = statusChipModel("Dodging"),
			persistentSuffix = "(Persistent)",
			referenceAvailableDescription = "Reference details available",
			statusOnlyDescription = "Status only"
		)
		val statusOnlyAnnouncement = statusChipAnnouncement(
			status = statusChipModel("Bleeding"),
			persistentSuffix = "(Persistent)",
			referenceAvailableDescription = "Reference details available",
			statusOnlyDescription = "Status only"
		)

		assertEquals("⬒ Dodging, Reference details available", interactiveAnnouncement)
		assertEquals("✹ Bleeding, Status only", statusOnlyAnnouncement)
	}

	@Test
	fun normalizedStatusLabels_preservesCustomAndBuiltInLabelsWithoutGrantingAllReferenceTargets() {
		val labels = normalizedStatusLabels(listOf(" burning ", "Bleeding", "dodging", "Burning"))

		assertEquals(listOf("Bleeding", "Burning", "Dodging"), labels)
		assertNull(ConditionConstants.referenceTargetFor("Bleeding"))
		assertTrue(ConditionConstants.referenceTargetFor("Burning") != null)
		assertTrue(ConditionConstants.referenceTargetFor("Dodging") != null)
	}
}
