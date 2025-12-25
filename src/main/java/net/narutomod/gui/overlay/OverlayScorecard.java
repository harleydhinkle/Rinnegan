package net.narutomod.gui.overlay;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.PlayerTracker;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@ElementsNarutomodMod.ModElement.Tag
public class OverlayScorecard extends ElementsNarutomodMod.ModElement {

    public static boolean isMenuOpen = false;

    public OverlayScorecard(ElementsNarutomodMod instance) {
        super(instance, 931);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new GUIRenderEventClass());
        net.narutomod.keybind.KeyScorecard.init();
    }

    public static class GUIRenderEventClass {

        @SubscribeEvent(priority = EventPriority.NORMAL)
        @SideOnly(Side.CLIENT)
        public void eventHandler(RenderGameOverlayEvent event) {

            if (!event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {

                if (!OverlayScorecard.isMenuOpen)
                    return;

                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayer entity = mc.player;
                if (entity == null)
                    return;

                int screenW = event.getResolution().getScaledWidth();
                int screenH = event.getResolution().getScaledHeight();
                int centerX = screenW / 2;
                int centerY = screenH / 2;

                // ===== dark background overlay =====
                GlStateManager.disableDepth();
                GlStateManager.enableBlend();
                Gui.drawRect(0, 0, screenW, screenH, 0xAA000000);

                // ===== draw the card PNG =====
                int cardWidth = 220;
                int cardHeight = 283;
                int cardX = centerX - cardWidth / 2;
                int cardY = centerY - cardHeight / 2;

                GlStateManager.color(1F, 1F, 1F, 1F); // IMPORTANT: reset to white
                mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/player_card_final.png"));
                mc.ingameGUI.drawModalRectWithCustomSizedTexture(
                        cardX, cardY,
                        0, 0,
                        cardWidth, cardHeight,
                        cardWidth, cardHeight
                );

                // ===== draw player values (no labels) =====
                int color = 0xFF000000; // black text
                GlStateManager.disableDepth();

                // X position for all values (pulled left from last screenshot)
                int valueX = cardX + 90;
                // move everything slightly up
                int shiftY = -12;

                // Name
                mc.fontRenderer.drawString(
                        entity.getDisplayNameString(),
                        valueX, cardY + 83 + shiftY, color
                );

                // Clan
                mc.fontRenderer.drawString(
                        entity.getEntityData().getString("playerClan"),
                        valueX, cardY + 118 + shiftY, color
                );

                // Kekkei Genkai
                mc.fontRenderer.drawString(
                        entity.getEntityData().getString("playerGenkai"),
                        valueX, cardY + 150 + shiftY, color
                );

                // Ninja Experience
                double battleXp = PlayerTracker.getBattleXp(entity);
                mc.fontRenderer.drawString(
                        String.valueOf((int) battleXp),
                        valueX, cardY + 185 + shiftY, color
                );

                // Ninja Level
                int ninjaLevel = (int) (battleXp / 500.0);
                mc.fontRenderer.drawString(
                        String.valueOf(ninjaLevel),
                        valueX, cardY + 215 + shiftY, color
                );

                // Health
                mc.fontRenderer.drawString(
                        String.format("%.0f / %.0f", entity.getHealth(), entity.getMaxHealth()),
                        valueX, cardY + 247 + shiftY, color
                );

                // Dojutsu
                mc.fontRenderer.drawString(
                        entity.getEntityData().getString("playerDojutsu"),
                        valueX, cardY + 280 + shiftY, color
                );

                GlStateManager.enableDepth();
                GlStateManager.disableBlend();
            }
        }
    }
}
