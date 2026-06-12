package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodMod;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.gui.GuiScrollEarthDragonGui;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemScrollEarthDragon extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:scroll_earth_dragon")
	public static final Item block = null;

	public ItemScrollEarthDragon(ElementsNarutomodMod instance) {
		super(instance, 945);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:scroll_earth_dragon", "inventory"));
	}

	public static class ItemCustom extends Item {
		public ItemCustom() {
			this.setMaxDamage(1);
			this.maxStackSize = 1;
			this.setUnlocalizedName("scroll_earth_dragon");
			this.setRegistryName("scroll_earth_dragon");
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public int getItemEnchantability() {
			return 0;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack stack, IBlockState state) {
			return 0.0F;
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add("C-rank jutsu scroll");
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ActionResult<ItemStack> ar = super.onItemRightClick(world, entity, hand);
			entity.openGui(NarutomodMod.instance, GuiScrollEarthDragonGui.GUIID, world, (int)entity.posX, (int)entity.posY, (int)entity.posZ);
			return ar;
		}
	}
}
