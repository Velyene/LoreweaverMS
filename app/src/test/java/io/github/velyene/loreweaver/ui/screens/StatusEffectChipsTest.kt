package io.github.velyene.loreweaver.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StatusEffectChipsTest {

	@Test
	fun normalizedStatusLabels_trimsCanonicalizesDedupesAndSorts() {
		val labels = listOf(" poisoned ", "Blessed", "poisoned", " burning ")

		assertEquals(
			listOf("Blessed", "Burning", "Poisoned"),
			normalizedStatusLabels(labels),
		)
	}

	@Test
	fun buildStatusChipModels_marksPersistentStateAndSrdStatus() {
		val chips = buildStatusChipModels(listOf("Poisoned", "Blessed"), isPersistent = true)

		assertEquals(listOf("Blessed", "Poisoned"), chips.map(StatusChipModel::name))
		assertTrue(chips.all(StatusChipModel::isPersistent))
		assertTrue(chips.any { it.name == "Poisoned" && it.isSrdCondition })
		assertFalse(chips.any { it.name == "Blessed" && it.isSrdCondition })
	}

	@Test
	fun statusChipDisplayText_includesDurationWhenPresent() {
		val chip = statusChipModel(name = "Poisoned", durationText = " (3)", isPersistent = false)

		assertEquals("Poisoned (3)", statusChipDisplayText(chip))
	}

	@Test
	fun statusChipDisplayText_appendsPersistentMarker() {
		val chip = statusChipModel(name = "Blessed", isPersistent = true)

		assertEquals("Blessed • Persistent", statusChipDisplayText(chip))
	}

	@Test
	fun statusChipDisplayText_keepsDurationVisibleWhenPersistent() {
		val chip = statusChipModel(name = "Blessed", durationText = " (3)", isPersistent = true)

		assertEquals("Blessed (3) • Persistent", statusChipDisplayText(chip))
	}

	@Test
	fun conditionMetadata_defaultsUnknownLabelsToCustomCategory() {
		val metadata = ConditionConstants.metadataFor("Battle Focus")

		assertEquals("Battle Focus", metadata.label)
		assertEquals(ConditionConstants.StatusCategory.DEBUFF, metadata.category)
		assertEquals(ConditionConstants.StatusPolicyBucket.CUSTOM_HOMEBREW, ConditionConstants.bucketFor("Battle Focus"))
		assertFalse(ConditionConstants.isOfficialCondition(metadata.label))
	}
}

