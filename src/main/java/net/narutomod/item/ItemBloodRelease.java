package net.narutomod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityBloodDragon;
import net.narutomod.entity.EntityBloodPrison;
import net.narutomod.entity.EntityHidingInBloodMist;
import net.narutomod.entity.EntityBloodSiphonChains;

import java.util.Arrays;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBloodRelease extends ElementsNarutomodMod.ModElement {

    // Define your jutsus
    public static final ItemJutsu.JutsuEnum BLOODDRAGON =
            new ItemJutsu.JutsuEnum(902, 3, "blood_dragon", 'A', 100d, new EntityBloodDragon.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum BLOODPRISON =
            new ItemJutsu.JutsuEnum(903, 3, "blood_prison", 'A', 100d, new EntityBloodPrison.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST =
            new ItemJutsu.JutsuEnum(904, 3, "hiding_in_blood_mist", 'A', 100d, new EntityHidingInBloodMist.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum BLOODSIPHONCHAINS =
            new ItemJutsu.JutsuEnum(905, 3, "blood_siphon_chains", 'A', 100d, new EntityBloodSiphonChains.EC.Jutsu());

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 902);
    }

    @Override
    public void initElements() {
        elements.items.add(() -> new ItemCustom());
    }

    public static class ItemCustom extends ItemJutsu {

        private final List<ItemJutsu.JutsuEnum> jutsuList;

        public ItemCustom() {
            super(ElementsNarutomodMod.instance, ItemJutsu.JutsuEnum.Type.BLOOD,
                    BLOODDRAGON, BLOODPRISON, HIDINGINBLOODMIST, BLOODSIPHONCHAINS);

            this.jutsuList = Arrays.asList(BLOODDRAGON, BLOODPRISON, HIDINGINBLOODMIST, BLOODSIPHONCHAINS);

            // Registry and translation key for 1.12
            setUnlocalizedName("blood_release");
            setRegistryName("blood_release");
        }

        @Override
        public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
            for (ItemJutsu.JutsuEnum j : jutsuList) {
                ItemJutsu.preloadJutsu(playerIn, j);
            }
            super.onCreated(stack, worldIn, playerIn);
        }

        @Override
        public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
            super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
            if (!worldIn.isRemote && entityIn instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityIn;
                boolean hasItem = false;

                for (ItemStack invStack : player.inventory.mainInventory) {
                    if (!invStack.isEmpty() && invStack.getItem() instanceof ItemBloodRelease.ItemCustom) {
                        hasItem = true;
                        break;
                    }
                }

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
