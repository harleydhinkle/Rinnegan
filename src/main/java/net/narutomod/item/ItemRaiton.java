package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemRaiton extends ItemLightningRelease {
	public ItemRaiton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class RangedItem extends ItemLightningRelease.RangedItem {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}
}
