package io.github.metalcupcake5.SkyblockStats.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;
import io.github.metalcupcake5.SkyblockStats.utils.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.FMLLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GetSlayers extends CommandBase {

    private SkyblockStats main;

    public GetSlayers(SkyblockStats main){
        this.main = main;
    }

    @Override
    public String getCommandName(){
        return "getSlayers";
    }

    @Override
    public List<String> getCommandAliases(){
        return Arrays.asList("getslayers", "getslayer");
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return
                ChatFormatting.LIGHT_PURPLE + "--------|" +
                ChatFormatting.GRAY + "/getSlayers" +
                ChatFormatting.LIGHT_PURPLE + "|--------\n" +
                ChatFormatting.GRAY + "- Usage: " + ChatFormatting.GREEN + "/getSlayers <username> [profile]\n" +
                ChatFormatting.GRAY + "- Aliases: " + ChatFormatting.GREEN + "/getslayers, /getslayer\n" +
                ChatFormatting.GRAY + "- Description: " + ChatFormatting.GREEN + "View comprehensive data of a player's slayers.\n" +
                ChatFormatting.LIGHT_PURPLE + "--------|" +
                ChatFormatting.GRAY + "/getSlayers" +
                ChatFormatting.LIGHT_PURPLE + "|--------";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }
    
    @Override
    public void processCommand(ICommandSender sender, String[] args){
        if(args.length > 0) {
            final String username = args[0];
            new Thread(() -> {
                FMLLog.info("Grabbing uuid...");
                main.getUtil().sendMessage(ChatFormatting.GRAY+"Fetching data.\n" +
                        ChatFormatting.GRAY + "This may take some time depending on your connection.");
                try {

                    URL url = new URL("https://sky.shiiyu.moe/api/v2/profile/"+username);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("User-Agent", "metalcupcake5/1.0");

                    int urlConnectionResponseCode = urlConnection.getResponseCode();

                    if (urlConnectionResponseCode != HttpURLConnection.HTTP_OK && urlConnectionResponseCode == HttpURLConnection.HTTP_SERVER_ERROR) {
                        BufferedReader urlin = new BufferedReader(new InputStreamReader(
                                urlConnection.getErrorStream()));
                        String urlinputLine;
                        StringBuffer urlResponse = new StringBuffer();

                        while ((urlinputLine = urlin.readLine()) != null) {
                            urlResponse.append(urlinputLine);
                        }
                        urlin.close();
                        JsonObject data = new Gson().fromJson(urlResponse.toString(), JsonObject.class);
                        String error = data.get("error").getAsString();
                        main.getUtil().sendError("Error: " + error + "\n" + ChatFormatting.RED + "API request failed with response code " + urlConnectionResponseCode);
                        return;
                    }

                    BufferedReader urlin = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
                    String urlinputLine;
                    StringBuffer urlResponse = new StringBuffer();

                    while ((urlinputLine = urlin.readLine()) != null) {
                        urlResponse.append(urlinputLine);
                    }
                    urlin.close();

                    JsonObject data = new Gson().fromJson(urlResponse.toString(), JsonObject.class);

                    JsonObject profiles = data.get("profiles").getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> profileSet = profiles.entrySet();
                    ArrayList<String> profileIds = new ArrayList<>();
                    ArrayList<String> profileNames = new ArrayList<>();

                    for (Map.Entry<String,JsonElement> me:profileSet)
                    {
                        String id = me.getValue().getAsJsonObject().get("profile_id").getAsString();
                        String name = me.getValue().getAsJsonObject().get("cute_name").getAsString();
                        profileIds.add(id);
                        profileNames.add(name);
                    }
                    String profileName;
                    if(args.length == 1){
                        profileName = main.getUtil().getRecentProfile(profiles);
                    }else{
                        profileName = args[1];
                    }

                    if(!profileNames.contains(main.getUtil().toProperCase(profileName))){
                        String validProfiles = String.join(", ", profileNames);
                        main.getUtil().sendMessage("Invalid profile!\nValid Profiles: "+validProfiles);
                        return;
                    }

                    int index = profileNames.indexOf(main.getUtil().toProperCase(profileName));
                    String id = profileIds.get(index);

                    URL apiURL = new URL("https://sky.shiiyu.moe/api/v2/profile/"+username);
                    HttpURLConnection apiConnection = (HttpURLConnection) apiURL.openConnection();
                    apiConnection.setRequestMethod("GET");
                    apiConnection.setRequestProperty("User-Agent", "metalcupcake5/1.0");

                    int responseCode = apiConnection.getResponseCode();

                    if (responseCode != HttpURLConnection.HTTP_OK) { // success
                        main.getUtil().sendError("API request failed with response code " + responseCode + ", please try again.");
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            apiConnection.getInputStream()));
                    String inputLine;
                    StringBuffer apiResponse = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        apiResponse.append(inputLine);
                    }
                    in.close();


                    JsonObject apiData = new Gson().fromJson(apiResponse.toString(), JsonObject.class);
                    JsonObject list = apiData.get("profiles").getAsJsonObject();
                    JsonObject profile = list.get(id).getAsJsonObject();

                    String display_name = profile.get("data").getAsJsonObject().get("display_name").getAsString();

                    //Slayer Data
                    String zombieLevel = main.getStatsUtil().getSlayerLevel(profile, "zombie");
                    String spiderLevel = main.getStatsUtil().getSlayerLevel(profile, "spider");
                    String wolfLevel = main.getStatsUtil().getSlayerLevel(profile, "wolf");

                    Integer zombieNext = main.getStatsUtil().getSlayerLevelData(profile, "zombie", "xpForNext") - main.getStatsUtil().getSlayerLevelData(profile, "zombie", "xp");
                    Integer spiderNext = main.getStatsUtil().getSlayerLevelData(profile, "spider", "xpForNext") - main.getStatsUtil().getSlayerLevelData(profile, "spider", "xp");
                    Integer wolfNext = main.getStatsUtil().getSlayerLevelData(profile, "wolf", "xpForNext") - main.getStatsUtil().getSlayerLevelData(profile, "wolf", "xp");

                    String zombieProgress = main.getStatsUtil().getSlayerProgress(profile, "zombie");
                    String spiderProgress = main.getStatsUtil().getSlayerProgress(profile, "spider");
                    String wolfProgress = main.getStatsUtil().getSlayerProgress(profile, "wolf");

                    Integer zombieXp = main.getStatsUtil().getSlayerLevelData(profile, "zombie", "xp");
                    Integer spiderXp = main.getStatsUtil().getSlayerLevelData(profile, "spider", "xp");
                    Integer wolfXp = main.getStatsUtil().getSlayerLevelData(profile, "wolf", "xp");

                    String zombie =
                            ChatFormatting.DARK_GREEN + "Zombie Slayer " + ChatFormatting.WHITE + zombieLevel + ChatFormatting.GRAY + ":" + "\n" + ChatFormatting.RESET +
                            ChatFormatting.GRAY + "  - XP to next level:" + ChatFormatting.WHITE + " " + zombieNext + "/" + main.getStatsUtil().getSlayerLevelData(profile, "zombie", "xpForNext") +"\n" +
                            ChatFormatting.GRAY + "  - Progress: " + zombieProgress + "\n" +
                            ChatFormatting.GRAY + "  - Total XP: " + ChatFormatting.WHITE + zombieXp;

                    String spider =
                            ChatFormatting.DARK_PURPLE + "Spider Slayer " + ChatFormatting.WHITE + spiderLevel + ChatFormatting.GRAY + ":" + "\n" + ChatFormatting.RESET +
                            ChatFormatting.GRAY + "  - XP to next level:" + ChatFormatting.WHITE + " " + spiderNext + "/" + main.getStatsUtil().getSlayerLevelData(profile, "spider", "xpForNext") + "\n" +
                            ChatFormatting.GRAY + "  - Progress: " + spiderProgress + "\n" +
                            ChatFormatting.GRAY + "  - Total XP: " + ChatFormatting.WHITE + spiderXp;

                    String wolf =
                            ChatFormatting.DARK_RED + "Wolf Slayer " + ChatFormatting.WHITE + wolfLevel + ChatFormatting.GRAY + ":" + "\n" + ChatFormatting.RESET +
                            ChatFormatting.GRAY + "  - XP to next level:" + ChatFormatting.WHITE + " " + wolfNext + "/" + main.getStatsUtil().getSlayerLevelData(profile, "wolf", "xpForNext") + "\n" +
                            ChatFormatting.GRAY + "  - Progress: " + wolfProgress + "\n" +
                            ChatFormatting.GRAY + "  - Total XP: " + ChatFormatting.WHITE + wolfXp;


                    String message = ChatFormatting.GRAY+"Slayers for " + ChatFormatting.LIGHT_PURPLE+ display_name +
                            ChatFormatting.GRAY + " on profile " + ChatFormatting.LIGHT_PURPLE + main.getUtil().toProperCase(profileName) + ChatFormatting.GRAY + ".\n" +
                            zombie + "\n" +
                            spider + "\n" +
                            wolf;

                    main.getUtil().sendDataMessage(message);

                    //Send skills text
                    IChatComponent skyLeaText = new ChatComponentText("" + ChatFormatting.GRAY + ChatFormatting.BOLD + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "SkyShiiyu" + ChatFormatting.GRAY + ChatFormatting.BOLD + "]");
                    IChatComponent profileText = new ChatComponentText("" + ChatFormatting.GRAY + ChatFormatting.BOLD + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "Profile" + ChatFormatting.GRAY + ChatFormatting.BOLD + "]");
                    IChatComponent skillsText = new ChatComponentText("" + ChatFormatting.GRAY + ChatFormatting.BOLD + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "Skills" + ChatFormatting.GRAY + ChatFormatting.BOLD + "]");

                    ChatStyle profileStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/getprofile " + username + " " + profileName));
                    ChatStyle skillStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/getskills " + username + " " + profileName));
                    ChatStyle skyLeaStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://sky.shiiyu.moe/stats/" + username + "/" + profileName));

                    skyLeaText.setChatStyle(skyLeaStyle);
                    profileText.setChatStyle(profileStyle);
                    skillsText.setChatStyle(skillStyle);

                    skyLeaText.appendText(" ").appendSibling(profileText).appendText(" ").appendSibling(skillsText);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(skyLeaText);
                    return;
                } catch (IOException e) {
                    main.getUtil().sendError("An error occurred! Check the logs for the error!");
                    return;
                }
            }).start();
        }else{
            main.getUtil().sendMessage(getCommandUsage(sender), false);
        }
    }
}
