package net.narutomod.procedure;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.narutomod.ClanEffectRegister;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.potion.*;

import java.util.*;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureClanEffects extends ElementsNarutomodMod.ModElement {
    private static boolean triedJsonLoad = false;

    public  ProcedureClanEffects(ElementsNarutomodMod instance) {
        super(instance, 840);
    }

    public static void executeProcedure(Map<String, Object> dependencies) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }

        String clan = entity.getEntityData().getString("clan");
        if (clan == null || clan.isEmpty()) {
            return;
        }

        if (!triedJsonLoad) {
            ClanEffectRegister.load();
            triedJsonLoad = true;
        }

        applyConfiguredClanEffects((EntityLivingBase) entity, clan);
    }

    private static void applyConfiguredClanEffects(EntityLivingBase entity, String clan) {
        List<ClanEffectRegister.EffectData> effects =
                ClanEffectRegister.claneffects.get(clan.toLowerCase(Locale.ROOT));
        if (effects == null || effects.isEmpty()) {
            return;
        }

        for (ClanEffectRegister.EffectData effectData : effects) {
            Potion potion = resolvePotion(effectData.effect);
            if (potion == null) {
                continue;
            }

            int duration = Math.max(1, effectData.duration);
            int amplifier = Math.max(0, effectData.amplifier);
            entity.addPotionEffect(new PotionEffect(potion, duration, amplifier, false, false));
        }
    }

    private static Potion resolvePotion(String effectId) {
        if (effectId == null || effectId.trim().isEmpty()) {
            return null;
        }

        String normalized = effectId.trim();
        Potion potion = Potion.getPotionFromResourceLocation(normalized);
        if (potion != null) {
            return potion;
        }

        switch (normalized.toLowerCase(Locale.ROOT)) {
            case "flight":
                return PotionFlight.potion;
            case "reach":
                return PotionReach.potion;
            default:
                return Potion.getPotionFromResourceLocation("minecraft:" + normalized.toLowerCase(Locale.ROOT));
        }
    }
}
