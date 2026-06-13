package net.narutomod.procedure;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.item.*;
import net.narutomod.justuconfig;


import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureKGDistribution extends ElementsNarutomodMod.ModElement {
    public ProcedureKGDistribution(ElementsNarutomodMod instance) {
        super(instance, 847);
    }
    public static void executeProcedure(Map<String, Object> dependencies) {
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load dependency entity for procedure KGDistribution!");
            return;
        }
        if (dependencies.get("x") == null) {
            System.err.println("Failed to load dependency x for procedure KGDistribution!");
            return;
        }
        if (dependencies.get("y") == null) {
            System.err.println("Failed to load dependency y for procedure KGDistribution!");
            return;
        }
        if (dependencies.get("z") == null) {
            System.err.println("Failed to load dependency z for procedure KGDistribution!");
            return;
        }
        if (dependencies.get("world") == null) {
            System.err.println("Failed to load dependency world for procedure KGDistribution!");
            return;
        }
        Entity entity = (Entity) dependencies.get("entity");
        ItemStack stack = ItemStack.EMPTY;
        int sharingan_weight = 0;
        int byakugan_weight = 0;
        int bone_weight = 0;
        int lava_weight = 0;
        int scorch_weight = 0;
        int ice_weight = 0;
        int magnet_weight = 0;
        int explosion_weight = 0;
        int storm_weight = 0; ;
        int boil_weight = 0; ;
        int wood_weight = 0;
        int eightgates_weight = 0;
        int dust_weight = 0;
        int crystal_weight = 0;
        int blood_weight = 0;
        int jinchuriki_weight = 0;
        int rngbase = 0;
        sharingan_weight = justuconfig.sharingan_weight_config;
        byakugan_weight = justuconfig.byakugan_weight_config;
        bone_weight = justuconfig.bone_weight_config;
        lava_weight = justuconfig.lava_weight_config;
        scorch_weight = justuconfig.scorch_weight_config;
        ice_weight = justuconfig.ice_weight_config;
        magnet_weight = justuconfig.magnet_weight_config;
        explosion_weight = justuconfig.explosion_weight_config;
        storm_weight = justuconfig.storm_weight_config;
        boil_weight = justuconfig.boil_weight_config;
        wood_weight = justuconfig.wood_weight_config;
        eightgates_weight = justuconfig.eightgates_weight_config;
        dust_weight = justuconfig.dust_weight_config;
        crystal_weight = justuconfig.crystal_weight_config;
        blood_weight = justuconfig.blood_weight_config;
        jinchuriki_weight = justuconfig.jinchuriki_weight_config;
        rngbase = ThreadLocalRandom.current().nextInt(1, ((eightgates_weight+dust_weight+crystal_weight+(blood_weight)+(sharingan_weight)+(byakugan_weight)+(bone_weight)+(lava_weight)+(scorch_weight)+(ice_weight)+(magnet_weight)+(explosion_weight)+(storm_weight)+(boil_weight) + (wood_weight) + (jinchuriki_weight))));
        entity.getEntityData().setBoolean("firstkg", true);
        if ((entity.getEntityData().getBoolean("firstkg"))) {
            if (rngbase <= rngbase - (rngbase - sharingan_weight) && ! (sharingan_weight == 0)) {
                //Sharingan
                stack = new ItemStack(ItemSharingan.helmet, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemOcularJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:sharinganopened"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (rngbase > rngbase - (rngbase - sharingan_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight)&& ! (byakugan_weight == 0)) {
                //Byakugan
                stack = new ItemStack(ItemByakugan.helmet, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemOcularJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:byakuganopened"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight)&& ! (bone_weight == 0)) {
                //bone
                stack = new ItemStack(ItemShikotsumyaku.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:shikotsumyaku_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(lava_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight)) {
                //lava
                stack = new ItemStack(ItemLavaRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:yooton_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(scorch_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight)) {
                //scorch
                stack = new ItemStack(ItemScorchRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:shakuton_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(ice_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight)) {
                //ice
                stack = new ItemStack(ItemIceRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:hyoton_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(magnet_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight)) {
                //magnet
                stack = new ItemStack(ItemMagnetRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:jiton_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(explosion_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight)) {
                //explosion
                //Item
                stack = new ItemStack(ItemExplosionRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                //Advacement
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:bakuton_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                //Stat
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(storm_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight)) {
                //storm
                stack = new ItemStack(ItemStormRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:ranton_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", true);
            } else if (!(boil_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight)) {
                //boil
                stack = new ItemStack(ItemWindRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:futon_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(wood_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight)) {
                //Wood
                stack = new ItemStack(ItemWoodRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:mokuton_acquired"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", true);
            } else if (!(jinchuriki_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight - jinchuriki_weight)) {
                //jinchuriki
                int jintail = EntityBijuManager.getRandomAvailableBiju();
                EntityBijuManager.setVesselByTails(entity, jintail);
                MinecraftServer server = ((EntityPlayerMP) entity).getServer();
                server.getPlayerList().sendMessage(new TextComponentString(entity.getName() +" Has Become the Jinchuriki of the "+jintail+"!"));
                entity.getEntityData().setBoolean("firstkg", true);

            }
            else if (!(dust_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight-jinchuriki_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight - jinchuriki_weight-dust_weight)) {
                //Dust
                stack = new ItemStack(ItemDustRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:kekkei_tota_awakened"));
                AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", true);
            }
            else if (!(crystal_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight-jinchuriki_weight-dust_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight - jinchuriki_weight-dust_weight-crystal_weight)) {
                stack = new ItemStack(ItemCrystalRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                //Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:kekkei_tota_awakened"));
                //AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
                //for (String crit : ap.getRemaningCriteria()) {
                //((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
            }
            entity.getEntityData().setBoolean("firstkg", true);
        }
        else if (!(eightgates_weight == 0) && rngbase > rngbase - (rngbase -crystal_weight- sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight-jinchuriki_weight-dust_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight - jinchuriki_weight-dust_weight-crystal_weight-eightgates_weight)) {
            stack = new ItemStack(ItemEightGates.block, (int) (1));
            ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
            ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
            ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
            Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:openedgates"));
            AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
            for (String crit : ap.getRemaningCriteria()) {
                ((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
            }
            entity.getEntityData().setBoolean("firstkg", true);
        }
        else if (!(blood_weight == 0) && rngbase > rngbase - (rngbase -crystal_weight- sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight-jinchuriki_weight-dust_weight-eightgates_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - bone_weight - lava_weight - scorch_weight - ice_weight - magnet_weight - explosion_weight - storm_weight - boil_weight - wood_weight - jinchuriki_weight-dust_weight-crystal_weight-eightgates_weight-blood_weight)) {
            stack = new ItemStack(ItemBloodRelease.block, (int) (1));
            ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
            ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
            ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
            //Advancement adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:openedgates"));
            //AdvancementProgress ap = (((EntityPlayerMP) entity).getAdvancements().getProgress(adv));
            //for (String crit : ap.getRemaningCriteria()) {
            //	((EntityPlayerMP) entity).getAdvancements().grantCriterion(adv, crit);
            //}
            entity.getEntityData().setBoolean("firstkg", true);
        }
    }
}