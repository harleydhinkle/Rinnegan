package net.narutomod.item;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemJutsu;
import net.narutomod.entity.EntityBloodDragon;
import net.narutomod.entity.EntityBloodPrison;
import net.narutomod.entity.EntityHidingInBloodMist;
import net.narutomod.entity.EntityBloodSiphonChains;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBloodRelease extends ElementsNarutomodMod.ModElement {

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 902); // unique ID in the 900s
    }

    @Override
    public void initElements() {
        this.elements.items.add(() -> new ItemCustom(
                EntityBloodDragon.EC.Jutsu.INSTANCE,
                EntityBloodPrison.EC.Jutsu.INSTANCE,
                EntityHidingInBloodMist.EC.Jutsu.INSTANCE,
                EntityBloodSiphonChains.EC.Jutsu.INSTANCE
        ).setRegistryName("blood_release"));
    }

    public static class ItemCustom extends ItemJutsu {

        public ItemCustom(ItemJutsu.IJutsuCallback... list) {
            super(ItemJutsu.JutsuEnum.Type.BLOOD, list);
            this.setUnlocalizedName("blood_release");
        }

        /**
         * When crafted / obtained -> preloads jutsu on the item
         */
        @Override
        public boolean onCreated(ItemStack stack, World world, EntityPlayer player) {
            // Preload jutsu into the item; player still needs XP to unlock
            for (ItemJutsu.IJutsuCallback jutsu : jutsuList) {
                ItemJutsu.preloadJutsu(player, jutsu);
            }
            return super.onCreated(stack, world, player);
        }

        /**
         * Keep jutsu preloaded while item is in inventory.
         * Player still must satisfy XP requirements to unlock.
         */
        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
            super.onUpdate(stack, world, entity, slot, held);

            if (!world.isRemote && entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                boolean hasItem = player.inventory.hasItemStack(stack);

                for (ItemJutsu.IJutsuCallback jutsu : jutsuList) {
                    if (hasItem) {
                        // Preload the jutsu so player can see it, but do not auto-unlock
                        ItemJutsu.preloadJutsu(player, jutsu);
                    } else {
                        // Optional: remove preloaded jutsu if item is removed
                        ItemJutsu.removePreloadedJutsu(player, jutsu);
                    }
                }
            }
        }
    }
}
