package fr.foxelia.proceduraldungeon.utilities;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import fr.foxelia.proceduraldungeon.Main;

public class DungeonConfig {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	private FileConfiguration config;
	private File configFile;
	
	public DungeonConfig(String dungeon) {
		this.configFile = new File(plugin.getDataFolder(), "dungeons/" + dungeon + "/config.yml");
		this.setupConfig();
	}
	
	private void setupConfig() {
		if(!(configFile.getParentFile().exists())) {
			try {
				configFile.getParentFile().mkdirs();
			} catch (Exception e) {
				Bukkit.getLogger().log(Level.SEVERE, "Cannot create dungeon folder for " + configFile.getName() + ".");
			}
		}	
	}
	
	public FileConfiguration getConfig() {
		return config;
	}

}
