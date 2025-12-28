package net.narutomod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemJutsu;
import net.narutomod.entity.EntityBloodDragon;
import net.narutomod.entity.EntityBloodPrison;
import net.narutomod.entity.EntityHidingInBloodMist;
import net.narutomod.entity.EntityBloodSiphonChains;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBloodRelease extends ElementsNarutomodMod.ModElement {

    public static final ItemJutsu.JutsuEnum BLOODDRAGON =
            new ItemJutsu.JutsuEnum("blood_dragon", 3, 'A', 100d, new EntityBloodDragon.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum BLOODPRISON =
            new ItemJutsu.JutsuEnum("blood_prison", 3, 'A', 100d, new EntityBloodPrison.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST =
            new ItemJutsu.JutsuEnum("hiding_in_blood_mist", 3, 'A', 100d, new EntityHidingInBloodMist.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum BLOODSIPHONCHAINS =
            new ItemJutsu.JutsuEnum("blood_siphon_chains", 3, 'A', 100d, new EntityBloodSiphonChains.EC.Jutsu());

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 902);
    }

    @Override
    public void initElements() {
        elements.items.add(new ItemCustom());
    }

    public static class ItemCustom extends ItemJutsu {

        public ItemCustom() {
            super(ItemJutsu.JutsuEnum.Type.BLOOD, BLOODDRAGON, BLOODPRISON, HIDINGINBLOODMIST, BLOODSIPHONCHAINS);
            setUnlocalizedName("blood_release");
            setRegistryName("blood_release");
        }

        // Preload jutsus when item is crafted or obtained
        @Override
        public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
            for (ItemJutsu.JutsuEnum j : new ItemJutsu.JutsuEnum[]{BLOODDRAGON, BLOODPRISON, HIDINGINBLOODMIST, BLOODSIPHONCHAINS}) {
                ItemJutsu.preloadJutsu(playerIn, j);
            }
        }

        // Keep jutsus unlocked while item is in inventory
        @Override
        public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
            if (!worldIn.isRemote && entityIn instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityIn;
                boolean hasItem = false;
                for (ItemStack invStack : player.inventory.mainInventory) {
                    if (!invStack.isEmpty() && invStack.getItem() instanceof ItemCustom) {
                        hasItem = true;
                        break;
                    }
                }

                for (ItemJutsu.JutsuEnum j : new ItemJutsu.JutsuEnum[]{BLOODDRAGON, BLOODPRISON, HIDINGINBLOODMIST, BLOODSIPHONCHAINS}) {
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
