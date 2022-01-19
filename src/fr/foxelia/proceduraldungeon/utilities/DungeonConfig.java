package fr.foxelia.proceduraldungeon.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.foxelia.proceduraldungeon.Main;

public class DungeonConfig {
	
	private JavaPlugin plugin = Main.getProceduralDungeon();
	
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
				return;
			}
		}
		if(!configFile.exists()) {
			try {
				InputStream inputStream = this.plugin.getResource("defaultdungeon.yml");
				config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				config.set("name", "dungeon-" + System.currentTimeMillis());
				config.save(configFile);
			} catch(Exception e) {
				Bukkit.getLogger().log(Level.SEVERE, "Cannot generate default values for the " + configFile.getParentFile().getName() + " dungeon!");
				e.printStackTrace();
				return;
			}
		} else {
			config = YamlConfiguration.loadConfiguration(configFile);
		}	
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public void saveConfig() {
		if(config != null && configFile != null)
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void reloadConfig() {
		if(configFile.exists()) config = YamlConfiguration.loadConfiguration(configFile);
	}

}
