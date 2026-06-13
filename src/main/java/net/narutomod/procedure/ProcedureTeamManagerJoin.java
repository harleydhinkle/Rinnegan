package net.narutomod.procedure;

import net.narutomod.item.ItemTeamScroll;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureTeamManagerJoin extends ElementsNarutomodMod.ModElement {
    public ProcedureTeamManagerJoin(ElementsNarutomodMod instance) {
        super(instance, 554);
    }

    public static void executeProcedure(Map<String, Object> dependencies) {
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load dependency entity for procedure TeamManagerJoin!");
            return;
        }
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer) || !(entity instanceof EntityLivingBase)) return;

        EntityPlayer player = (EntityPlayer) entity;

        ItemStack helditem = ((EntityLivingBase)entity).getHeldItemMainhand();
        if (helditem.isEmpty() || helditem.getItem() != ItemTeamScroll.block) {
            helditem = ((EntityLivingBase)entity).getHeldItemOffhand();
        }
        if (helditem.isEmpty() || helditem.getItem() != ItemTeamScroll.block) return;


        ItemTeamScroll.ItemCustom.addTeamMember(helditem, player);
    }
}

