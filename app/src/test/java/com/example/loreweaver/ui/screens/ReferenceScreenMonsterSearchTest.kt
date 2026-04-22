package com.example.loreweaver.ui.screens

import com.example.loreweaver.domain.util.MonsterReferenceEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenMonsterSearchTest {

	@Test
	fun filterMonsterEntries_returnsEmptyWhileBundledMonsterCorpusIsRemoved() {
		assertTrue(filterMonsterEntries().isEmpty())
	}

	@Test
	fun filterMonsterEntries_isStableAcrossRepeatedCalls() {
		assertEquals(emptyList<MonsterReferenceEntry>(), filterMonsterEntries())
		assertEquals(emptyList<MonsterReferenceEntry>(), filterMonsterEntries())
	}
}


