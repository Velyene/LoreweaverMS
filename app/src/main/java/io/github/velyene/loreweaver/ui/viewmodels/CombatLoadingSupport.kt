package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.util.UiText

internal fun CombatUiState.beginCombatLoading(): CombatUiState =
	copy(isLoading = true, error = null, onRetry = null)

internal fun CombatUiState.withCombatError(
	message: UiText?,
	onRetry: (() -> Unit)? = null
): CombatUiState = copy(
	isLoading = false,
	error = message,
	onRetry = onRetry
)

internal fun CombatUiState.clearLoadedEncounterState(
	error: UiText? = null,
	onRetry: (() -> Unit)? = null
): CombatUiState = copy(
	isCombatActive = false,
	currentEncounterId = null,
	currentEncounterName = "",
	encounterLifecycle = EncounterLifecycle.DRAFT,
	initiativeMode = InitiativeMode.MANUAL,
	combatants = emptyList(),
	currentTurnIndex = 0,
	currentRound = 1,
	encounterNotes = "",
	activeStatuses = emptyList(),
	canGoToPreviousTurn = false,
	turnStep = CombatTurnStep.SELECT_ACTION,
	pendingAction = null,
	selectedTargetId = null,
	encounterDifficulty = null,
	generationSettings = io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings(),
	generationDetails = null,
	isLoading = false,
	error = error,
	onRetry = onRetry
)

internal fun CombatUiState.snapshotEncounterPresentation(
	encounter: Encounter,
	session: SessionRecord
): CombatUiState {
	val snapshot = session.snapshot ?: return this
	return withEncounterPresentation(
		encounter = encounter,
		combatants = snapshot.combatants,
		encounterLifecycle = EncounterLifecycle.ACTIVE,
		requestedTurnIndex = snapshot.currentTurnIndex,
		currentRound = snapshot.currentRound,
		activeStatuses = session.log,
		isCombatActive = true,
		resetTransientTurnState = true
	)
}

internal fun CombatUiState.activeEncounterPresentation(
	encounter: Encounter,
	lastSession: SessionRecord?
): CombatUiState = withEncounterPresentation(
	encounter = encounter,
	combatants = encounter.participants,
	encounterLifecycle = EncounterLifecycle.ACTIVE,
	requestedTurnIndex = encounter.currentTurnIndex,
	currentRound = encounter.currentRound,
	activeStatuses = encounter.activeLog.ifEmpty { lastSession?.log ?: emptyList() },
	isCombatActive = true,
	resetTransientTurnState = true
)

internal fun CombatUiState.setupEncounterPresentation(
	encounter: Encounter,
	lastSession: SessionRecord?
): CombatUiState {
	val encounterCombatants = lastSession?.snapshot?.combatants ?: encounter.participants
	val lifecycle = when {
		lastSession?.isCompleted == true -> EncounterLifecycle.COMPLETED
		lastSession != null -> EncounterLifecycle.PAUSED
		else -> EncounterLifecycle.DRAFT
	}
	return withEncounterPresentation(
		encounter = encounter,
		combatants = encounterCombatants,
		encounterLifecycle = lifecycle,
		requestedTurnIndex = lastSession?.snapshot?.currentTurnIndex ?: encounter.currentTurnIndex,
		currentRound = encounter.currentRound,
		activeStatuses = lastSession?.log ?: emptyList(),
		isCombatActive = false,
		resetTransientTurnState = true
	)
}

internal fun formatCombatError(prefix: UiText, exception: Exception): UiText {
	return UiText.StringResource(
		R.string.error_with_detail,
		listOf(prefix, exceptionDetail(exception))
	)
}

internal fun shouldClearEncounterAfterActiveLoad(encounter: Encounter): Boolean {
	return encounter.status != EncounterStatus.ACTIVE
}

