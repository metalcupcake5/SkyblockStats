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

public class GetSkills extends CommandBase {
    private SkyblockStats main;

    public GetSkills(SkyblockStats main){
        this.main = main;
    }

    @Override
    public String getCommandName(){
        return "getSkills";
    }

    @Override
    public List<String> getCommandAliases(){
        return Arrays.asList("getskills");
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return
                ChatFormatting.LIGHT_PURPLE + "--------|" +
                        ChatFormatting.GRAY + "/getSkills" +
                        ChatFormatting.LIGHT_PURPLE + "|--------\n" +
                        ChatFormatting.GRAY + "- Usage: " + ChatFormatting.GREEN + "/getSkills <username> <profile>\n" +
                        ChatFormatting.GRAY + "- Aliases: " + ChatFormatting.GREEN + "/getskills\n" +
                        ChatFormatting.GRAY + "- Description: " + ChatFormatting.GREEN + "View all of a player's skills.\n" +
                        ChatFormatting.LIGHT_PURPLE + "--------|" +
                        ChatFormatting.GRAY + "/getSkills" +
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

                    //Skills Data
                    String average = main.getStatsUtil().getAverageLevel(profile);
                    String farming = main.getStatsUtil().getLevel(profile, "farming");
                    String combat = main.getStatsUtil().getLevel(profile, "combat");
                    String foraging = main.getStatsUtil().getLevel(profile, "foraging");
                    String fishing = main.getStatsUtil().getLevel(profile, "fishing");
                    String taming = main.getStatsUtil().getLevel(profile, "taming");
                    String enchanting = main.getStatsUtil().getLevel(profile, "enchanting");
                    String alchemy = main.getStatsUtil().getLevel(profile, "alchemy");
                    String runecrafting = main.getStatsUtil().getLevel(profile, "runecrafting");
                    String carpentry = main.getStatsUtil().getLevel(profile, "carpentry");
                    String mining = main.getStatsUtil().getLevel(profile, "mining");

                    String c = ChatFormatting.GRAY + ",";
                    String colon = ChatFormatting.GRAY + ": ";

                    String message = ChatFormatting.GRAY+"Skills for " + ChatFormatting.LIGHT_PURPLE+ display_name +
                            ChatFormatting.GRAY + " on profile " + ChatFormatting.LIGHT_PURPLE + main.getUtil().toProperCase(profileName) + ChatFormatting.GRAY + ".\n" +
                            ChatFormatting.DARK_PURPLE + "Taming" + colon + ChatFormatting.WHITE + taming + c + ChatFormatting.GREEN + " Farming" + colon + ChatFormatting.WHITE + farming + "\n" +
                            ChatFormatting.GRAY + "Mining" + colon + ChatFormatting.WHITE + mining + ChatFormatting.DARK_RED + " Combat" + colon + ChatFormatting.WHITE + combat + "\n" +
                            ChatFormatting.DARK_GREEN + "Foraging" + colon + ChatFormatting.WHITE + foraging + c + ChatFormatting.DARK_AQUA + " Fishing" + colon + ChatFormatting.WHITE + fishing + "\n" +
                            ChatFormatting.AQUA + "Enchanting" + colon + ChatFormatting.WHITE + enchanting + c + ChatFormatting.BLUE + " Alchemy" + colon + ChatFormatting.WHITE + alchemy + "\n" +
                            ChatFormatting.YELLOW + "Carpentry" + colon + ChatFormatting.WHITE + carpentry + c + ChatFormatting.LIGHT_PURPLE + " Runecrafting" + colon + ChatFormatting.WHITE + runecrafting + "\n" +
                            ChatFormatting.GRAY + "Average Skill Level" + colon + average;

                    main.getUtil().sendDataMessage(message);
                    IChatComponent skyLeaText = new ChatComponentText("" + ChatFormatting.GRAY + ChatFormatting.BOLD + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "SkyShiiyu" + ChatFormatting.GRAY + ChatFormatting.BOLD + "]");
                    IChatComponent profileText = new ChatComponentText("" + ChatFormatting.GRAY + ChatFormatting.BOLD + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "Profile" + ChatFormatting.GRAY + ChatFormatting.BOLD + "]");
                    IChatComponent slayerText = new ChatComponentText("" + ChatFormatting.GRAY + ChatFormatting.BOLD + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "Slayers" + ChatFormatting.GRAY + ChatFormatting.BOLD + "]");
                    ChatStyle skyLeaStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://sky.shiiyu.moe/stats/" + username + "/" + profileName));
                    ChatStyle profileStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/getprofile " + username + " " + profileName));
                    ChatStyle slayerStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/getslayers " + username + " " + profileName));
                    skyLeaText.setChatStyle(skyLeaStyle);
                    profileText.setChatStyle(profileStyle);
                    slayerText.setChatStyle(slayerStyle);

                    skyLeaText.appendText(" ").appendSibling(profileText).appendText(" ").appendSibling(slayerText);
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
