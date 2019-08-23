package co.uk.yllo.ollsentials2;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
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

public class OllsentialsGames implements Listener {
    private Main plugin;
    public static OllsentialsGames instance;
    Server server = Main.getPlugin().getServer();
    Logger getLog = Main.getPlugin().getLogger();
    private String prefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(Main.getPlugin().getConfig().getStringList("prefix")));

    private static ArrayList<String> cooldown = new ArrayList<>();
    private static ArrayList<String> arrowGameList = new ArrayList<>();
    private static ArrayList<String> hordeGameList = new ArrayList<>();
    private static ArrayList<Block> blocksRadius = new ArrayList<>();
    private static ArrayList<NamespacedKey> zombies = new ArrayList<>();

    public OllsentialsGames(Main plugin){
        this.plugin=plugin;
    }
    @EventHandler
    public void checkPlayerInArea(PlayerMoveEvent e) {
        Player player = e.getPlayer();
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

    @EventHandler
    public void onSignChanged(SignChangeEvent e) {
        ConfigurationSection user = Main.getPlugin().getConfig().getConfigurationSection("users").getConfigurationSection("user_" + e.getPlayer().getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = Main.getPlugin().getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
        List<String> groupPermission = allGroups.getStringList(".groupPermissions");
        if (groupPermission.contains("admin.sign")) {
            String[] lines = e.getLines();
            for (int i = 0; i < 4; i++) {
                String line = lines[i];
                line = ChatColor.translateAlternateColorCodes('&', line);
                e.setLine(i, line);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ConfigurationSection user = Main.getPlugin().getConfig().getConfigurationSection("users").getConfigurationSection("user_" + e.getPlayer().getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = Main.getPlugin().getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
        List<String> groupPermission = allGroups.getStringList(".groupPermissions");
        ConfigurationSection warps = Main.getPlugin().getConfig().getConfigurationSection("warps");
        Player player = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (groupPermission.contains("basic.sign")) {
                if (e.getClickedBlock().getState() instanceof Sign) {
                    Sign sign = (Sign) e.getClickedBlock().getState();
                    if (sign.getLine(0).contains("[BUY]") || sign.getLine(0).contains("[buy]")) { //checks if a buy sign
                        if (!sign.getLine(1).isEmpty()) { // ALL 4 lines have to have something in for this to work.
                            if (!sign.getLine(2).isEmpty()) {
                                if (!sign.getLine(3).isEmpty()) {
                                    Material getMaterial = Material.getMaterial(sign.getLine(1).toUpperCase());
                                    String getAmount = sign.getLine(2);
                                    String price = sign.getLine(3);
                                    int userBalance = user.getInt(".balance");
                                    if (userBalance > Integer.valueOf(price)) {
                                        player.getInventory().addItem(new ItemStack(getMaterial, Integer.valueOf(getAmount)));
                                        int result = userBalance - Integer.valueOf(price);
                                        user.set(".balance", result);
                                        Main.getPlugin().saveConfig();
                                    } else {
                                        player.sendMessage(prefix + " You don't have enough balance!");
                                    }
                                } else {
                                    getLog.info("Sign incorrectly set up");
                                }
                            } else {
                                getLog.info("Sign incorrectly set up");
                            }
                        } else {
                            getLog.info("Sign incorrectly set up");
                        }
                    } //buy sign
                    if (sign.getLine(0).contains("[SELL]") || sign.getLine(0).contains("[sell]")) { //checks if a sell sign
                        if (!sign.getLine(1).isEmpty()) { // ALL 4 lines have to have something in for this to work.
                            if (!sign.getLine(2).isEmpty()) {
                                if (!sign.getLine(3).isEmpty()) {
                                    Material getMaterial = Material.getMaterial(sign.getLine(1).toUpperCase());
                                    String getAmount = sign.getLine(2);
                                    String price = sign.getLine(3);
                                    int userBalance = user.getInt(".balance");
                                    if (player.getInventory().getItemInMainHand().getType() == getMaterial) {
                                        player.getInventory().removeItem(new ItemStack(getMaterial, Integer.valueOf(getAmount)));
                                        int result = userBalance + Integer.valueOf(price);
                                        user.set(".balance", result);
                                        Main.getPlugin().saveConfig();
                                    } else {
                                        player.sendMessage(prefix + " You need to have the item in your hand!");
                                    }
                                } else {
                                    getLog.info("Sign incorrectly set up");
                                }
                            } else {
                                getLog.info("Sign incorrectly set up");
                            }
                        } else {
                            getLog.info("Sign incorrectly set up");
                        }
                    } // sell sign
                    if (sign.getLine(0).contains("[PLAY]") || sign.getLine(0).contains("[play]")) {
                        if (sign.getLine(1).contains("TARGET") || sign.getLine(1).contains("target")) {
                            String price = sign.getLine(2);
                            int userBalance = user.getInt(".balance");
                            Location signLoc = sign.getBlock().getLocation().getBlock().getLocation();
                            if (!arrowGameList.contains(player.getName())) {
                                player.sendMessage(prefix + " Welcome to target practice!");
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> player.sendMessage(prefix + " Your points will add up and give you an amount which will go into your balance!"), 5L);
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> player.sendMessage(prefix + " White wool: 5 points"), 10L);
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> player.sendMessage(prefix + ChatColor.RED + " Red" + ChatColor.WHITE + " wool: 10 points"), 10L);
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> player.sendMessage(prefix + ChatColor.GREEN + " Green" + ChatColor.WHITE + " wool: 20 points"), 10L);
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> player.sendMessage(prefix + ChatColor.YELLOW + " Yellow" + ChatColor.WHITE + " wool: 50 points"), 10L);
                                arrowGameList.add(player.getName());
                                int result = userBalance - Integer.valueOf(price);
                                user.set(".balance", result);
                                Main.getPlugin().saveConfig();
                            } else {
                                player.sendMessage(prefix + " Thanks for playing target practice! Come back soon!");
                                arrowGameList.remove(player.getName());
                            }
                        }
                        if (sign.getLine(1).contains("HORDE") || sign.getLine(1).contains("horde")) {
                            if (warps.contains("warp_horde")) {
                                String price = sign.getLine(2);
                                int userBalance = user.getInt(".balance");
                                Location signLoc = sign.getBlock().getLocation().getBlock().getLocation();
                                String warpWorld = warps.getConfigurationSection("warp_horde").getString(".warpWorld");
                                World hordeWorld = player.getServer().getWorld(warpWorld);
                                double hordeX = warps.getConfigurationSection("warp_horde").getDouble(".warpX");
                                double hordeY = warps.getConfigurationSection("warp_horde").getDouble(".warpY");
                                double hordeZ = warps.getConfigurationSection("warp_horde").getDouble(".warpZ");
                                Location hordeTeleport = new Location(hordeWorld, hordeX, hordeY, hordeZ);
                                if (!hordeGameList.contains(player.getName())) {
                                    int result = userBalance - Integer.valueOf(price);
                                    hordeGameList.add(player.getName());
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> player.teleport(hordeTeleport), 25L); // delay of 5s
                                    if (blocksRadius.isEmpty()) {
                                        getRadiusFromBlock(hordeTeleport, 25); // if its empty (which it may be if the server has just loaded!)
                                        for (Entity ent : Main.getPlugin().getServer().getWorld("world").getNearbyEntities(hordeTeleport, 5, 5, 5)) {
                                            if (ent.getType() != EntityType.ZOMBIE) {
                                                ent.remove();
                                                getLog.info("lol");
                                            } else {
                                                zombies.add(EntityType.ZOMBIE.getKey());
                                            }
                                        }
                                    }
                                    user.set(".balance", result);
                                    Main.getPlugin().saveConfig();
                                } else {
                                    player.sendMessage(prefix + " Thanks for playing horde mode! Come back soon!");
                                    hordeGameList.remove(player.getName());
                                }
                            } else {
                                player.sendMessage(prefix + "Horde mode has not been set up! Please set a warp first!");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void arrowGame(ProjectileHitEvent e) {
        Player shooter = (Player) e.getEntity().getShooter();
        ConfigurationSection user = Main.getPlugin().getConfig().getConfigurationSection("users").getConfigurationSection("user_" + shooter.getUniqueId().toString());

        if (arrowGameList.contains(shooter.getName())) {
            if (e.getHitBlock().getType() == Material.WHITE_WOOL) {
                Location shooterLoc = new Location(shooter.getWorld(), shooter.getLocation().getX(), shooter.getLocation().getY(), shooter.getLocation().getZ());
                shooter.playSound(shooterLoc, Sound.ENTITY_PLAYER_LEVELUP, 1.18F, 1);
                shooter.sendMessage(prefix + " You just got 5 points!");
                int points = 5;
                int balance = user.getInt(".balance");
                user.set(".balance", balance + points);
                Main.getPlugin().saveConfig();
            }
            if (e.getHitBlock().getType() == Material.RED_WOOL) {
                Location shooterLoc = new Location(shooter.getWorld(), shooter.getLocation().getX(), shooter.getLocation().getY(), shooter.getLocation().getZ());
                shooter.playSound(shooterLoc, Sound.ENTITY_PLAYER_LEVELUP, 1.18F, 1);
                shooter.sendMessage(prefix + " You just got 10 points!");
                int points = 10;
                int balance = user.getInt(".balance");
                user.set(".balance", balance + points);
                Main.getPlugin().saveConfig();
            }
            if (e.getHitBlock().getType() == Material.GREEN_WOOL) {
                Location shooterLoc = new Location(shooter.getWorld(), shooter.getLocation().getX(), shooter.getLocation().getY(), shooter.getLocation().getZ());
                shooter.playSound(shooterLoc, Sound.ENTITY_PLAYER_LEVELUP, 1.18F, 1);
                shooter.sendMessage(prefix + " You just got 20 points!");
                int points = 20;
                int balance = user.getInt(".balance");
                user.set(".balance", balance + points);
                Main.getPlugin().saveConfig();
            }
            if (e.getHitBlock().getType() == Material.YELLOW_WOOL) {
                Location shooterLoc = new Location(shooter.getWorld(), shooter.getLocation().getX(), shooter.getLocation().getY(), shooter.getLocation().getZ());
                shooter.playSound(shooterLoc, Sound.ENTITY_PLAYER_LEVELUP, 1.18F, 1);
                shooter.sendMessage(prefix + " You just got 50 points!");
                int points = 50;
                int balance = user.getInt(".balance");
                user.set(".balance", balance + points);
                Main.getPlugin().saveConfig();
            }
        }
    }

    public void getRadiusFromBlock(Location loc, int radius) {
        for (double x = (loc.getX() - radius); x <= (loc.getX() + radius); x++) {
            for (double y = (loc.getY() - radius); y <= (loc.getY() + radius); y++) {
                for (double z = (loc.getZ() - radius); z <= (loc.getZ() + radius); z++) {
                    Location l = new Location(loc.getWorld(), x, y, z);
                    if (l.distance(loc) <= radius) {
                        blocksRadius.add(l.getBlock());
                    }
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
