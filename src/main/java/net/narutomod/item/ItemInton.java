package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

/**
 * Binary compatibility facade for addons compiled against the old class name.
 */
@Deprecated
public class ItemInton extends ItemYinRelease {
	public ItemInton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class RangedItem extends ItemYinRelease.RangedItem {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}
}
