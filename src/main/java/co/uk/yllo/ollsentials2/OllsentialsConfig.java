package co.uk.yllo.ollsentials2;

import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class OllsentialsConfig implements Listener {

    FileConfiguration config = Main.getPlugin().getConfig();
    OllsentialGroups groups = new OllsentialGroups();
    Server server = Main.getPlugin().getServer();
    Logger getLog = Main.getPlugin().getLogger();

    public OllsentialsConfig(){
        File Config = new File("plugins/ollsentials2", "config.yml");
        FileConfiguration Cfg = YamlConfiguration.loadConfiguration(Config);
        try {
            if(!Config.exists()) {
                Cfg.save(Config);
                updateConfig();
            } else {
                getLog.info("Config already exists!");
            }
        } catch(IOException e) {
            // Handle any IO exception here
            e.printStackTrace();
        }
    }

    public void updateConfig() {
        config.createSection("prefix");
        List<String> prefix = config.getStringList("prefix");
        prefix.add("SERVER");
        config.set("prefix", prefix);
        config.createSection("spawn");
        ConfigurationSection spawn = config.getConfigurationSection("spawn");
        spawn.set("world", "defaultworld");
        spawn.set("x", 0);
        spawn.set("y", 0);
        spawn.set("z", 0);
        spawn.set("yaw", 0);
        spawn.set("pitch", 0);
        config.createSection("warps");
        config.createSection("users");
        config.createSection("groups");

        List<String> allGroups = config.getStringList("groups");
        if (allGroups.isEmpty()) { //if no groups are set up, this is default value for users
            groups.createGroup("admin", "&aADMIN");
            groups.addPermission("admin", "chat.color");
            groups.addPermission("admin", "admin.rights");
            groups.addPermission("admin", "admin.groups");
            groups.addPermission("admin", "admin.basic");
            groups.addPermission("admin", "admin.sign");
            groups.addPermission("admin", "admin.hammer");
            groups.addPermission("admin", "admin.name");
            groups.addPermission("admin", "basic.sign");
            groups.createGroup("member", "&7MEMBER");
            groups.addPermission("member", "basic.home");
            groups.addPermission("member", "basic.sign");
            groups.createGroup("guest", "&6GUEST");
            groups.addPermission("guest", "basic.home");
            groups.addPermission("guest", "basic.sign");
            getLog.info("All default groups now set.");
        } else {
            getLog.info("All groups loaded.");
        }

        getLog.info("Config successfully set up!");
        Main.getPlugin().saveConfig();
    }

}
