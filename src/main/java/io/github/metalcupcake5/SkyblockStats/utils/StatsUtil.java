package io.github.metalcupcake5.SkyblockStats.utils;

import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;

public class StatsUtil {

    private final SkyblockStats main;

    public StatsUtil(SkyblockStats main) {
        this.main = main;
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

    public static Integer getStat(JsonObject profileObj, String stat){
        JsonObject profileData = profileObj.get("data").getAsJsonObject();
        JsonObject stats = profileData.get("stats").getAsJsonObject();
        if(!stats.has(stat)){
            return null;
        }
        return stats.get(stat).getAsInt();
    }
}
