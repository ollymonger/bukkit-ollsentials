##Welcome
Hi and thanks for visiting the github for Ollsentials.
Please find all the permissions below with their descriptions and usages.

On first load, the server will create 3 groups. One being an admin group, one a member and the other a guest. 
You will need to log into the server, open the config file and find your userId then change your group from guest to admin,
in order to gain access to the commands listed below.

I made this plugin so that people only have to download one plugin for a permissions manager and basic parts from the
Essentials plugin for a semi-vanilla experience.

##permissions:
chat.color - talk in colour in chat
admin.rights - admin commands (BAN,UNBAN, KICK)
admin.basic - basic admin commands (set spawn, set name, teleport players)
admin.group - group commands for admin (creategroup, addpermissions, addusertogroup, removeusertogroup)
admin.sign - create signs ([BUY], [SELL])
admin.hammer - lets you destroy/place blocks in spawn area.
admin.name - lets you set your name/other player's names
vip.name - lets you set your name.
basic.home - home commands to set home, use home and use /spawn command.
basic.sign - use of signs (BUY AND SELL).


package co.uk.yllo.ollsentials2;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.*;
import org.bukkit.block.data.type.Fire;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Main extends JavaPlugin implements Listener {

    private String prefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(this.getConfig().getStringList("prefix")));
    private static ArrayList<String> spawnProtection = new ArrayList<>();
    private static ArrayList<String> outOfSpawnProtection = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Plugin: Ollsentials now enabled");
        getLogger().info("Plugin Version: " + getDescription().getVersion());
        getLogger().info("Last Updated: (19/08/19)");
        getLogger().info("Most Recent Update: OnEnable calls create group(s) if no groups are present.");
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups");
        if (!allGroups.contains("group_guest")) { //if no groups are set up, this is default value for users
            createGroup("admin", "&aADMIN");
            addPermission("admin", "chat.color");
            addPermission("admin", "admin.rights");
            addPermission("admin", "admin.groups");
            addPermission("admin", "admin.basic");
            addPermission("admin", "admin.sign");
            addPermission("admin", "admin.hammer");
            addPermission("admin", "admin.name");
            createGroup("member", "&7MEMBER");
            addPermission("member", "basic.home");
            addPermission("member", "basic.sign");
            createGroup("guest", "&6GUEST");
            addPermission("guest", "basic.home");
            addPermission("guest", "basic.sign");
            getLogger().info("All default groups now set.");
        } else {
            getLogger().info("All groups loaded.");
        }
        getData();
    }


    private void getData() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs(); //makes directory if none exists
            }
            File file = new File(getDataFolder(), "config.yml"); //sets file name and file folder
            if (!file.exists()) { //makes file if none exists
                getLogger().info("CONFIG NOT FOUND, CREATING A NEW CONFIG!");
                createConfig();
            } else {
                getLogger().info("CONFIG FOUND! Loading!");
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                    this.getConfig().load(reader);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    private void createConfig() {
        this.getConfig().createSection("prefix");
        List<String> prefix = this.getConfig().getStringList("prefix");
        prefix.add("SERVER");
        this.getConfig().set("prefix", prefix);
        this.getConfig().createSection("spawn");
        ConfigurationSection spawn = this.getConfig().getConfigurationSection("spawn");
        spawn.set("world", "defaultworld");
        spawn.set("x", 0);
        spawn.set("y", 0);
        spawn.set("z", 0);
        spawn.set("yaw", 0);
        spawn.set("pitch", 0);
        this.getConfig().createSection("users");
        this.getConfig().createSection("groups");

        getLogger().info("Config successfully set up!");
        saveConfig();
    }

    private void createUser(Player p) {
        ConfigurationSection allUsers = this.getConfig().getConfigurationSection("users");
        UUID id = p.getUniqueId();

        double x, y, z;
        x = 0;
        y = 0;
        z = 0;
        float yaw, pitch;
        yaw = p.getLocation().getYaw();
        pitch = p.getLocation().getPitch();

        String world = "null"; //Gets world name and sets to string to save properly

        List<String> userId = allUsers.getStringList("users");
        userId.add(id.toString());
        List<String> lastLogin = allUsers.getStringList("users");
        lastLogin.add(new Date().toString());
        List<String> homeworld = allUsers.getStringList("users");
        homeworld.add(world);
        List<Double> xhome = allUsers.getDoubleList("users");
        xhome.add(x);
        List<Double> yhome = allUsers.getDoubleList("users");
        yhome.add(y);
        List<Double> zhome = allUsers.getDoubleList("users");
        zhome.add(z);
        List<Float> yawHome = allUsers.getFloatList("users");
        yawHome.add(yaw);
        List<Float> pitchHome = allUsers.getFloatList("users");
        pitchHome.add(pitch);

        allUsers.set("user_" + id + ".id", id.toString());
        allUsers.set("user_" + id + ".userName", p.getName());
        allUsers.set("user_" + id + ".newName", p.getName());
        allUsers.set("user_" + id + ".lastLogin", lastLogin);
        allUsers.set("user_" + id + ".group", "guest");
        allUsers.set("user_" + id + ".homepos.world", world);
        allUsers.set("user_" + id + ".homepos.x", xhome);
        allUsers.set("user_" + id + ".homepos.y", yhome);
        allUsers.set("user_" + id + ".homepos.z", zhome);
        allUsers.set("user_" + id + ".homepos.yaw", yawHome);
        allUsers.set("user_" + id + ".homepos.pitch", pitchHome);


        getLogger().info("User: " + id + " has been created in the config.");

        saveConfig();
    }

    private void createGroup(String gName, String prefix) { // adds group for name inserted on cmd
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups");
        if (allGroups.contains("groups_" + gName)) {
            getLogger().info("already exists");
        } else {

            List<String> groupName = allGroups.getStringList("group_" + gName);
            groupName.add(gName);
            allGroups.set("group_" + gName + ".groupName", groupName);

            List<String> groupPrefix = allGroups.getStringList("group_" + gName);
            groupPrefix.add(prefix);
            allGroups.set("group_" + gName + ".groupPrefix", groupPrefix);

            List<String> groupPerms = allGroups.getStringList("group_" + gName);
            groupPerms.add("not-set");
            allGroups.set("group_" + gName + ".groupPermissions", groupPerms);

            getLogger().info("called create group with " + gName);
            saveConfig();
        }
    }

    private void addUserToGroup(String gName, Player id) {
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups");
        ConfigurationSection allUsers = this.getConfig().getConfigurationSection("users");

        UUID uuid = id.getUniqueId();
        List<String> groupUsers = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + gName).getStringList(".groupUsers"); // gets overall config section for group users
        if (!groupUsers.contains(String.valueOf(uuid))) {  // if the group users doesnt contain their user id,
            allUsers.set("user_" + uuid + ".group", gName);// sets our user's group to the groupnam// e inputted
            groupUsers.add(String.valueOf(uuid)); // adds to list
            allGroups.set("group_" + gName + ".groupUsers", groupUsers);
            getLogger().info("user: " + uuid + " added to group " + gName);
            saveConfig();
        } else {
            getLogger().info("user: " + uuid + " is already a member of group: " + gName + ", code: 5");
        }
    }

    private void removeUserFromGroup(String gName, Player id) {
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups");
        UUID uuid = id.getUniqueId();
        List<String> groupUsers = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + gName).getStringList(".groupUsers"); // gets overall config section for group users

        if (groupUsers.contains(String.valueOf(uuid))) {
            ConfigurationSection allUsers = this.getConfig().getConfigurationSection("users"); // selects our user
            allUsers.set("user_" + uuid + ".group", "guest");// sets our user's group to the groupname inputted
            groupUsers.remove(String.valueOf(uuid));
            allGroups.set("group_" + gName + ".groupUsers", groupUsers);
            getLogger().info("user: " + uuid + " removed from group: " + gName);
            saveConfig();
        }
    }

    private void addPermission(String gName, String permission) {
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups");
        List<String> groupPermissions = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + gName).getStringList(".groupPermissions"); // gets overall config section for group perms
        groupPermissions.add(permission);
        allGroups.set("group_" + gName + ".groupPermissions", groupPermissions);
        saveConfig();
        getLogger().info("set " + gName + " " + permission);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        ConfigurationSection users = this.getConfig().getConfigurationSection("users");

        if (!users.contains("user_" + id.toString())) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> createUser(p), 50L); // delay of 5s
        } else {
            String lastLogin = users.getString("user_" + p.getUniqueId().toString() + ".lastLogin");
            p.sendMessage("You last played: " + lastLogin);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + e.getPlayer().getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
        String selectedGroup = allGroups.getString(".groupPrefix").replace("[", "").replace("]", "");
        List<String> groupPermission = allGroups.getStringList(".groupPermissions");
        String prefix = ChatColor.translateAlternateColorCodes('&', selectedGroup);
        String message = ChatColor.translateAlternateColorCodes('&', e.getMessage());
        if (!user.getString(".newName").equals(e.getPlayer().getName())) {
            if (groupPermission.contains("chat.color")) {
                e.setFormat(ChatColor.GRAY + "<" + prefix + ChatColor.GRAY + "> " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', user.getString(".newName")) + ": " + ChatColor.GRAY + message);
            } else {
                e.setFormat(ChatColor.GRAY + "<" + prefix + ChatColor.GRAY + "> " + ChatColor.WHITE + user.getString(".newName") + ": " + ChatColor.GRAY + e.getMessage());
            }
        } else {
            if (groupPermission.contains("chat.color")) {
                e.setFormat(ChatColor.GRAY + "<" + prefix + ChatColor.GRAY + "> " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', user.getString(".userName")) + ": " + ChatColor.GRAY + message);
            } else {
                e.setFormat(ChatColor.GRAY + "<" + prefix + ChatColor.GRAY + "> " + ChatColor.WHITE + user.getString(".userName") + ": " + ChatColor.GRAY + e.getMessage());
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

    @EventHandler
    public void checkPlayerInSpawn(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getLocation().distanceSquared(player.getWorld().getSpawnLocation()) < Math.pow(getServer().getSpawnRadius(), 3)) {
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
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String name = sender.getName();
        Player player = this.getServer().getPlayer(name);

        ConfigurationSection user = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = this.getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
        List<String> selectedGroup = allGroups.getStringList(".groupPermissions");

        if (cmd.getName().equalsIgnoreCase("creategroup")) {
            if (selectedGroup.contains("admin.groups")) {
                if (args.length == 0) {
                    sender.sendMessage(prefix + "Syntax: /creategroup groupname groupprefix");
                } else {
                    getLogger().info(String.valueOf(allGroups.getConfigurationSection("group_" + userGroup + ".groupPermissions")));
                    createGroup(args[0], args[1]); // calls create group with args[0] as name and gets the user ID
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!"); // you dont have perms msg
            }
        }
        if (cmd.getName().equalsIgnoreCase("addusertogroup")) {
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
                    removeUserFromGroup(userGroup, player); // removes player from previous group
                    addUserToGroup(args[1], target); //sets them to the new group
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!"); // you dont have perms msg
            }
        }
        if (cmd.getName().equalsIgnoreCase("removeuserfromgroup")) {
            if (selectedGroup.contains("admin.groups")) {
                if (args.length == 0) {
                    sender.sendMessage(prefix + " Syntax: /removeuserfromgroup username groupname");
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (player == target) {
                        sender.sendMessage(prefix + " You cannot remove yourself from a group! (code: 6)");
                    } else {
                        removeUserFromGroup(args[1], target);
                    }
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!"); // you dont have perms msg
            }
        }
        if (cmd.getName().equalsIgnoreCase("addpermission")) {
            if (selectedGroup.contains("admin.groups")) {

                if (args.length == 0) {
                    sender.sendMessage(prefix + " Syntax: /addpermission groupname permission");
                } else {
                    addPermission(args[0], args[1]);
                }
            } else {
                sender.sendMessage(prefix + " You don't have permission for this!");
            }
        }
        if (cmd.getName().equalsIgnoreCase("setspawn")) {
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

        if (cmd.getName().equalsIgnoreCase("name")) {
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
                            target.sendMessage(prefix + " " + sender.getName() + " has set your name to: "+args[1]);
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
            ConfigurationSection users = this.getConfig().getConfigurationSection("spawn");
            if (users.getString("world").contains("defaultworld")) {
                sender.sendMessage(prefix + " Please use the command: /setspawn before trying to go to the spawn!");
            } else {
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
            if (selectedGroup.contains("basic.home")) {
                ConfigurationSection users = this.getConfig().getConfigurationSection("users").getConfigurationSection("user_" + player.getUniqueId().toString());
                if (users.getString("homepos.world").contains("null")) {
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

https://github.com/ollymonger/ollsentials.git
Updated plugin to 1.14.4 > OnEnable now calls all groups to be created > command /name now functional for admins and vips
