package io.github.metalcupcake5.SkyblockStats.utils;

import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;

public class StatsUtil {

    private final SkyblockStats main;

    public StatsUtil(SkyblockStats main) {
        this.main = main;
    }

    public static String getLevel(JsonObject profileObj, String skillName){
        JsonObject profileData = profileObj.get("data").getAsJsonObject();
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
        JsonObject profileData = profileObj.get("data").getAsJsonObject();
        JsonObject stats = profileData.get("stats").getAsJsonObject();
        if(!stats.has(stat)){
            return null;
        }
        return stats.get(stat).getAsInt();
    }

    public static String shadeIfMax(Integer level){
        if(level == 50){
            return ChatFormatting.GOLD + level.toString();
        }
        return ChatFormatting.WHITE + level.toString();
    }
}
