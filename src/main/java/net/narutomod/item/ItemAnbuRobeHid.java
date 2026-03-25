
package net.narutomod.item;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.creativetab.TabModTab;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAnbuRobeHid extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:anbu_robebody_hid")
	public static final Item body = null;

	public ItemAnbuRobeHid(ElementsNarutomodMod instance) {
		super(instance, 828);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ModelHoody();
					this.texture = "narutomod:textures/robe_hood.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = true;
				}
			}
			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (entity.ticksExisted % 10 == 6 && entity instanceof EntityLivingBase) {
					entity.setAlwaysRenderNameTag(!((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.CHEST).equals(itemstack));
				}
			}
		}.setUnlocalizedName("anbu_robebody_hid").setRegistryName("anbu_robebody_hid").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:anbu_robebody", "inventory"));
	}
	// Made with Blockbench 4.6.1
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelHoody extends ItemRobe.ModelRobe {
		public ModelHoody() {
			super();
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -7.85F, -4.0F, 8, 8, 8, 1.0F, false));
		}
	}
}
