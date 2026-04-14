package net.narutomod.procedure;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.item.*;
import net.narutomod.justuconfig;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureReRollKg extends ElementsNarutomodMod.ModElement {
    public ProcedureReRollKg(ElementsNarutomodMod instance) {
        super(instance, 0);
    }

    public static void executeProcedure(HashMap<String, Object> dependencies) {
        if (dependencies.get("entity") == null) {
            throw new NullPointerException("Entity cannot be null.");
        }
        Entity entity = (Entity) dependencies.get("entity");
        HashMap cmdparams = (HashMap) dependencies.get("cmdparams");
        ItemStack stack = ItemStack.EMPTY;
        String username = "";
        if (cmdparams.values().size() < 1) {
            ((EntityPlayer) entity).sendStatusMessage(new TextComponentString("/reroll <target>"), false);
        }

        username = (String) (new Object() {
            public String getText() {
                String param = (String) cmdparams.get("0");
                if (param != null) {
                    return param;
                }
                return "";
            }
        }.getText());

        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(username);

        int sharingan_weight = 0;
        int byakugan_weight = 0;
        int shikotsumyaku_weight = 0;
        int yooton_weight = 0;
        int shakuton_weight = 0;
        int hyoton_weight = 0;
        int jiton_weight = 0;
        int bakuton_weight = 0;
        int ranton_weight = 0;
        ;
        int futton_weight = 0;
        ;
        int kekkeitota_weight = 0;
        int jinchuriki_weight = 0;
        int rngbase = 0;
        sharingan_weight = justuconfig.sharingan_weight_config;
        byakugan_weight = justuconfig.byakugan_weight_config;
        shikotsumyaku_weight = justuconfig.shikotsumyaku_weight_config;
        yooton_weight = justuconfig.yooton_weight_config;
        shakuton_weight = justuconfig.shakuton_weight_config;
        hyoton_weight = justuconfig.hyoton_weight_config;
        jiton_weight = justuconfig.jiton_weight_config;
        bakuton_weight = justuconfig.bakuton_weight_config;
        ranton_weight = justuconfig.ranton_weight_config;
        futton_weight = justuconfig.futton_weight_config;
        kekkeitota_weight = justuconfig.kekkeitota_weight_config;
        jinchuriki_weight = justuconfig.jinchuriki_weight_config;
        rngbase = ThreadLocalRandom.current().nextInt(1, (((sharingan_weight)+(byakugan_weight)+(shikotsumyaku_weight)+(yooton_weight)+(shakuton_weight)+(hyoton_weight)+(jiton_weight)+(bakuton_weight)+(ranton_weight)+(futton_weight) + (kekkeitota_weight) + (jinchuriki_weight))));

        if (player != null) {
            if (rngbase <= rngbase - (rngbase - sharingan_weight) && ! (sharingan_weight == 0)) {
                //Sharingan
                stack = new ItemStack(ItemSharingan.helmet, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemDojutsu.Base) stack.getItem()).setOwner(stack, player);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:sharinganopened"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (rngbase > rngbase - (rngbase - sharingan_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight)&& ! (byakugan_weight == 0)) {
                //Byakugan
                stack = new ItemStack(ItemByakugan.helmet, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemDojutsu.Base) stack.getItem()).setOwner(stack, player);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:byakuganopened"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight)&& ! (shikotsumyaku_weight == 0)) {
                //shikotsumyaku
                stack = new ItemStack(ItemShikotsumyaku.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:shikotsumyaku_acquired"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(yooton_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight)) {
                //yooton
                stack = new ItemStack(ItemYooton.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:yooton_acquired"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(shakuton_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight)) {
                //shakuton
                stack = new ItemStack(ItemShakuton.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:shakuton_acquired"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(hyoton_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight)) {
                //hyoton
                stack = new ItemStack(ItemHyoton.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:hyoton_acquired"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(jiton_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight)) {
                //jiton
                stack = new ItemStack(ItemJiton.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:jiton_acquired"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(bakuton_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight)) {
                //bakuton
                //Item
                stack = new ItemStack(ItemBakuton.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                //Advacement
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:bakuton_acquired"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                //Stat
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(ranton_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight - ranton_weight)) {
                //ranton
                stack = new ItemStack(ItemRanton.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:ranton_acquired"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", true);
            } else if (!(futton_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight - ranton_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight - ranton_weight - futton_weight)) {
                //futton
                stack = new ItemStack(ItemFuton.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:futton_acquired"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", false);
            } else if (!(kekkeitota_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight - ranton_weight - futton_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight - ranton_weight - futton_weight - kekkeitota_weight)) {
                //kekkeitota
                stack = new ItemStack(ItemKekkeiMora.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                Advancement adv = ((MinecraftServer) (player).mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:kekkei_tota_awakened"));
                AdvancementProgress ap = ((player).getAdvancements().getProgress(adv));
                for (String crit : ap.getRemaningCriteria()) {
                    (player).getAdvancements().grantCriterion(adv, crit);
                }
                entity.getEntityData().setBoolean("firstkg", true);
            } else if (!(jinchuriki_weight == 0) && rngbase > rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight - ranton_weight - futton_weight - kekkeitota_weight) && rngbase <= rngbase - (rngbase - sharingan_weight - byakugan_weight - shikotsumyaku_weight - yooton_weight - shakuton_weight - hyoton_weight - jiton_weight - bakuton_weight - ranton_weight - futton_weight - kekkeitota_weight - jinchuriki_weight)) {
                //jinchuriki
                int jintail = EntityBijuManager.getRandomAvailableBiju();
                EntityBijuManager.setVesselByTails(entity, jintail);
                MinecraftServer server = (player).getServer();
                server.getPlayerList().sendMessage(new TextComponentString(entity.getName() +" Has Become the Jinchuriki of the "+jintail+"!"));
                entity.getEntityData().setBoolean("firstkg", true);

            }
        }
    }
}
