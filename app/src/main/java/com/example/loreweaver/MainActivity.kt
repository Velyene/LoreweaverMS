/*
 * FILE: MainActivity.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Activity & Entry Point
 * 2. Navigation Routes (@Serializable)
 * 3. LoreweaverApp (NavHost & BottomBar Logic)
 * 4. BottomBarScreen Configuration
 */

package com.example.loreweaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.loreweaver.domain.util.ReferenceDetailResolver
import com.example.loreweaver.ui.screens.CampaignDetailScreen
import com.example.loreweaver.ui.screens.CampaignListScreen
import com.example.loreweaver.ui.screens.CharacterDetailScreen
import com.example.loreweaver.ui.screens.CharacterFormScreen
import com.example.loreweaver.ui.screens.CharacterListScreen
import com.example.loreweaver.ui.screens.CombatTrackerScreen
import com.example.loreweaver.ui.screens.HomeScreen
import com.example.loreweaver.ui.screens.LogScreen
import com.example.loreweaver.ui.screens.PromptLibraryScreen
import com.example.loreweaver.ui.screens.ReferenceScreen
import com.example.loreweaver.ui.screens.SessionHistoryScreen
import com.example.loreweaver.ui.screens.SessionSummaryScreen
import com.example.loreweaver.ui.theme.LoreweaverTheme
import com.example.loreweaver.ui.viewmodels.ReferenceCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

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

@Serializable
object HomeRoute

@Serializable
object CampaignListRoute

@Serializable
object CharacterListRoute

@Serializable
object PromptLibraryRoute

@Serializable
object LogRoute

@Serializable
object AdventureLogRoute

@Serializable
data class ReferenceRoute(
	val category: ReferenceCategory? = null,
	val query: String = "",
	val detailCategory: String? = null,
	val detailSlug: String? = null,
)

@Serializable
data class CampaignDetailRoute(val id: String)

@Serializable
data class TrackerRoute(val encounterId: String? = null)

@Serializable
object SummaryRoute

@Serializable
data class CharacterDetailRoute(val id: String)

@Serializable
data class CharacterFormRoute(val id: String? = null)

