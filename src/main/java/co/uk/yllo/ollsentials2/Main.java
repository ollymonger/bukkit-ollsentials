package co.uk.yllo.ollsentials2;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;

import java.util.*;

public final class Main extends JavaPlugin implements Listener {

    private String prefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(this.getConfig().getStringList("prefix")));


    private static Plugin plugin;
    private OllsentialsConfig instance;
    private OllsentialGroups groups;
    private OllsentialsUser user;
    private OllsentialsGames games;
    private OllsentialsSpawn spawn;

    //To access the plugin variable from other classes
    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin: Ollsentials now enabled");
        getLogger().info("Plugin Version: " + getDescription().getVersion());
        getLogger().info("Last Updated: (20/08/19)");
        getLogger().info("Most Recent Update: Players can no longer move whilst playing minigame.");
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new OllsentialsUser(this), this);
        Bukkit.getPluginManager().registerEvents(new OllsentialGroups(), this);
        Bukkit.getPluginManager().registerEvents(new OllsentialsConfig(), this);
        Bukkit.getPluginManager().registerEvents(new OllsentialsSpawn(this), this);
        Bukkit.getPluginManager().registerEvents(new OllsentialsGames(this), this);

        this.instance = new OllsentialsConfig();
        this.groups = new OllsentialGroups();
        this.spawn = new OllsentialsSpawn(this);
        this.user = new OllsentialsUser(this);
        this.games = new OllsentialsGames(this);
    }


    private void createWarp(String warpName, Player p) { // adds group for name inserted on cmd
        ConfigurationSection allWarps = this.getConfig().getConfigurationSection("warps");
        if (allWarps.contains("warp_" + warpName)) {
            getLogger().info("already exists");
        } else {
            allWarps.set("warp_" + warpName + ".warpName", warpName);
            allWarps.set("warp_" + warpName + ".warpWorld", p.getLocation().getWorld().getName());
            allWarps.set("warp_" + warpName + ".warpX", p.getLocation().getX());
            allWarps.set("warp_" + warpName + ".warpY", p.getLocation().getY());
            allWarps.set("warp_" + warpName + ".warpZ", p.getLocation().getZ());
            getLogger().info("called create warp with " + warpName);
            saveConfig();
        }
    }

    @EventHandler
    public void checkBlockPlaced(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();
        ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups");
        ConfigurationSection specificGroup = allGroups.getConfigurationSection("group_" + userGroup);
        List<String> groupPermission = specificGroup.getStringList(".groupPermissions");
        if (!groupPermission.contains("admin.hammer")) {
            if (player.getLocation().distanceSquared(player.getWorld().getSpawnLocation()) < Math.pow(getServer().getSpawnRadius(), 3)) {
                blockPlaceEvent.setCancelled(true);
                player.sendMessage(prefix + " You cannot place this block!");

            }
        }
    }

    @EventHandler
    public void checkBlockBroken(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups");
        ConfigurationSection specificGroup = allGroups.getConfigurationSection("group_" + userGroup);
        List<String> groupPermission = specificGroup.getStringList(".groupPermissions");
        if (!groupPermission.contains("admin.hammer")) {
            if (player.getLocation().distanceSquared(player.getWorld().getSpawnLocation()) < Math.pow(getServer().getSpawnRadius(), 3)) {
                blockBreakEvent.setCancelled(true);
                player.sendMessage(prefix + " You cannot break this block!");
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String name = sender.getName();
        Player player = this.getServer().getPlayer(name);

        if (cmd.getName().equalsIgnoreCase("creategroup")) {
            ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
            String userGroup = user.getString(".group");
            ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
            List<String> selectedGroup = allGroups.getStringList(".groupPermissions");
            if (selectedGroup.contains("admin.groups")) {
                if (args.length == 0) {
                    sender.sendMessage(prefix + "Syntax: /creategroup groupname groupprefix");
                } else {
                    getLogger().info(String.valueOf(allGroups.getConfigurationSection("group_" + userGroup + ".groupPermissions")));
                    groups.createGroup(args[0], args[1]); // calls create group with args[0] as name and gets the user ID
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!"); // you dont have perms msg
            }
        }
        if (cmd.getName().equalsIgnoreCase("addusertogroup")) {
            ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
            String userGroup = user.getString(".group");
            ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
            List<String> selectedGroup = allGroups.getStringList(".groupPermissions");
            if (selectedGroup.contains("admin.groups")) {
                if (args.length == 0) {
                    sender.sendMessage(prefix + " Syntax: /addusertogroup username groupname");
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                        /*if(player == target) {
                            sender.sendMessage(prefix + " You cannot add yourself to another group! (code: 7)");
                        } else {
                            addUserToGroup(args[1], target);
                        }*/
                    groups.removeUserFromGroup(userGroup, player); // removes player from previous group
                    groups.addUserToGroup(args[1], target); //sets them to the new group
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!"); // you dont have perms msg
            }
        }
        if (cmd.getName().equalsIgnoreCase("removeuserfromgroup")) {
            ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
            String userGroup = user.getString(".group");
            ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
            List<String> selectedGroup = allGroups.getStringList(".groupPermissions");
            if (selectedGroup.contains("admin.groups")) {
                if (args.length == 0) {
                    sender.sendMessage(prefix + " Syntax: /removeuserfromgroup username groupname");
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (player == target) {
                        sender.sendMessage(prefix + " You cannot remove yourself from a group! (code: 6)");
                    } else {
                        groups.removeUserFromGroup(args[1], target);
                    }
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!"); // you dont have perms msg
            }
        }
        if (cmd.getName().equalsIgnoreCase("addpermission")) {
            ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
            String userGroup = user.getString(".group");
            ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
            List<String> selectedGroup = allGroups.getStringList(".groupPermissions");
            if (selectedGroup.contains("admin.groups")) {

                if (args.length == 0) {
                    sender.sendMessage(prefix + " Syntax: /addpermission groupname permission");
                } else {
                    groups.addPermission(args[0], args[1]);
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!");
            }
        }
        if (cmd.getName().equalsIgnoreCase("setspawn")) {
            ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
            String userGroup = user.getString(".group");
            ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
            List<String> selectedGroup = allGroups.getStringList(".groupPermissions");
            if (selectedGroup.contains("admin.basic")) {
                sender.sendMessage(prefix + " Spawn set to: " + player.getLocation().getBlock().getLocation());
                ConfigurationSection spawn = this.getConfig().getConfigurationSection("spawn");
                double x, y, z;
                String world = player.getWorld().getName(); //Gets world name and sets to string to save properly
                x = player.getLocation().getX();
                y = player.getLocation().getY();
                z = player.getLocation().getZ();
                spawn.set("world", world);
                spawn.set("x", x);
                spawn.set("y", y);
                spawn.set("z", z);
                spawn.set("yaw", player.getLocation().getYaw());
                spawn.set("pitch", player.getLocation().getPitch());
                player.getWorld().setSpawnLocation(player.getLocation().getBlock().getLocation());
                saveConfig();
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!");
            }
        }
        if (cmd.getName().equalsIgnoreCase("setwarp")) {
            ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
            String userGroup = user.getString(".group");
            ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
            List<String> selectedGroup = allGroups.getStringList(".groupPermissions");
            if (selectedGroup.contains("admin.basic")) {
                createWarp(args[0], player.getPlayer());
            }
        }

        if (cmd.getName().equalsIgnoreCase("name")) {
            ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
            String userGroup = user.getString(".group");
            ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
            List<String> selectedGroup = allGroups.getStringList(".groupPermissions");
            if (selectedGroup.contains("vip.name")) {
                if (args.length == 0) {
                    sender.sendMessage(prefix + " /name newname/clear");
                } else if (args[0].equalsIgnoreCase("clear")) {
                    player.setDisplayName(player.getName());
                    player.setPlayerListName(player.getName());
                    user.set(".newName", player.getName());
                    sender.sendMessage(prefix + " You have cleared your name!");
                    saveConfig();
                } else if (args.length > 1) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }
                    String msg = builder.toString();
                    player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', msg));
                    player.setDisplayName(ChatColor.translateAlternateColorCodes('&', msg));
                    sender.sendMessage(prefix + " You have set your name to: " + ChatColor.translateAlternateColorCodes('&', msg));
                    user.set(".newName", msg);
                    saveConfig();
                } else {
                    player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', args[0]));
                    player.setDisplayName(ChatColor.translateAlternateColorCodes('&', args[0]));
                    sender.sendMessage(prefix + " You have set your name to: " + ChatColor.translateAlternateColorCodes('&', args[0]));
                    user.set(".newName", args[0]);
                    saveConfig();
                }
            }
            if (selectedGroup.contains("admin.name")) {
                ConfigurationSection allUsers = this.getConfig().getConfigurationSection("users");
                StringBuilder builder = new StringBuilder();
                if (args.length == 0) {
                    sender.sendMessage(prefix + " Change another player's name: /name username newname");
                    sender.sendMessage(prefix + " Change your own name: /name newname/clear");
                }
                if (args.length > 1) {
                    for (int i = 1; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }
                    String msg = builder.toString();
                    Player target = Bukkit.getPlayer(args[0]);
                    String targetUUID = target.getUniqueId().toString();
                    if (Bukkit.getPlayerExact(args[0]) != null) {
                        if (args[1].equalsIgnoreCase("clear")) { // second word = clear
                            target.setDisplayName(target.getName());
                            target.setPlayerListName(target.getName());
                            allUsers.set("user_" + targetUUID + ".newName", target.getName());
                            sender.sendMessage(prefix + " You cleared: " + args[0] + "'s name.");
                            target.sendMessage(prefix + " " + sender.getName() + " has cleared your name!");

                        } else {
                            allUsers.set("user_" + targetUUID + ".newName", args[1]);
                            sender.sendMessage(prefix + " You set: " + args[0] + "'s name to: " + ChatColor.translateAlternateColorCodes('&', msg));
                            target.sendMessage(prefix + " " + sender.getName() + " has set your name to: " + args[1]);
                            target.setPlayerListName(ChatColor.translateAlternateColorCodes('&', msg));
                            saveConfig();
                        }
                    } else {
                        sender.sendMessage(prefix + " Player is not online!");
                    }
                } else if (args.length >= 1 & !args[0].equalsIgnoreCase("clear")) {
                    player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', args[0]));
                    player.setDisplayName(ChatColor.translateAlternateColorCodes('&', args[0]));
                    sender.sendMessage(prefix + " You have set your name to: " + ChatColor.translateAlternateColorCodes('&', args[0]));
                    user.set(".newName", args[0]);
                    saveConfig();
                } else if (args[0].equalsIgnoreCase("clear")) { // first word = clear
                    player.setDisplayName(player.getName());
                    player.setPlayerListName(player.getName());
                    user.set(".newName", player.getName());
                    sender.sendMessage(prefix + " You have cleared your name!");
                    saveConfig();
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("spawn")) {
            double x, y, z;
            float yaw, pitch;
            ConfigurationSection users = this.getConfig().getConfigurationSection("users");
            ConfigurationSection spawns = this.getConfig().getConfigurationSection("spawn");
            String playerWorld = spawns.getString("world"); // retrieve world name from file!
            World world = player.getServer().getWorld(playerWorld);
            x = this.getConfig().getConfigurationSection("spawn").getDouble("x");
            y = this.getConfig().getConfigurationSection("spawn").getDouble("y");
            z = this.getConfig().getConfigurationSection("spawn").getDouble("z");
            yaw = (float) this.getConfig().getConfigurationSection("spawn").getDouble("yaw");
            pitch = (float) this.getConfig().getConfigurationSection("spawn").getDouble("pitch");
            Location spawn = new Location(world, x, y, z, yaw, pitch);
            sender.sendMessage(prefix + " Sending you to the spawn!");
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> player.teleport(spawn), 50L); // delay of 5s

        }
        if (cmd.getName().equalsIgnoreCase("sethome")) {
            UUID id = player.getUniqueId();
            ConfigurationSection allUsers = this.getConfig().getConfigurationSection("users");
            sender.sendMessage(prefix + " Home set to: " + player.getLocation().getBlock().getLocation());
            double x, y, z;
            String world = player.getWorld().getName(); //Gets world name and sets to string to save properly
            x = player.getLocation().getX();
            y = player.getLocation().getY();
            z = player.getLocation().getZ();

            allUsers.set("user_" + id + ".homepos.world", world);
            allUsers.set("user_" + id + ".homepos.x", x);
            allUsers.set("user_" + id + ".homepos.y", y);
            allUsers.set("user_" + id + ".homepos.z", z);
            allUsers.set("user_" + id + ".homepos.yaw", player.getLocation().getYaw());
            allUsers.set("user_" + id + ".homepos.pitch", player.getLocation().getPitch());
            saveConfig();
        }
        if (cmd.getName().equalsIgnoreCase("home")) {
            ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
            String userGroup = user.getString(".group");
            ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
            List<String> selectedGroup = allGroups.getStringList(".groupPermissions");
            if (selectedGroup.contains("basic.home")) {

                ConfigurationSection users = this.getConfig().getConfigurationSection("users");
                if (user.getString("homepos.world").contains("null")) {
                    sender.sendMessage(prefix + " Please use the command: /sethome before trying to go home!");
                } else {
                    String worldEx = users.getString("homepos.world");
                    World world = this.getServer().getWorld(String.valueOf(worldEx));
                    double x = users.getDouble(".homepos.x");
                    double y = users.getDouble(".homepos.y");
                    double z = users.getDouble(".homepos.z");
                    float yaw = (float) users.getDouble(".homepos.yaw");
                    float pitch = (float) users.getDouble(".homepos.pitch");

                    Location sendHome = new Location(world, x, y, z, yaw, pitch);
                    sendHome.setYaw(yaw);
                    sendHome.setPitch(pitch);
                    sender.sendMessage(prefix + " Sending you back home!");
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> player.teleport(sendHome), 50L); // delay of 5s
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this command!");
            }
        }

        return true;
    }
}
