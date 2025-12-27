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

    @ObjectHolder("narutomod:blood_release")
    public static final Item block = null;

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 999); // OK for now
    }

    /*
     * ======================
     * BLOOD RELEASE JUTSU
     * ======================
     */

    public static final ItemJutsu.JutsuEnum BLOODDRAGON =
        new ItemJutsu.JutsuEnum(
            0,
            "blood_dragon",
            'S',
            200d,
            new EntityBloodDragon.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum BLOODPRISON =
        new ItemJutsu.JutsuEnum(
            1,
            "blood_prison",
            'S',
            150d,
            new EntityBloodPrison.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST =
        new ItemJutsu.JutsuEnum(
            2,
            "hiding_in_blood_mist",
            'A',
            80d,
            new EntityHidingInBloodMist.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum BLOODSIPHONCHAINS =
        new ItemJutsu.JutsuEnum(
            3,
            "blood_siphon_chains",
            'A',
            100d,
            new EntityBloodSiphonChains.EC.Jutsu()
        );

    @Override
    public void initElements() {
        this.elements.items.add(() ->
            new ItemCustom(
                BLOODDRAGON,
                BLOODPRISON,
                HIDINGINBLOODMIST,
                BLOODSIPHONCHAINS
            ).setRegistryName("blood_release")
        );
    }

    /*
     * ======================
     * BLOOD RELEASE ITEM
     * (Grants jutsu automatically and permanently)
     * ======================
     */

    public static class ItemCustom extends ItemJutsu.Base {

        public ItemCustom(ItemJutsu.JutsuEnum... list) {
            super(ItemJutsu.JutsuEnum.Type.BLOOD, list);
            this.setUnlocalizedName("blood_release");
        }

        /**
         * Unlock jutsu permanently when the item is obtained
         */
        @Override
        public boolean onCreated(ItemStack stack, World world, EntityPlayer player) {
            for (ItemJutsu.JutsuEnum j : jutsuList) {
                ItemJutsu.unlockJutsu(player, j); // Unlock permanently
            }
            return super.onCreated(stack, world, player);
        }

        /**
         * No need for onUpdate lock/unlock
         */
        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
            super.onUpdate(stack, world, entity, slot, held);
            // Locking logic removed
        }
    }
}
