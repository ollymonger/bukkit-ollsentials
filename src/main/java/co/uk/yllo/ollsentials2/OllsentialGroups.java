package co.uk.yllo.ollsentials2;

import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class OllsentialGroups implements Listener {
    FileConfiguration config = Main.getPlugin().getConfig();
    Server server = Main.getPlugin().getServer();
    Logger getLog = Main.getPlugin().getLogger();

    public static OllsentialGroups instance;

    public void createGroup(String gName, String prefix) { // adds group for name inserted on cmd
        ConfigurationSection allGroups = config.getConfigurationSection("groups");
        if (allGroups.contains("groups_" + gName)) {
            getLog.info("already exists");
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

            getLog.info("called create group with " + gName);
            Main.getPlugin().saveConfig();
        }
    }

    public void addPermission(String gName, String permission) {
        ConfigurationSection allGroups = config.getConfigurationSection("groups");
        List<String> groupPermissions = config.getConfigurationSection("groups").getConfigurationSection("group_" + gName).getStringList(".groupPermissions"); // gets overall config section for group perms
        groupPermissions.add(permission);
        allGroups.set("group_" + gName + ".groupPermissions", groupPermissions);
        Main.getPlugin().saveConfig();
        getLog.info("set " + gName + " " + permission);
    }

    public void addUserToGroup(String gName, Player id) {
        ConfigurationSection allGroups = Main.getPlugin().getConfig().getConfigurationSection("groups");
        ConfigurationSection allUsers = Main.getPlugin().getConfig().getConfigurationSection("users");

        UUID uuid = id.getUniqueId();
        List<String> groupUsers = Main.getPlugin().getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + gName).getStringList(".groupUsers"); // gets overall config section for group users
        if (!groupUsers.contains(String.valueOf(uuid))) {  // if the group users doesnt contain their user id,
            allUsers.set("user_" + uuid + ".group", gName);// sets our user's group to the groupnam// e inputted
            groupUsers.add(String.valueOf(uuid)); // adds to list
            allGroups.set("group_" + gName + ".groupUsers", groupUsers);
            getLog.info("user: " + uuid + " added to group " + gName);
            Main.getPlugin().saveConfig();
        } else {
            getLog.info("user: " + uuid + " is already a member of group: " + gName + ", code: 5");
        }
    }

    public void removeUserFromGroup(String gName, Player id) {
        ConfigurationSection allGroups = Main.getPlugin().getConfig().getConfigurationSection("groups");
        UUID uuid = id.getUniqueId();
        List<String> groupUsers = Main.getPlugin().getConfig().getConfigurationSection("groups").getConfigurationSection("group_" + gName).getStringList(".groupUsers"); // gets overall config section for group users

        if (groupUsers.contains(String.valueOf(uuid))) {
            ConfigurationSection allUsers = Main.getPlugin().getConfig().getConfigurationSection("users"); // selects our user
            allUsers.set("user_" + uuid + ".group", "guest");// sets our user's group to the groupname inputted
            groupUsers.remove(String.valueOf(uuid));
            allGroups.set("group_" + gName + ".groupUsers", groupUsers);
            getLog.info("user: " + uuid + " removed from group: " + gName);
            Main.getPlugin().saveConfig();
        }
    }

}
