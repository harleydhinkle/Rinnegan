package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;

import java.util.Iterator;
import java.util.ArrayList;

@ElementsNarutomodMod.ModElement.Tag
public class EntityNinjaIwa extends ElementsNarutomodMod.ModElement {
    public static final int ENTITYID = 305;
    public static final int ENTITYID_RANGED = 306;

    public EntityNinjaIwa(ElementsNarutomodMod instance) {
        super(instance, 625);
    }

    @Override
    public void initElements() {
        elements.entities.add(() -> EntityEntryBuilder.create()
                .entity(EntityCustom.class)
                .id(new ResourceLocation("narutomod", "ninja_iwa"), ENTITYID)
                .name("ninja_iwa")
                .tracker(64, 3, true)
                .egg(-6737152, -6710887)
                .build());
    }

    // kept for MCreator parity (even if unused)
    private Biome[] allbiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation, Biome> in) {
        Iterator<Biome> itr = in.iterator();
        ArrayList<Biome> ls = new ArrayList<Biome>();
        while (itr.hasNext()) ls.add(itr.next());
        return ls.toArray(new Biome[ls.size()]);
    }

    /**
     * SpongeForge-safe pattern (same file):
     * - NO renderer/model inner classes here
     * - Client renderer/model live as top-level @SideOnly(Side.CLIENT) classes below
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityNinjaIwaClient.register();
    }

    public static class EntityCustom extends EntityMob {
        public EntityCustom(World world) {
            super(world);
            setSize(0.6f, 1.8f);
            experienceValue = 0;
            this.isImmuneToFire = false;
            setNoAI(false);
            enablePersistence();
        }

        @Override
        protected void initEntityAI() {
            super.initEntityAI();
            this.tasks.addTask(1, new EntityAISwimming(this));
            this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.2, false));
            this.tasks.addTask(3, new EntityAIWander(this, 1));
            this.tasks.addTask(4, new EntityAILookIdle(this));
            this.targetTasks.addTask(5, new EntityAIHurtByTarget(this, false));
        }

        @Override
        public EnumCreatureAttribute getCreatureAttribute() {
            return EnumCreatureAttribute.UNDEFINED;
        }

        @Override
        protected boolean canDespawn() {
            return false;
        }

        @Override
        protected Item getDropItem() {
            return null;
        }

        @Override
        public net.minecraft.util.SoundEvent getAmbientSound() {
            return net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.illusion_illager.ambient"));
        }

        @Override
        public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
            return net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.generic.hurt"));
        }

        @Override
        public net.minecraft.util.SoundEvent getDeathSound() {
            return net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.generic.death"));
        }

        @Override
        protected float getSoundVolume() {
            return 1.0F;
        }

        @Override
        protected void applyEntityAttributes() {
            super.applyEntityAttributes();
            if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null)
                this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
            if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
            if (this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
                this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
            if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
                this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
        }
    }
}

/* =======================================================================
 * CLIENT ONLY (same .java file, NOT inner classes)
 * Matches ?base jar? pattern and avoids SpongeForge side crashes.
 * ======================================================================= */

@SideOnly(Side.CLIENT)
class EntityNinjaIwaClient {
    static void register() {
        net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
                EntityNinjaIwa.EntityCustom.class,
                manager -> new net.minecraft.client.renderer.entity.RenderLiving<EntityNinjaIwa.EntityCustom>(
                        manager,
                        new ModelNinjaIwaBiped64(),
                        0.5F
                ) {
                    @Override
                    protected net.minecraft.util.ResourceLocation getEntityTexture(EntityNinjaIwa.EntityCustom entity) {
                        return new net.minecraft.util.ResourceLocation("narutomod:textures/ninja_iwa.png");
                    }
                }
        );
    }
}

@SideOnly(Side.CLIENT)
class ModelNinjaIwaBiped64 extends net.minecraft.client.model.ModelBiped {
    public ModelNinjaIwaBiped64() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.leftArmPose = net.minecraft.client.model.ModelBiped.ArmPose.EMPTY;
        this.rightArmPose = net.minecraft.client.model.ModelBiped.ArmPose.EMPTY;

        this.bipedHead = new net.minecraft.client.model.ModelRenderer(this);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHead.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedHead, 0, 0,
                -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

        this.bipedHeadwear = new net.minecraft.client.model.ModelRenderer(this);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHeadwear.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedHeadwear, 32, 0,
                -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));

        this.bipedBody = new net.minecraft.client.model.ModelRenderer(this);
        this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedBody.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedBody, 16, 16,
                -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
        this.bipedBody.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedBody, 16, 32,
                -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));

        this.bipedRightArm = new net.minecraft.client.model.ModelRenderer(this);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.bipedRightArm.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedRightArm, 40, 16,
                -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
        this.bipedRightArm.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedRightArm, 40, 32,
                -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));

        this.bipedLeftArm = new net.minecraft.client.model.ModelRenderer(this);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.bipedLeftArm.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedLeftArm, 32, 48,
                -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
        this.bipedLeftArm.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedLeftArm, 48, 48,
                -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));

        this.bipedRightLeg = new net.minecraft.client.model.ModelRenderer(this);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.bipedRightLeg.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedRightLeg, 0, 16,
                -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
        this.bipedRightLeg.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedRightLeg, 0, 32,
                -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));

        this.bipedLeftLeg = new net.minecraft.client.model.ModelRenderer(this);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bipedLeftLeg.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedLeftLeg, 16, 48,
                -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
        this.bipedLeftLeg.cubeList.add(new net.minecraft.client.model.ModelBox(this.bipedLeftLeg, 0, 48,
                -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, net.minecraft.entity.Entity e) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
    }
}
