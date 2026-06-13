package net.narutomod.item;

import net.narutomod.PlayerTracker;
import net.narutomod.gui.GuiScrollHidingInRockGui;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemScrollHidingInRock extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder("narutomod:scroll_hiding_in_rock")
    public static final Item block = null;

    public ItemScrollHidingInRock(ElementsNarutomodMod instance) {
        super(instance, 465);
    }

    @Override
    public void initElements() {
        elements.items.add(() -> new ItemCustom());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(block, 0,
                new ModelResourceLocation("narutomod:scroll_hiding_in_rock", "inventory"));
    }

    public static class ItemCustom extends Item {
        public ItemCustom() {
            setMaxDamage(1);
            maxStackSize = 1;
            setUnlocalizedName("scroll_hiding_in_rock");
            setRegistryName("scroll_hiding_in_rock");
            setCreativeTab(TabModTab.tab);
        }

        @Override
        public int getItemEnchantability() {
            return 0;
        }

        @Override
        public int getMaxItemUseDuration(ItemStack itemstack) {
            return 0;
        }

        @Override
        public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
            return 0F;
        }

        @Override
        public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
            super.addInformation(itemstack, world, list, flag);
            list.add("C-Rank jutsu scroll");
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
            ItemStack stack = entity.getHeldItem(hand);

            // === Gamerule gate (server authoritative) ===
            if (!world.isRemote && PlayerTracker.noKamuiAndHidingInRock(world)) {
                if (entity instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) entity).sendStatusMessage(
                            new TextComponentString("This jutsu is disabled by gamerule: noKamuiAndHidingInRock"),
                            true
                    );
                }
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
            }

            // Open the GUI (client+server flow)
            int x = (int) entity.posX;
            int y = (int) entity.posY;
            int z = (int) entity.posZ;
            entity.openGui(NarutomodMod.instance, GuiScrollHidingInRockGui.GUIID, world, x, y, z);

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
    }
}

