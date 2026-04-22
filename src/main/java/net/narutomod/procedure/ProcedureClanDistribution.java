package net.narutomod.procedure;

import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.ClanConfig;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.narutomod.ElementsNarutomodMod;


import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureClanDistribution extends ElementsNarutomodMod.ModElement {
    public ProcedureClanDistribution(ElementsNarutomodMod instance) {
        super(instance, 999);}
    public static void executeProcedure(Map<String, Object> dependencies) {
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load entity dependency. No instance found.");
            
        }
        if (dependencies.get("x") == null) {
            System.err.println("Failed to load dependency x for procedure ClanDistribution!");
            return;
        }
        if (dependencies.get("y") == null) {
            System.err.println("Failed to load dependency y for procedure ClanDistribution!");
            return;
        }
        if (dependencies.get("z") == null) {
            System.err.println("Failed to load dependency z for procedure ClanDistribution!");
            return;
        }
        if (dependencies.get("world") == null) {
            System.err.println("Failed to load dependency world for procedure ClanDistribution!");
            return;
        }
        Entity entity = (Entity) dependencies.get("entity");
        int x = (int) dependencies.get("x");
        int y = (int) dependencies.get("y");
        int z = (int) dependencies.get("z");
        ItemStack stack = ItemStack.EMPTY;
        int uchiha_weight = 0;
        int uzumaki_weight = 0;
        int hyuga_weight = 0;
        int senju_weight = 0;
        int nara_weight = 0;
        int akimichi_weight = 0;
        int yamanaka_weight = 0;
        int aburame_weight = 0;
        int inuzuka_weight = 0;
        int kaguya_weight = 0;
        int hozuki_weight = 0;
        int sarutobi_weight = 0;
        int kazekage_weight = 0;
        boolean no_clan = false;
        int rng = 0;

        uchiha_weight = ClanConfig.uchiha_weight_config;
        uzumaki_weight = ClanConfig.uzumaki_weight_config;
        hyuga_weight = ClanConfig.hyuga_weight_config;
        senju_weight = ClanConfig.senju_weight_config;
        nara_weight = ClanConfig.nara_weight_config;
        akimichi_weight = ClanConfig.akimichi_weight_config;
        yamanaka_weight = ClanConfig.yamanaka_weight_config;
        aburame_weight = ClanConfig.aburame_weight_config;
        inuzuka_weight = ClanConfig.inuzuka_weight_config;
        kaguya_weight = ClanConfig.kaguya_weight_config;
        hozuki_weight = ClanConfig.hozuki_weight_config;
        sarutobi_weight = ClanConfig.sarutobi_weight_config;
        kazekage_weight = ClanConfig.kazekage_weight_config;
        no_clan = ClanConfig.no_clan_config;
        rng = ThreadLocalRandom.current().nextInt(1,(uchiha_weight + uzumaki_weight + hyuga_weight + senju_weight + nara_weight + akimichi_weight + yamanaka_weight + aburame_weight + inuzuka_weight + kaguya_weight + hozuki_weight + sarutobi_weight + kazekage_weight));
        entity.getEntityData().setBoolean("firstclan", true);
        entity.getEntityData().setString("clan","");
        if (entity.getEntityData().getBoolean("firstclan") && no_clan) {
            if (rng <= rng - (rng - uchiha_weight) && !(uchiha_weight == 0)) {
                // Uchiha
                entity.getEntityData().setString("clan","uchiha");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight) && !(uzumaki_weight == 0)) {
                // Uzumaki
                entity.getEntityData().setString("clan","uzumaki");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight) && !(hyuga_weight == 0)) {
                // Hyuga
                entity.getEntityData().setString("clan","hyuga");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight) && !(senju_weight == 0)) {
                // Senju
                entity.getEntityData().setString("clan","senju");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight) && !(nara_weight == 0)) {
                // Nara
                entity.getEntityData().setString("clan","nara");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight) && !(akimichi_weight == 0)) {
                // Akimichi
                entity.getEntityData().setString("clan","akimichi");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight) && !(yamanaka_weight == 0)) {
                // Yamanaka
                entity.getEntityData().setString("clan","yamanaka");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight) && !(aburame_weight == 0)) {
                // Aburame
                entity.getEntityData().setString("clan","aburame");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight) && !(inuzuka_weight == 0)) {
                // Inuzuka
                entity.getEntityData().setString("clan","inuzuka");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight) && !(kaguya_weight == 0)) {
                // Kaguya
                entity.getEntityData().setString("clan","kaguya");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight) && !(hozuki_weight == 0)) {
                // Hozuki
                entity.getEntityData().setString("clan","hozuki");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight - sarutobi_weight) && !(sarutobi_weight == 0)) {
                // Sarutobi
                entity.getEntityData().setString("clan","sarutobi");
                entity.getEntityData().setBoolean("firstclan", false);

            } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight - sarutobi_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight - sarutobi_weight - kazekage_weight) && !(kazekage_weight == 0)) {
                // Kazekage
                entity.getEntityData().setString("clan","kazekage");
                entity.getEntityData().setBoolean("firstclan", false);
            }
            ProcedureUtils.sendChat((EntityPlayer)entity, String.format("Your clan is %s", entity.getEntityData().getString("clan")));
        }
    }
}
