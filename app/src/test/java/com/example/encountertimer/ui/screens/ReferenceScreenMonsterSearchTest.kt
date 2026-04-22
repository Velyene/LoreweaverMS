package com.example.encountertimer.ui.screens

import org.junit.Assert.assertTrue
import org.junit.Test

class ReferenceScreenMonsterSearchTest {

	@Test
	fun filterMonsterEntries_returnsEmptyWhileBundledMonsterCorpusIsRemoved() {
		assertTrue(filterMonsterEntries().isEmpty())
		assertTrue(filterMonsterEntries().isEmpty())
		assertTrue(filterMonsterEntries().isEmpty())
	}
}

