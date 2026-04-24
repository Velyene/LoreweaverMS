package io.github.velyene.loreweaver.navigation

import io.github.velyene.loreweaver.ui.viewmodels.ReferenceCategory
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object CampaignListRoute

@Serializable
object CharacterListRoute

@Serializable
object PromptLibraryRoute

@Serializable
object SessionHistoryRoute

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
data class CombatTrackerRoute(val encounterId: String? = null)

@Serializable
object SessionSummaryRoute

@Serializable
data class CharacterDetailRoute(val id: String)

@Serializable
data class CharacterFormRoute(val id: String? = null)

