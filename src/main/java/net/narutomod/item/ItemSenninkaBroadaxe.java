package net.narutomod.item;

import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSenninkaBroadaxe extends ElementsNarutomodMod.ModElement {
   @ObjectHolder("narutomod:senninka_broadaxe")
   public static final Item block = null;

   public ItemSenninkaBroadaxe(ElementsNarutomodMod instance) {
      super(instance, 941);
   }

   public void initElements() {
      this.elements.items.add(() -> {
         return ((Item)(new ItemToolCustom() {
         }).setUnlocalizedName("senninka_broadaxe").setRegistryName("senninka_broadaxe")).setCreativeTab((CreativeTabs)null);
      });
   }

   @SideOnly(Side.CLIENT)
   public void registerModels(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:senninka_broadaxe", "inventory"));
   }

   private static class ItemToolCustom extends Item {
      protected ItemToolCustom() {
         this.setMaxStackSize(100);
         this.setMaxDamage(1);
      }

      @Override
      public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
         Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);
         if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            // +8 base attack damage, -3 attack speed
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                  new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 8.0D, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                  new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.0D, 0));
         }
         return multimap;
      }

      @Override
      public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean isSelected) {
         super.onUpdate(itemstack, world, entity, par4, isSelected);
         // Destroy the axe every 20 ticks if the owner no longer has an active Senninka item
         if (!world.isRemote && entity.ticksExisted % 20 == 3
               && (!isSelected || entity instanceof EntityPlayer
                     && !ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer)entity, ItemSenninka.block))) {
            itemstack.shrink(1);
         }
      }

      @Override
      public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
         return 0.0F; // cannot break blocks
      }

      @Override
      public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
         stack.damageItem(1, entityLiving);
         return true;
      }

      @Override
      public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
         stack.damageItem(2, attacker);
         return true;
      }

      @Override
      public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
         return true;
      }

      @Override
      public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
         return stack.getItem() == ItemSenninkaBroadaxe.block;
      }

      @Override
      public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
         // Block left-click attack while the player is actively using an item
         return attacker.isHandActive() ? true : super.onLeftClickEntity(itemstack, attacker, target);
      }

      @Override
      public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
         // Right-click to hold (block/guard pose via EnumAction.BLOCK)
         playerIn.setActiveHand(handIn);
         return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
      }

      @Override
      public EnumAction getItemUseAction(ItemStack stack) {
         return EnumAction.BLOCK; // plays the shield/guard animation while held
      }

      @Override
      public int getMaxItemUseDuration(ItemStack stack) {
         return 72000; // essentially unlimited use duration
      }

      @Override
      public boolean isFull3D() {
         return true; // render as a proper 3D model in the player's hand
      }

      @Override
      public int getItemEnchantability() {
         return 0; // cannot be enchanted
      }
   }
}
