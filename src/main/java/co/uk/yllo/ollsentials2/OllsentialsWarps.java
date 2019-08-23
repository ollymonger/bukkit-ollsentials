package co.uk.yllo.ollsentials2;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OllsentialsWarps implements Listener {
    Server server = Main.getPlugin().getServer();
    Logger getLog = Main.getPlugin().getLogger();
    private Main plugin;

    public OllsentialsWarps(Main plugin){
        this.plugin=plugin;
    }

    public void createWarp(String warpName, Player p) { // adds group for name inserted on cmd
        ConfigurationSection allWarps = Main.getPlugin().getConfig().getConfigurationSection("warps");
        if (allWarps.contains("warp_" + warpName)) {
            getLog.info("already exists");
        } else {
            allWarps.set("warp_" + warpName + ".warpName", warpName);
            allWarps.set("warp_" + warpName + ".warpWorld", p.getLocation().getWorld().getName());
            allWarps.set("warp_" + warpName + ".warpX", p.getLocation().getX());
            allWarps.set("warp_" + warpName + ".warpY", p.getLocation().getY());
            allWarps.set("warp_" + warpName + ".warpZ", p.getLocation().getZ());
            getLog.info("called create warp with " + warpName);
            Main.getPlugin().saveConfig();
        }
    }
}
