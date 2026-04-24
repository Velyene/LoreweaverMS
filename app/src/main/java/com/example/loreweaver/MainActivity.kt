/*
 * FILE: MainActivity.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Activity & Entry Point
 */

package com.example.loreweaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.loreweaver.navigation.LoreweaverApp
import com.example.loreweaver.ui.theme.LoreweaverTheme
import dagger.hilt.android.AndroidEntryPoint

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

