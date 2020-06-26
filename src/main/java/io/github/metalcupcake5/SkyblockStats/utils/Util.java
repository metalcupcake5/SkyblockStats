package io.github.metalcupcake5.SkyblockStats.utils;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.util.Map;
import java.util.Set;

@Getter
public class Util {

    private final SkyblockStats main;

    public Util(SkyblockStats main) {
        this.main = main;
    }

    private static final String MESSAGE_PREFIX = ChatFormatting.GRAY + "[" + ChatFormatting.LIGHT_PURPLE + SkyblockStats.MOD_NAME + ChatFormatting.GRAY + "] ";

    public static void sendMessage(String text, boolean prefix){
        ChatComponentText message = new ChatComponentText((prefix ? MESSAGE_PREFIX : "")+text);
        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    }

    public static void sendMessage(String text){
        sendMessage(text, true);
    }

    public static void sendDataMessage(String text){
        String content =
                ChatFormatting.STRIKETHROUGH + "------------------------------" + "\n" +
                text + "\n" +
                ChatFormatting.STRIKETHROUGH + "------------------------------";
        sendMessage(content, false);
    }

    public static void sendClickableMessage(String text, String command){
        IChatComponent clickableText = new ChatComponentText(text);
        ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        clickableText.setChatStyle(style);
        Minecraft.getMinecraft().thePlayer.addChatMessage(clickableText);
    }

    public static void sendError(String text){
        sendMessage(ChatFormatting.RED + text);
    }

    public static String getFairySouls(JsonObject profileObj){
        JsonObject profileData = profileObj.get("data").getAsJsonObject();
        JsonObject souls = profileData.get("fairy_souls").getAsJsonObject();
        Integer collected = souls.get("collected").getAsInt();
        Integer total = souls.get("total").getAsInt();
        return collected + "/" + total;
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

    public static String getRecentProfile(JsonObject profiles){
        Set<Map.Entry<String, JsonElement>> profileSet = profiles.entrySet();
        for (Map.Entry<String,JsonElement> me:profileSet)
        {
            String name = me.getValue().getAsJsonObject().get("cute_name").getAsString();
            Boolean current = me.getValue().getAsJsonObject().get("current").getAsBoolean();
            if(current){
                return name;
            }
        }
        return null;
    }
}
