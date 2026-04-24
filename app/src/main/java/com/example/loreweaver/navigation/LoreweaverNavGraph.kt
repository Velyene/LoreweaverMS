/*
 * FILE: LoreweaverNavGraph.kt
 *
 * TABLE OF CONTENTS:
 * 1. App Root (LoreweaverApp)
 * 2. Navigation Graph (LoreweaverNavGraph)
 * 3. Bottom Bar Selection Helpers
 */

package com.example.loreweaver.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.loreweaver.domain.util.ReferenceDetailResolver
import com.example.loreweaver.ui.screens.AdventureLogScreen
import com.example.loreweaver.ui.screens.CampaignDetailScreen
import com.example.loreweaver.ui.screens.CampaignListScreen
import com.example.loreweaver.ui.screens.CharacterDetailScreen
import com.example.loreweaver.ui.screens.CharacterFormScreen
import com.example.loreweaver.ui.screens.CharacterListScreen
import com.example.loreweaver.ui.screens.CombatTrackerScreen
import com.example.loreweaver.ui.screens.HomeScreen
import com.example.loreweaver.ui.screens.PromptLibraryScreen
import com.example.loreweaver.ui.screens.ReferenceScreen
import com.example.loreweaver.ui.screens.SessionHistoryScreen
import com.example.loreweaver.ui.screens.SessionSummaryScreen
import com.example.loreweaver.ui.viewmodels.ReferenceCategory
import kotlin.reflect.KClass

@Composable
fun LoreweaverApp() {
	val navController = rememberNavController()
	val bottomNavItems = listOf(
		BottomBarScreen.Home,
		BottomBarScreen.CampaignList,
		BottomBarScreen.CharacterList,
		BottomBarScreen.Reference,
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
						},
					)
				}
			}
		},
	) { innerPadding: PaddingValues ->
		LoreweaverNavGraph(
			navController = navController,
			modifier = Modifier.padding(innerPadding),
		)
	}
}

@Composable
private fun LoreweaverNavGraph(
	navController: NavHostController,
	modifier: Modifier = Modifier,
) {
	NavHost(
		navController = navController,
		startDestination = HomeRoute,
		modifier = modifier,
		enterTransition = {
			slideIntoContainer(
				AnimatedContentTransitionScope.SlideDirection.Left,
				tween(400),
			) + fadeIn(tween(400))
		},
		exitTransition = {
			slideOutOfContainer(
				AnimatedContentTransitionScope.SlideDirection.Left,
				tween(400),
			) + fadeOut(tween(400))
		},
	) {
		composable<HomeRoute> {
			HomeScreen(
				onNewEncounter = { navController.navigate(CombatTrackerRoute()) },
				onCampaigns = { navController.navigate(CampaignListRoute) },
				onResumeEncounter = { navController.navigate(CombatTrackerRoute()) },
				onCampaignClick = { id -> navController.navigate(CampaignDetailRoute(id)) },
			) { navController.navigate(ReferenceRoute()) }
		}
		composable<CampaignListRoute> {
			CampaignListScreen(
				onCampaignClick = { id -> navController.navigate(CampaignDetailRoute(id)) },
				onBack = { navController.popBackStack() },
			)
		}
		composable<CampaignDetailRoute> { backStackEntry: NavBackStackEntry ->
			val dest = backStackEntry.toRoute<CampaignDetailRoute>()
			CampaignDetailScreen(
				campaignId = dest.id,
				onBack = { navController.popBackStack() },
				onEncounterClick = { encounterId ->
					navController.navigate(CombatTrackerRoute(encounterId))
				},
			)
		}
		composable<CombatTrackerRoute> { backStackEntry: NavBackStackEntry ->
			val dest = backStackEntry.toRoute<CombatTrackerRoute>()
			CombatTrackerScreen(
				encounterId = dest.encounterId,
				onBack = { navController.popBackStack() },
				onEndEncounter = { navController.navigate(SessionSummaryRoute) },
			)
		}
		composable<SessionSummaryRoute> {
			SessionSummaryScreen(
				onDone = {
					navController.navigate(HomeRoute) {
						popUpTo<HomeRoute> { inclusive = true }
					}
				},
				onOpenAdventureLog = { navController.navigate(AdventureLogRoute) },
			)
		}
		composable<CharacterListRoute> {
			CharacterListScreen(
				onCharacterClick = { charId ->
					navController.navigate(CharacterDetailRoute(charId))
				},
				onAddCharacter = { navController.navigate(CharacterFormRoute()) },
				onBack = { navController.popBackStack() },
			)
		}
		composable<SessionHistoryRoute> {
			SessionHistoryScreen(onBack = { navController.popBackStack() })
		}
		composable<AdventureLogRoute> {
			AdventureLogScreen(onBack = { navController.popBackStack() })
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
							detailSlug = ReferenceDetailResolver.slugFor(cond),
						)
					)
				},
				onDelete = { navController.popBackStack() },
				onBack = { navController.popBackStack() },
			)
		}
		composable<CharacterFormRoute> { backStackEntry: NavBackStackEntry ->
			val dest = backStackEntry.toRoute<CharacterFormRoute>()
			CharacterFormScreen(
				characterId = dest.id,
				onSave = { navController.popBackStack() },
				onBack = { navController.popBackStack() },
			)
		}
		composable<ReferenceRoute> { backStackEntry: NavBackStackEntry ->
			val dest = backStackEntry.toRoute<ReferenceRoute>()
			ReferenceScreen(
				onBack = { navController.popBackStack() },
				initialCategory = dest.category,
				initialQuery = dest.query,
				initialDetailCategory = dest.detailCategory,
				initialDetailSlug = dest.detailSlug,
			)
		}
	}
}

private fun NavDestination?.isBottomBarSelected(screen: BottomBarScreen): Boolean {
	if (this?.hierarchy?.any { it.hasRoute(screen.routeClass) } == true) return true

	return when (screen) {
		is BottomBarScreen.Home -> hasAnyRoute(
			CombatTrackerRoute::class,
			SessionSummaryRoute::class,
			AdventureLogRoute::class,
			SessionHistoryRoute::class,
		)

		is BottomBarScreen.CampaignList -> hasAnyRoute(CampaignDetailRoute::class)
		is BottomBarScreen.CharacterList -> hasAnyRoute(
			CharacterDetailRoute::class,
			CharacterFormRoute::class,
		)

		else -> false
	}
}

private fun NavDestination?.hasAnyRoute(vararg routes: KClass<*>): Boolean {
	return routes.any { route -> this?.hasRoute(route) == true }
}


