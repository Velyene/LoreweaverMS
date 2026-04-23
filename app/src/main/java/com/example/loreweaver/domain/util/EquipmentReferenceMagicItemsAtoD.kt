@file:Suppress("kotlin:S1192")

package com.example.loreweaver.domain.util

internal object EquipmentReferenceMagicItemsAtoD {
	val MAGIC_ITEMS: List<MagicItemReferenceEntry> =
		EquipmentReferenceMagicItemsData.magicItemsStartingIn('A'..'D')
}
