package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.gui.overlay.OverlayScorecard;

import java.util.Map;

public class ProcedureProcedureReturnIsOpenMenu extends ElementsNarutomodMod.ModElement {
    public ProcedureProcedureReturnIsOpenMenu(ElementsNarutomodMod instance) {
        super(instance, 932);
    }

    public static boolean executeProcedure(Map<String, Object> dependencies) {
        return OverlayScorecard.isMenuOpen;
    }
}



