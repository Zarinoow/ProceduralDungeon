package fr.foxelia.proceduraldungeon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import fr.foxelia.proceduraldungeon.commands.DungeonCommand;
import fr.foxelia.proceduraldungeon.commands.DungeonCommandCompleter;
import fr.foxelia.proceduraldungeon.utilities.ActionType;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import net.md_5.bungee.api.ChatColor;
import oshi.util.tuples.Pair;

public class Main extends JavaPlugin {
	
	/*
	 * Todo list
	 * 
	 * % chance d'une salle
	 * Générée le donjon
	 * Crée une room
	 * 
	 */
	private static Main mainclass;
	
	private static Map<String, DungeonManager> dungeons = new HashMap<>();
	private static Map<CommandSender, Pair<ActionType, String>> confirmation = new HashMap<>();
	
	@Override
	public void onEnable() {
		Main.mainclass = this;
		/*
		 * Init message
		 */
		
		getLogger().log(Level.INFO, ChatColor.DARK_GREEN + "======");
		getLogger().log(Level.INFO, ChatColor.DARK_GREEN + "ProceduralDungeon");
		getLogger().log(Level.INFO, ChatColor.DARK_GREEN + "Initializing...");
		getLogger().log(Level.INFO, "");
		getLogger().log(Level.INFO, ChatColor.DARK_GREEN + "Version 1.0.0");
		getLogger().log(Level.INFO, ChatColor.RED + "/!\\ Attention Pré-release /!\\");
//		getLogger().log(Level.INFO, ChatColor.RED + "/!\\ Attention ReleaseCandidate /!\\");
		getLogger().log(Level.INFO, ChatColor.RED + "Ce build n'est pas terminée");
		getLogger().log(Level.INFO, ChatColor.RED + "Il peut donc contenir des bugs");
		getLogger().log(Level.INFO, "");
		getLogger().log(Level.INFO, ChatColor.DARK_GREEN + "By Foxelia Server");
		getLogger().log(Level.INFO, ChatColor.DARK_GREEN + "======");
		
		/*
		 * Load Config
		 */
		saveDefaultConfig();
		
		/*
		 * Register Class (Listeners, Command & Code)
		 */
		restoreDungeons();
		
		getCommand("dungeon").setExecutor(new DungeonCommand());
		getCommand("dungeon").setTabCompleter(new DungeonCommandCompleter());
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	private void restoreDungeons() {
		File memoryFile = new File(Main.getProceduralDungeon().getDataFolder(), "dungeonslist.memory");
		if(!memoryFile.exists()) return;
		getLogger().log(Level.INFO, ChatColor.GREEN + "Restoring the dungeons sessions...");
		Long delay = System.currentTimeMillis();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(memoryFile)));
			String line = reader.readLine();
			
			while(line != null) {
				DungeonManager dm = new DungeonManager(line);
				if(dm.getDungeonFolder().exists()) {
					dm.restoreDungeon();
					if(Main.getDungeons().containsKey(dm.getName().toLowerCase())) {
						getLogger().log(Level.WARNING, "Cannot load " + line + " dungeon folder! Another instance with the same name is already running. This dungeon will be automagically removed from the config.");
					} else Main.getDungeons().put(dm.getName().toLowerCase(), dm);
				} else getLogger().log(Level.WARNING, "The dungeon folder " + line + " doesn't exists! This dungeon will be automagically removed from the config.");
				line = reader.readLine();
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		Main.saveDungeons();
		getLogger().log(Level.INFO, ChatColor.GREEN + "Dungeons restored! Took " + String.valueOf(System.currentTimeMillis() - delay) + "ms.");
	}
	
	/*
	 * Static actions
	 */
	
	public static void sendInternalError(CommandSender receiver) {
		receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', getMain().getConfig().getString("messages.errors.internalerror")));
	}
	
	public static void saveDungeons() {
		File memoryFile = new File(getProceduralDungeon().getDataFolder(), "dungeonslist.memory");
		if(!memoryFile.getParentFile().exists()) memoryFile.getParentFile().mkdirs();
		if(memoryFile.exists()) memoryFile.delete();
		try {
			memoryFile.createNewFile();
			
			FileWriter writer = new FileWriter(memoryFile);
			BufferedWriter bw = new BufferedWriter(writer);
			
			for(DungeonManager dm : getDungeons().values()) {
				bw.write(dm.getDungeonFolder().getName());
				bw.newLine();
			}
			
			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/*
	 * Statics Getters
	 */
	
	public static Main getMain() {
		return mainclass;
	}
	
	public static JavaPlugin getProceduralDungeon() {
		return getPlugin(Main.class);
	}
	
	public static Map<String, DungeonManager> getDungeons() {
		return dungeons;
	}
	
	public static Map<CommandSender, Pair<ActionType, String>> getConfirmation() {
		return confirmation;
	}
	
	/*
	 * Config getters
	 */
	public static String getErrorMessage(String message) {
		return getMain().getConfig().getString("messages.errors." + message);
	}
	
	public static String getSuccessMessage(String message) {
		return getMain().getConfig().getString("messages.success." + message);
	}
	
	public static String getTaskMessage(String message) {
		return getMain().getConfig().getString("messages.tasks." + message);
	}
	
	public static String getOthersMessage(String message) {
		return getMain().getConfig().getString("messages.others." + message);
	}
}
