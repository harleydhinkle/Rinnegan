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
public class EntityAnbu extends ElementsNarutomodMod.ModElement {
    public static final int ENTITYID = 303;
    public static final int ENTITYID_RANGED = 304;

    public EntityAnbu(ElementsNarutomodMod instance) {
        super(instance, 623);
    }

    @Override
    public void initElements() {
        elements.entities.add(() -> EntityEntryBuilder.create()
                .entity(EntityCustom.class)
                .id(new ResourceLocation("narutomod", "anbu"), ENTITYID)
                .name("anbu")
                .tracker(64, 3, true)
                .egg(-1, -52429)
                .build());
    }

    // kept (even if unused) to match MCreator patterns
    private Biome[] allbiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation, Biome> in) {
        Iterator<Biome> itr = in.iterator();
        ArrayList<Biome> ls = new ArrayList<Biome>();
        while (itr.hasNext()) ls.add(itr.next());
        return ls.toArray(new Biome[ls.size()]);
    }

    /**
     * SpongeForge-safe pattern (same file):
     * - NO client classes as inner classes of EntityAnbu
     * - Client renderer/model live as top-level @SideOnly(Side.CLIENT) classes below
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityAnbuClient.register();
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
 * This matches the ?base jar? pattern and avoids SpongeForge side crashes.
 * ======================================================================= */

@SideOnly(Side.CLIENT)
class EntityAnbuClient {
    static void register() {
        net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
                EntityAnbu.EntityCustom.class,
                manager -> new net.minecraft.client.renderer.entity.RenderLiving<EntityAnbu.EntityCustom>(
                        manager,
                        new ModelAnbu(),
                        0.5F
                ) {
                    @Override
                    protected net.minecraft.util.ResourceLocation getEntityTexture(EntityAnbu.EntityCustom entity) {
                        return new net.minecraft.util.ResourceLocation("narutomod:textures/ninja_anbu.png");
                    }
                }
        );
    }
}

@SideOnly(Side.CLIENT)
class ModelAnbu extends net.minecraft.client.model.ModelBase {
    private final net.minecraft.client.model.ModelRenderer bipedHead;
    private final net.minecraft.client.model.ModelRenderer bipedHeadwear;
    private final net.minecraft.client.model.ModelRenderer mask1;
    private final net.minecraft.client.model.ModelRenderer mask2;
    private final net.minecraft.client.model.ModelRenderer mask3;
    private final net.minecraft.client.model.ModelRenderer bipedBody;
    private final net.minecraft.client.model.ModelRenderer sword;
    private final net.minecraft.client.model.ModelRenderer bipedRightArm;
    private final net.minecraft.client.model.ModelRenderer bipedLeftArm;
    private final net.minecraft.client.model.ModelRenderer bipedRightLeg;
    private final net.minecraft.client.model.ModelRenderer bipedLeftLeg;

