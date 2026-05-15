package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemJiton extends ItemMagnetRelease {
	public ItemJiton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class RangedItem extends ItemMagnetRelease.RangedItem {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}
}
