package net.narutomod.procedure;

import net.narutomod.item.ItemLavaRelease;
import net.narutomod.item.ItemWaterRelease;
import net.narutomod.item.ItemShikotsumyaku;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemScorchRelease;
import net.narutomod.item.ItemStormRelease;
import net.narutomod.item.ItemLightningRelease;
import net.narutomod.item.ItemNinjaArts;
import net.narutomod.item.ItemFireRelease;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemMagnetRelease;
import net.narutomod.item.ItemDustRelease;
import net.narutomod.item.ItemMedicalNinjutsu;
import net.narutomod.item.ItemIceRelease;
import net.narutomod.item.ItemBoilRelease;
import net.narutomod.item.ItemWindRelease;
import net.narutomod.item.ItemEarthRelease;
import net.narutomod.item.ItemOcularJutsu;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemExplosionRelease;
import net.narutomod.gui.GuiScrollGenjutsuGui;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnPlayerPostTick extends ElementsNarutomodMod.ModElement {
    public ProcedureOnPlayerPostTick(ElementsNarutomodMod instance) {
        super(instance, 154);
    }

    public static void executeProcedure(Map<String, Object> dependencies) {
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load dependency entity for procedure OnPlayerPostTick!");
            return;
        }
        if (dependencies.get("x") == null) {
            System.err.println("Failed to load dependency x for procedure OnPlayerPostTick!");
            return;
        }
        if (dependencies.get("y") == null) {
            System.err.println("Failed to load dependency y for procedure OnPlayerPostTick!");
            return;
        }
        if (dependencies.get("z") == null) {
            System.err.println("Failed to load dependency z for procedure OnPlayerPostTick!");
            return;
        }
        if (dependencies.get("world") == null) {
            System.err.println("Failed to load dependency world for procedure OnPlayerPostTick!");
            return;
        }
        Entity entity = (Entity) dependencies.get("entity");
        int x = (int) dependencies.get("x");
        int y = (int) dependencies.get("y");
        int z = (int) dependencies.get("z");
        World world = (World) dependencies.get("world");
        ItemStack stack = ItemStack.EMPTY;
        double rand = 0;
        double rngbase = 0;
        boolean achievedMedical = false;
        if (((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).experienceLevel : 0) >= 10)
                && ((entity.getEntityData().getDouble((NarutomodModVariables.BATTLEXP))) > 0))) {
            if (((!(world.isRemote)) && (!(entity.getEntityData().getBoolean((NarutomodModVariables.FirstGotNinjutsu)))))) {
                entity.getEntityData().setBoolean((NarutomodModVariables.FirstGotNinjutsu), (true));
                if ((!((entity instanceof EntityPlayer)
                        ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemNinjaArts.block, (int) (1)))
                        : false))) {
                    stack = new ItemStack(ItemNinjaArts.block, (int) (1));
                    ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                    if (entity instanceof EntityPlayer) {
                        ItemStack _setstack = (stack);
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                    }
                }
                if (((((!((entity instanceof EntityPlayer)
                        ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemFireRelease.block, (int) (1)))
                        : false))
                        && (!((entity instanceof EntityPlayer)
                        ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemWaterRelease.block, (int) (1)))
                        : false)))
                        && ((!((entity instanceof EntityPlayer)
                        ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemLightningRelease.block, (int) (1)))
                        : false))
                        && (!((entity instanceof EntityPlayer)
                        ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemWindRelease.block, (int) (1)))
                        : false))))
                        && (!((entity instanceof EntityPlayer)
                        ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemEarthRelease.block, (int) (1)))
                        : false)))) {
                    if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:bakuton_acquired")))
                              .isDone()
                            : false)) {
                        stack = new ItemStack(ItemEarthRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemLightningRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemExplosionRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                    } else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:ranton_acquired")))
                              .isDone()
                            : false)) {
                        stack = new ItemStack(ItemWaterRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemLightningRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemStormRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                    } else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:futton_acquired")))
                              .isDone()
                            : false)) {
                        stack = new ItemStack(ItemWaterRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemFireRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemBoilRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                    } else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:jiton_acquired")))
                              .isDone()
                            : false)) {
                        stack = new ItemStack(ItemWindRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemEarthRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemMagnetRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                    } else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:yooton_acquired")))
                              .isDone()
                            : false)) {
                        stack = new ItemStack(ItemEarthRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemFireRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemLavaRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                    } else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:hyoton_acquired")))
                              .isDone()
                            : false)) {
                        stack = new ItemStack(ItemWindRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemWaterRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemIceRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                    } else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:shakuton_acquired")))
                              .isDone()
                            : false)) {
                        stack = new ItemStack(ItemFireRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemWindRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemScorchRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                    } else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:kekkei_tota_awakened")))
                              .isDone()
                            : false)) {
                        stack = new ItemStack(ItemFireRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemEarthRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemWindRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        stack = new ItemStack(ItemDustRelease.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                    } else {
                        rand = (double) ((EntityLivingBase) entity).getRNG().nextDouble();
                        if (((rand) <= 0.2)) {
                            stack = new ItemStack(ItemFireRelease.block, (int) (1));
                        } else if (((rand) <= 0.4)) {
                            stack = new ItemStack(ItemWaterRelease.block, (int) (1));
                        } else if (((rand) <= 0.6)) {
                            stack = new ItemStack(ItemLightningRelease.block, (int) (1));
                        } else if (((rand) <= 0.8)) {
                            stack = new ItemStack(ItemWindRelease.block, (int) (1));
                        } else {
                            stack = new ItemStack(ItemEarthRelease.block, (int) (1));
                        }
                    }
                    ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                    if (entity instanceof EntityPlayer) {
                        ItemStack _setstack = (stack);
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                    }
                    if ((!ItemSharingan.hasAny((EntityPlayer) entity)
                            && (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:sharinganopened")))
                              .isDone()
                            : false))) {
                        GuiScrollGenjutsuGui.giveGenjutsu((EntityPlayer) entity);
                        stack = new ItemStack(ItemSharingan.helmet, (int) (1));
                        ((ItemOcularJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                        entity.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, world.getTotalWorldTime());
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                    } else if (((!((entity instanceof EntityPlayer)
                            ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemByakugan.helmet, (int) (1)))
                            : false))
                            && (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:byakuganopened")))
                              .isDone()
                            : false))) {
                        stack = new ItemStack(ItemByakugan.helmet, (int) (1));
                        ((ItemOcularJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                        entity.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, world.getTotalWorldTime());
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                    } else if (((!((entity instanceof EntityPlayer)
                            ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemShikotsumyaku.block, (int) (1)))
                            : false))
                            && (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:shikotsumyaku_acquired")))
                              .isDone()
                            : false))) {
                        stack = new ItemStack(ItemShikotsumyaku.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                        ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                    }
                }
                if ((!((entity instanceof EntityPlayer)
                        ? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemMedicalNinjutsu.block, (int) (1)))
                        : false))) {
                    achievedMedical = (boolean) (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                            ? ((EntityPlayerMP) entity).getAdvancements()
                              .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                           .getAdvancement(new ResourceLocation("narutomod:achievementmedicalgenin")))
                              .isDone()
                            : false);
                    if (((achievedMedical) || ((!(entity.getEntityData().getBoolean("MedicalNinjaChecked")))
                            && (((EntityLivingBase) entity).getRNG().nextDouble() <= 0.25)))) {
                        stack = new ItemStack(ItemMedicalNinjutsu.block, (int) (1));
                        ((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
                        if (entity instanceof EntityPlayer) {
                            ItemStack _setstack = (stack);
                            _setstack.setCount(1);
                            ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
                        }
                        if ((!(achievedMedical))) {
                            if (entity instanceof EntityPlayerMP) {
                                Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
                                        .getAdvancement(new ResourceLocation("narutomod:achievementmedicalgenin"));
                                AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
                                if (!_ap.isDone()) {
                                    Iterator _iterator = _ap.getRemaningCriteria().iterator();
                                    while (_iterator.hasNext()) {
                                        String _criterion = (String) _iterator.next();
                                        ((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
                                    }
                                }
                            }
                        }
                    }
                    entity.getEntityData().setBoolean("MedicalNinjaChecked", (true));
                }
            }
            {
                Map<String, Object> $_dependencies = new HashMap<>();
                $_dependencies.put("entity", entity);
                $_dependencies.put("world", world);
                ProcedureBasicNinjaSkills.executeProcedure($_dependencies);
            }
        }
        if ((((entity.ticksExisted % 20) == 0) && (!(world.isRemote)))) {
            if (ItemOcularJutsu.hasAnyDojutsu((EntityPlayer) entity)) {
                if ((!ItemSharingan.isWearingMangekyo((EntityPlayer) entity) && (entity.getEntityData().getBoolean("susanoo_activated")))) {
                    {
                        Map<String, Object> $_dependencies = new HashMap<>();
                        $_dependencies.put("entity", entity);
                        $_dependencies.put("world", world);
                        ProcedureSusanoo.executeProcedure($_dependencies);
                    }
                }
                if (ItemSharingan.isBlinded((EntityPlayer) entity)) {
                    if (entity instanceof EntityLivingBase)
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) 1200, (int) 0, (false), (false)));
                }
            } else if ((((!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemLavaRelease.block)
                    && !ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemStormRelease.block))
                    && (!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemIceRelease.block)
                    && !ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemMagnetRelease.block)))
                    && ((!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemScorchRelease.block)
                    && !ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemExplosionRelease.block))
                    && ((!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemDustRelease.block)
                    && !ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemBoilRelease.block))
                    && (!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemShikotsumyaku.block)
                    && !EntityBijuManager.isJinchuriki((EntityPlayer) entity)))))) {
                if ((entity.getEntityData().getBoolean("susanoo_activated"))) {
                    {
                        Map<String, Object> $_dependencies = new HashMap<>();
                        $_dependencies.put("entity", entity);
                        $_dependencies.put("world", world);
                        ProcedureSusanoo.executeProcedure($_dependencies);
                    }
                }
                if ((PlayerTracker.Deaths.mostRecentTime((EntityPlayer) entity) < entity.getEntityData()
                        .getLong(NarutomodModVariables.MostRecentWornDojutsuTime))) {
                    if ((!((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false))) {
                        if (entity instanceof EntityLivingBase)
                            ((EntityLivingBase) entity)
                                    .addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) 1200, (int) 0, (false), (false)));
                    }
                } else if (((ModConfig.AUTO_KEKKEIGENKAI_ASSIGNMENT && ((entity.getEntityData().getDouble((NarutomodModVariables.BATTLEXP))) >= 300))
                        && (((((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:sharinganopened")))
                          .isDone()
                        : false))
                        && (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:byakuganopened")))
                          .isDone()
                        : false)))
                        && ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:shakuton_acquired")))
                          .isDone()
                        : false))
                        && (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:yooton_acquired")))
                          .isDone()
                        : false))))
                        && (((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:bakuton_acquired")))
                          .isDone()
                        : false))
                        && (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:ranton_acquired")))
                          .isDone()
                        : false)))
                        && (((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:hyoton_acquired")))
                          .isDone()
                        : false))
                        && (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:jiton_acquired")))
                          .isDone()
                        : false)))
                        && ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation("narutomod:futton_acquired")))
                          .isDone()
                        : false))
                        && ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(
                                  ((WorldServer) (entity).world).getAdvancementManager().getAdvancement(
                                          new ResourceLocation("narutomod:shikotsumyaku_acquired")))
                          .isDone()
                        : false))
                        && (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
                        ? ((EntityPlayerMP) entity).getAdvancements()
                          .getProgress(((WorldServer) (entity).world).getAdvancementManager()
                                       .getAdvancement(new ResourceLocation(
                                               "narutomod:kekkei_tota_awakened")))
                          .isDone()
                        : false)))))))
                        && (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).experienceLevel : 0) >= 10)))) {
                    if (((((EntityLivingBase) entity).getRNG().nextFloat() <= 0.50)
                            && ((entity instanceof EntityPlayer) && (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemEarthRelease.block)
                            || ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemWindRelease.block))
                            || (ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemFireRelease.block)
                            || ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemWaterRelease.block)))
                            || ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemLightningRelease.block))))) {
                        {
                            Map<String, Object> $_dependencies = new HashMap<>();
                            $_dependencies.put("entity", entity);
                            $_dependencies.put("x", x);
                            $_dependencies.put("y", y);
                            $_dependencies.put("z", z);
                            $_dependencies.put("world", world);
                            ProcedureKGDistribution.executeProcedure($_dependencies);
                            ProcedureClanDistribution.executeProcedure($_dependencies);
                        }
                    }
                }
            }
        }
        {
            Map<String, Object> $_dependencies = new HashMap<>();
            $_dependencies.put("entity", entity);
            ProcedureSyncInventory.executeProcedure($_dependencies);
        }
        {
            Map<String, Object> $_dependencies = new HashMap<>();
            ProcedureDebug.executeProcedure($_dependencies);
        }
        {
            Map<String, Object> $_dependencies = new HashMap<>();
            $_dependencies.put("entity", entity);
            ProcedureClanEffects.executeProcedure($_dependencies);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Entity entity = event.player;
            World world = entity.world;
            int i = (int) entity.posX;
            int j = (int) entity.posY;
            int k = (int) entity.posZ;
            java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
            dependencies.put("x", i);
            dependencies.put("y", j);
            dependencies.put("z", k);
            dependencies.put("world", world);
            dependencies.put("entity", entity);
            dependencies.put("event", event);
            this.executeProcedure(dependencies);
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}