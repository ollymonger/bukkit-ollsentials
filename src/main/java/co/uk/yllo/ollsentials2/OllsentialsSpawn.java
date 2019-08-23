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

public class OllsentialsSpawn implements Listener {
    private Main plugin;

    public OllsentialsSpawn(Main plugin){
        this.plugin=plugin;
    }
    private static ArrayList<String> spawnProtection = new ArrayList<>();
    private static ArrayList<String> outOfSpawnProtection = new ArrayList<>();
    private static ArrayList<String> cooldown = new ArrayList<>();
    private static ArrayList<String> arrowGameList = new ArrayList<>();
    private static ArrayList<String> hordeGameList = new ArrayList<>();
    private static ArrayList<Block> blocksRadius = new ArrayList<>();
    private String prefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(Main.getPlugin().getConfig().getStringList("prefix")));

    @EventHandler
    public void checkBlockPlaced(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();
        ConfigurationSection user = Main.getPlugin().getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = Main.getPlugin().getConfig().getConfigurationSection("groups");
        ConfigurationSection specificGroup = allGroups.getConfigurationSection("group_" + userGroup);
        List<String> groupPermission = specificGroup.getStringList(".groupPermissions");
        if (!groupPermission.contains("admin.hammer")) {
            if (player.getLocation().distanceSquared(player.getWorld().getSpawnLocation()) < Math.pow(Main.getPlugin().getServer().getSpawnRadius(), 3)) {
                blockPlaceEvent.setCancelled(true);
                player.sendMessage(prefix + " You cannot place this block!");

            }
        }
    }

    @EventHandler
    public void checkBlockBroken(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        ConfigurationSection user = Main.getPlugin().getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = Main.getPlugin().getConfig().getConfigurationSection("groups");
        ConfigurationSection specificGroup = allGroups.getConfigurationSection("group_" + userGroup);
        List<String> groupPermission = specificGroup.getStringList(".groupPermissions");
        if (!groupPermission.contains("admin.hammer")) {
            if (player.getLocation().distanceSquared(player.getWorld().getSpawnLocation()) < Math.pow(Main.getPlugin().getServer().getSpawnRadius(), 3)) {
                blockBreakEvent.setCancelled(true);
                player.sendMessage(prefix + " You cannot break this block!");
            }
        }
    }

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
