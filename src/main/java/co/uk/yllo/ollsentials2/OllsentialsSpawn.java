package co.uk.yllo.ollsentials2;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class OllsentialsSpawn implements Listener {
    private Main plugin;
    public static OllsentialsSpawn instance;
    Server server = Main.getPlugin().getServer();
    Logger getLog = Main.getPlugin().getLogger();

    public OllsentialsSpawn(Main plugin){
        this.plugin=plugin;
    }
    private static ArrayList<String> spawnProtection = new ArrayList<>();
    private static ArrayList<String> outOfSpawnProtection = new ArrayList<>();
    private static ArrayList<String> cooldown = new ArrayList<>();
    private static ArrayList<String> arrowGameList = new ArrayList<>();
    private static ArrayList<String> hordeGameList = new ArrayList<>();
    private static ArrayList<Block> blocksRadius = new ArrayList<>();
    private static ArrayList<NamespacedKey> zombies = new ArrayList<>();
    private String prefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(Main.getPlugin().getConfig().getStringList("prefix")));

    @EventHandler
    public void checkPlayerInArea(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getLocation().distanceSquared(player.getWorld().getSpawnLocation()) < Math.pow(Main.getPlugin().getServer().getSpawnRadius(), 3.05)) {
            if (!spawnProtection.contains(player.getName())) {
                outOfSpawnProtection.remove(player.getName());
                spawnProtection.add(player.getName());
                player.getWorld().setPVP(false);
                player.sendMessage(prefix + " You have entered the spawn protection radius!");
                launchFirework(player, 1);
            }
        } else {
            if (!outOfSpawnProtection.contains(player.getName())) {
                spawnProtection.remove(player.getName());
                outOfSpawnProtection.add(player.getName());
                player.getWorld().setPVP(true);
                player.sendMessage(prefix + " You have left the spawn protection radius!");
            }
        }
        ConfigurationSection warps = Main.getPlugin().getConfig().getConfigurationSection("warps");
        if (warps.isConfigurationSection("warp_horde")) {
            if (hordeGameList.contains(player.getName())) {
                if (blocksRadius.contains(player.getLocation().getBlock())) {
                } else {
                    hordeGameList.remove(player.getName());
                    player.sendMessage(prefix + " Thanks for playing the horde mode! Come back soon!");
                    player.teleport(player.getWorld().getSpawnLocation());
                }
            }

        }
        if (arrowGameList.contains(player.getName())) { // checking if the arrowgamelist contains their username
            if (e.getFrom().getX() != e.getTo().getX()) { // full block }
                Location oldLocation = new Location(player.getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
                if (!cooldown.contains(player.getName())) {
                    cooldown.add(player.getName());
                    player.sendMessage(prefix + " You're still playing the minigame! Right-click the sign to finish!");
                    player.teleport(oldLocation);
                }
                if (cooldown.contains(player.getName())) {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> cooldown.remove(player.getName()), 50L); // delay of 5s
                    player.teleport(oldLocation);
                }
            } else if (e.getFrom().getZ() != e.getTo().getZ()) {
                Location oldLocation = new Location(player.getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
                if (!cooldown.contains(player.getName())) {
                    player.sendMessage(prefix + " You're still playing the minigame! Right-click the sign to finish!");
                    cooldown.add(player.getName());
                    player.teleport(oldLocation);
                }
                if (cooldown.contains(player.getName())) {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> cooldown.remove(player.getName()), 50L); // delay of 5s
                    player.teleport(oldLocation);
                }
            }
        }
    }

    private void launchFirework(Player player, int speed) {
        Firework fw = (Firework) player.getWorld().spawn(player.getEyeLocation(), Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL).withColor(Color.ORANGE).withFade(Color.RED).build());
        fw.setFireworkMeta(meta);
        fw.setVelocity(player.getLocation().getDirection().multiply(speed));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
    }
}
