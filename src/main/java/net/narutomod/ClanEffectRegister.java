package net.narutomod;

import com.google.gson.*;
import java.io.FileReader;
import java.util.*;

public class ClanEffectRegister {

    public static Map<String, List<EffectData>> claneffects = new HashMap<>();

    public static void load() {
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(
                    new FileReader("")
            )
        }
    }
}
