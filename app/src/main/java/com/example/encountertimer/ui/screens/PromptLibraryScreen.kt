/*
 * FILE: PromptLibraryScreen.kt
 * 
 * TABLE OF CONTENTS:
 * 1. Main Screen (PromptLibraryScreen)
 * 2. Prompt Card (PromptCard)
 */

package com.example.encountertimer.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.encountertimer.LorePrompt
import com.example.encountertimer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptLibraryScreen() {

	val displayPrompts = listOf(
		LorePrompt(
			"1",
			stringResource(R.string.prompt_whispering_woods_title),
			stringResource(R.string.prompt_whispering_woods_desc),
			stringResource(R.string.prompt_cat_environment),
			R.drawable.ic_launcher_background
		),
		LorePrompt(
			"2",
			stringResource(R.string.prompt_tavern_rumor_title),
			stringResource(R.string.prompt_tavern_rumor_desc),
			stringResource(R.string.prompt_cat_plot_hook)
		),
		LorePrompt(
			"3",
			stringResource(R.string.prompt_crit_desc_title),
			stringResource(R.string.prompt_crit_desc_desc),
			stringResource(R.string.prompt_cat_combat)
		)
	)

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.prompt_library_title)) },
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer,
					titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
				)
			)
		}
	) { padding ->
		LazyColumn(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			items(displayPrompts, key = { it.id }) { prompt ->
				PromptCard(prompt)
			}
		}
	}
}

@Composable
fun PromptCard(prompt: LorePrompt) {
	@Suppress("DEPRECATION")
	val clipboardManager = LocalClipboardManager.current
	val context = LocalContext.current
	val copiedMessage = stringResource(R.string.prompt_copied_to_clipboard)
	Card(
		modifier = Modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant,
			contentColor = MaterialTheme.colorScheme.onSurfaceVariant
		)
	) {
		Column {
			// If there's an image, show it!
			prompt.imageResId?.let { resId ->
				Image(
					painter = painterResource(id = resId),
					contentDescription = stringResource(
						R.string.prompt_image_content_description,
						prompt.title
					),
					modifier = Modifier
						.fillMaxWidth()
						.height(150.dp),
					contentScale = ContentScale.Crop
				)
			}

			Column(modifier = Modifier.padding(16.dp)) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = prompt.title,
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold
					)
					SuggestionChip(
						onClick = { },
						label = { Text(prompt.category) },
						colors = SuggestionChipDefaults.suggestionChipColors(
							labelColor = MaterialTheme.colorScheme.secondary
						)
					)
				}

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					text = prompt.content,
					style = MaterialTheme.typography.bodyMedium
				)

				Spacer(modifier = Modifier.height(8.dp))

				Button(
					onClick = {
						clipboardManager.setText(AnnotatedString(prompt.content))
						Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
					},
					modifier = Modifier.align(androidx.compose.ui.Alignment.End)
				) {
					Text(stringResource(R.string.prompt_copy_to_journal))
				}
			}
		}
	}
}
