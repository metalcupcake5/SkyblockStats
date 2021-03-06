package io.github.metalcupcake5.SkyblockStats.utils;

import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;

public class StatsUtil {

    private final SkyblockStats main;

    public StatsUtil(SkyblockStats main) {
        this.main = main;
    }

    public static String getLevel(JsonObject profileObj, String skillName){
        JsonObject profileData = profileObj.getAsJsonObject("data");
        JsonObject skills = profileData.get("levels").getAsJsonObject();
        if(!skills.has(skillName)){
            return null;
        }
        JsonObject skill = skills.get(skillName).getAsJsonObject();
        Integer level = skill.get("level").getAsInt();
        if(skill.get("maxLevel").getAsInt() == level){
            return ChatFormatting.GOLD + level.toString();
        }
        return ChatFormatting.WHITE + level.toString();
    }

    public static Integer getStat(JsonObject profileObj, String stat){
        JsonObject profileData = profileObj.getAsJsonObject("data");
        JsonObject stats = profileData.get("stats").getAsJsonObject();
        if(!stats.has(stat)){
            return null;
        }
        return stats.get(stat).getAsInt();
    }

    public static String getSlayerLevel(JsonObject profileObj, String slayer){
        JsonObject profileData = profileObj.getAsJsonObject("data");
        JsonObject slayers = profileData.getAsJsonObject("slayers");
        if(!slayers.has(slayer)){
            return null;
        }
        JsonObject slayerData = slayers.getAsJsonObject(slayer);
        JsonObject levels = slayerData.getAsJsonObject("level");
        Integer level = levels.get("currentLevel").getAsInt();
        if(levels.get("maxLevel").getAsInt() == level){
            return ChatFormatting.GOLD + level.toString();
        }
        return ChatFormatting.WHITE + level.toString();
    }

    public static String getAverageLevel(JsonObject profileObj){
        JsonObject profileData = profileObj.getAsJsonObject("data");
        Double level = profileData.get("average_level").getAsDouble();
        Double average = Math.round(level * 100.0) / 100.0;
        if(average == 50){
            return ChatFormatting.GOLD + average.toString();
        }
        return ChatFormatting.WHITE + average.toString();
    }

    public static Integer getSlayerLevelData(JsonObject profileObj, String slayer, String data){
        JsonObject profileData = profileObj.getAsJsonObject("data");
        JsonObject slayers = profileData.getAsJsonObject("slayers");
        if(!slayers.has(slayer)){
            return null;
        }
        JsonObject slayerData = slayers.getAsJsonObject(slayer);
        JsonObject levels = slayerData.getAsJsonObject("level");
        Integer response = levels.get(data).getAsInt();
        return response;
    }

    public String getSlayerProgress(JsonObject profileObj, String slayer){
        JsonObject profileData = profileObj.getAsJsonObject("data");
        JsonObject slayers = profileData.getAsJsonObject("slayers");
        if(!slayers.has(slayer)){
            return null;
        }
        JsonObject slayerData = slayers.getAsJsonObject(slayer);
        JsonObject levels = slayerData.getAsJsonObject("level");
        Double progress = levels.get("progress").getAsDouble();
        long average = Math.round(progress * 100.0);
        return ChatFormatting.WHITE + "" + average + "%";
    }
}
