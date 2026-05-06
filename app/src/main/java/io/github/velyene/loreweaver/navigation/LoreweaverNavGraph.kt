/*
 * FILE: LoreweaverNavGraph.kt
 *
 * TABLE OF CONTENTS:
 * 1. App Root (LoreweaverApp)
 * 2. Navigation Graph (LoreweaverNavGraph)
 * 3. Bottom Bar Selection Helpers
 */

package io.github.velyene.loreweaver.navigation

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
import androidx.compose.runtime.remember
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
import io.github.velyene.loreweaver.ui.screens.AdventureLogScreen
import io.github.velyene.loreweaver.ui.screens.CampaignDetailScreen
import io.github.velyene.loreweaver.ui.screens.CampaignListScreen
import io.github.velyene.loreweaver.ui.screens.CharacterDetailScreen
import io.github.velyene.loreweaver.ui.screens.CharacterFormScreen
import io.github.velyene.loreweaver.ui.screens.CharacterListScreen
import io.github.velyene.loreweaver.ui.screens.CombatTrackerScreen
import io.github.velyene.loreweaver.ui.screens.ConditionConstants
import io.github.velyene.loreweaver.ui.screens.DmSessionHubScreen
import io.github.velyene.loreweaver.ui.screens.EnemyLibraryScreen
import io.github.velyene.loreweaver.ui.screens.HomeScreen
import io.github.velyene.loreweaver.ui.screens.PromptLibraryScreen
import io.github.velyene.loreweaver.ui.screens.ReferenceScreen
import io.github.velyene.loreweaver.ui.screens.SavedEncounterPickerScreen
import io.github.velyene.loreweaver.ui.screens.SessionDetailScreen
import io.github.velyene.loreweaver.ui.screens.SessionHistoryScreen
import io.github.velyene.loreweaver.ui.screens.SessionSummaryScreen
import io.github.velyene.loreweaver.ui.screens.SessionSummaryMode
import io.github.velyene.loreweaver.ui.viewmodels.EnemyLibraryEncounterSetupDraft
import io.github.velyene.loreweaver.ui.viewmodels.consumeEnemyLibraryEncounterSetupDraft
import io.github.velyene.loreweaver.ui.viewmodels.stashEnemyLibraryEncounterSetupDraft
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
				onNewEncounter = { navController.navigate(DmSessionHubRoute) },
				onCampaigns = { navController.navigate(CampaignListRoute) },
				onResumeEncounter = { navController.navigate(CombatTrackerRoute()) },
				onLatestSessionClick = { sessionId -> navController.navigate(SessionDetailRoute(sessionId)) },
				onCampaignClick = { id -> navController.navigate(CampaignDetailRoute(id)) },
			) { navController.navigate(ReferenceRoute()) }
		}
		composable<DmSessionHubRoute> {
			DmSessionHubScreen(
				onBack = { navController.popBackStack() },
				onNewEncounter = { navController.navigate(CombatTrackerRoute(startFresh = true)) },
				onResumeActiveEncounter = { navController.navigate(CombatTrackerRoute()) },
				onOpenSavedEncounters = { navController.navigate(SavedEncounterPickerRoute) },
				onManageCharacters = { navController.navigate(CharacterListRoute) },
				onEnemyLibrary = { navController.navigate(EnemyLibraryRoute) },
				onSessionLogNotes = { navController.navigate(AdventureLogRoute) },
				onCampaigns = { navController.navigate(CampaignListRoute) },
				onAboutHelp = { navController.navigate(ReferenceRoute()) },
			)
		}
		composable<SavedEncounterPickerRoute> {
			SavedEncounterPickerScreen(
				onBack = { navController.popBackStack() },
				onOpenEncounter = { encounterId -> navController.navigate(CombatTrackerRoute(encounterId = encounterId)) },
			)
		}
		composable<EnemyLibraryRoute> {
			EnemyLibraryScreen(
				onBack = { navController.popBackStack() },
				onOpenEncounterSetup = { stagedEnemies ->
					navController.currentBackStackEntry?.savedStateHandle?.let { savedStateHandle ->
						stashEnemyLibraryEncounterSetupDraft(savedStateHandle, stagedEnemies)
					}
					navController.navigate(CombatTrackerRoute(startFresh = true))
				},
			)
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
				onSessionClick = { sessionId -> navController.navigate(SessionDetailRoute(sessionId)) },
				onEncounterClick = { encounterId ->
					navController.navigate(CombatTrackerRoute(encounterId))
				},
			)
		}
		composable<CombatTrackerRoute> { backStackEntry: NavBackStackEntry ->
			val dest = backStackEntry.toRoute<CombatTrackerRoute>()
			val stagedLibraryDraft = remember(backStackEntry, dest.startFresh) {
				if (dest.startFresh) {
					consumeEnemyLibraryEncounterSetupDraft(navController.previousBackStackEntry?.savedStateHandle)
				} else {
					EnemyLibraryEncounterSetupDraft()
				}
			}
			CombatTrackerScreen(
				encounterId = dest.encounterId,
				startFresh = dest.startFresh,
				stagedLibraryDraft = stagedLibraryDraft,
				onBack = { navController.popBackStack() },
				onExitHome = {
					navController.navigate(HomeRoute) {
						popUpTo<HomeRoute> { inclusive = true }
					}
				},
				onEndEncounter = { sessionId ->
					navController.navigate(EncounterSummaryRoute(sessionId = sessionId)) {
						popUpTo<CombatTrackerRoute> { inclusive = true }
					}
				},
			)
		}
		composable<RewardReviewRoute> {
			SessionSummaryScreen(
				summaryMode = SessionSummaryMode.ENCOUNTER_COMPLETION,
				onSaveEncounter = { sessionId ->
					navController.navigate(SessionDetailRoute(sessionId))
				},
				onDone = {
					navController.navigate(HomeRoute) {
						popUpTo<HomeRoute> { inclusive = true }
					}
				},
				onStartAnotherEncounter = {
					navController.navigate(CombatTrackerRoute(startFresh = true)) {
						popUpTo<RewardReviewRoute> { inclusive = true }
					}
				},
			)
		}
		composable<EncounterSummaryRoute> { backStackEntry: NavBackStackEntry ->
			val dest = backStackEntry.toRoute<EncounterSummaryRoute>()
			SessionSummaryScreen(
				sessionId = dest.sessionId,
				summaryMode = SessionSummaryMode.ENCOUNTER_COMPLETION,
				onSaveEncounter = { sessionId ->
					navController.navigate(SessionDetailRoute(sessionId))
				},
				onDone = {
					navController.navigate(HomeRoute) {
						popUpTo<HomeRoute> { inclusive = true }
					}
				},
				onStartAnotherEncounter = {
					navController.navigate(CombatTrackerRoute(startFresh = true)) {
						popUpTo<EncounterSummaryRoute> { inclusive = true }
					}
				},
			)
		}
		composable<SessionSummaryRoute> {
			SessionSummaryScreen(
				onDone = {
					navController.navigate(HomeRoute) {
						popUpTo<HomeRoute> { inclusive = true }
					}
				},
				onContinueCampaign = { campaignId ->
					navController.navigate(CampaignDetailRoute(campaignId)) {
						popUpTo<SessionSummaryRoute> { inclusive = true }
					}
				},
				onOpenSessionDetail = { sessionId ->
					navController.navigate(SessionDetailRoute(sessionId))
				},
				onStartAnotherEncounter = {
					navController.navigate(CombatTrackerRoute()) {
						popUpTo<SessionSummaryRoute> { inclusive = true }
					}
				},
				onOpenSessionHistory = { navController.navigate(SessionHistoryRoute) },
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
			SessionHistoryScreen(
				onBack = { navController.popBackStack() },
				onSessionClick = { sessionId -> navController.navigate(SessionDetailRoute(sessionId)) },
			)
		}
		composable<SessionDetailRoute> { backStackEntry: NavBackStackEntry ->
			val dest = backStackEntry.toRoute<SessionDetailRoute>()
			SessionDetailScreen(
				sessionId = dest.id,
				onBack = { navController.popBackStack() },
				onOpenCampaign = { campaignId -> navController.navigate(CampaignDetailRoute(campaignId)) },
			)
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
				onLookupCondition = { label ->
					ConditionConstants.referenceTargetFor(label)?.let { target ->
						navController.navigate(
							ReferenceRoute(
								category = target.category,
								query = target.query,
								detailCategory = target.detailCategory,
								detailSlug = target.detailSlug,
							)
						)
					}
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
			DmSessionHubRoute::class,
			SavedEncounterPickerRoute::class,
			EnemyLibraryRoute::class,
			CombatTrackerRoute::class,
			RewardReviewRoute::class,
			EncounterSummaryRoute::class,
			SessionSummaryRoute::class,
			SessionDetailRoute::class,
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


