package net.narutomod;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class justuconfig {

    public static Configuration config;

    public static void init() {
        File configDir = NarutomodMod.CONFIG_DIR != null ? NarutomodMod.CONFIG_DIR : new File("config");
        if (!configDir.exists() && !configDir.mkdirs()) {
            System.err.println("Could not create config directory: " + configDir.getAbsolutePath());
        }
        File file = new File(configDir, "jutsuconfig.cfg");
        config = new Configuration(file);
        load();
    }

    public static double Flying_Thunder_God_distents = 200d;
    public static double JINTON_MAX_DAMAGE = 0d;
    public static double JINTON_MAX_TOTAL_DAMAGE = 10000d;
    public static double JINTON_DAMAGE_MULT = 1d;
    public static float scorch_damage = 6.0F;
    public static float bloodchainsdamage = 6.0F;
    public static float bloodchainsheal = bloodchainsdamage / 2;
    public static int lighting_RESISTANCE_strangth = 2;
    public static int lighting_SPEED_strangth = 10;
    public static int lighting_JUMP_BOOST_strangth = 5;
    public static float damage_c3 = 200.0f;
    public static int damage_c4 = 2;
    public static int sharingan_weight_config = 3;
    public static int byakugan_weight_config = 3;
    public static int bone_weight_config = 3;
    public static int lava_weight_config = 4;
    public static int scorch_weight_config = 4;
    public static int ice_weight_config = 4;
    public static int magnet_weight_config = 5;
    public static int explosion_weight_config = 4;
    public static int storm_weight_config = 4;
    public static int boil_weight_config = 4;
    public static int wood_weight_config = 3;
    public static int eightgates_weight_config = 2;
    public static int dust_weight_config = 2;
    public static int crystal_weight_config = 2;
    public static int blood_weight_config = 2;
    public static int jinchuriki_weight_config = 1;




    public static void load() {
        try {
            config.load();

            config.addCustomCategoryComment("jutsu", "General Jutsus settings");

            Flying_Thunder_God_distents = config.getFloat(
                    "Flying Thunder God distance",
                    "jutsu",
                    200,
                    0,
                    Float.MAX_VALUE,
                    "Maximum teleport distance"
            );

            JINTON_MAX_DAMAGE = config.getFloat(
                    "Dust max damage",
                    "jutsu",
                    0,
                    0,
                    Float.MAX_VALUE,
                    "Max damage per hit"
            );

            JINTON_MAX_TOTAL_DAMAGE = config.getFloat(
                    "Dust max total damage",
                    "jutsu",
                    10000,
                    0,
                    Float.MAX_VALUE,
                    "Total beam damage cap"
            );

            JINTON_DAMAGE_MULT = config.getFloat(
                    "Dust damage multiplier",
                    "jutsu",
                    1,
                    0,
                    Float.MAX_VALUE,
                    "Damage multiplier"
            );

            scorch_damage = config.getFloat(
                    "Scorch damage",
                    "jutsu",
                    6.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "Scorch fireball damage"
            );

            bloodchainsdamage = config.getFloat(
                    "Blood Chains damage",
                    "jutsu",
                    6.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "Blood chains damage"
            );

            bloodchainsheal = config.getFloat(
                    "Blood Chains heal",
                    "jutsu",
                    bloodchainsdamage / 2,
                    0.0F,
                    Float.MAX_VALUE,
                    "Blood chains healing amount"
            );

            lighting_RESISTANCE_strangth = config.getInt(
                    "Lightning resistance strength",
                    "jutsu",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Resistance amplifier"
            );

            lighting_SPEED_strangth = config.getInt(
                    "Lightning speed strength",
                    "jutsu",
                    10,
                    0,
                    Integer.MAX_VALUE,
                    "Speed amplifier"
            );

            lighting_JUMP_BOOST_strangth = config.getInt(
                    "Lightning jump boost strength",
                    "jutsu",
                    5,
                    0,
                    Integer.MAX_VALUE,
                    "Jump boost amplifier"
            );

            damage_c3 = config.getFloat(
                    "C3 damage",
                    "jutsu",
                    200.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "C3 damage"
            );

            damage_c4 = config.getInt(
                    "C4 damage",
                    "jutsu",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "C4 damage per tick"
            );

            config.addCustomCategoryComment("kg", "KG Distrubution weights");

            sharingan_weight_config = config.getInt(
                    "Sharingan weight",
                    "kg",
                    3,
                    0,
                    Integer.MAX_VALUE,
                    "Sharingan distribution weight"
            );

            byakugan_weight_config = config.getInt(
                    "Byakugan weight",
                    "kg",
                    3,
                    0,
                    Integer.MAX_VALUE,
                    "Byakugan distribution weight"
            );

            bone_weight_config = config.getInt(
                    "Bone weight",
                    "kg",
                    3,
                    0,
                    Integer.MAX_VALUE,
                    "Bone distribution weight"
            );

            lava_weight_config = config.getInt(
                    "Lava weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Lava distribution weight"
            );

            scorch_weight_config = config.getInt(
                    "Scorch weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Scorch distribution weight"
            );

            ice_weight_config = config.getInt(
                    "Ice weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Ice distribution weight"
            );

            magnet_weight_config = config.getInt(
                    "Magnet weight",
                    "kg",
                    5,
                    0,
                    Integer.MAX_VALUE,
                    "Magnet distribution weight"
            );

            explosion_weight_config = config.getInt(
                    "Explosion weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Explosion distribution weight"
            );

            storm_weight_config = config.getInt(
                    "Storm weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Storm distribution weight"
            );

            boil_weight_config = config.getInt(
                    "Boil weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Boil distribution weight"
            );

            wood_weight_config = config.getInt(
                    "Wood weight",
                    "kg",
                    3,
                    0,
                    Integer.MAX_VALUE,
                    "Wood distribution weight"
            );

            eightgates_weight_config = config.getInt(
                    "Eight Gates weight",
                    "kg",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Eight Gates distribution weight"
            );

            dust_weight_config = config.getInt(
                    "Dust weight",
                    "kg",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Dust distribution weight"
            );

            crystal_weight_config = config.getInt(
                    "Crystal weight",
                    "kg",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Crystal distribution weight"
            );

            blood_weight_config = config.getInt(
                    "Blood weight",
                    "kg",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Blood distribution weight"
            );

            jinchuriki_weight_config = config.getInt(
                    "Jinchuriki weight",
                    "kg",
                    1,
                    0,
                    Integer.MAX_VALUE,
                    "Jinchuriki distribution weight"
            );
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
