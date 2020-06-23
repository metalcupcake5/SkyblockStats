package io.github.metalcupcake5.SkyblockStats.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;
import io.github.metalcupcake5.SkyblockStats.utils.ChatFormatting;
import io.github.metalcupcake5.SkyblockStats.utils.Symbols;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.FMLLog;
import scala.Int;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GetProfile extends CommandBase {
    
    private SkyblockStats main;

    public GetProfile(SkyblockStats main){
        this.main = main;
    }

    @Override
    public String getCommandName(){
        return "getProfile";
    }

    @Override
    public List<String> getCommandAliases(){
        return Arrays.asList("getpf", "getprofile");
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return
                ChatFormatting.LIGHT_PURPLE + "--------|" +
                ChatFormatting.GRAY + "/getProfile" +
                ChatFormatting.LIGHT_PURPLE + "|--------\n" +
                ChatFormatting.GRAY + "- Usage: " + ChatFormatting.GREEN + "/getProfile <username> <profile>\n" +
                ChatFormatting.GRAY + "- Aliases: " + ChatFormatting.GREEN + "/getprofile, /getpf\n" +
                ChatFormatting.LIGHT_PURPLE + "--------|" +
                ChatFormatting.GRAY + "/getProfile" +
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

                    URL url = new URL("https://sky.lea.moe/api/v2/profile/"+username);
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

                    URL apiURL = new URL("https://sky.lea.moe/api/v2/profile/"+username);
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

                    //Profile Data

                    //Items and stuff
                    String armor = main.getItemUtil().getArmorSet(profile);
                    String armorRarity = main.getItemUtil().getArmorSetRarity(profile);
                    String sword = main.getItemUtil().getHighestSword(profile);
                    String fairy_souls = main.getUtil().getFairySouls(profile);

                    //Stats
                    String average = main.getStatsUtil().getAverageLevel(profile);
                    Integer health = main.getStatsUtil().getStat(profile, "health");
                    Integer defense = main.getStatsUtil().getStat(profile, "defense");
                    Integer intel = main.getStatsUtil().getStat(profile, "intelligence");
                    Integer strength = main.getStatsUtil().getStat(profile, "strength");
                    Integer cc = main.getStatsUtil().getStat(profile, "crit_chance");
                    Integer cd = main.getStatsUtil().getStat(profile, "crit_damage");
                    Integer scc = main.getStatsUtil().getStat(profile, "sea_creature_chance");
                    Integer mf = main.getStatsUtil().getStat(profile, "magic_find");
                    Integer luck = main.getStatsUtil().getStat(profile, "pet_luck");

                    //Slayer
                    String zombie = main.getStatsUtil().getSlayerLevel(profile, "zombie");
                    String spider = main.getStatsUtil().getSlayerLevel(profile, "spider");
                    String wolf = main.getStatsUtil().getSlayerLevel(profile, "wolf");

                    //Format Stats
                    String stats = ChatFormatting.RED + Symbols.HEALTH.getSymbol() + ": " + health + " " + ChatFormatting.GREEN + Symbols.DEFENSE.getSymbol() + ": " + defense + " " + ChatFormatting.AQUA + Symbols.INTELLIGENCE.getSymbol() + ": " + intel + "\n" +
                            ChatFormatting.RED + Symbols.STRENGTH.getSymbol() + ": " + strength + " " + ChatFormatting.BLUE + Symbols.CRIT_CHANCE.getSymbol() + ": " + cc + " " + ChatFormatting.BLUE + Symbols.CRIT_DAMAGE.getSymbol() + ": " + cd + "\n" +
                            ChatFormatting.DARK_AQUA + Symbols.SEA_CREATURE_CHANCE.getSymbol() + ": " + scc + " " + ChatFormatting.AQUA + Symbols.MAGIC_FIND.getSymbol() + ": " + mf + " " + ChatFormatting.LIGHT_PURPLE + Symbols.PET_LUCK.getSymbol() + ": " + luck + "\n";

                    //String formatting
                    String armor_message = armor == null ? ChatFormatting.GRAY + "No full set worn." : main.getUtil().parseRarity(armor, armorRarity);

                    String message = ChatFormatting.GRAY+"Stats for " + ChatFormatting.LIGHT_PURPLE+ display_name +
                            ChatFormatting.GRAY + " on profile " + ChatFormatting.LIGHT_PURPLE + main.getUtil().toProperCase(profileName) + ChatFormatting.GRAY + ".\n" +
                            stats +
                            ChatFormatting.GRAY + "Average Skill Level" + ChatFormatting.WHITE + ": " + average + "\n" +
                            ChatFormatting.LIGHT_PURPLE + "Fairy Souls" + ChatFormatting.WHITE + ": " + ChatFormatting.DARK_PURPLE + fairy_souls + "\n" +
                            ChatFormatting.GRAY + "Armor Set" + ChatFormatting.WHITE + ": " + armor_message + "\n" +
                            ChatFormatting.GRAY + "Sword" + ChatFormatting.WHITE + ": " + sword + "\n" +
                            ChatFormatting.GRAY + "Zombie " + zombie + ChatFormatting.GRAY + ", Spider " + spider + ChatFormatting.GRAY + ", Wolf " + wolf;
                            //ChatFormatting.GRAY + "Farming: "+ farming ;

                    main.getUtil().sendDataMessage(message);

                    //Send skills text
                    IChatComponent skillsText = new ChatComponentText("" + ChatFormatting.GRAY + ChatFormatting.BOLD + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "Skills" + ChatFormatting.GRAY + ChatFormatting.BOLD + "]");
                    IChatComponent skyLeaText = new ChatComponentText("" + ChatFormatting.GRAY + ChatFormatting.BOLD + "[" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + "SkyLea" + ChatFormatting.GRAY + ChatFormatting.BOLD + "]");
                    ChatStyle skillStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/getskills " + username + " " + profileName));
                    ChatStyle skyLeaStyle = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://sky.lea.moe/stats/" + username + "/" + profileName));
                    skillsText.setChatStyle(skillStyle);
                    skyLeaText.setChatStyle(skyLeaStyle);

                    skyLeaText.appendText(" ").appendSibling(skillsText);
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
