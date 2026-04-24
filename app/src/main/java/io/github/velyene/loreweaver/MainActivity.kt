/*
 * FILE: MainActivity.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Activity & Entry Point
 */

package io.github.velyene.loreweaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.github.velyene.loreweaver.navigation.LoreweaverApp
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme

@Suppress("unused")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			LoreweaverTheme(darkTheme = true) {
				LoreweaverApp()
			}
		}
	}
}

