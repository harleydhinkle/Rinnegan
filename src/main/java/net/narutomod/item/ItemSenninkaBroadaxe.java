/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.ModelRegistryEvent
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.common.registry.GameRegistry$ObjectHolder
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.narutomod.item;

import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSenninkaBroadaxe
extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder(value="narutomod:senninka_broadaxe")
    public static final Item block = null;

    public ItemSenninkaBroadaxe(ElementsNarutomodMod instance) {
        super(instance, 941);
    }

    @Override
    public void initElements() {
        this.elements.items.add(() -> ((Item)new ItemToolCustom(){}.func_77655_b("senninka_broadaxe").setRegistryName("senninka_broadaxe")).func_77637_a(null));
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation((Item)block, (int)0, (ModelResourceLocation)new ModelResourceLocation("narutomod:senninka_broadaxe", "inventory"));
    }

    private static class ItemToolCustom
    extends Item {
        protected ItemToolCustom() {
            this.func_77656_e(100);
            this.func_77625_d(1);
        }

        public Multimap<String, AttributeModifier> func_111205_h(EntityEquipmentSlot equipmentSlot) {
            Multimap multimap = super.func_111205_h(equipmentSlot);
            if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
                multimap.put((Object)SharedMonsterAttributes.field_111264_e.func_111108_a(), (Object)new AttributeModifier(field_111210_e, "Tool modifier", 8.0, 0));
                multimap.put((Object)SharedMonsterAttributes.field_188790_f.func_111108_a(), (Object)new AttributeModifier(field_185050_h, "Tool modifier", -3.0, 0));
            }
            return multimap;
        }

        public void func_77663_a(ItemStack itemstack, World world, Entity entity, int par4, boolean isSelected) {
            super.func_77663_a(itemstack, world, entity, par4, isSelected);
            if (!world.field_72995_K && entity.field_70173_aa % 20 == 3 && (!isSelected || entity instanceof EntityPlayer && !ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer)entity, ItemSenninka.block))) {
                itemstack.func_190918_g(1);
            }
        }

        public float func_150893_a(ItemStack par1ItemStack, IBlockState par2Block) {
            return 0.0f;
        }

        public boolean func_179218_a(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
            stack.func_77972_a(1, entityLiving);
            return true;
        }

        public boolean func_77644_a(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
            stack.func_77972_a(2, attacker);
            return true;
        }

        public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
            return true;
        }

        public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
            return stack.func_77973_b() == block;
        }

        public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
            if (attacker.func_184587_cr()) {
                return true;
            }
            return super.onLeftClickEntity(itemstack, attacker, target);
        }

        public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
            playerIn.func_184598_c(handIn);
            return new ActionResult(EnumActionResult.SUCCESS, (Object)playerIn.func_184586_b(handIn));
        }

        public EnumAction func_77661_b(ItemStack stack) {
            return EnumAction.BLOCK;
        }

        public int func_77626_a(ItemStack stack) {
            return 72000;
        }

        public boolean func_77662_d() {
            return true;
        }

        public int func_77619_b() {
            return 0;
        }
    }
}

