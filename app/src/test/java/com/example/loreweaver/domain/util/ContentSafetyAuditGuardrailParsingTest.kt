package com.example.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentSafetyAuditGuardrailParsingTest {
	private val audit = ContentSafetyAuditTest()

	@Test
	fun extractKotlinComments_collectsEveryLineAndBlockCommentInOrder() {
		val source = """
			package demo
			// first header
			val answer = 42 /* second header */
			fun keepScanning() {
				// third header
			}
		""".trimIndent()

		val comments = extractKotlinComments(source)

		assertEquals(
			listOf(
				"// first header",
				"/* second header */",
				"// third header"
			).joinToString("\n"),
			comments
		)
	}

	@Test
	fun extractKotlinComments_ignoresCommentMarkersInsideRegularAndTripleQuotedStrings() {
		val source =
			"val url = \"https://example.com/from basic rules\"\n" +
				"val prose = \"\"\"not // a comment\\nnor /* a block */ comment\"\"\"\n" +
				"// actual provenance comment"

		val comments = extractKotlinComments(source)

		assertEquals("// actual provenance comment", comments.trim())
		assertFalse(comments.contains("not // a comment"))
		assertFalse(comments.contains("/* a block */"))
	}

	@Test
	fun extractKotlinTextBlocks_combinesAdjacentInlineLiteralsAcrossWhitespacePlusAndParentheses() {
		val source =
			"val prose = (\"First sentence stays descriptive enough to matter. \" +\n" +
				"    \"Second sentence keeps the guardrail parsing realistic.\")"

		val blocks = invokePrivate(
			"extractKotlinTextBlocks",
			arrayOf(String::class.java),
			source
		) as List<Any?>
		val inlineBlocks = blocks
			.mapNotNull { block ->
				block?.takeIf {
					readField(
						it,
						"kind"
					) == "inline string literal"
				}
			}
			.map { block -> readField(block, "rawText") as String }

		assertEquals(1, inlineBlocks.size)
		assertEquals(
			"First sentence stays descriptive enough to matter. Second sentence keeps the guardrail parsing realistic.",
			inlineBlocks.single()
		)
	}

	@Test
	fun containsWholeTerm_requiresTokenBoundaries() {
		assertTrue(containsWholeTerm("Tasha's Hideous Laughter", "Tasha's"))
		assertFalse(containsWholeTerm("Natasha keeps notes here", "Tasha"))
		assertFalse(containsWholeTerm("BasicRulesDigest", "Basic Rules"))
	}

	private fun extractKotlinComments(source: String): String {
		return invokePrivate("extractKotlinComments", arrayOf(String::class.java), source) as String
	}

	private fun containsWholeTerm(text: String, term: String): Boolean {
		return invokePrivate(
			"containsWholeTerm",
			arrayOf(String::class.java, String::class.java),
			text,
			term
		) as Boolean
	}


	private fun invokePrivate(
		methodName: String,
		parameterTypes: Array<Class<*>>,
		vararg args: Any?
	): Any? {
		val method = audit.javaClass.getDeclaredMethod(methodName, *parameterTypes)
		method.isAccessible = true
		return method.invoke(audit, *args)
	}

	private fun readField(instance: Any, fieldName: String): Any? {
		val field = instance.javaClass.getDeclaredField(fieldName)
		field.isAccessible = true
		return field.get(instance)
	}
}
