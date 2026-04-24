package net.narutomod;

import net.minecraftforge.common.config.Configuration;

public class justuconfig {

    public static Configuration config;

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
    public static double Flying_Thunder_God_distents = 200.0D;
    public static double JINTON_MAX_DAMAGE = 0.0D;
    public static double JINTON_MAX_TOTAL_DAMAGE = 10000.0D;
    public static double JINTON_DAMAGE_MULT = 1.0D;
    public static float scorch_damage = 6.0F;
    public static float bloodchainsdamage = 6.0F;
    public static float bloodchainsheal = 3.0F;

    public static void load() {
        try {
            config.load();

            config.addCustomCategoryComment("jutsus", "General Jutsus settings");

            Flying_Thunder_God_distents = config.getFloat(
                    "Flying thunder god teleport distance",
                    "jutsus",
                    1000,
                    1,
                    Float.MAX_VALUE,
                    ""
            );
            JINTON_MAX_DAMAGE = config.getFloat(
                    "dust Max Damage",
                    "kg",
                    0.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "Maximum damage for dust"
            );

            JINTON_MAX_TOTAL_DAMAGE = config.getFloat(
                    "dust Max Total Damage",
                    "kg",
                    10000.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "Maximum total damage for dust"
            );

            JINTON_DAMAGE_MULT = config.getFloat(
                    "dust Damage Multiplier",
                    "kg",
                    1.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "Damage multiplier for dust"
            );

            scorch_damage = config.getFloat(
                    "Scorch Damage",
                    "kg",
                    6.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "Damage dealt by Scorch"
            );

            bloodchainsdamage = config.getFloat(
                    "Blood Chains Damage",
                    "kg",
                    6.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "Damage dealt by Blood Chains"
            );

            bloodchainsheal = config.getFloat(
                    "Blood Chains Heal",
                    "kg",
                    3.0F,
                    0.0F,
                    Float.MAX_VALUE,
                    "Healing from Blood Chains"
            );


            config.addCustomCategoryComment("kg", "KG Distrubution weights");
            sharingan_weight_config = config.getInt(
                    "Sharingan distribution weight",
                    "kg",
                    3,
                    0,
                    Integer.MAX_VALUE,
                    "Sharingan's distribution weight"
            );

            byakugan_weight_config = config.getInt(
                    "Byakugan distribution weight",
                    "kg",
                    3,
                    0,
                    Integer.MAX_VALUE,
                    "Byakugan's distribution weight"
            );

            bone_weight_config = config.getInt(
                    "Bone distribution weight",
                    "kg",
                    3,
                    0,
                    Integer.MAX_VALUE,
                    "Bone's distribution weight"
            );

            lava_weight_config = config.getInt(
                    "Lava distribution weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Lava's distribution weight"
            );

            scorch_weight_config = config.getInt(
                    "Scorch distribution weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Scorch's distribution weight"
            );

            ice_weight_config = config.getInt(
                    "Ice distribution weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Ice's distribution weight"
            );

            magnet_weight_config = config.getInt(
                    "Magnet distribution weight",
                    "kg",
                    5,
                    0,
                    Integer.MAX_VALUE,
                    "Magnet's distribution weight"
            );

            explosion_weight_config = config.getInt(
                    "Explosion distribution weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Explosion's distribution weight"
            );

            storm_weight_config = config.getInt(
                    "Storm distribution weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Storm's distribution weight"
            );

            boil_weight_config = config.getInt(
                    "Boil distribution weight",
                    "kg",
                    4,
                    0,
                    Integer.MAX_VALUE,
                    "Boil's distribution weight"
            );

            wood_weight_config = config.getInt(
                    "Wood distribution weight",
                    "kg",
                    3,
                    0,
                    Integer.MAX_VALUE,
                    "Wood's distribution weight"
            );

            eightgates_weight_config = config.getInt(
                    "Eight Gates distribution weight",
                    "kg",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Eight Gates's distribution weight"
            );

            dust_weight_config = config.getInt(
                    "Dust distribution weight",
                    "kg",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Dust's distribution weight"
            );

            crystal_weight_config = config.getInt(
                    "Crystal distribution weight",
                    "kg",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Crystal's distribution weight"
            );

            blood_weight_config = config.getInt(
                    "Blood distribution weight",
                    "kg",
                    2,
                    0,
                    Integer.MAX_VALUE,
                    "Blood's distribution weight"
            );

            jinchuriki_weight_config = config.getInt(
                    "Jinchuriki distribution weight",
                    "kg",
                    1,
                    0,
                    Integer.MAX_VALUE,
                    "Jinchuriki's distribution weight"
            );
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
