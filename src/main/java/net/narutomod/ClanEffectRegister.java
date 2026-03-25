package net.narutomod;

import com.google.gson.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ClanEffectRegister {

    public static Map<String, List<EffectData>> claneffects = new HashMap<>();
    private static final Map<String, List<EffectData>> DEFAULT_CLAN_EFFECTS = buildDefaultClanEffects();

    public static void load() {
        File baseConfigDir = NarutomodMod.CONFIG_DIR != null ? NarutomodMod.CONFIG_DIR : new File("config");
        if (!baseConfigDir.exists() && !baseConfigDir.mkdirs()) {
            System.err.println("Could not create config directory: " + baseConfigDir.getAbsolutePath());
        }

        File clanEffectsFile = new File(baseConfigDir, "ClanEffects.json");
        File legacyClanEffectsFile = new File("ClanEffects.json");

        if (!clanEffectsFile.exists() && legacyClanEffectsFile.exists()) {
            clanEffectsFile = legacyClanEffectsFile;
        }

        if (!clanEffectsFile.exists()) {
            writeDefaultFile(clanEffectsFile);
        }

        try {
            Gson gson = new Gson();

            JsonObject json = gson.fromJson(
                    new FileReader(clanEffectsFile),
                    JsonObject.class);

            if (json == null) {
                setDefaultsInMemory();
                return;
            }

            claneffects.clear();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String clan = entry.getKey();
                JsonArray effectsArray = entry.getValue().getAsJsonArray();

                List<EffectData> effects = new ArrayList<>();

                for (JsonElement element : effectsArray) {
                    JsonObject obj = element.getAsJsonObject();

                    String effect = obj.get("effect").getAsString();
                    int duration = obj.get("duration").getAsInt();
                    int amplifier = obj.get("amplifier").getAsInt();

                    effects.add(new EffectData(effect, duration, amplifier));
                }

                claneffects.put(clan.toLowerCase(Locale.ROOT), effects);
            }
        } catch (FileNotFoundException e) {
            setDefaultsInMemory();
            System.err.println("ClanEffects.json not found: " + e.getMessage());
        } catch (JsonParseException | IllegalStateException e) {
            setDefaultsInMemory();
            System.err.println("Invalid ClanEffects.json format: " + e.getMessage());
        }
    }

    private static void writeDefaultFile(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        JsonObject root = new JsonObject();
        for (Map.Entry<String, List<EffectData>> entry : DEFAULT_CLAN_EFFECTS.entrySet()) {
            JsonArray effects = new JsonArray();
            for (EffectData effectData : entry.getValue()) {
                JsonObject effectObj = new JsonObject();
                effectObj.addProperty("effect", effectData.effect);
                effectObj.addProperty("duration", effectData.duration);
                effectObj.addProperty("amplifier", effectData.amplifier);
                effects.add(effectObj);
            }
            root.add(entry.getKey(), effects);
        }

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("Failed to write default ClanEffects.json: " + e.getMessage());
        }
    }

    private static void setDefaultsInMemory() {
        claneffects.clear();
        for (Map.Entry<String, List<EffectData>> entry : DEFAULT_CLAN_EFFECTS.entrySet()) {
            claneffects.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    private static Map<String, List<EffectData>> buildDefaultClanEffects() {
        Map<String, List<EffectData>> defaults = new HashMap<>();
        addDefault(defaults, "uchiha", "minecraft:fire_resistance", 1, 0);
        addDefault(defaults, "uzumaki", "minecraft:regeneration", 1, 1);
        addDefault(defaults, "hyuga", "minecraft:night_vision", 1, 0);
        addDefault(defaults, "senju", "minecraft:saturation", 1, 5);
        addDefault(defaults, "nara", "minecraft:speed", 1, 1);
        addDefault(defaults, "akimichi", "minecraft:strength", 1, 1);
        addDefault(defaults, "yamanaka", "minecraft:haste", 1, 2);
        addDefault(defaults, "aburame", "minecraft:jump_boost", 1, 1);
        addDefault(defaults, "inuzuka", "flight", 1, 0);
        addDefault(defaults, "kaguya", "minecraft:resistance", 1, 0);
        addDefault(defaults, "hozuki", "minecraft:water_breathing", 1, 0);
        addDefault(defaults, "sarutobi", "reach", 1, 1);
        addDefault(defaults, "kazekage", "minecraft:resistance", 1, 0);
        return defaults;
    }

    private static void addDefault(Map<String, List<EffectData>> map, String clan, String effect, int duration, int amplifier) {
        List<EffectData> effects = map.get(clan);
        if (effects == null) {
            effects = new ArrayList<>();
            map.put(clan, effects);
        }
        effects.add(new EffectData(effect, duration, amplifier));
    }

    public static class EffectData {
        public final String effect;
        public final int duration;
        public final int amplifier;

        public EffectData(String effect, int duration, int amplifier) {
            this.effect = effect;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }
}
