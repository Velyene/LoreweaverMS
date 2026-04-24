package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentSafetyAuditGuardrailXmlTest {
	private val audit = ContentSafetyAuditTest()

	@Test
	fun extractXmlComments_collectsEveryCommentInOrder_includingMultilineBodies() {
		val source =
			"<resources>\n" +
				"    <!-- first provenance note -->\n" +
				"    <string name=\"safe_one\">Alpha</string>\n" +
				"    <!-- second provenance note\n" +
				"    that spans lines -->\n" +
				"</resources>"

		val comments =
			invokePrivate("extractXmlComments", arrayOf(String::class.java), source) as String

		assertEquals(
			listOf(
				"<!-- first provenance note -->",
				"<!-- second provenance note\n    that spans lines -->"
			).joinToString("\n"),
			comments
		)
	}

	@Test
	fun extractXmlTextBlocks_returnsOnlyStringBodies_withOriginalLineNumbers() {
		val source =
			"<resources>\n" +
				"    <!-- comment should not become prose -->\n" +
				"    <string name=\"first\">Alpha entry.</string>\n" +
				"    <string name=\"second\">\n" +
				"        Beta entry\n" +
				"        continues here.\n" +
				"    </string>\n" +
				"</resources>"

		@Suppress("UNCHECKED_CAST")
		val blocks =
			invokePrivate("extractXmlTextBlocks", arrayOf(String::class.java), source) as List<Any>

		assertEquals(2, blocks.size)
		assertEquals("xml string", readField(blocks[0], "kind"))
		assertEquals(3, readField(blocks[0], "lineNumber"))
		assertEquals("Alpha entry.", readField(blocks[0], "rawText"))
		assertEquals("xml string", readField(blocks[1], "kind"))
		assertEquals(4, readField(blocks[1], "lineNumber"))
		assertEquals(
			"\n        Beta entry\n        continues here.\n    ",
			readField(blocks[1], "rawText")
		)
	}

	@Test
	fun extractXmlTextBlocks_excludesXmlCommentsEntirely() {
		val source = """
			<resources>
				<!-- comment one -->
				<!-- comment two -->
			</resources>
		""".trimIndent()

		@Suppress("UNCHECKED_CAST")
		val blocks =
			invokePrivate("extractXmlTextBlocks", arrayOf(String::class.java), source) as List<Any>

		assertTrue(blocks.isEmpty())
	}

	@Test
	fun extractXmlTextBlocks_ignoresCommentedOutStringsAndStringArrays() {
		val source =
			"<resources>\n" +
				"    <!-- <string name=\"commented\">Should stay ignored.</string> -->\n" +
				"    <string-array name=\"rules\">\n" +
				"        <item>Should not be treated as a standalone string resource.</item>\n" +
				"    </string-array>\n" +
				"    <string name=\"actual\">Actual entry.</string>\n" +
				"</resources>"

		@Suppress("UNCHECKED_CAST")
		val blocks =
			invokePrivate("extractXmlTextBlocks", arrayOf(String::class.java), source) as List<Any>

		assertEquals(1, blocks.size)
		assertEquals("xml string", readField(blocks.single(), "kind"))
		assertEquals(6, readField(blocks.single(), "lineNumber"))
		assertEquals("Actual entry.", readField(blocks.single(), "rawText"))
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
