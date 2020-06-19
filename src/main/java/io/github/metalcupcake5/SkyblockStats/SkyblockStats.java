package io.github.metalcupcake5.SkyblockStats;

import io.github.metalcupcake5.SkyblockStats.commands.GetProfile;
import io.github.metalcupcake5.SkyblockStats.utils.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.ClientCommandHandler;
import lombok.*;

@Getter
@Mod(modid = SkyblockStats.MODID, version = SkyblockStats.VERSION, name = SkyblockStats.MOD_NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class SkyblockStats {


    public static final String MODID = "skyblockstats";
    public static final String MOD_NAME = "SkyblockStats";
    public static final String VERSION = "0.0.1-SNAPSHOT";

    @Getter private static SkyblockStats instance;
    private Util util = new Util(this);
    private ItemUtil itemUtil = new ItemUtil(this);
    private StatsUtil statsUtil = new StatsUtil(this);
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		ClientCommandHandler.instance.registerCommand(new GetProfile(this));
    }
}
