package net.narutomod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.narutomod.PlayerTracker;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.Set;
import java.util.HashMap;

import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAdminAbuse extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder("narutomod:admin_abuse")
    public static final Item block = null;

    public ItemAdminAbuse(ElementsNarutomodMod instance) {super(instance, 2015); }

    @Override
    public void initElements() {
        elements.items.add(() -> new ItemSword(EnumHelper.addToolMaterial("ADMIN_ABUSE", 4, 5000000, 40f, 30000f, 0)) {
            @Override
            public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    elb.attackEntityFrom(DamageSource.OUT_OF_WORLD, elb.getMaxHealth() * (float) 1000);
                } else {
                    entity.onKillCommand();
                }
                return false;
            }
            public Set<String> getToolClasses(ItemStack stack) {
                HashMap<String, Integer> ret = new HashMap<String, Integer>();
                ret.put("sword", 0);
                return ret.keySet();
            }
        }.setUnlocalizedName("admin_abuse").setRegistryName("admin_abuse").setCreativeTab(TabModTab.tab));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:admin_abuse", "inventory"));
    }
}