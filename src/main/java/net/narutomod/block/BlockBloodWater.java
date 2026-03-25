package net.narutomod.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyInteger;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;

import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class BlockBloodWater extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder("narutomod:blood_water")
    public static final Block block = null;

    public BlockBloodWater(ElementsNarutomodMod instance) {
        super(instance, 414);
    }

    @Override
    public void initElements() {
        elements.blocks.add(() -> new BlockCustom().setRegistryName("blood_water"));
        elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation("narutomod:blood_water", "inventory"));
    }

    /**
     * Client-only color handler. This runs once on the CLIENT and prevents biome tint.
     * (No SideOnly fields -> no Mohist/Forge stripping -> no NoSuchFieldError)
     */
    @SideOnly(Side.CLIENT)
    @Mod.EventBusSubscriber(modid = "narutomod", value = Side.CLIENT)
    public static class ClientColorHandler {
        @SubscribeEvent
        public static void registerBlockColors(ColorHandlerEvent.Block event) {
            event.getBlockColors().registerBlockColorHandler(
                    (state, world, pos, tintIndex) -> 0xFF0000,
                    block
            );
        }
    }

    /**
     * OPTION B:
     * - "Fake" water block (does NOT flow)
     * - BUT has the LEVEL property so block/liquid model + your blockstates(level=0..15) work.
     */
    public static class BlockCustom extends Block {
        // Liquid renderer expects this
        public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

        private static final AxisAlignedBB NULL = NULL_AABB;

        public BlockCustom() {
            super(Material.WATER);
            this.setUnlocalizedName("blood_water");
            this.setHardness(100f);
            this.setResistance(5f);
            this.setLightOpacity(3);
            this.disableStats();
            this.setCreativeTab(null);

            // Do NOT tick/spread like a fluid
            this.setTickRandomly(false);

            // Default state must include LEVEL
            this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
        }

        // --- State container / meta mapping (required for LEVEL) ---

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, LEVEL);
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            if (meta < 0) meta = 0;
            if (meta > 15) meta = 15;
            return this.getDefaultState().withProperty(LEVEL, meta);
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            Integer lvl = state.getValue(LEVEL);
            return lvl == null ? 0 : lvl.intValue();
        }

        // --- Rendering (translucent like water) ---

        @SideOnly(Side.CLIENT)
        @Override
        public BlockRenderLayer getBlockLayer() {
            return BlockRenderLayer.TRANSLUCENT;
        }

        @Override
        public boolean isOpaqueCube(IBlockState state) {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state) {
            return false;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
            return true;
        }

        // No collision so entities can be inside it
        @Override
        public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
            return NULL;
        }

        // Optional: keep your pull-down effect
        @Override
        public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
            return motion.addVector(0.0d, -1.5d, 0.0d);
        }
    }

    // Keep your inside check
    public static boolean isInsideBlock(Entity entityIn) {
        return isInsideBlock(entityIn, true);
    }

    public static boolean isInsideBlock(Entity entityIn, boolean testHead) {
        if (entityIn.getRidingEntity() instanceof EntityBoat) {
            return false;
        }
        double d0 = entityIn.posY + (testHead ? (double) entityIn.getEyeHeight() : 0d);
        BlockPos blockpos = new BlockPos(entityIn.posX, d0, entityIn.posZ);
        IBlockState iblockstate = entityIn.world.getBlockState(blockpos);
        return iblockstate.getBlock() == block;
    }
}
