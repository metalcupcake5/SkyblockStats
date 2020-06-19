package io.github.metalcupcake5.SkyblockStats.utils;


import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

@Getter
public class Util {

    private final SkyblockStats main;

    public Util(SkyblockStats main) {
        this.main = main;
    }

    private static final String MESSAGE_PREFIX = ChatFormatting.GRAY + "[" + ChatFormatting.LIGHT_PURPLE + SkyblockStats.MOD_NAME + ChatFormatting.GRAY + "] ";

    public void sendMessage(String text, boolean prefix){
        ChatComponentText message = new ChatComponentText((prefix ? MESSAGE_PREFIX : "")+text);
        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    }

    public void sendMessage(String text){
        sendMessage(text, true);
    }

    public void sendError(String text){
        sendMessage(ChatFormatting.RED + text, false);
    }

    public static Integer getLevel(JsonObject profileObj, String skillName){
        JsonObject profileData = profileObj.get("data").getAsJsonObject();
        JsonObject skills = profileData.get("levels").getAsJsonObject();
        if(!skills.has(skillName)){
            return null;
        }
        JsonObject skill = skills.get(skillName).getAsJsonObject();
        return skill.get("level").getAsInt();
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

    public static String getFairySouls(JsonObject profileObj){
        JsonObject profileData = profileObj.get("data").getAsJsonObject();
        JsonObject souls = profileData.get("fairy_souls").getAsJsonObject();
        Integer collected = souls.get("collected").getAsInt();
        Integer total = souls.get("total").getAsInt();
        return collected + "/" + total;
    }

    public static Integer getStat(JsonObject profileObj, String stat){
        JsonObject profileData = profileObj.get("data").getAsJsonObject();
        JsonObject stats = profileData.get("stats").getAsJsonObject();
        if(!stats.has(stat)){
            return null;
        }
        return stats.get(stat).getAsInt();
    }

    public static String parseRarity(String text, String rarity){
        switch(rarity){
            case "common":
                return ChatFormatting.GRAY + text;
            case "uncommon":
                return ChatFormatting.GREEN + text;
            case "rare":
                return ChatFormatting.BLUE + text;
            case "epic":
                return ChatFormatting.DARK_PURPLE + text;
            case "legendary":
                return ChatFormatting.GOLD + text;
            default:
                return null;
        }
    }

    public static String toProperCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }
}
