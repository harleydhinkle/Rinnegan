package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemRanton extends ItemStormRelease {
	public ItemRanton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class RangedItem extends ItemStormRelease.RangedItem {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}
}
