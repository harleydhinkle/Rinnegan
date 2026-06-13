package net.narutomod.gui;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemLightningRelease;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

@ElementsNarutomodMod.ModElement.Tag
public class GuiScrollLightningRatTremorGui extends ElementsNarutomodMod.ModElement {
	public static int GUIID = 75;

	public GuiScrollLightningRatTremorGui(ElementsNarutomodMod instance) {
		super(instance, 947);
	}

	public static class GuiContainerMod extends GuiNinjaScroll.GuiContainerMod {
		public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player) {
			super(world, x, y, z, player, GUIID);
		}

		@Override
		protected void handleButtonAction(EntityPlayer player, int buttonID) {
			if (player.world.isRemote || !player.world.isBlockLoaded(new BlockPos(this.x, this.y, this.z))) {
				return;
			}
			ItemStack stack = GuiNinjaScroll.enableJutsu(player, (ItemLightningRelease.RangedItem)ItemLightningRelease.block, ItemLightningRelease.RAT_TREMOR, true);
			if (stack != null) {
				super.handleButtonAction(player, buttonID);
			}
		}
	}

	public static class GuiWindow extends GuiNinjaScroll.GuiWindow {
		public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
			super(new GuiContainerMod(world, x, y, z, entity));
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			super.drawGuiContainerBackgroundLayer(par1, par2, par3);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/blocks/raiton.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 89, this.guiTop + 49, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/shen_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 0, this.guiTop + 108, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/chou_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 56, this.guiTop + 108, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/hai_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 112, this.guiTop + 108, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/yin_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 168, this.guiTop + 108, 0, 0, 48, 48, 48, 48);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			this.fontRenderer.drawString(I18n.translateToLocal(ItemLightningRelease.block.getUnlocalizedName() + ".name") + ": " + ItemLightningRelease.RAT_TREMOR.getName(), 38, 13, -16777216);
		}
	}
}
