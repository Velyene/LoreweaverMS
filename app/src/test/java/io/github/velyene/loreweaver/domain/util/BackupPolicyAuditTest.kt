package io.github.velyene.loreweaver.domain.util

import io.github.velyene.loreweaver.data.AppDatabase
import io.github.velyene.loreweaver.di.AppModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

class BackupPolicyAuditTest {
	private companion object {
		const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
		const val MANIFEST_PATH = "src/main/AndroidManifest.xml"
		const val BACKUP_RULES_PATH = "src/main/res/xml/backup_rules.xml"
		const val DATA_EXTRACTION_RULES_PATH = "src/main/res/xml/data_extraction_rules.xml"
	}

	private val manifestFile = resolveProjectFile(MANIFEST_PATH)
	private val backupRulesFile = resolveProjectFile(BACKUP_RULES_PATH)
	private val dataExtractionRulesFile = resolveProjectFile(DATA_EXTRACTION_RULES_PATH)

	@Test
	fun manifest_declaresExpectedBackupRuleResources() {
		val application = parseXml(manifestFile).documentElement.getElementsByTagName("application").item(0) as Element

		assertEquals("true", application.getAttributeNS(ANDROID_NAMESPACE, "allowBackup"))
		assertEquals("false", application.getAttributeNS(ANDROID_NAMESPACE, "usesCleartextTraffic"))
		assertEquals("@xml/backup_rules", application.getAttributeNS(ANDROID_NAMESPACE, "fullBackupContent"))
		assertEquals("@xml/data_extraction_rules", application.getAttributeNS(ANDROID_NAMESPACE, "dataExtractionRules"))
	}

	@Test
	fun backupRules_cloudBackupRemainsPreferencesOnly() {
		val includes = parseXml(backupRulesFile)
			.documentElement
			.getElementsByTagName("include")
			.toElementList()
			.map { it.getAttribute("domain") to it.getAttribute("path") }

		assertEquals(listOf("sharedpref" to "${AppModule.REFERENCE_PREFERENCES_NAME}.xml"), includes)
	}

	@Test
	fun dataExtractionRules_preserveDocumentedCloudAndDeviceTransferPolicy() {
		val document = parseXml(dataExtractionRulesFile)
		val root = document.documentElement

		val cloudBackupIncludes = root.getElementsByTagName("cloud-backup")
			.item(0)
			.childNodes
			.toElementList()
			.filter { it.tagName == "include" }
			.map { it.getAttribute("domain") to it.getAttribute("path") }
		val deviceTransferIncludes = root.getElementsByTagName("device-transfer")
			.item(0)
			.childNodes
			.toElementList()
			.filter { it.tagName == "include" }
			.map { it.getAttribute("domain") to it.getAttribute("path") }

		assertEquals(listOf("sharedpref" to "${AppModule.REFERENCE_PREFERENCES_NAME}.xml"), cloudBackupIncludes)
		assertEquals(
			listOf(
				"database" to AppDatabase.DATABASE_NAME,
				"database" to "${AppDatabase.DATABASE_NAME}-wal",
				"database" to "${AppDatabase.DATABASE_NAME}-shm",
				"database" to "${AppDatabase.DATABASE_NAME}-journal",
				"sharedpref" to "${AppModule.REFERENCE_PREFERENCES_NAME}.xml",
			),
			deviceTransferIncludes,
		)
	}

	private fun parseXml(file: File): Document {
		assertTrue("Expected XML file to exist: ${file.path}", file.exists())

		val factory = DocumentBuilderFactory.newInstance().apply {
			isNamespaceAware = true
			setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
		}
		return factory.newDocumentBuilder().parse(file)
	}

	private fun resolveProjectFile(path: String): File = sequenceOf(
		File(path),
		File("app/$path"),
		File("../$path"),
		File("../app/$path"),
	).firstOrNull(File::exists)
		?: File(path)

	private fun org.w3c.dom.NodeList.toElementList(): List<Element> {
		val elements = mutableListOf<Element>()
		for (index in 0 until length) {
			val node = item(index)
			if (node is Element) {
				elements += node
			}
		}
		return elements
	}
}

