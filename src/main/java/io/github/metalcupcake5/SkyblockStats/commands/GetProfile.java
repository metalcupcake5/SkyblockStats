package io.github.metalcupcake5.SkyblockStats.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.metalcupcake5.SkyblockStats.SkyblockStats;
import io.github.metalcupcake5.SkyblockStats.utils.ChatFormatting;
import io.github.metalcupcake5.SkyblockStats.utils.Symbols;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.FMLLog;

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
        return Arrays.asList("profile", "getprofile");
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return
                ChatFormatting.LIGHT_PURPLE + "--------|" +
                ChatFormatting.GRAY + "/getProfile" +
                ChatFormatting.LIGHT_PURPLE + "|--------\n" +
                ChatFormatting.GRAY + "- Usage: " + ChatFormatting.GREEN + "/getProfile <username> <profile>\n" +
                ChatFormatting.GRAY + "- Aliases: " + ChatFormatting.GREEN + "/profile, /getprofile\n" +
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
        if(args.length > 1) {
            final String profileName = args[1];
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

                    if(urlConnectionResponseCode == HttpURLConnection.HTTP_SERVER_ERROR){
                        main.getUtil().sendError("Error: " + data.get("error").getAsString());
                        return;
                    }

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
                    Integer farming = main.getUtil().getLevel(profile, "farming");
                    String armor = main.getUtil().getArmorSet(profile);
                    String armorRarity = main.getUtil().getArmorSetRarity(profile);
                    String fairy_souls = main.getUtil().getFairySouls(profile);
                    Integer health = main.getUtil().getStat(profile, "health");
                    Integer defense = main.getUtil().getStat(profile, "defense");
                    Integer intel = main.getUtil().getStat(profile, "intelligence");
                    Integer cc = main.getUtil().getStat(profile, "crit_chance");
                    Integer cd = main.getUtil().getStat(profile, "crit_damage");
                    String sword = main.getUtil().getHighestSword(profile);

                    //String formatting
                    String armor_message = armor == null ? ChatFormatting.GRAY + "No full set worn." : main.getUtil().parseRarity(armor, armorRarity);

                    String message = ChatFormatting.GRAY+"Stats for " + ChatFormatting.LIGHT_PURPLE+ display_name +
                            ChatFormatting.GRAY + " on profile " + ChatFormatting.LIGHT_PURPLE + main.getUtil().toProperCase(profileName) + ChatFormatting.GRAY + ".\n" +
                            ChatFormatting.RED + Symbols.HEALTH.getSymbol() + ": " + health + " " + ChatFormatting.GREEN + Symbols.DEFENSE.getSymbol() + ": " + defense + " " +
                            ChatFormatting.AQUA + Symbols.INTELLIGENCE.getSymbol() + ": " + intel + "\n" +
                            ChatFormatting.BLUE + Symbols.CRIT_CHANCE.getSymbol() + ": " + cc + " " +ChatFormatting.BLUE + Symbols.CRIT_DAMAGE.getSymbol() + ": " + cd + "\n" +
                            ChatFormatting.LIGHT_PURPLE + "Fairy Souls" + ChatFormatting.WHITE + ": " + ChatFormatting.DARK_PURPLE + fairy_souls + "\n" +
                            ChatFormatting.GRAY + "Armor Set" + ChatFormatting.WHITE + ": " + armor_message + "\n" +
                            ChatFormatting.GRAY + "Sword" + ChatFormatting.WHITE + ": " + sword + "\n" +
                            ChatFormatting.GRAY + "Farming: "+ChatFormatting.WHITE+farming ;

                    main.getUtil().sendMessage(message);
                    return;
                } catch (IOException e) {
                    System.out.println("////////\nERROR\n////////");
                    FMLLog.severe(e.toString());
                    System.out.println(e.toString());
                    main.getUtil().sendError("An error occurred! Check the logs for the error!");
                    return;
                }
            }).start();
        }else{
            main.getUtil().sendMessage(getCommandUsage(sender));
        }
    }
}
