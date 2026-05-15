package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemFutton extends ItemBoilRelease {
	public ItemFutton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class RangedItem extends ItemBoilRelease.RangedItem {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}
}
