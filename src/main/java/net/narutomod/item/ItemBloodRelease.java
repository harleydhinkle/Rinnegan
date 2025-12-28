package net.narutomod.item;

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

    public static final ItemJutsu.JutsuEnum BLOODDRAGON = new ItemJutsu.JutsuEnum(902, 3, "blood_dragon", 'A', 100d, EntityBloodDragon.EC.Jutsu.INSTANCE);
    public static final ItemJutsu.JutsuEnum BLOODPRISON = new ItemJutsu.JutsuEnum(903, 3, "blood_prison", 'A', 100d, EntityBloodPrison.EC.Jutsu.INSTANCE);
    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST = new ItemJutsu.JutsuEnum(904, 3, "hiding_in_blood_mist", 'A', 100d, EntityHidingInBloodMist.EC.Jutsu.INSTANCE);
    public static final ItemJutsu.JutsuEnum BLOODSIPHONCHAINS = new ItemJutsu.JutsuEnum(905, 3, "blood_siphon_chains", 'A', 100d, EntityBloodSiphonChains.EC.Jutsu.INSTANCE);

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 902);
    }

    @Override
    public void initElements() {
        elements.items.add(() -> new ItemCustom(
                BLOODDRAGON,
                BLOODPRISON,
                HIDINGINBLOODMIST,
                BLOODSIPHONCHAINS
        ));
    }

    public static class ItemCustom extends ItemJutsu {

        public ItemCustom(ItemJutsu.JutsuEnum... list) {
            super(ItemJutsu.JutsuEnum.Type.BLOOD, list);
            setUnlocalizedName("blood_release");
            setRegistryName("blood_release");
        }

        /**
         * When crafted / obtained -> preload jutsu
         */
        public boolean onCreated(ItemStack stack, World world, EntityPlayer player) {
            for (ItemJutsu.JutsuEnum j : jutsuList) {
                ItemJutsu.unlockJutsu(player, j); // keeps XP requirement intact
            }
            return true;
        }

        /**
         * Jutsu remains preloaded only while item is in inventory
         */
        public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
            super.onUpdate(stack, world, entity, slot, held);

            if (!world.isRemote && entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                boolean hasItem = player.inventory.hasItemStack(stack);

                for (ItemJutsu.JutsuEnum j : jutsuList) {
                    if (hasItem) {
                        ItemJutsu.unlockJutsu(player, j);
                    } else {
                        ItemJutsu.lockJutsu(player, j);
                    }
                }
            }
        }
    }
}
