package net.narutomod.procedure;

import net.narutomod.item.ItemYangRelease;
import net.narutomod.item.ItemLavaRelease;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemWaterRelease;
import net.narutomod.item.ItemShikotsumyaku;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemScorchRelease;
import net.narutomod.item.ItemSageArts;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemStormRelease;
import net.narutomod.item.ItemLightningRelease;
import net.narutomod.item.ItemNinjaArts;
import net.narutomod.item.ItemWoodRelease;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemFireRelease;
import net.narutomod.item.ItemMagnetRelease;
import net.narutomod.item.ItemDustRelease;
import net.narutomod.item.ItemMedicalNinjutsu;
import net.narutomod.item.ItemYinRelease;
import net.narutomod.item.ItemIceRelease;
import net.narutomod.item.ItemGourd;
import net.narutomod.item.ItemBoilRelease;
import net.narutomod.item.ItemWindRelease;
import net.narutomod.item.ItemEightGates;
import net.narutomod.item.ItemEarthRelease;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemExplosionRelease;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnPlayerDeath extends ElementsNarutomodMod.ModElement {
	public ProcedureOnPlayerDeath(ElementsNarutomodMod instance) {
		super(instance, 729);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure OnPlayerDeath!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		boolean keepInventory = false;
		ItemStack stack = ItemStack.EMPTY;
		ItemStack stack2 = ItemStack.EMPTY;
		if ((entity instanceof EntityPlayerMP)) {
			keepInventory = (boolean) entity.world.getGameRules().getBoolean("keepInventory");
			if (((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRinnegan.helmet, (int) (1)))
					: false)) {
				stack = ProcedureUtils.getItemStackIgnoreDurability(((EntityPlayer) entity).inventory, new ItemStack(ItemRinnegan.helmet));
				if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("KoH_id")) {
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).setHealth((float) 2);
					if (dependencies.get("event") != null) {
						Object _obj = dependencies.get("event");
						if (_obj instanceof net.minecraftforge.fml.common.eventhandler.Event) {
							net.minecraftforge.fml.common.eventhandler.Event _evt = (net.minecraftforge.fml.common.eventhandler.Event) _obj;
							if (_evt.isCancelable())
								_evt.setCanceled(true);
						}
					}
				} else if ((!(keepInventory))) {
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemRinnegan.helmet, (int) (1)).getItem(), -1, (int) (-1),
								null);
				}
			}
			if (((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemTenseigan.helmet, (int) (1)))
					: false)) {
				stack = ProcedureUtils.getItemStackIgnoreDurability(((EntityPlayer) entity).inventory, new ItemStack(ItemTenseigan.helmet));
				if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("KoH_id")) {
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).setHealth((float) 2);
					if (dependencies.get("event") != null) {
						Object _obj = dependencies.get("event");
						if (_obj instanceof net.minecraftforge.fml.common.eventhandler.Event) {
							net.minecraftforge.fml.common.eventhandler.Event _evt = (net.minecraftforge.fml.common.eventhandler.Event) _obj;
							if (_evt.isCancelable())
								_evt.setCanceled(true);
						}
					}
				} else if ((!(keepInventory))) {
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem(), -1, (int) (-1),
								null);
				}
			}
			if ((((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)))
					: false) && (!(keepInventory)))) {
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem(), -1,
							(int) 1, null);
			}
			if (((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemByakugan.helmet, (int) (1)))
					: false)) {
				stack = ProcedureUtils.getItemStackIgnoreDurability(((EntityPlayer) entity).inventory, new ItemStack(ItemByakugan.helmet));
				{
					ItemStack _stack = (stack);
					if (!_stack.hasTagCompound())
						_stack.setTagCompound(new NBTTagCompound());
					_stack.getTagCompound().setBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED), (false));
				}
			}
			if (entity instanceof EntityPlayer)
				((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemAsuraPathArmor.body, (int) (1)).getItem(), -1, (int) (-1),
						null);
			if ((!(keepInventory))) {
				entity.getEntityData().setBoolean((NarutomodModVariables.FirstGotNinjutsu), (false));
				if (EntityBijuManager.isJinchuriki((EntityPlayer) entity)) {
					EntityBijuManager.unsetPlayerAsJinchuriki((EntityPlayer) entity);
				}
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemWoodRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemEightGates.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemLightningRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemWindRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemNinjaArts.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemEarthRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemYangRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemYinRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemFireRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemDustRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemWaterRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemExplosionRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemIceRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemMagnetRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemScorchRelease.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemLavaRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemStormRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemBoilRelease.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemShikotsumyaku.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemGourd.body, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemMedicalNinjutsu.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemSageArts.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
			} else {
				if ((EntityBijuManager.cloakLevel((EntityPlayer) entity) > 0)) {
					EntityBijuManager.toggleBijuCloak((EntityPlayer) entity);
				}
				if (entity.world.getGameRules().getBoolean(PlayerTracker.FORCE_DOJUTSU_DROP_RULE)) {
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemByakugan.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemSharingan.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemMangekyoSharingan.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						stack2 = new ItemStack(ItemSharingan.helmet, (int) (1));
						((ItemSharingan.Base) stack2.getItem()).copyOwner(stack2, stack);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack2);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						((stack)).shrink((int) 1);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemMangekyoSharinganObito.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						stack2 = new ItemStack(ItemSharingan.helmet, (int) (1));
						((ItemSharingan.Base) stack2.getItem()).copyOwner(stack2, stack);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack2);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						((stack)).shrink((int) 1);
					}
				}
			}
			PlayerTracker.PlayerHook.addPersistentData(entity, NarutomodModVariables.FirstGotNinjutsu,
					entity.getEntityData().getBoolean(NarutomodModVariables.FirstGotNinjutsu));
			ProcedureSync.EntityNBTTag.removeAndSync(entity, NarutomodModVariables.forceBowPose);
			entity.getEntityData().setBoolean("susanoo_activated", (false));
			entity.getEntityData().setInteger("ForceExtinguish", 5);
			entity.setNoGravity(false);
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		if (event != null && event.getEntity() != null) {
			Entity entity = event.getEntity();
			int i = (int) entity.posX;
			int j = (int) entity.posY;
			int k = (int) entity.posZ;
			World world = entity.world;
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
