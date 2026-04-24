package io.github.velyene.loreweaver.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.ALL_CLASSES
import io.github.velyene.loreweaver.domain.model.classInfoFor
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

@Composable
fun CharacterFormScreen(
	characterId: String? = null,
	viewModel: CharacterViewModel = hiltViewModel(),
	onSave: () -> Unit,
	onBack: () -> Unit,
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val classes = ALL_CLASSES.map { it.displayName }
	val screenTitle = if (characterId == null) {
		stringResource(R.string.create_character_title)
	} else {
		stringResource(R.string.edit_character_title)
	}
	val newResourceName = stringResource(R.string.new_resource_name)
	val newActionName = stringResource(R.string.new_action_name)

	var formState by remember {
		mutableStateOf(CharacterFormState())
	}

	LaunchedEffect(characterId, uiState.characters) {
		val character = characterId?.let { id -> uiState.characters.find { it.id == id } }
		character?.let {
			formState = it.toFormState()
		}
	}

	val classInfo = classInfoFor(formState.type)
	val callbacks = createCharacterFormCallbacks(
		formState = formState,
		onFormStateChange = { formState = it },
		characterId = characterId,
		characters = uiState.characters,
		classInfo = classInfo,
		viewModel = viewModel,
		newResourceName = newResourceName,
		newActionName = newActionName,
		onSave = onSave
	)

	CharacterFormContent(
		screenTitle = screenTitle,
		classes = classes,
		formState = formState,
		classInfo = classInfo,
		callbacks = callbacks,
		onBack = onBack
	)
}
