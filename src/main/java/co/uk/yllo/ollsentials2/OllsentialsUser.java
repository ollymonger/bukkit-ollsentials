package co.uk.yllo.ollsentials2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class OllsentialsUser implements Listener {
    private Main plugin;
    public static OllsentialsUser instance;
    Server server = Main.getPlugin().getServer();
    Logger getLog = Main.getPlugin().getLogger();

    public OllsentialsUser(Main plugin){
        this.plugin=plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        ConfigurationSection users = Main.getPlugin().getConfig().getConfigurationSection("users");
        if (!users.contains("user_" + id.toString())) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> createUser(p), 50L); // delay of 5s
        } else {
            String lastLogin = users.getString("user_" + p.getUniqueId().toString() + ".lastLogin");
            p.sendMessage("You last played: " + lastLogin);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        ConfigurationSection user = Main.getPlugin().getConfig().getConfigurationSection("users").getConfigurationSection("user_" + e.getPlayer().getUniqueId().toString());
        String userGroup = user.getString(".group");
        ConfigurationSection allGroups = Main.getPlugin().getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + userGroup);
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


    public void createUser(Player p) {
        ConfigurationSection allUsers = plugin.getConfig().getConfigurationSection("users");
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
        allUsers.set("user_" + id + ".balance", 0);
        allUsers.set("user_" + id + ".lastLogin", lastLogin);
        allUsers.set("user_" + id + ".group", "guest");
        allUsers.set("user_" + id + ".homepos.world", world);
        allUsers.set("user_" + id + ".homepos.x", xhome);
        allUsers.set("user_" + id + ".homepos.y", yhome);
        allUsers.set("user_" + id + ".homepos.z", zhome);
        allUsers.set("user_" + id + ".homepos.yaw", yawHome);
        allUsers.set("user_" + id + ".homepos.pitch", pitchHome);


        getLog.info("User: " + id + " has been created in the config.");

        Main.getPlugin().saveConfig();
    }
}
