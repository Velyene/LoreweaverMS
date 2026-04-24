package io.github.velyene.loreweaver.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.velyene.loreweaver.R
import kotlin.reflect.KClass

sealed class BottomBarScreen(
	val routeObject: Any,
	val routeClass: KClass<*>,
	val labelRes: Int,
	val icon: ImageVector,
) {
	data object Home : BottomBarScreen(
		routeObject = HomeRoute,
		routeClass = HomeRoute::class,
		labelRes = R.string.bottom_nav_home,
		icon = Icons.Default.Home,
	)

	data object CampaignList : BottomBarScreen(
		routeObject = CampaignListRoute,
		routeClass = CampaignListRoute::class,
		labelRes = R.string.bottom_nav_campaigns,
		icon = Icons.Default.Map,
	)

	data object CharacterList : BottomBarScreen(
		routeObject = CharacterListRoute,
		routeClass = CharacterListRoute::class,
		labelRes = R.string.bottom_nav_characters,
		icon = Icons.AutoMirrored.Filled.List,
	)

	data object Reference : BottomBarScreen(
		routeObject = ReferenceRoute(),
		routeClass = ReferenceRoute::class,
		labelRes = R.string.bottom_nav_rules,
		icon = Icons.Default.Info,
	)
}