@Composable
fun LoreweaverApp() {
	val navController = rememberNavController()

	val bottomNavItems = listOf(
		BottomBarScreen.Home,
		BottomBarScreen.CampaignList,
		BottomBarScreen.CharacterList,
		BottomBarScreen.Reference
	)

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		bottomBar = {
			val navBackStackEntry by navController.currentBackStackEntryAsState()
			val currentDestination = navBackStackEntry?.destination

			NavigationBar {
				bottomNavItems.forEach { screen ->
					val label = stringResource(screen.labelRes)
					NavigationBarItem(
						icon = { Icon(screen.icon, contentDescription = label) },
						label = { Text(label) },
						selected = currentDestination.isBottomBarSelected(screen),
						onClick = {
							navController.navigate(screen.routeObject) {
								popUpTo(navController.graph.findStartDestination().id) {
									saveState = true
								}
								launchSingleTop = true
								restoreState = true
							}
						}
					)
				}
			}
		}
	) { innerPadding: PaddingValues ->
		NavHost(
			navController = navController,
			startDestination = HomeRoute,
			modifier = Modifier.padding(innerPadding),
			enterTransition = {
				slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
					tween(400)
				) + fadeIn(tween(400))
			},
			exitTransition = {
				slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
					tween(400)
				) + fadeOut(tween(400))
			}
		) {
			composable<HomeRoute> {
				HomeScreen(
					onNewEncounter = { navController.navigate(TrackerRoute()) },
					onCampaigns = { navController.navigate(CampaignListRoute) },
					onResumeEncounter = { navController.navigate(TrackerRoute()) },
					onCampaignClick = { id -> navController.navigate(CampaignDetailRoute(id)) }
				) { navController.navigate(ReferenceRoute()) }
			}
			composable<CampaignListRoute> {
				CampaignListScreen(
					onCampaignClick = { id -> navController.navigate(CampaignDetailRoute(id)) },
					onBack = { navController.popBackStack() }
				)
			}
			composable<CampaignDetailRoute> { backStackEntry: NavBackStackEntry ->
				val dest = backStackEntry.toRoute<CampaignDetailRoute>()
				CampaignDetailScreen(
					campaignId = dest.id,
					onBack = { navController.popBackStack() },
					onEncounterClick = { encounterId ->
						navController.navigate(TrackerRoute(encounterId))
					}
				)
			}
			composable<TrackerRoute> { backStackEntry: NavBackStackEntry ->
				val dest = backStackEntry.toRoute<TrackerRoute>()
				CombatTrackerScreen(
					encounterId = dest.encounterId,
					onBack = { navController.popBackStack() },
					onEndEncounter = { navController.navigate(SummaryRoute) }
				)
			}
			composable<SummaryRoute> {
				SessionSummaryScreen(
					onDone = {
						navController.navigate(HomeRoute) {
							popUpTo<HomeRoute> { inclusive = true }
						}
					},
					onViewLog = { navController.navigate(AdventureLogRoute) }
				)
			}
			composable<CharacterListRoute> {
				CharacterListScreen(
					onCharacterClick = { charId ->
						navController.navigate(
							CharacterDetailRoute(
								charId
							)
						)
					},
					onAddCharacter = { navController.navigate(CharacterFormRoute()) },
					onBack = { navController.popBackStack() }
				)
			}
			composable<LogRoute> {
				SessionHistoryScreen(onBack = { navController.popBackStack() })
			}
			composable<AdventureLogRoute> {
				LogScreen(onBack = { navController.popBackStack() })
			}
			composable<PromptLibraryRoute> {
				PromptLibraryScreen()
			}
			composable<CharacterDetailRoute> { backStackEntry: NavBackStackEntry ->
				val dest = backStackEntry.toRoute<CharacterDetailRoute>()
				CharacterDetailScreen(
					characterId = dest.id,
					onEdit = { id -> navController.navigate(CharacterFormRoute(id)) },
					onLookupCondition = { cond ->
						navController.navigate(
							ReferenceRoute(
								category = ReferenceCategory.CORE_RULES,
								query = cond,
								detailCategory = ReferenceDetailResolver.CATEGORY_CONDITIONS,
								detailSlug = ReferenceDetailResolver.slugFor(cond)
							)
						)
					},
					onDelete = { navController.popBackStack() },
					onBack = { navController.popBackStack() }
				)
			}
			composable<CharacterFormRoute> { backStackEntry: NavBackStackEntry ->
				val dest = backStackEntry.toRoute<CharacterFormRoute>()
				CharacterFormScreen(
					characterId = dest.id,
					onSave = { navController.popBackStack() },
					onBack = { navController.popBackStack() }
				)
			}
			composable<ReferenceRoute> { backStackEntry: NavBackStackEntry ->
				val dest = backStackEntry.toRoute<ReferenceRoute>()
				ReferenceScreen(
					onBack = { navController.popBackStack() },
					initialCategory = dest.category,
					initialQuery = dest.query,
					initialDetailCategory = dest.detailCategory,
					initialDetailSlug = dest.detailSlug
				)
			}
		}
	}
}

private fun NavDestination?.isBottomBarSelected(screen: BottomBarScreen): Boolean {
	if (this?.hierarchy?.any { it.hasRoute(screen.routeClass) } == true) return true

	return when (screen) {
		is BottomBarScreen.Home -> hasAnyRoute(
			TrackerRoute::class,
			SummaryRoute::class,
			AdventureLogRoute::class,
			LogRoute::class
		)

		is BottomBarScreen.CampaignList -> hasAnyRoute(CampaignDetailRoute::class)
		is BottomBarScreen.CharacterList -> hasAnyRoute(
			CharacterDetailRoute::class,
			CharacterFormRoute::class
		)

		else -> false
	}
}

private fun NavDestination?.hasAnyRoute(vararg routes: KClass<*>): Boolean {
	return routes.any { route -> this?.hasRoute(route) == true }
}

sealed class BottomBarScreen(
	val routeObject: Any,
	val routeClass: KClass<*>,
	val labelRes: Int,
	val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
	object Home : BottomBarScreen(
		HomeRoute,
		HomeRoute::class,
		R.string.bottom_nav_home,
		Icons.Default.Home
	)

	object CampaignList :
		BottomBarScreen(
			CampaignListRoute,
			CampaignListRoute::class,
			R.string.bottom_nav_campaigns,
			Icons.Default.Map
		)

	object CharacterList : BottomBarScreen(
		CharacterListRoute,
		CharacterListRoute::class,
		R.string.bottom_nav_characters,
		Icons.AutoMirrored.Filled.List
	)


	object Reference : BottomBarScreen(
		ReferenceRoute(),
		ReferenceRoute::class,
		R.string.bottom_nav_rules,
		Icons.Default.Info
	)
}
