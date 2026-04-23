/*
 * FILE: GameRulesScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Screen entry point and tab constants
 * 2. Tab content entry points (Ability, Skills, Combat, Dice Roller)
 * 3. Dice roller UI — modifier, manual modifier, and result cards
 * 4. Reusable layout helpers (RulesTabLayout, RulesSectionCard, TwoColumnTable)
 * 5. Dice-roll logic helpers (toggleRollModifier, buildRollSummary)
 * 6. Travel, environment, and rest tabs
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.loreweaver.domain.model.*

private val GAME_RULES_TABS = listOf(
	"Abilities",
	"Skills",
	"Combat",
	"Travel",
	"Environment",
	"Rest",
	"Dice"
)

private val ABILITY_SCORE_RANGES = listOf(
	"1" to "-5",
	"2-3" to "-4",
	"4-5" to "-3",
	"6-7" to "-2",
	"8-9" to "-1",
	"10-11" to "+0",
	"12-13" to "+1",
	"14-15" to "+2",
	"16-17" to "+3",
	"18-19" to "+4",
	"20-21" to "+5",
	"22-23" to "+6",
	"24-25" to "+7",
	"26-27" to "+8",
	"28-29" to "+9",
	"30" to "+10"
)

private val PROFICIENCY_BONUS_RANGES = listOf(
	"1-4" to "+2",
	"5-8" to "+3",
	"9-12" to "+4",
	"13-16" to "+5",
	"17-20" to "+6"
)

private val SKILLS_BY_ABILITY = Ability.entries
	.associateWith { ability -> Skill.entries.filter { it.ability == ability } }
	.filterValues { it.isNotEmpty() }

private data class RollSummary(
	val rolledText: String,
	val usedText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRulesScreen(
	onBack: () -> Unit
) {
	var selectedTab by remember { mutableIntStateOf(0) }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Fifth-Edition Rules Reference") },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
					}
				}
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			PrimaryTabRow(selectedTabIndex = selectedTab) {
				GAME_RULES_TABS.forEachIndexed { index, title ->
					Tab(
						selected = selectedTab == index,
						onClick = { selectedTab = index },
						text = { Text(title) }
					)
				}
			}

			GameRulesTabContent(selectedTab = selectedTab)
		}
	}
}

@Composable
private fun GameRulesTabContent(selectedTab: Int) {
	when (selectedTab) {
		0 -> AbilityScoreTab()
		1 -> SkillsTab()
		2 -> CombatTab()
		3 -> TravelTab()
		4 -> EnvironmentTab()
		5 -> RestTab()
		6 -> DiceRollerTab()
	}
}

@Composable
fun AbilityScoreTab() {
	RulesTabLayout(
		title = "Ability Scores & Modifiers",
		description = "Each of a creature's abilities has a score, a number that defines the magnitude of that ability. " +
			"A score of 10 or 11 is the normal human average. Adventurers can have scores as high as 20, " +
			"and monsters and divine beings can have scores as high as 30."
	) {
		RulesSectionCard(title = "Ability Score Modifier Table") {
			TwoColumnTable(
				headers = "Score" to "Modifier",
				rows = ABILITY_SCORE_RANGES
			)
		}

		Spacer(modifier = Modifier.height(24.dp))

		Text(
			"The Six Abilities",
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.Bold
		)

		Spacer(modifier = Modifier.height(12.dp))

		Ability.entries.forEach { ability ->
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp)
			) {
				Column(modifier = Modifier.padding(12.dp)) {
					Text(
						"${ability.displayName} (${ability.abbreviation})",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold
					)
					Text(
						getAbilityDescription(ability),
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}
		}

		SectionSpacer()

		RulesSectionCard(title = "Proficiency Bonus by Level") {
			TwoColumnTable(
				headers = "Level" to "Proficiency Bonus",
				rows = PROFICIENCY_BONUS_RANGES
			)
		}
	}
}

@Composable
fun SkillsTab() {
	RulesTabLayout(
		title = "Skills",
		description = "A skill represents a specific aspect of an ability score. When you're proficient in a skill, " +
			"you add your proficiency bonus to ability checks involving that skill."
	) {
		SKILLS_BY_ABILITY.forEach { (ability, skillsForAbility) ->
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp)
			) {
				Column(modifier = Modifier.padding(16.dp)) {
					Text(
						ability.displayName,
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.primary
					)

					Spacer(modifier = Modifier.height(8.dp))

					skillsForAbility.forEach { skill ->
						Text(
							"• ${skill.displayName}",
							style = MaterialTheme.typography.bodyLarge,
							modifier = Modifier.padding(vertical = 2.dp)
						)
					}
				}
			}
		}

		SectionSpacer()

		RulesSectionCard(title = "Passive Checks") {
			Text(
				"A passive check doesn't involve any die rolls. It represents the average result for a task done repeatedly.",
				style = MaterialTheme.typography.bodyMedium
			)

			Spacer(modifier = Modifier.height(8.dp))

			Text(
				"Formula: 10 + all modifiers that normally apply to the check",
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Bold
			)

			Spacer(modifier = Modifier.height(4.dp))

			Text(
				"• Add 5 if you have advantage\n• Subtract 5 if you have disadvantage",
				style = MaterialTheme.typography.bodySmall
			)
		}
	}
}

@Composable
fun CombatTab() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp)
	) {
		Text(
			"Combat Rules",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold
		)

		Spacer(modifier = Modifier.height(16.dp))

		Text(
			"Combat is organized into rounds and turns. Each round represents about six seconds. " +
				"During a round, each participant takes a turn in initiative order.",
			style = MaterialTheme.typography.bodyMedium
		)

		Spacer(modifier = Modifier.height(24.dp))

		// Combat Actions
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Combat Actions",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(12.dp))

				CombatAction.entries.take(10).forEach { action ->
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 4.dp),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.surfaceVariant
						)
					) {
						Column(modifier = Modifier.padding(12.dp)) {
							Text(
								action.displayName,
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold
							)
							Text(
								action.description,
								style = MaterialTheme.typography.bodyMedium
							)
						}
					}
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Cover
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Cover",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(12.dp))

				CoverType.entries.filter { it != CoverType.NONE }.forEach { cover ->
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 4.dp),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.surfaceVariant
						)
					) {
						Column(modifier = Modifier.padding(12.dp)) {
							Text(
								cover.displayName,
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold,
								color = MaterialTheme.colorScheme.primary
							)
							Text(cover.description)
							Text(
								"+${cover.acBonus} AC and DEX saves",
								fontWeight = FontWeight.Bold,
								color = MaterialTheme.colorScheme.primary
							)
						}
					}
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Attack Rolls
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Attack Rolls",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"d20 + ability modifier + proficiency bonus (if proficient)",
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text("• Melee: STR modifier (or DEX for finesse weapons)")
				Text("• Ranged: DEX modifier")
				Text("• Natural 20 = Critical Hit (roll damage dice twice)")
				Text("• Natural 1 = Automatic Miss")
				Text("• Hit if total ≥ target's AC")
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Damage Types
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Damage Types",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(12.dp))

				val commonTypes = listOf(
					DamageType.BLUDGEONING,
					DamageType.PIERCING,
					DamageType.SLASHING,
					DamageType.FIRE,
					DamageType.COLD,
					DamageType.LIGHTNING,
					DamageType.POISON,
					DamageType.NECROTIC,
					DamageType.RADIANT
				)

				commonTypes.forEach { type ->
					Text("• ${type.displayName}: ${type.description}")
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Death Saving Throws
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Death Saving Throws",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.error
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"When you drop to 0 HP, make death saves at the start of each turn:",
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text("• DC 10 to succeed (no ability modifier)")
				Text("• 3 successes = Stabilized")
				Text("• 3 failures = Death")
				Text("• Natural 20 = Regain 1 HP")
				Text("• Natural 1 = 2 failures")
				Text("• Taking damage = 1 failure (critical hit = 2)")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Stabilizing: DC 10 Wisdom (Medicine) check",
					fontWeight = FontWeight.Bold
				)
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Creature Sizes
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Creature Sizes",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(12.dp))

				CreatureSize.entries.forEach { size ->
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 4.dp),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(
							size.displayName,
							modifier = Modifier.weight(1f),
							fontWeight = FontWeight.Bold
						)
						Text(
							size.description,
							modifier = Modifier.weight(1f)
						)
					}
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Opportunity Attacks
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Opportunity Attacks",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"When a hostile creature you can see moves out of your reach, you can use your reaction to make one melee attack.",
					style = MaterialTheme.typography.bodyMedium
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text("Avoiding opportunity attacks:")
				Text("• Take the Disengage action")
				Text("• Teleport")
				Text("• Be moved without using your movement, an action, or a reaction")
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Resistance & Vulnerability
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Resistance & Vulnerability",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Resistance:",
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.primary
				)
				Text("Damage of that type is halved")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Vulnerability:",
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.error
				)
				Text("Damage of that type is doubled")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Note: Multiple resistances/vulnerabilities to the same type don't stack",
					style = MaterialTheme.typography.bodySmall
				)
			}
		}
	}
}

@Composable
fun DiceRollerTab() {
	var rollModifier by remember { mutableStateOf(RollModifier.NORMAL) }
	var rollResult by remember { mutableStateOf<D20RollResult?>(null) }
	var manualModifier by remember { mutableIntStateOf(0) }

	RulesTabLayout(title = "Dice Roller") {
		RollModifierSection(
			rollModifier = rollModifier,
			onRollModifierChange = { selectedModifier ->
				rollModifier = toggleRollModifier(
					currentModifier = rollModifier,
					targetModifier = selectedModifier
				)
			}
		)

		SectionSpacer()

		ManualModifierSection(
			manualModifier = manualModifier,
			onDecrease = { manualModifier-- },
			onIncrease = { manualModifier++ }
		)

		SectionSpacer()

		Button(
			onClick = {
				rollResult = D20RollResult.roll(manualModifier, rollModifier)
			},
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
		) {
			Text(
				"ROLL D20",
				style = MaterialTheme.typography.titleLarge
			)
		}

		rollResult?.let { result ->
			SectionSpacer()
			DiceRollResultCard(
				result = result,
				rollModifier = rollModifier
			)
		}
	}
}

@Composable
private fun RollModifierSection(
	rollModifier: RollModifier,
	onRollModifierChange: (RollModifier) -> Unit
) {
	RulesSectionCard(title = "Roll Modifier") {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			FilterChip(
				selected = rollModifier == RollModifier.ADVANTAGE,
				onClick = { onRollModifierChange(RollModifier.ADVANTAGE) },
				label = { Text("Advantage") },
				modifier = Modifier.weight(1f)
			)
			FilterChip(
				selected = rollModifier == RollModifier.NORMAL,
				onClick = { onRollModifierChange(RollModifier.NORMAL) },
				label = { Text("Normal") },
				modifier = Modifier.weight(1f)
			)
			FilterChip(
				selected = rollModifier == RollModifier.DISADVANTAGE,
				onClick = { onRollModifierChange(RollModifier.DISADVANTAGE) },
				label = { Text("Disadvantage") },
				modifier = Modifier.weight(1f)
			)
		}
	}
}

@Composable
private fun ManualModifierSection(
	manualModifier: Int,
	onDecrease: () -> Unit,
	onIncrease: () -> Unit
) {
	RulesSectionCard(title = "Manual Modifier") {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Button(onClick = onDecrease) {
				Text("-")
			}
			Text(
				manualModifier.toSignedModifier(),
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.weight(1f),
				textAlign = TextAlign.Center
			)
			Button(onClick = onIncrease) {
				Text("+")
			}
		}
	}
}

@Composable
private fun DiceRollResultCard(
	result: D20RollResult,
	rollModifier: RollModifier
) {
	val rollSummary = buildRollSummary(result = result, rollModifier = rollModifier)
	val criticalResult = when {
		result.isCriticalSuccess -> "CRITICAL SUCCESS!" to MaterialTheme.colorScheme.primary
		result.isCriticalFailure -> "CRITICAL FAILURE!" to MaterialTheme.colorScheme.error
		else -> null
	}

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = diceResultContainerColor(result)
		)
	) {
		DiceRollResultContent(
			result = result,
			rollSummary = rollSummary,
			criticalResult = criticalResult
		)
	}
}

@Composable
private fun DiceRollResultContent(
	result: D20RollResult,
	rollSummary: RollSummary,
	criticalResult: Pair<String, Color>?
) {
	Column(
		modifier = Modifier.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			"Result",
			style = MaterialTheme.typography.titleMedium
		)

		Spacer(modifier = Modifier.height(8.dp))

		Text(
			result.total.toString(),
			style = MaterialTheme.typography.displayLarge,
			fontWeight = FontWeight.Bold
		)

		Spacer(modifier = Modifier.height(8.dp))

		RollSummaryText(rollSummary = rollSummary)

		Text(
			"Modifier: ${result.modifier.toSignedModifier()}",
			style = MaterialTheme.typography.bodyMedium
		)

		CriticalResultText(criticalResult = criticalResult)
	}
}

@Composable
private fun RollSummaryText(rollSummary: RollSummary) {
	Text(
		rollSummary.rolledText,
		style = MaterialTheme.typography.bodyMedium
	)

	rollSummary.usedText?.let { usedText ->
		Text(
			usedText,
			style = MaterialTheme.typography.bodyMedium
		)
	}
}

@Composable
private fun CriticalResultText(criticalResult: Pair<String, Color>?) {
	criticalResult?.let { (label, color) ->
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			label,
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.Bold,
			color = color
		)
	}
}

private fun getAbilityDescription(ability: Ability): String {
	return when (ability) {
		Ability.STRENGTH -> "Measures physical power, athletic training, and brute force"
		Ability.DEXTERITY -> "Measures agility, reflexes, and balance"
		Ability.CONSTITUTION -> "Measures health, stamina, and vital force"
		Ability.INTELLIGENCE -> "Measures mental acuity, accuracy of recall, and reasoning"
		Ability.WISDOM -> "Measures perception, intuition, and attunement to the world"
		Ability.CHARISMA -> "Measures force of personality, confidence, and eloquence"
	}
}

@Composable
private fun RulesTabLayout(
	title: String,
	description: String? = null,
	content: @Composable ColumnScope.() -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp)
	) {
		Text(
			title,
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold
		)

		description?.let { summary ->
			Spacer(modifier = Modifier.height(16.dp))
			Text(
				summary,
				style = MaterialTheme.typography.bodyMedium
			)
			Spacer(modifier = Modifier.height(24.dp))
		} ?: Spacer(modifier = Modifier.height(16.dp))

		content()
	}
}

@Composable
private fun RulesSectionCard(
	title: String,
	modifier: Modifier = Modifier,
	titleColor: Color = Color.Unspecified,
	content: @Composable ColumnScope.() -> Unit
) {
	Card(modifier = modifier) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				title,
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold,
				color = titleColor
			)

			Spacer(modifier = Modifier.height(12.dp))
			content()
		}
	}
}

@Composable
private fun TwoColumnTable(
	headers: Pair<String, String>,
	rows: List<Pair<String, String>>,
	firstColumnWeight: Float = 1f,
	secondColumnWeight: Float = 1f,
	highlightSecondColumn: Boolean = false,
	rowVerticalPadding: androidx.compose.ui.unit.Dp = 4.dp
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(
			headers.first,
			fontWeight = FontWeight.Bold,
			modifier = Modifier.weight(firstColumnWeight)
		)
		Text(
			headers.second,
			fontWeight = FontWeight.Bold,
			modifier = Modifier.weight(secondColumnWeight)
		)
	}

	HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

	rows.forEach { (firstValue, secondValue) ->
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = rowVerticalPadding),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				firstValue,
				modifier = Modifier.weight(firstColumnWeight)
			)
			Text(
				secondValue,
				fontWeight = if (highlightSecondColumn) FontWeight.Bold else FontWeight.Normal,
				modifier = Modifier.weight(secondColumnWeight)
			)
		}
	}
}

@Composable
private fun SectionSpacer() {
	Spacer(modifier = Modifier.height(16.dp))
}

private fun toggleRollModifier(
	currentModifier: RollModifier,
	targetModifier: RollModifier
): RollModifier {
	return if (targetModifier == RollModifier.NORMAL || currentModifier != targetModifier) {
		targetModifier
	} else {
		RollModifier.NORMAL
	}
}

private fun buildRollSummary(
	result: D20RollResult,
	rollModifier: RollModifier
): RollSummary {
	val secondRoll = result.secondRoll ?: return RollSummary(
		rolledText = "Rolled: ${result.naturalRoll}"
	)

	val usedRoll = if (rollModifier == RollModifier.ADVANTAGE) {
		maxOf(result.naturalRoll, secondRoll)
	} else {
		minOf(result.naturalRoll, secondRoll)
	}

	return RollSummary(
		rolledText = "Rolled: ${result.naturalRoll} and $secondRoll (${rollModifier.name.lowercase()})",
		usedText = "Used: $usedRoll"
	)
}

@Composable
private fun diceResultContainerColor(result: D20RollResult): Color {
	return when {
		result.isCriticalSuccess -> MaterialTheme.colorScheme.primaryContainer
		result.isCriticalFailure -> MaterialTheme.colorScheme.errorContainer
		else -> MaterialTheme.colorScheme.surfaceVariant
	}
}

private fun Int.toSignedModifier(): String {
	return if (this >= 0) "+$this" else toString()
}

@Composable
fun TravelTab() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp)
	) {
		Text(
			"Travel & Movement",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Travel Pace Table
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Travel Pace",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(12.dp))

				TravelPace.entries.forEach { pace ->
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 4.dp),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.surfaceVariant
						)
					) {
						Column(modifier = Modifier.padding(12.dp)) {
							Text(
								pace.displayName,
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold,
								color = MaterialTheme.colorScheme.primary
							)
							Text("• Per Minute: ${pace.feetPerMinute} feet")
							Text("• Per Hour: ${pace.milesPerHour} miles")
							Text("• Per Day: ${pace.milesPerDay} miles")
							if (pace.perceptionPenalty != 0) {
								Text(
									"• Effect: ${pace.perceptionPenalty} penalty to passive Perception",
									color = MaterialTheme.colorScheme.error
								)
							}
							if (pace.allowsStealth) {
								Text(
									"• Allows Stealth",
									color = MaterialTheme.colorScheme.primary
								)
							}
						}
					}
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Difficult Terrain
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Difficult Terrain",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Movement through difficult terrain costs 2 feet of speed for every 1 foot moved (half speed).",
					style = MaterialTheme.typography.bodyMedium
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text("Difficult Terrain Types:", fontWeight = FontWeight.Bold)
				TerrainType.entries.filter { it.isDifficult }.forEach { terrain ->
					Text("• ${terrain.displayName}")
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Forced March
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Forced March",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Characters can travel for 8 hours per day without issue. Beyond that:",
					style = MaterialTheme.typography.bodyMedium
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text("• Each hour past 8 requires a Constitution save")
				Text("• DC = 10 + 1 for each hour past 8")
				Text("• Failure = 1 level of exhaustion")
			}
		}
	}
}

@Composable
fun EnvironmentTab() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp)
	) {
		Text(
			"Environment",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Falling
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Falling",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Take 1d6 bludgeoning damage per 10 feet fallen, up to a maximum of 20d6 (200 feet).",
					style = MaterialTheme.typography.bodyMedium
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text("• 10 feet = 1d6 (avg 3-4 damage)")
				Text("• 30 feet = 3d6 (avg 10-11 damage)")
				Text("• 50 feet = 5d6 (avg 17-18 damage)")
				Text("• 100 feet = 10d6 (avg 35 damage)")
				Text("• 200+ feet = 20d6 (avg 70 damage, max 120)")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Landing: Creature lands prone unless it avoids taking damage.",
					fontWeight = FontWeight.Bold
				)
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Suffocating
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Suffocating",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text("Hold Breath:")
				Text("• Duration = 1 + CON modifier minutes (min 30 seconds)")

				Spacer(modifier = Modifier.height(8.dp))

				Text("When Out of Breath:")
				Text("• Survive for CON modifier rounds (min 1)")
				Text("• Then drop to 0 HP and start dying")
				Text("• Can't regain HP until breathing again")
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Vision & Light
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Vision & Light",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(12.dp))

				LightLevel.entries.forEach { light ->
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 4.dp),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.surfaceVariant
						)
					) {
						Column(modifier = Modifier.padding(12.dp)) {
							Text(
								light.displayName,
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold
							)
							Text(light.description, style = MaterialTheme.typography.bodySmall)
							Text(
								"Effect: ${light.effectOnPerception}",
								fontWeight = FontWeight.Bold
							)
						}
					}
				}

				Spacer(modifier = Modifier.height(12.dp))

				Text("Special Vision Types:", fontWeight = FontWeight.Bold)
				VisionType.entries.forEach { vision ->
					Text("• ${vision.displayName}: ${vision.description}")
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Jumping
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Jumping",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Long Jump:",
					fontWeight = FontWeight.Bold
				)
				Text("• With 10ft running start: Jump up to STR score in feet")
				Text("• Standing: Jump half that distance")
				Text("• Each foot jumped costs 1 foot of movement")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"High Jump:",
					fontWeight = FontWeight.Bold
				)
				Text("• With 10ft running start: Jump 3 + STR modifier feet")
				Text("• Standing: Jump half that distance")
				Text("• Can reach height of jump + 1.5× your height")
			}
		}
	}
}

@Composable
fun RestTab() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp)
	) {
		Text(
			"Resting",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Short Rest
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Short Rest",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.primary
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Duration: At least 1 hour",
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text("During a short rest, you can:")
				Text("• Eat, drink, read, and tend wounds")
				Text("• Spend Hit Dice to regain HP")
				Text("• Do nothing more strenuous than light activity")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Hit Dice:",
					fontWeight = FontWeight.Bold
				)
				Text("• Roll the die + CON modifier")
				Text("• Regain that many HP (minimum 0)")
				Text("• Can spend multiple Hit Dice")
				Text("• Maximum = character level")
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Long Rest
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Long Rest",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.primary
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Duration: At least 8 hours",
					fontWeight = FontWeight.Bold
				)
				Text("(6 hours sleeping + 2 hours light activity)")

				Spacer(modifier = Modifier.height(8.dp))

				Text("Benefits:")
				Text("• Recover ALL lost HP")
				Text("• Regain half of max Hit Dice (minimum 1)")
				Text("• Most class features recharge")
				Text("• Spell slots fully restore")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Restrictions:",
					fontWeight = FontWeight.Bold
				)
				Text("• Can only benefit from 1 long rest per 24 hours")
				Text("• Must have at least 1 HP to benefit")
				Text("• Interrupted by 1+ hour of strenuous activity")
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Downtime Activities
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Downtime Activities",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(12.dp))

				DowntimeActivity.entries.forEach { activity ->
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 4.dp),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.surfaceVariant
						)
					) {
						Column(modifier = Modifier.padding(12.dp)) {
							Text(
								activity.displayName,
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold
							)
							Text("• Minimum: ${activity.minimumDays} day(s)")
							if (activity.costPerDay > 0) {
								Text("• Cost: ${activity.costPerDay} gp/day")
							}
							Text(activity.description, style = MaterialTheme.typography.bodySmall)
						}
					}
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		// Food & Water
		Card(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					"Food & Water",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Daily Requirements:",
					fontWeight = FontWeight.Bold
				)
				Text("• Food: 1 pound per day")
				Text("• Water: 1 gallon (2 in hot weather)")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Without Food:",
					fontWeight = FontWeight.Bold
				)
				Text("• Can survive 3 + CON modifier days (min 1)")
				Text("• After that: 1 level of exhaustion per day")

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					"Without Water:",
					fontWeight = FontWeight.Bold
				)
				Text("• Half water: DC 15 CON save or 1 exhaustion level")
				Text("• No water: Automatic 1 level of exhaustion")
				Text("• If already exhausted: Take 2 levels instead")
			}
		}
	}
}
