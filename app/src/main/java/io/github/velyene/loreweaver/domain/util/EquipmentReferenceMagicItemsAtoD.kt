@file:Suppress("kotlin:S1192")

package io.github.velyene.loreweaver.domain.util

internal object EquipmentReferenceMagicItemsAtoD {
	val MAGIC_ITEMS: List<MagicItemReferenceEntry> =
		EquipmentReferenceMagicItemsData.magicItemsStartingIn('A'..'D')
}
