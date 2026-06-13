package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemMokuton extends ItemWoodRelease {
	public ItemMokuton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class ItemCustom extends ItemWoodRelease.ItemCustom {
		public ItemCustom(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}
}