    public ModelAnbu() {
        textureWidth = 64;
        textureHeight = 64;

        bipedHead = new net.minecraft.client.model.ModelRenderer(this);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.cubeList.add(new net.minecraft.client.model.ModelBox(bipedHead, 0, 0,
                -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

        bipedHeadwear = new net.minecraft.client.model.ModelRenderer(this);
        bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);

        mask1 = new net.minecraft.client.model.ModelRenderer(this);
        mask1.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHeadwear.addChild(mask1);
        mask1.cubeList.add(new net.minecraft.client.model.ModelBox(mask1, 24, 0,
                -4.0F, -8.0F, -4.3F, 8, 8, 0, 0.0F, false));

        mask2 = new net.minecraft.client.model.ModelRenderer(this);
        mask2.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHeadwear.addChild(mask2);
        mask2.cubeList.add(new net.minecraft.client.model.ModelBox(mask2, 48, 0,
                -4.0F, -8.0F, -4.3F, 8, 8, 0, 0.0F, false));

        mask3 = new net.minecraft.client.model.ModelRenderer(this);
        mask3.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHeadwear.addChild(mask3);
        mask3.cubeList.add(new net.minecraft.client.model.ModelBox(mask3, 40, 8,
                -4.0F, -8.0F, -4.3F, 8, 8, 0, 0.0F, false));

        bipedBody = new net.minecraft.client.model.ModelRenderer(this);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedBody.cubeList.add(new net.minecraft.client.model.ModelBox(bipedBody, 16, 16,
                -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
        bipedBody.cubeList.add(new net.minecraft.client.model.ModelBox(bipedBody, 16, 32,
                -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));

        sword = new net.minecraft.client.model.ModelRenderer(this);
        sword.setRotationPoint(-2.5F, 2.0F, 3.5F);
        bipedBody.addChild(sword);
        setRotationAngle(sword, 0.0F, 0.0F, -0.6109F);
        sword.cubeList.add(new net.minecraft.client.model.ModelBox(sword, 60, 16,
                -0.5F, -9.0F, -0.5F, 1, 6, 1, 0.2F, false));
        sword.cubeList.add(new net.minecraft.client.model.ModelBox(sword, 60, 24,
                -0.5F, -3.0F, -0.5F, 1, 12, 1, 0.2F, false));

        bipedRightArm = new net.minecraft.client.model.ModelRenderer(this);
        bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        bipedRightArm.cubeList.add(new net.minecraft.client.model.ModelBox(bipedRightArm, 40, 16,
                -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
        bipedRightArm.cubeList.add(new net.minecraft.client.model.ModelBox(bipedRightArm, 40, 32,
                -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));

        bipedLeftArm = new net.minecraft.client.model.ModelRenderer(this);
        bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        bipedLeftArm.cubeList.add(new net.minecraft.client.model.ModelBox(bipedLeftArm, 32, 48,
                -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
        bipedLeftArm.cubeList.add(new net.minecraft.client.model.ModelBox(bipedLeftArm, 48, 48,
                -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));

        bipedRightLeg = new net.minecraft.client.model.ModelRenderer(this);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        bipedRightLeg.cubeList.add(new net.minecraft.client.model.ModelBox(bipedRightLeg, 0, 16,
                -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
        bipedRightLeg.cubeList.add(new net.minecraft.client.model.ModelBox(bipedRightLeg, 0, 32,
                -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));

        bipedLeftLeg = new net.minecraft.client.model.ModelRenderer(this);
        bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        bipedLeftLeg.cubeList.add(new net.minecraft.client.model.ModelBox(bipedLeftLeg, 16, 48,
                -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
        bipedLeftLeg.cubeList.add(new net.minecraft.client.model.ModelBox(bipedLeftLeg, 0, 48,
                -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
    }

    @Override
    public void render(net.minecraft.entity.Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        bipedHead.render(f5);
        bipedHeadwear.render(f5);
        bipedBody.render(f5);
        bipedRightArm.render(f5);
        bipedLeftArm.render(f5);
        bipedRightLeg.render(f5);
        bipedLeftLeg.render(f5);
    }

    private void setRotationAngle(net.minecraft.client.model.ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, net.minecraft.entity.Entity e) {
        // (ModelBase has setRotationAngles, so this is the right override)
        this.bipedRightArm.rotateAngleX = net.minecraft.util.math.MathHelper.cos(f * 0.6662F + (float) Math.PI) * f1;
        this.bipedRightLeg.rotateAngleX = net.minecraft.util.math.MathHelper.cos(f * 1.0F) * 1.0F * f1;
        this.bipedLeftLeg.rotateAngleX = net.minecraft.util.math.MathHelper.cos(f * 1.0F) * -1.0F * f1;

        this.bipedHeadwear.rotateAngleY = f3 / (180F / (float) Math.PI);
        this.bipedHeadwear.rotateAngleX = f4 / (180F / (float) Math.PI);

        this.bipedLeftArm.rotateAngleX = net.minecraft.util.math.MathHelper.cos(f * 0.6662F) * f1;

        this.bipedHead.rotateAngleY = f3 / (180F / (float) Math.PI);
        this.bipedHead.rotateAngleX = f4 / (180F / (float) Math.PI);
    }
}
