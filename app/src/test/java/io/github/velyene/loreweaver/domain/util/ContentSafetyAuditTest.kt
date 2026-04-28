/*
 * FILE: ContentSafetyAuditTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Audit configuration, allowlists, and file inventories
 * 2. Source-content provenance and forbidden-content checks
 * 3. Long-prose review coverage and reference-corpus inventory verification
 * 4. Exact SRD attribution notice verification
 */

package io.github.velyene.loreweaver.domain.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ContentSafetyAuditTest {
	private companion object {
		const val LONG_PROSE_REVIEW_HEADER =
			"Long embedded prose blocks need manual review or allowlisting"
		const val REFERENCE_CORPUS_INVENTORY_HEADER =
			"Excluded SRD/reference corpus prose inventory is out of sync"
		const val FORBIDDEN_FILE_HEADER =
			"Hard do-not-ship forbidden files are still present under app/src/main"

		const val README_PATH = "README.md"
		const val NOTICE_PATH = "NOTICE"
		const val SRD_NOTICE_PATH = "SRD_NOTICE.md"
		const val LEGAL_SOURCES_README_PATH = "legal_sources/README.md"
		const val HARD_DO_NOT_SHIP_AUDIT_PATH = "HARD_DO_NOT_SHIP_AUDIT.md"
		const val SRD_EQUIPMENT_ALLOWLIST_AUDIT_PATH = "SRD_EQUIPMENT_ALLOWLIST_AUDIT.md"
		const val SRD_GAMEPLAY_TOOLBOX_AUDIT_PATH = "SRD_GAMEPLAY_TOOLBOX_AUDIT.md"
		const val SRD_MAGIC_ITEMS_AUDIT_PATH = "SRD_MAGIC_ITEMS_AUDIT.md"
		const val SRD_MONSTERS_A_TO_Z_AUDIT_PATH = "SRD_MONSTERS_A_TO_Z_AUDIT.md"
		const val SRD_MONSTER_STAT_BLOCK_AUDIT_PATH = "SRD_MONSTER_STAT_BLOCK_AUDIT.md"
		const val REVIEWED_ON_2026_04_18 = "2026-04-18"
		const val REVIEWED_ON_2026_04_20 = "2026-04-20"
		const val REVIEWED_ON_2026_04_21 = "2026-04-21"
		const val FILE_EXTENSION_KOTLIN = "kt"
		const val FILE_EXTENSION_XML = "xml"
		const val NONE_LIST_ITEM = "- None"
		const val TRIPLE_QUOTED_STRING_DELIMITER = "\"\"\""
		const val INLINE_STRING_LITERAL_KIND = "inline string literal"

		val MARKDOWN_BANNED_PROVENANCE_TERMS = setOf(
			"Basic Rules",
			"D&D Beyond",
			"Player's Handbook",
			"Monster Manual",
			"Xanathar",
			"Xanathar's",
			"Tasha",
			"Tasha's"
		)

		val APP_OWNED_CONTENT_BANNED_PROVENANCE_TERMS = MARKDOWN_BANNED_PROVENANCE_TERMS +
			"2014 spellcasting chapter excerpt"

		val MARKDOWN_SUSPICIOUS_PROVENANCE_PHRASES = setOf(
			"2014",
			"provided excerpt",
			"sourced from",
			"copied from",
			"copied official text",
			"from d&d beyond",
			"from basic rules",
			"from player's handbook",
			"from monster manual",
			"from xanathar",
			"from tasha"
		)

		val SOURCE_COMMENT_HEADER_SUSPICIOUS_PROVENANCE_PHRASES =
			MARKDOWN_SUSPICIOUS_PROVENANCE_PHRASES

		val HARD_DO_NOT_SHIP_ALLOWED_BANNED_PROVENANCE_TERMS = MARKDOWN_BANNED_PROVENANCE_TERMS
		val BASIC_RULES_ALLOWED_BANNED_PROVENANCE_TERMS = setOf("Basic Rules")

		val HARD_DO_NOT_SHIP_ALLOWED_SUSPICIOUS_PROVENANCE_PHRASES = setOf(
			"2014",
			"copied from",
			"copied official text",
			"from basic rules",
			"sourced from"
		)
		val PROVIDED_EXCERPT_ALLOWED_SUSPICIOUS_PROVENANCE_PHRASES = setOf("provided excerpt")
	}

	private val sourceRoot: File = sequenceOf(
		File("src/main"),
		File("app/src/main")
	).firstOrNull(File::exists)
		?: error("Could not locate app source root for content audit")

	private val srdNoticeFile: File = sequenceOf(
		File(SRD_NOTICE_PATH),
		File("../$SRD_NOTICE_PATH")
	).firstOrNull(File::exists)
		?: File(SRD_NOTICE_PATH)

	private val projectRoot: File = sequenceOf(
		File("."),
		File("..")
	).map { it.canonicalFile }
		.firstOrNull { candidate ->
			File(candidate, "settings.gradle.kts").exists() || File(
				candidate,
				SRD_NOTICE_PATH
			).exists()
		}
		?: File(".").absoluteFile

	private val referenceCorpusAuditSnapshotFile =
		File(projectRoot, "EXCLUDED_REFERENCE_CORPUS_AUDIT.md")

	private val markdownAndLegalAuditFiles = listOf(
		File(projectRoot, README_PATH),
		File(projectRoot, NOTICE_PATH),
		File(projectRoot, SRD_NOTICE_PATH),
		File(projectRoot, LEGAL_SOURCES_README_PATH),
		File(projectRoot, HARD_DO_NOT_SHIP_AUDIT_PATH),
		File(projectRoot, SRD_EQUIPMENT_ALLOWLIST_AUDIT_PATH),
		File(projectRoot, SRD_GAMEPLAY_TOOLBOX_AUDIT_PATH),
		File(projectRoot, SRD_MAGIC_ITEMS_AUDIT_PATH),
		File(projectRoot, SRD_MONSTERS_A_TO_Z_AUDIT_PATH),
		File(projectRoot, SRD_MONSTER_STAT_BLOCK_AUDIT_PATH)
	).filter(File::exists)

	private val allowedBannedProvenanceTermsByMarkdownFile = mapOf(
		HARD_DO_NOT_SHIP_AUDIT_PATH to HARD_DO_NOT_SHIP_ALLOWED_BANNED_PROVENANCE_TERMS,
		SRD_GAMEPLAY_TOOLBOX_AUDIT_PATH to BASIC_RULES_ALLOWED_BANNED_PROVENANCE_TERMS,
		SRD_MAGIC_ITEMS_AUDIT_PATH to BASIC_RULES_ALLOWED_BANNED_PROVENANCE_TERMS
	)

	private val allowedSuspiciousProvenanceTermsByMarkdownFile = mapOf(
		HARD_DO_NOT_SHIP_AUDIT_PATH to HARD_DO_NOT_SHIP_ALLOWED_SUSPICIOUS_PROVENANCE_PHRASES,
		SRD_GAMEPLAY_TOOLBOX_AUDIT_PATH to PROVIDED_EXCERPT_ALLOWED_SUSPICIOUS_PROVENANCE_PHRASES,
		SRD_MAGIC_ITEMS_AUDIT_PATH to PROVIDED_EXCERPT_ALLOWED_SUSPICIOUS_PROVENANCE_PHRASES,
		SRD_MONSTERS_A_TO_Z_AUDIT_PATH to PROVIDED_EXCERPT_ALLOWED_SUSPICIOUS_PROVENANCE_PHRASES,
		SRD_MONSTER_STAT_BLOCK_AUDIT_PATH to PROVIDED_EXCERPT_ALLOWED_SUSPICIOUS_PROVENANCE_PHRASES
	)

	private data class AuditFinding(
		val term: String,
		val relativePath: String
	)

	private data class ProseFinding(
		val relativePath: String,
		val kind: String,
		val lineNumber: Int,
		val charCount: Int,
		val lineCount: Int,
		val sentenceCount: Int,
		val preview: String
	)

	private data class ExtractedTextBlock(
		val kind: String,
		val lineNumber: Int,
		val rawText: String
	)

	private data class KotlinStringToken(
		val kind: String,
		val lineNumber: Int,
		val content: String,
		val startIndex: Int,
		val endIndexExclusive: Int
	)

	private data class ReviewedProseFile(
		val reviewedOn: String,
		val rationale: String,
		val followUpNote: String
	)

	private data class ReferenceCorpusProseFile(
		val inventoriedOn: String,
		val rationale: String,
		val followUpNote: String
	)

	private data class ReferenceCorpusFindingRollup(
		val relativePath: String,
		val findings: List<ProseFinding>,
		val priorityLabel: String,
		val maxCharCount: Int,
		val averageCharCount: Int,
		val representativeFindings: List<ProseFinding>
	)

	private val reviewedLongProseFiles = mapOf(
		// These files were explicitly reviewed in this chat and are treated as the current trusted baseline.
		// Pre-existing files are NOT grandfathered automatically; they must be individually reviewed and listed here.
		"java/io/github/velyene/loreweaver/domain/util/EncounterDifficulty.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_18,
			rationale = "Reviewed in this chat and confirmed by the user as safe baseline content.",
			followUpNote = "Only re-review if it starts carrying longer explanatory prose or blocked branded terms."
		),
		// Reviewed during the strict-mode copyright-safety pass; mostly original guidance and summaries.
		"java/io/github/velyene/loreweaver/domain/util/CharacterCreationReference.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_18,
			rationale = "Character-building guidance was rewritten into setting-neutral original prose.",
			followUpNote = "Re-review if new example characters or campaign-setting references are added."
		),
		// Short guidance blocks and disease summaries were paraphrased and intentionally retained.
		"java/io/github/velyene/loreweaver/domain/util/DiseaseReference.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_18,
			rationale = "Contains compact original disease-management guidance and short disease summaries.",
			followUpNote = "Keep future additions concise and mechanically focused."
		),
		// Poison entries were realigned to canonical Gameplay Toolbox names while keeping the prose compact and mechanical.
		"java/io/github/velyene/loreweaver/domain/util/PoisonReference.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_20,
			rationale = "Contains reviewed poison summaries using canonical Gameplay Toolbox names with concise mechanics-forward wording.",
			followUpNote = "Keep future poison additions short, mechanical, and tied to audited SRD scope."
		),
		// Trap entries were realigned to canonical Gameplay Toolbox names while keeping the prose compact and mechanical.
		"java/io/github/velyene/loreweaver/domain/util/TrapReference.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_20,
			rationale = "Contains reviewed trap summaries using canonical Gameplay Toolbox names with concise mechanics-forward wording.",
			followUpNote = "Keep future trap additions short, mechanical, and tied to audited SRD scope."
		),
		// Core system digest was added as a concise, reviewed reference layer for table use.
		"java/io/github/velyene/loreweaver/domain/util/CoreRulesReference.kt" to ReviewedProseFile(
			reviewedOn = "2026-04-19",
			rationale = "Contains reviewed condensed core-rules summaries and lookup tables for the reference UI.",
			followUpNote = "Keep future additions procedural and compact rather than book-style prose."
		),
		// Large item/rules reference file contains many reviewed explanatory blocks that are still useful UI content.
		"java/io/github/velyene/loreweaver/domain/util/EquipmentReference.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_18,
			rationale = "Contains reviewed explanatory reference cards and item guidance used directly by the rules UI.",
			followUpNote = "Prefer adding short summaries instead of book-style prose."
		),
		"java/io/github/velyene/loreweaver/domain/util/EquipmentReferenceGearData.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_21,
			rationale = "Split equipment gear summaries were reviewed as part of the shared dataset extraction and remain concise, mechanics-focused reference text.",
			followUpNote = "Keep future gear notes short and oriented toward search-friendly rules guidance."
		),
		"java/io/github/velyene/loreweaver/domain/util/EquipmentReferenceMagicItemsData.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_21,
			rationale = "Split magic-item dataset was re-reviewed after the extraction repair to ensure the retained prose is compact reference material rather than copied long-form text.",
			followUpNote = "Prefer table-backed mechanics and short paraphrases when expanding individual item entries."
		),
		"java/io/github/velyene/loreweaver/domain/util/EquipmentReferenceSectionsData.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_21,
			rationale = "Section-level equipment summaries were reviewed after the file split and remain short original explanatory text.",
			followUpNote = "Re-review if chapter summaries become substantially longer or more quote-like."
		),
		"java/io/github/velyene/loreweaver/domain/util/EquipmentReferenceSupplementalData.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_21,
			rationale = "Supplemental equipment tables include compact descriptive cells that were reviewed during the split stabilization pass.",
			followUpNote = "Keep future table prose terse and mechanically descriptive."
		),
		"java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataAtoC.kt" to ReviewedProseFile(
			reviewedOn = "2026-04-27",
			rationale = "Restored local monster-reference entries for the shipped A-C slice were reviewed in this chat as mechanical SRD-backed content.",
			followUpNote = "Keep future imports sectioned, searchable, and limited to reviewed SRD-backed monster data."
		),
		"java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataAnimals.kt" to ReviewedProseFile(
			reviewedOn = "2026-04-28",
			rationale = "Animal stat blocks added from reviewed SRD-backed reference content are parsed into the local monster corpus for search and encounter use.",
			followUpNote = "Keep future animal additions limited to reviewed SRD-backed stat blocks or similarly concise mechanics-forward content."
		),
		"java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataDtoG.kt" to ReviewedProseFile(
			reviewedOn = "2026-04-27",
			rationale = "Restored local monster-reference entries for the shipped D-G slice were reviewed in this chat as mechanical SRD-backed content.",
			followUpNote = "Keep future imports sectioned, searchable, and limited to reviewed SRD-backed monster data."
		),
		"java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataHtoN.kt" to ReviewedProseFile(
			reviewedOn = "2026-04-27",
			rationale = "Restored local monster-reference entries for the shipped H-N slice were reviewed in this chat as mechanical SRD-backed content.",
			followUpNote = "Keep future imports sectioned, searchable, and limited to reviewed SRD-backed monster data."
		),
		"java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataOtoR.kt" to ReviewedProseFile(
			reviewedOn = "2026-04-27",
			rationale = "Restored local monster-reference entries for the shipped O-R slice were reviewed in this chat as mechanical SRD-backed content.",
			followUpNote = "Keep future imports sectioned, searchable, and limited to reviewed SRD-backed monster data."
		),
		"java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataStoW.kt" to ReviewedProseFile(
			reviewedOn = "2026-04-27",
			rationale = "Restored local monster-reference entries for the shipped S-W slice were reviewed in this chat as mechanical SRD-backed content.",
			followUpNote = "Keep future imports sectioned, searchable, and limited to reviewed SRD-backed monster data."
		),
		"java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataXtoZ.kt" to ReviewedProseFile(
			reviewedOn = "2026-04-28",
			rationale = "Restored local monster-reference entries for the shipped X-Z slice were reviewed in this chat as mechanical SRD-backed content.",
			followUpNote = "Keep future imports sectioned, searchable, and limited to reviewed SRD-backed monster data."
		),
		// Mental strain tables and GM guidance were rewritten into more original summaries.
		"java/io/github/velyene/loreweaver/domain/util/MadnessReference.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_18,
			rationale = "Long-form horror guidance and table text were paraphrased during the strict-mode pass.",
			followUpNote = "Watch for new tables that read too close to source-book phrasing."
		),
		// Object durability notes are long enough to trigger heuristics but have already been manually compressed.
		"java/io/github/velyene/loreweaver/domain/util/ObjectStats.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_18,
			rationale = "Contains reviewed object-durability guidance blocks written in concise original language.",
			followUpNote = "Keep future large guidance sections short and procedural."
		),
		// Setting-neutral spellcasting theory and reference guidance are intentionally retained for the UI.
		"java/io/github/velyene/loreweaver/domain/util/SpellcastingReference.kt" to ReviewedProseFile(
			reviewedOn = REVIEWED_ON_2026_04_18,
			rationale = "Contains reviewed setting-neutral magic theory and spellcasting help text.",
			followUpNote = "Avoid reintroducing branded lore or long lore-forward explanations."
		)
	)

	// Current post-quarantine shipped state: no excluded SRD/reference corpus files remain under app/src/main.
	private val excludedReferenceCorpusProseFiles = emptyMap<String, ReferenceCorpusProseFile>()

	@Test
	fun brandedSettingReferences_areAbsentFromAppOwnedContent() {
		val blockedTerms = setOf(
			"D&D",
			"D&amp;D",
			"Dungeons & Dragons",
			"Dungeon Master's Guide",
			"Player's Handbook",
			"Forgotten Realms",
			"Mystra",
			"Bruenor",
			"Mithral Hall",
			"Calishite",
			"Chondathan",
			"Damaran",
			"Illuskan",
			"Mulan",
			"Rashemi",
			"Shou",
			"Tethyrian",
			"Turami",
			"City of Brass",
			"Dispater",
			"Street of Steel",
			"Gate of Ashes",
			"Sea of Fire",
			"The Weave of Magic"
		)

		val findings = findBlockedTerms(blockedTerms)
		assertTrue(
			formatFindings("Branded setting references found in app-owned content", findings),
			findings.isEmpty()
		)
	}

	@Test
	fun proprietaryLegacySpellNames_areAbsentFromAppOwnedContent() {
		val blockedTerms = setOf(
			"Melfâ€™s Acid Arrow",
			"Melf's Acid Arrow",
			"Mordenkainenâ€™s Sword",
			"Mordenkainen's Sword",
			"Ottoâ€™s Irresistible Dance",
			"Otto's Irresistible Dance",
			"Nystulâ€™s Magic Aura",
			"Nystul's Magic Aura",
			"Evardâ€™s Black Tentacles",
			"Evard's Black Tentacles",
			"Mordenkainenâ€™s Faithful Hound",
			"Mordenkainen's Faithful Hound",
			"Tenserâ€™s Floating Disk",
			"Tenser's Floating Disk",
			"Otilukeâ€™s Freezing Sphere",
			"Otiluke's Freezing Sphere",
			"Tashaâ€™s Hideous Laughter",
			"Tasha's Hideous Laughter",
			"Drow Poison"
		)

		val findings = findBlockedTerms(blockedTerms)
		assertTrue(
			formatFindings("Legacy proprietary spell names found in app-owned content", findings),
			findings.isEmpty()
		)
	}

	@Test
	fun bannedSourceProvenanceMarkers_areAbsentFromAppOwnedContent() {
		val findings = findBlockedTerms(blockedTerms = APP_OWNED_CONTENT_BANNED_PROVENANCE_TERMS)
		assertTrue(
			formatFindings("Banned source/provenance markers found in app-owned content", findings),
			findings.isEmpty()
		)
	}

	@Test
	fun selectedMarkdownAndLegalFiles_areFreeOfBannedProvenanceMarkers() {
		val findings = findBlockedTermsInRepoFiles(
			files = markdownAndLegalAuditFiles,
			blockedTerms = MARKDOWN_BANNED_PROVENANCE_TERMS,
			allowedTermsByRelativePath = allowedBannedProvenanceTermsByMarkdownFile
		)

		assertTrue(
			formatFindings(
				"Banned provenance markers found in selected markdown/legal files",
				findings
			),
			findings.isEmpty()
		)
	}

	@Test
	fun hardDoNotShipFlaggedFiles_areAbsentFromShippedSource() {
		val discoveredFlaggedFiles = sourceRoot
			.walkTopDown()
			.filter { file -> file.isFile && file.extension == FILE_EXTENSION_KOTLIN }
			.map(::normalizePath)
			.filter(::isHardDoNotShipFlaggedPath)
			.toSet()

		assertTrue(
			formatForbiddenFileFindings(discoveredFlaggedFiles),
			discoveredFlaggedFiles.isEmpty()
		)
	}

	@Test
	fun suspiciousSourceCommentsAndHeaders_areAbsentFromShippedSource() {
		val findings = findBlockedTermsInComments(
			blockedTerms = SOURCE_COMMENT_HEADER_SUSPICIOUS_PROVENANCE_PHRASES
		)

		assertTrue(
			formatFindings("Suspicious source comments/headers found in shipped source", findings),
			findings.isEmpty()
		)
	}

	@Test
	fun selectedMarkdownAndLegalFiles_areFreeOfSuspiciousNonCommentProvenancePhrases() {
		val findings = findBlockedTermsInRepoFiles(
			files = markdownAndLegalAuditFiles,
			blockedTerms = MARKDOWN_SUSPICIOUS_PROVENANCE_PHRASES,
			allowedTermsByRelativePath = allowedSuspiciousProvenanceTermsByMarkdownFile,
			textTransform = String::lowercase
		)

		assertTrue(
			formatFindings(
				"Suspicious provenance phrases found in selected markdown/legal files",
				findings
			),
			findings.isEmpty()
		)
	}

	@Test
	fun longEmbeddedProseBlocks_areReviewedOrAbsent() {
		val unreviewedFindings = findLongProseBlocks { relativePath ->
			relativePath !in excludedReferenceCorpusProseFiles
		}.filterNot { finding ->
			finding.relativePath in reviewedLongProseFiles
		}

		assertTrue(
			formatProseFindings(unreviewedFindings),
			unreviewedFindings.isEmpty()
		)
	}

	@Test
	fun excludedReferenceCorpusLongProse_isExplicitlyInventoried() {
		val discoveredCorpusPaths = discoverReferenceCorpusPaths()
		val missingInventoryEntries = discoveredCorpusPaths - excludedReferenceCorpusProseFiles.keys
		val staleInventoryEntries = excludedReferenceCorpusProseFiles.keys - discoveredCorpusPaths

		val findings = findLongProseBlocks(::isReferenceCorpusPath)
		val uninventoriedFindingPaths = findings.map(ProseFinding::relativePath)
			.toSet() - excludedReferenceCorpusProseFiles.keys

		writeReferenceCorpusAuditSnapshot(
			discoveredCorpusPaths = discoveredCorpusPaths,
			findings = findings,
			missingInventoryEntries = missingInventoryEntries,
			staleInventoryEntries = staleInventoryEntries,
			uninventoriedFindingPaths = uninventoriedFindingPaths
		)

		assertTrue(
			formatReferenceCorpusInventory(
				discoveredCorpusPaths = discoveredCorpusPaths,
				findings = findings,
				missingInventoryEntries = missingInventoryEntries,
				staleInventoryEntries = staleInventoryEntries,
				uninventoriedFindingPaths = uninventoriedFindingPaths
			),
			missingInventoryEntries.isEmpty() &&
				staleInventoryEntries.isEmpty() &&
				uninventoriedFindingPaths.isEmpty()
		)
	}

	@Test
	fun projectIncludesExactSrdAttributionNotice() {
		assertTrue(
			"Expected SRD attribution notice at ${srdNoticeFile.path}",
			srdNoticeFile.exists()
		)

		val noticeText = srdNoticeFile.readText()
		val normalizedNoticeText = noticeText.replace(Regex("\\s+"), " ").trim()
		val requiredAttribution = listOf(
			"This work includes material from the System Reference Document 5.2.1 (\"SRD 5.2.1\")",
			"by Wizards of the Coast LLC, available at https://www.dndbeyond.com/srd.",
			"The SRD 5.2.1 is licensed under the Creative Commons Attribution 4.0",
			"International License, available at https://creativecommons.org/licenses/by/4.0/legalcode."
		).joinToString(" ")

		assertTrue(
			"SRD notice must include the exact CC-BY attribution statement provided for this project.",
			normalizedNoticeText.contains(requiredAttribution)
		)
	}

	private fun findBlockedTerms(
		blockedTerms: Set<String>,
		isAllowedPath: (String) -> Boolean = { false }
	): List<AuditFinding> {
		return sourceRoot
			.walkTopDown()
			.filter { file ->
				file.isFile && (file.extension == FILE_EXTENSION_KOTLIN || file.extension == FILE_EXTENSION_XML)
			}
			.flatMap { file ->
				val relativePath = normalizePath(file)
				if (isAllowedPath(relativePath)) {
					emptySequence()
				} else {
					val text = file.readText()
					blockedTerms.asSequence()
						.filter { term -> containsWholeTerm(text, term) }
						.map { term -> AuditFinding(term = term, relativePath = relativePath) }
				}
			}
			.sortedWith(compareBy(AuditFinding::term, AuditFinding::relativePath))
			.toList()
	}

	private fun findBlockedTermsInComments(blockedTerms: Set<String>): List<AuditFinding> {
		return sourceRoot
			.walkTopDown()
			.filter { file -> file.isFile && (file.extension == FILE_EXTENSION_KOTLIN || file.extension == FILE_EXTENSION_XML) }
			.flatMap { file ->
				val relativePath = normalizePath(file)
				val commentText = when (file.extension) {
					FILE_EXTENSION_KOTLIN -> extractKotlinComments(file.readText())
					FILE_EXTENSION_XML -> extractXmlComments(file.readText())
					else -> ""
				}

				blockedTerms.asSequence()
					.filter { term -> containsWholeTerm(commentText.lowercase(), term.lowercase()) }
					.map { term -> AuditFinding(term = term, relativePath = relativePath) }
			}
			.sortedWith(compareBy(AuditFinding::term, AuditFinding::relativePath))
			.toList()
	}

	private fun findBlockedTermsInRepoFiles(
		files: List<File>,
		blockedTerms: Set<String>,
		allowedTermsByRelativePath: Map<String, Set<String>> = emptyMap(),
		textTransform: (String) -> String = { it }
	): List<AuditFinding> {
		return files.asSequence()
			.flatMap { file ->
				val relativePath = normalizeProjectPath(file)
				val text = textTransform(
					stripNonContentMarkup(
						file.readText(),
						file.extension.lowercase()
					)
				)
				val allowedTerms = allowedTermsByRelativePath[relativePath].orEmpty()
					.map(textTransform)
					.toSet()
				blockedTerms.asSequence()
					.filter { term ->
						val transformedTerm = textTransform(term)
						transformedTerm !in allowedTerms && containsWholeTerm(text, transformedTerm)
					}
					.map { term -> AuditFinding(term = term, relativePath = relativePath) }
			}
			.sortedWith(compareBy(AuditFinding::term, AuditFinding::relativePath))
			.toList()
	}

	private fun findLongProseBlocks(includePath: (String) -> Boolean = { true }): List<ProseFinding> {
		return sourceRoot
			.walkTopDown()
			.filter { file ->
				file.isFile && (file.extension == FILE_EXTENSION_KOTLIN || file.extension == FILE_EXTENSION_XML)
			}
			.flatMap { file ->
				val relativePath = normalizePath(file)
				if (!includePath(relativePath)) {
					return@flatMap emptySequence()
				}

				val text = file.readText()
				val extractedBlocks = when (file.extension) {
					FILE_EXTENSION_KOTLIN -> extractKotlinTextBlocks(text)
					FILE_EXTENSION_XML -> extractXmlTextBlocks(text)
					else -> emptyList()
				}

				extractedBlocks.asSequence().mapNotNull { block ->
					block.toProseFinding(relativePath)
				}
			}
			.sortedWith(
				compareBy(
					ProseFinding::relativePath,
					ProseFinding::lineNumber,
					ProseFinding::kind,
					ProseFinding::charCount
				)
			)
			.toList()
	}

	private fun discoverReferenceCorpusPaths(): Set<String> {
		return sourceRoot
			.walkTopDown()
			.filter { file -> file.isFile && file.extension == FILE_EXTENSION_KOTLIN }
			.map(::normalizePath)
			.filter(::isReferenceCorpusPath)
			.toSet()
	}

	private fun extractXmlTextBlocks(text: String): List<ExtractedTextBlock> {
		val extractedBlocks = mutableListOf<ExtractedTextBlock>()
		var index = 0

		while (index < text.length) {
			when {
				text.startsWith("<!--", index) -> {
					index = advancePastXmlComment(text, index)
				}

				text.startsWith("<string", index) && isXmlStringTagStart(text, index) -> {
					val tagEnd = findXmlTagEnd(text, index) ?: break
					if (text[tagEnd - 1] == '/') {
						index = tagEnd + 1
						continue
					}

					val contentStart = tagEnd + 1
					val closingTagStart = text.indexOf("</string>", contentStart)
					if (closingTagStart == -1) {
						index = contentStart
						continue
					}

					extractedBlocks += ExtractedTextBlock(
						kind = "xml string",
						lineNumber = lineNumberForIndex(text, index),
						rawText = text.substring(contentStart, closingTagStart)
					)
					index = closingTagStart + "</string>".length
				}

				else -> index++
			}
		}

		return extractedBlocks
	}

	private fun isXmlStringTagStart(text: String, index: Int): Boolean {
		val nextCharIndex = index + "<string".length
		if (nextCharIndex >= text.length) return false

		return when (text[nextCharIndex]) {
			' ', '\t', '\r', '\n', '>' -> true
			else -> false
		}
	}

	private fun findXmlTagEnd(text: String, startIndex: Int): Int? {
		var index = startIndex
		var quoteChar: Char? = null

		while (index < text.length) {
			when (val current = text[index]) {
				quoteChar -> quoteChar = null
				'"', '\'' -> if (quoteChar == null) quoteChar = current
				'>' -> if (quoteChar == null) return index
			}
			index++
		}

		return null
	}

	private fun advancePastXmlComment(text: String, startIndex: Int): Int {
		val commentEnd = text.indexOf("-->", startIndex + 4)
		return if (commentEnd == -1) text.length else commentEnd + 3
	}

	private fun extractKotlinTextBlocks(text: String): List<ExtractedTextBlock> {
		val tokens = mutableListOf<KotlinStringToken>()
		var index = 0
		var lineNumber = 1

		while (index < text.length) {
			when {
				text.startsWith("//", index) -> {
					index += 2
					while (index < text.length && text[index] != '\n') {
						index++
					}
				}

				text.startsWith("/*", index) -> {
					index += 2
					while (index < text.length && !text.startsWith("*/", index)) {
						if (text[index] == '\n') lineNumber++
						index++
					}
					if (index < text.length) index += 2
				}

				text.startsWith(TRIPLE_QUOTED_STRING_DELIMITER, index) -> {
					val startIndex = index
					val startLine = lineNumber
					index += 3
					val contentStart = index
					while (index < text.length && !text.startsWith(TRIPLE_QUOTED_STRING_DELIMITER, index)) {
						if (text[index] == '\n') lineNumber++
						index++
					}
					val contentEnd = index.coerceAtMost(text.length)
					if (index < text.length) index += 3
					tokens += KotlinStringToken(
						kind = "triple-quoted string",
						lineNumber = startLine,
						content = text.substring(contentStart, contentEnd),
						startIndex = startIndex,
						endIndexExclusive = index
					)
				}

				text[index] == '"' -> {
					val startIndex = index
					val startLine = lineNumber
					index++
					val content = StringBuilder()
					while (index < text.length) {
						val current = text[index]
						when {
							current == '\\' && index + 1 < text.length -> {
								val next = text[index + 1]
								content.append(
									when (next) {
										'n' -> '\n'
										't' -> '\t'
										'r' -> '\r'
										else -> next
									}
								)
								index += 2
							}

							current == '"' -> {
								index++
								break
							}

							else -> {
								content.append(current)
								if (current == '\n') lineNumber++
								index++
							}
						}
					}
					tokens += KotlinStringToken(
						kind = "inline string literal",
						lineNumber = startLine,
						content = content.toString(),
						startIndex = startIndex,
						endIndexExclusive = index
					)
				}

				else -> {
					if (text[index] == '\n') lineNumber++
					index++
				}
			}
		}

		val combinedInlineTokens = combineAdjacentInlineTokens(
			tokens = tokens.filter { it.kind == "inline string literal" },
			sourceText = text
		)

		return buildList {
			addAll(tokens.filter { it.kind == "triple-quoted string" }.map { token ->
				ExtractedTextBlock(
					kind = token.kind,
					lineNumber = token.lineNumber,
					rawText = token.content
				)
			})
			addAll(combinedInlineTokens.map { token ->
				ExtractedTextBlock(
					kind = token.kind,
					lineNumber = token.lineNumber,
					rawText = token.content
				)
			})
		}
	}

	private fun combineAdjacentInlineTokens(
		tokens: List<KotlinStringToken>,
		sourceText: String
	): List<KotlinStringToken> {
		if (tokens.isEmpty()) return emptyList()

		val combinedTokens = mutableListOf<KotlinStringToken>()
		var current = tokens.first()

		tokens.drop(1).forEach { next ->
			val separator = sourceText.substring(current.endIndexExclusive, next.startIndex)
			if (separator.matches(Regex("[\\s+()]*"))) {
				current = current.copy(
					content = current.content + next.content,
					endIndexExclusive = next.endIndexExclusive
				)
			} else {
				combinedTokens += current
				current = next
			}
		}

		combinedTokens += current
		return combinedTokens
	}

	private fun ExtractedTextBlock.toProseFinding(relativePath: String): ProseFinding? {
		val normalized = normalizeBlockText(rawText)
		if (normalized.isBlank()) return null

		val charCount = normalized.length
		val lineCount = normalized.lines().count { it.isNotBlank() }
		val sentenceCount = Regex("[.!?]").findAll(normalized).count()

		if (!isReviewWorthyProse(normalized, kind, lineCount, sentenceCount)) return null

		return ProseFinding(
			relativePath = relativePath,
			kind = kind,
			lineNumber = lineNumber,
			charCount = charCount,
			lineCount = lineCount,
			sentenceCount = sentenceCount,
			preview = normalized.take(100)
		)
	}

	private fun normalizeBlockText(rawBlock: String): String {
		return rawBlock
			.trim()
			.lines()
			.joinToString("\n") { it.trim() }
			.trim()
	}

	private fun isReviewWorthyProse(
		text: String,
		kind: String,
		lineCount: Int,
		sentenceCount: Int
	): Boolean {
		if (kind != INLINE_STRING_LITERAL_KIND) {
			return (text.length >= 400 || lineCount >= 5) && sentenceCount >= 3
		}

		val wordMatches = Regex("\\b\\p{L}[\\p{L}'â€™-]*\\b").findAll(text).toList()
		val wordCount = wordMatches.size
		if (wordCount < 16) return false
		if (sentenceCount == 0) return false

		if (!text.contains(' ')) return false
		if (text.contains("%1$") || text.contains("%2$") || text.contains("%3$") || text.contains($$"${")) return false
		if (text.contains("://")) return false

		val lettersOnly = text.filter(Char::isLetter)
		if (lettersOnly.isNotEmpty() && lettersOnly.all { it.isUpperCase() }) return false

		val lowercaseWordRatio =
			wordMatches.count { match -> match.value.any(Char::isLowerCase) }.toDouble() / wordCount
		val clauseSeparatorCount = text.count { it == ',' || it == ';' || it == ':' }

		val mediumMultiSentence = text.length >= 120 && sentenceCount >= 2
		val longSingleSentence = text.length >= 220 && wordCount >= 30 && lowercaseWordRatio >= 0.6
		val clauseHeavySummary =
			text.length >= 170 && wordCount >= 24 && clauseSeparatorCount >= 2 && lowercaseWordRatio >= 0.6

		return mediumMultiSentence || longSingleSentence || clauseHeavySummary
	}

	private fun isReferenceCorpusPath(@Suppress("UNUSED_PARAMETER") relativePath: String): Boolean {
		return false
	}

	private fun isHardDoNotShipFlaggedPath(relativePath: String): Boolean {
		return relativePath == "java/io/github/velyene/loreweaver/domain/util/ClassSpellListReference.kt" ||
			relativePath == "java/io/github/velyene/loreweaver/domain/util/CharacterClassReference.kt" ||
			relativePath == "java/io/github/velyene/loreweaver/domain/util/MonsterReference.kt" ||
			relativePath.substringAfterLast('/').startsWith("SpellDescriptionReference")
	}

	private fun lineNumberForIndex(text: String, index: Int): Int {
		return text.take(index).count { it == '\n' } + 1
	}

	private fun normalizePath(file: File): String {
		return sourceRoot.toPath().relativize(file.toPath()).toString().replace('\\', '/')
	}

	private fun normalizeProjectPath(file: File): String {
		return projectRoot.toPath().relativize(file.canonicalFile.toPath()).toString()
			.replace('\\', '/')
	}

	private fun stripNonContentMarkup(text: String, extension: String): String {
		return when (extension) {
			"md", "markdown" -> text.replace(Regex("<!--([\\s\\S]*?)-->"), " ")
			else -> text
		}
	}

	private fun containsWholeTerm(text: String, term: String): Boolean {
		val pattern = Regex("(?<![\\p{L}\\p{N}])${Regex.escape(term)}(?![\\p{L}\\p{N}])")
		return pattern.containsMatchIn(text)
	}

	private fun formatFindings(header: String, findings: List<AuditFinding>): String {
		return if (findings.isEmpty()) {
			header
		} else {
			buildString {
				appendLine(header)
				findings.forEach { finding ->
					appendLine("- ${finding.term} -> ${finding.relativePath}")
				}
			}.trimEnd()
		}
	}

	private fun formatProseFindings(findings: List<ProseFinding>): String {
		return if (findings.isEmpty()) {
			buildString {
				appendLine(LONG_PROSE_REVIEW_HEADER)
				appendLine("Reviewed long-prose files:")
				reviewedLongProseFiles.forEach { (path, metadata) ->
					appendLine("- $path [reviewed ${metadata.reviewedOn}] ${metadata.rationale}")
				}
			}.trimEnd()
		} else {
			buildString {
				appendLine(LONG_PROSE_REVIEW_HEADER)
				findings.forEach { finding ->
					appendLine(
						"- ${finding.relativePath}:${finding.lineNumber} [${finding.kind}] chars=${finding.charCount}, lines=${finding.lineCount}, sentences=${finding.sentenceCount} :: ${finding.preview}"
					)
				}
			}.trimEnd()
		}
	}

	private fun formatReferenceCorpusInventory(
		discoveredCorpusPaths: Set<String>,
		findings: List<ProseFinding>,
		missingInventoryEntries: Set<String>,
		staleInventoryEntries: Set<String>,
		uninventoriedFindingPaths: Set<String>
	): String {
		return buildString {
			val groupedFindings = findings
				.groupBy(ProseFinding::relativePath)
				.toSortedMap()

			appendLine(REFERENCE_CORPUS_INVENTORY_HEADER)
			appendLine(
				"Summary: discoveredFiles=${discoveredCorpusPaths.size}, findings=${findings.size}, fileGroups=${groupedFindings.size}, missingInventory=${missingInventoryEntries.size}, staleInventory=${staleInventoryEntries.size}, uninventoriedPaths=${uninventoriedFindingPaths.size}"
			)

			if (missingInventoryEntries.isNotEmpty()) {
				appendLine("Missing inventory entries:")
				missingInventoryEntries.sorted().forEach { path ->
					appendLine("- $path")
				}
			}

			if (staleInventoryEntries.isNotEmpty()) {
				appendLine("Stale inventory entries:")
				staleInventoryEntries.sorted().forEach { path ->
					appendLine("- $path")
				}
			}

			if (uninventoriedFindingPaths.isNotEmpty()) {
				appendLine("Uninventoried finding paths:")
				uninventoriedFindingPaths.sorted().forEach { path ->
					appendLine("- $path")
				}
			}

			appendLine("Current excluded SRD/reference prose inventory:")
			discoveredCorpusPaths.sorted().forEach { path ->
				val metadata = excludedReferenceCorpusProseFiles[path]
				if (metadata == null) {
					appendLine("- $path [missing inventory metadata]")
					return@forEach
				}

				appendLine("- $path [inventoried ${metadata.inventoriedOn}] ${metadata.rationale}")
				appendLine("  Follow-up: ${metadata.followUpNote}")
			}

			if (groupedFindings.isNotEmpty()) {
				appendLine("Current findings within the excluded SRD/reference corpus:")
				groupedFindings.forEach { (path, pathFindings) ->
					appendLine("- $path :: findings=${pathFindings.size}")
					pathFindings
						.sortedWith(
							compareBy(
								ProseFinding::lineNumber,
								ProseFinding::kind,
								ProseFinding::charCount
							)
						)
						.forEach { finding ->
							appendLine(
								"  - line ${finding.lineNumber} [${finding.kind}] chars=${finding.charCount}, lines=${finding.lineCount}, sentences=${finding.sentenceCount} :: ${finding.preview}"
							)
						}
				}
			}
		}.trimEnd()
	}

	private fun formatForbiddenFileFindings(discoveredFlaggedFiles: Set<String>): String {
		return buildString {
			appendLine(FORBIDDEN_FILE_HEADER)
			appendLine("Discovered flagged files: ${discoveredFlaggedFiles.size}")
			if (discoveredFlaggedFiles.isEmpty()) {
				appendLine(NONE_LIST_ITEM)
			} else {
				discoveredFlaggedFiles.sorted().forEach { path ->
					appendPathBullet(path)
				}
			}
		}.trimEnd()
	}

	private fun extractKotlinComments(text: String): String {
		val comments = mutableListOf<String>()
		var index = 0

		while (index < text.length) {
			when {
				text.startsWith(TRIPLE_QUOTED_STRING_DELIMITER, index) -> {
					index += 3
					while (index < text.length && !text.startsWith(TRIPLE_QUOTED_STRING_DELIMITER, index)) {
						index++
					}
					if (index < text.length) index += 3
				}

				text[index] == '"' -> {
					index++
					while (index < text.length) {
						when {
							text[index] == '\\' && index + 1 < text.length -> index += 2
							text[index] == '"' -> {
								index++
								break
							}

							else -> index++
						}
					}
				}

				text.startsWith("//", index) -> {
					val startIndex = index
					index += 2
					while (index < text.length && text[index] != '\n') {
						index++
					}
					comments += text.substring(startIndex, index)
				}

				text.startsWith("/*", index) -> {
					val startIndex = index
					index += 2
					while (index < text.length && !text.startsWith("*/", index)) {
						index++
					}
					if (index < text.length) index += 2
					comments += text.substring(startIndex, index.coerceAtMost(text.length))
				}

				else -> index++
			}
		}

		return comments.joinToString("\n")
	}

	private fun extractXmlComments(text: String): String {
		return Regex("<!--([\\s\\S]*?)-->")
			.findAll(text)
			.joinToString("\n") { it.value }
	}

	private fun writeReferenceCorpusAuditSnapshot(
		discoveredCorpusPaths: Set<String>,
		findings: List<ProseFinding>,
		missingInventoryEntries: Set<String>,
		staleInventoryEntries: Set<String>,
		uninventoriedFindingPaths: Set<String>
	) {
		referenceCorpusAuditSnapshotFile.parentFile?.mkdirs()
		referenceCorpusAuditSnapshotFile.writeText(
			renderReferenceCorpusInventoryMarkdown(
				discoveredCorpusPaths = discoveredCorpusPaths,
				findings = findings,
				missingInventoryEntries = missingInventoryEntries,
				staleInventoryEntries = staleInventoryEntries,
				uninventoriedFindingPaths = uninventoriedFindingPaths
			)
		)
	}

	private fun renderReferenceCorpusInventoryMarkdown(
		discoveredCorpusPaths: Set<String>,
		findings: List<ProseFinding>,
		missingInventoryEntries: Set<String>,
		staleInventoryEntries: Set<String>,
		uninventoriedFindingPaths: Set<String>
	): String {
		val groupedFindings = findings
			.groupBy(ProseFinding::relativePath)
			.toSortedMap()
		val rollups = buildReferenceCorpusFindingRollups(groupedFindings)
		val rollupsByPriority = rollups.groupBy(ReferenceCorpusFindingRollup::priorityLabel)
		val priorityOrder = listOf("Critical", "High", "Medium", "Low")

		return buildString {
			appendLine("# Excluded Reference Corpus Audit")
			appendLine()
			appendLine("Generated by `ContentSafetyAuditTest.excludedReferenceCorpusLongProse_isExplicitlyInventoried()`.")
			appendLine()
			appendLine("- Snapshot file: `${referenceCorpusAuditSnapshotFile.name}`")
			appendLine("- Project root: `${projectRoot.path.replace('\\', '/')}`")
			appendLine("- Discovered excluded corpus files: ${discoveredCorpusPaths.size}")
			appendLine("- Total prose findings: ${findings.size}")
			appendLine("- Files with findings: ${groupedFindings.size}")
			appendLine("- Missing inventory entries: ${missingInventoryEntries.size}")
			appendLine("- Stale inventory entries: ${staleInventoryEntries.size}")
			appendLine("- Uninventoried finding paths: ${uninventoriedFindingPaths.size}")
			appendLine()

			appendLine("## Inventory Drift")
			appendLine()
			appendLine("### Missing inventory entries")
			if (missingInventoryEntries.isEmpty()) {
				appendLine(NONE_LIST_ITEM)
			} else {
				missingInventoryEntries.sorted().forEach { path ->
					appendCodePathBullet(path)
				}
			}
			appendLine()

			appendLine("### Stale inventory entries")
			if (staleInventoryEntries.isEmpty()) {
				appendLine(NONE_LIST_ITEM)
			} else {
				staleInventoryEntries.sorted().forEach { path ->
					appendCodePathBullet(path)
				}
			}
			appendLine()

			appendLine("### Uninventoried finding paths")
			if (uninventoriedFindingPaths.isEmpty()) {
				appendLine(NONE_LIST_ITEM)
			} else {
				uninventoriedFindingPaths.sorted().forEach { path ->
					appendCodePathBullet(path)
				}
			}
			appendLine()

			appendLine("## Review priority")
			appendLine()
			appendLine("Thresholds: Critical â‰¥ 800 chars, High â‰¥ 500, Medium â‰¥ 300, Low < 300.")
			appendLine()
			priorityOrder.forEach { priority ->
				val priorityRollups = rollupsByPriority[priority].orEmpty()
				appendLine("### $priority (${priorityRollups.size} files)")
				if (priorityRollups.isEmpty()) {
					appendLine(NONE_LIST_ITEM)
				} else {
					priorityRollups.forEach { rollup ->
						appendLine("- `${rollup.relativePath}` :: maxChars=${rollup.maxCharCount}, findings=${rollup.findings.size}, topLines=${rollup.representativeFindings.joinToString { it.lineNumber.toString() }}")
					}
				}
				appendLine()
			}

			appendLine("## Largest findings first")
			appendLine()
			if (rollups.isEmpty()) {
				appendLine(NONE_LIST_ITEM)
			} else {
				rollups.forEachIndexed { index, rollup ->
					appendLine("### ${index + 1}. `${rollup.relativePath}`")
					appendLine("- Review priority: ${rollup.priorityLabel}")
					appendLine("- Finding count: ${rollup.findings.size}")
					appendLine("- Max chars: ${rollup.maxCharCount}")
					appendLine("- Average chars: ${rollup.averageCharCount}")
					appendLine("- Top hit lines: ${rollup.representativeFindings.joinToString { it.lineNumber.toString() }}")
					appendLine("- Representative findings:")
					rollup.representativeFindings.forEach { finding ->
						appendLine("  - Line ${finding.lineNumber} [${finding.kind}] chars=${finding.charCount}, sentences=${finding.sentenceCount} :: ${finding.preview}")
					}
					appendLine()
				}
			}

			appendLine("## Excluded corpus inventory")
			appendLine()
			discoveredCorpusPaths.sorted().forEach { path ->
				val metadata = excludedReferenceCorpusProseFiles[path]
				val rollup = rollups.firstOrNull { it.relativePath == path }
				appendLine("### `$path`")
				if (metadata == null) {
					appendLine("- Status: Missing inventory metadata")
				} else {
					appendLine("- Inventoried on: ${metadata.inventoriedOn}")
					appendLine("- Rationale: ${metadata.rationale}")
					appendLine("- Follow-up: ${metadata.followUpNote}")
				}

				val pathFindings = groupedFindings[path].orEmpty()
				appendLine("- Finding count: ${pathFindings.size}")
				if (rollup != null) {
					appendLine("- Review priority: ${rollup.priorityLabel}")
					appendLine("- Max chars: ${rollup.maxCharCount}")
					appendLine("- Average chars: ${rollup.averageCharCount}")
					appendLine("- Top hit lines: ${rollup.representativeFindings.joinToString { it.lineNumber.toString() }}")
				}
				if (pathFindings.isEmpty()) {
					appendLine("- Findings: None")
				} else {
					appendLine("- Representative findings:")
					rollup?.representativeFindings?.forEach { finding ->
						appendLine("  - Line ${finding.lineNumber} [${finding.kind}] chars=${finding.charCount}, sentences=${finding.sentenceCount} :: ${finding.preview}")
					}
					appendLine("- Findings:")
					pathFindings
						.sortedWith(
							compareBy(
								ProseFinding::lineNumber,
								ProseFinding::kind,
								ProseFinding::charCount
							)
						)
						.forEach { finding ->
							appendLine("  - Line ${finding.lineNumber} [${finding.kind}] chars=${finding.charCount}, lines=${finding.lineCount}, sentences=${finding.sentenceCount} :: ${finding.preview}")
						}
				}
				appendLine()
			}
		}.trimEnd() + "\n"
	}

	private fun StringBuilder.appendPathBullet(path: String) {
		appendLine("- $path")
	}

	private fun StringBuilder.appendCodePathBullet(path: String) {
		appendLine("- `$path`")
	}

	private fun buildReferenceCorpusFindingRollups(
		groupedFindings: Map<String, List<ProseFinding>>
	): List<ReferenceCorpusFindingRollup> {
		return groupedFindings.map { (relativePath, pathFindings) ->
			val sortedBySize = pathFindings.sortedWith(
				compareByDescending<ProseFinding> { it.charCount }
					.thenBy(ProseFinding::lineNumber)
					.thenBy(ProseFinding::kind)
			)
			ReferenceCorpusFindingRollup(
				relativePath = relativePath,
				findings = pathFindings,
				priorityLabel = reviewPriorityFor(
					pathFindings.maxOfOrNull(ProseFinding::charCount) ?: 0
				),
				maxCharCount = pathFindings.maxOfOrNull(ProseFinding::charCount) ?: 0,
				averageCharCount = pathFindings.map(ProseFinding::charCount).average().toInt(),
				representativeFindings = sortedBySize.take(3)
			)
		}.sortedWith(
			compareByDescending<ReferenceCorpusFindingRollup> { it.maxCharCount }
				.thenByDescending { it.findings.size }
				.thenBy { it.relativePath }
		)
	}

	private fun reviewPriorityFor(maxCharCount: Int): String {
		return when {
			maxCharCount >= 800 -> "Critical"
			maxCharCount >= 500 -> "High"
			maxCharCount >= 300 -> "Medium"
			else -> "Low"
		}
	}
}
