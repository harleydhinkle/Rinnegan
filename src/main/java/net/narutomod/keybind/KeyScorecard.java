package net.narutomod.keybind;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.settings.KeyBinding;

import net.narutomod.gui.overlay.OverlayScorecard;

import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyScorecard {

    private static KeyBinding SCORECARD_KEY;

    public static void init() {
        // Key name in Controls menu, default key is M.
        SCORECARD_KEY = new KeyBinding(
                "key.narutomod.scorecard",
                Keyboard.KEY_M,              // change this to another key if you want
                "key.categories.narutomod"
        );
        ClientRegistry.registerKeyBinding(SCORECARD_KEY);
        MinecraftForge.EVENT_BUS.register(new KeyScorecard());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (SCORECARD_KEY.isPressed()) {
            // Toggle the scorecard visibility
            OverlayScorecard.isMenuOpen = !OverlayScorecard.isMenuOpen;
        }
    }
}
