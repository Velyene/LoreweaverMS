package io.github.velyene.loreweaver.ui.screens

import androidx.compose.ui.graphics.Color
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ConditionConstantsTest {

	@Test
	fun metadataFor_knownCondition_returnsMappedFamilyAndGlyph() {
		val metadata = ConditionConstants.metadataFor("Poisoned")

		assertEquals(ConditionConstants.StatusCategory.DAMAGE_OVER_TIME, metadata.category)
		assertEquals(Color(0xFF6BCB3D), metadata.color)
		assertEquals("☠", metadata.iconGlyph)
		assertFalse(metadata.persistsAcrossEncounters)
	}

	@Test
	fun metadataFor_persistentCondition_defaultsToPersistent() {
		val metadata = ConditionConstants.metadataFor("Exhaustion")

		assertEquals(ConditionConstants.StatusCategory.PERSISTENT, metadata.category)
		assertTrue(metadata.persistsAcrossEncounters)
		assertTrue(ConditionConstants.defaultPersistsAcrossEncounters("Exhaustion"))
	}

	@Test
	fun metadataFor_unknownCondition_fallsBackToDebuff() {
		val metadata = ConditionConstants.metadataFor("Custom Hex")

		assertEquals("Custom Hex", metadata.label)
		assertEquals(ConditionConstants.StatusCategory.DEBUFF, metadata.category)
		assertEquals("•", metadata.iconGlyph)
	}

	@Test
	fun bucketFor_separatesOfficialBuiltInCustomAndHomebrewLabels() {
		assertEquals(
			ConditionConstants.StatusPolicyBucket.OFFICIAL_CONDITION,
			ConditionConstants.bucketFor("Exhaustion")
		)
		assertEquals(
			ConditionConstants.StatusPolicyBucket.BUILT_IN_STATUS,
			ConditionConstants.bucketFor("Blessed")
		)
		assertEquals(
			ConditionConstants.StatusPolicyBucket.CUSTOM_EFFECT,
			ConditionConstants.bucketFor("Burning")
		)
		assertEquals(
			ConditionConstants.StatusPolicyBucket.CUSTOM_HOMEBREW,
			ConditionConstants.bucketFor("Bleeding")
		)
	}

	@Test
	fun referenceTargetFor_routesKnownStatusesToApplicableProjectContent() {
		val officialTarget = ConditionConstants.referenceTargetFor("Blinded")
		val spellTarget = ConditionConstants.referenceTargetFor("Blessed")
		val actionTarget = ConditionConstants.referenceTargetFor("Dodging")
		val hazardTarget = ConditionConstants.referenceTargetFor("Burning")
		val diseaseTarget = ConditionConstants.referenceTargetFor("Diseased")

		assertEquals(ReferenceCategory.CORE_RULES, officialTarget?.category)
		assertEquals(ReferenceDetailResolver.CATEGORY_CONDITIONS, officialTarget?.detailCategory)
		assertEquals(ReferenceCategory.SPELLCASTING, spellTarget?.category)
		assertEquals(ReferenceDetailResolver.CATEGORY_SPELLS, spellTarget?.detailCategory)
		assertEquals(ReferenceCategory.CORE_RULES, actionTarget?.category)
		assertEquals(ReferenceDetailResolver.CATEGORY_ACTIONS, actionTarget?.detailCategory)
		assertEquals(ReferenceCategory.CORE_RULES, hazardTarget?.category)
		assertEquals(ReferenceDetailResolver.CATEGORY_HAZARDS, hazardTarget?.detailCategory)
		assertEquals(ReferenceCategory.DISEASES, diseaseTarget?.category)
		assertNull(diseaseTarget?.detailCategory)
	}

	@Test
	fun referenceTargetFor_returnsNullForHomebrewOnlyLabel() {
		assertNull(ConditionConstants.referenceTargetFor("Bleeding"))
		assertFalse(ConditionConstants.shouldAllowOfficialConditionLookup("Blessed"))
		assertTrue(ConditionConstants.shouldAllowOfficialConditionLookup("Blinded"))
	}
}
