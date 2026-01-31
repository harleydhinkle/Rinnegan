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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

@ElementsNarutomodMod.ModElement.Tag
public class OverlayScorecard extends ElementsNarutomodMod.ModElement {

    public static boolean isMenuOpen = false;

    // ===== DEBUG: set true to show mouse coords + guides =====
    private static final boolean DEBUG = false;

    // === CARD SIZE ===
    private static final int TEX_W = 288;
    private static final int TEX_H = 401;

    // Draw 1:1
    private static final int CARD_W = 288;
    private static final int CARD_H = 401;

    // Put PNG here:
    // assets/narutomod/textures/gui/player_card_288x401.png
    private static final ResourceLocation CARD_TEX =
            new ResourceLocation("narutomod", "textures/gui/player_card_288x401.png");

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

            if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
            if (!OverlayScorecard.isMenuOpen) return;

            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer entity = mc.player;
            if (entity == null) return;

            int screenW = event.getResolution().getScaledWidth();
            int screenH = event.getResolution().getScaledHeight();
            int centerX = screenW / 2;
            int centerY = screenH / 2;

            // ===== dim background =====
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            Gui.drawRect(0, 0, screenW, screenH, 0xAA000000);

            // ===== center card =====
            int cardX = centerX - CARD_W / 2;
            int cardY = centerY - CARD_H / 2;

            // ===== draw card =====
            GlStateManager.color(1F, 1F, 1F, 1F);
            mc.renderEngine.bindTexture(CARD_TEX);

            float oldZ = mc.ingameGUI.prevVignetteBrightness;
            mc.ingameGUI.prevVignetteBrightness = 500.0F;

            mc.ingameGUI.drawModalRectWithCustomSizedTexture(
                    cardX, cardY,
                    0, 0,
                    CARD_W, CARD_H,
                    TEX_W, TEX_H
            );

            // ===== TEXT POSITIONS tuned to your latest screenshot =====
            int color = 0xFF000000;

            // Default X for most values
            int valueX = cardX + 115;

            // Experience label is long, so push its value farther right
            int valueXExp = cardX + 165;

            // Y positions for each row (288x401 space)
            int yName   = cardY +  78;
            int yClan   = cardY + 118;
            int yGenkai = cardY + 150;  // moved UP so it stays in Genkai bar
            int yRank   = cardY + 190;

            // These were too low before; moved UP so they land in correct bars
            int yXP     = cardY + 232;
            int yLevel  = cardY + 272;
            int yHealth = cardY + 312;

            // Name
            mc.fontRenderer.drawString(
                    safe(entity.getDisplayNameString(), "Unknown"),
                    valueX, yName, color
            );

            // Clan
            mc.fontRenderer.drawString(
                    safe(entity.getEntityData().getString("playerClan"), "None"),
                    valueX, yClan, color
            );

            // Kekkei Genkai
            mc.fontRenderer.drawString(
                    safe(entity.getEntityData().getString("playerGenkai"), "None"),
                    valueX, yGenkai, color
            );

            // Ninja Rank
            mc.fontRenderer.drawString(
                    safe(entity.getEntityData().getString("playerRank"), "None"),
                    valueX, yRank, color
            );

            // Ninja Experience
            double battleXp = PlayerTracker.getBattleXp(entity);
            mc.fontRenderer.drawString(
                    String.valueOf((int) battleXp),
                    valueXExp, yXP, color
            );

            // Ninja Level
            int ninjaLevel = (int) (battleXp / 500.0);
            mc.fontRenderer.drawString(
                    String.valueOf(ninjaLevel),
                    valueX, yLevel, color
            );

            // Health
            mc.fontRenderer.drawString(
                    String.format("%.0f / %.0f", entity.getHealth(), entity.getMaxHealth()),
                    valueX, yHealth, color
            );

            // ===== Dojutsu text (bottom area) =====
            int dojutsuTextX = cardX + 140;
            int dojutsuTextY = cardY + 360;
            mc.fontRenderer.drawString(
                    safe(entity.getEntityData().getString("playerDojutsu"), "None"),
                    dojutsuTextX, dojutsuTextY, color
            );

            // ===== Head-slot icon in red square =====
            ItemStack head = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (!head.isEmpty()) {
                int iconX = cardX + 34;
                int iconY = cardY + 334;

                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(head, iconX, iconY);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, head, iconX, iconY, null);
                RenderHelper.disableStandardItemLighting();
            }

            // ===== DEBUG overlay (mouse coords relative to card) =====
            if (DEBUG && Mouse.isInsideWindow()) {
                int mouseX = Mouse.getX() * screenW / mc.displayWidth;
                int mouseY = screenH - (Mouse.getY() * screenH / mc.displayHeight) - 1;

                int relX = mouseX - cardX;
                int relY = mouseY - cardY;

                mc.fontRenderer.drawString("Card XY: " + cardX + "," + cardY, 6, 6, 0xFFFFFF00);
                mc.fontRenderer.drawString("Mouse rel: " + relX + "," + relY, 6, 18, 0xFFFFFF00);

                Gui.drawRect(mouseX - 10, mouseY, mouseX + 10, mouseY + 1, 0x80FFFF00);
                Gui.drawRect(mouseX, mouseY - 10, mouseX + 1, mouseY + 10, 0x80FFFF00);
            }

            mc.ingameGUI.prevVignetteBrightness = oldZ;

            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
        }

        private static String safe(String s, String fallback) {
            if (s == null) return fallback;
            s = s.trim();
            return s.isEmpty() ? fallback : s;
        }
    }
}
