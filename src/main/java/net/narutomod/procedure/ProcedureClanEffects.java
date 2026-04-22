package net.narutomod.procedure;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.narutomod.ElementsNarutomodMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureClanEffects extends ElementsNarutomodMod.ModElement {
    public  ProcedureClanEffects(ElementsNarutomodMod instance) {
        super(instance, 840);
    }

    public static void executeProcedure(Map<String, Object> dependencies) {
        Entity entity = (Entity) dependencies.get("entity");
        if (entity.getEntityData().getString("clan").equalsIgnoreCase("uchiha")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("uzumaki")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("hyuga")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("senju")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("nara")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("akimichi")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("yamanaka")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("aburame")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("inuzuka")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("kaguya")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("hozuki")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("sarutobi")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));

        } else if (entity.getEntityData().getString("clan").equalsIgnoreCase("kazekage")) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)2000, (int)2, false,false));
        }
        }
}
