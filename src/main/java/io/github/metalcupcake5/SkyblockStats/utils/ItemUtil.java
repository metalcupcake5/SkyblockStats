package io.github.metalcupcake5.SkyblockStats.utils;

import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;

public class ItemUtil {

    private final SkyblockStats main;

    public ItemUtil(SkyblockStats main) {
        this.main = main;
    }

    public static String getArmorSet(JsonObject profileObj){
        JsonObject items = profileObj.get("items").getAsJsonObject();
        if(!items.has("armor_set")){
            return null;
        }
        return items.get("armor_set").getAsString();
    }

    public static String getArmorSetRarity(JsonObject profileObj){
        JsonObject items = profileObj.get("items").getAsJsonObject();
        if(!items.has("armor_set_rarity")){
            return null;
        }
        return items.get("armor_set_rarity").getAsString();
    }

    public static String getHighestSword(JsonObject profileObj){
        JsonObject items = profileObj.get("items").getAsJsonObject();
        if(!items.has("highest_rarity_sword")){
            return null;
        }
        JsonObject sword = items.get("highest_rarity_sword").getAsJsonObject();
        String name = sword.get("display_name").getAsString();
        String rarity = sword.get("rarity").getAsString();
        return Util.parseRarity(name, rarity);
    }

}
