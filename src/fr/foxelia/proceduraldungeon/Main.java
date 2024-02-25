package fr.foxelia.proceduraldungeon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import fr.foxelia.proceduraldungeon.gui.GUIManager;
import fr.foxelia.proceduraldungeon.utilities.*;
import fr.foxelia.tools.minecraft.ui.color.ColoredConsole;
import fr.foxelia.tools.minecraft.ui.gui.GUI;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.foxelia.proceduraldungeon.commands.DungeonCommand;
import fr.foxelia.proceduraldungeon.commands.DungeonCommandCompleter;
import fr.foxelia.proceduraldungeon.gui.GUIListeners;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	
	private static Main mainclass;
	
	private static Map<String, DungeonManager> dungeons = new HashMap<>();
	private static Map<CommandSender, Pair<ActionType, String>> confirmation = new HashMap<>();
	private static Map<HumanEntity, DungeonManager> renaming = new HashMap<>();
	private static Map<Player, Location> exitLocation = new HashMap<>();
	
	@Override
	public void onEnable() {
		Main.mainclass = this;
		/*
		 * Init message
		 */

		getLogger().log(Level.INFO, ColoredConsole.DARK_GREEN + "======" + ColoredConsole.RESET);
		getLogger().log(Level.INFO, ColoredConsole.DARK_GREEN + getDescription().getName() + ColoredConsole.RESET);
		getLogger().log(Level.INFO, ColoredConsole.DARK_GREEN + "Initializing..." + ColoredConsole.RESET);
		getLogger().log(Level.INFO, "");
		getLogger().log(Level.INFO, ColoredConsole.DARK_GREEN + "Version " + getDescription().getVersion() + ColoredConsole.RESET);
		getLogger().log(Level.INFO, "");
		getLogger().log(Level.INFO, ColoredConsole.GOLD + "Designed for the Foxelia organization" + ColoredConsole.RESET);
		getLogger().log(Level.INFO, ColoredConsole.YELLOW + "By " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + ColoredConsole.RESET);
		getLogger().log(Level.INFO, ColoredConsole.DARK_GREEN + "======" + ColoredConsole.RESET);
		
		/*
		 * Load Config
		 */
		saveDefaultConfig();
		GUI.init(this);
		
		/*
		 * Register Class (Listeners, Command & Code)
		 */
		restoreDungeons();
		
		getCommand("dungeon").setExecutor(new DungeonCommand());
		getCommand("dungeon").setTabCompleter(new DungeonCommandCompleter());
		getServer().getPluginManager().registerEvents(new GUIListeners(), this);
		
		BStats metrics = new BStats(this, 13962);
		
		metrics.addCustomChart(new BStats.SimplePie("dungeon_number", () -> String.valueOf(getDungeons().size())));
		metrics.addCustomChart(new BStats.SimplePie("average_room_count", () -> String.valueOf(calcRoomAverage())));
	}
	
	private int calcRoomAverage() {
		int roomaverage = 0;
		for(DungeonManager dm : getDungeons().values()) {
			roomaverage += dm.getDungeonRooms().getRooms().size();
		}
		roomaverage /= getDungeons().size();
		
		return roomaverage;
	}
	
	@Override
	public void onDisable() {
		getDungeons().values().forEach(dungeon -> GUIManager.closeAllGUIOf(dungeon));
	}
	
	private void restoreDungeons() {
		File memoryFile = new File(Main.getProceduralDungeon().getDataFolder(), "dungeonslist.memory");
		if(!memoryFile.exists()) return;
		getLogger().log(Level.INFO, ColoredConsole.GREEN + "Restoring the dungeons sessions..." + ColoredConsole.RESET);
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
		getLogger().log(Level.INFO, ColoredConsole.GREEN + "Dungeons restored! Took " + ColoredConsole.YELLOW + (System.currentTimeMillis() - delay) + "ms" + ColoredConsole.GREEN + "." + ColoredConsole.RESET);
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
	
	public static Map<HumanEntity, DungeonManager> getRenaming(){
		return renaming;
	}
	
	public static Map<Player, Location> getExitLocation() {
		return exitLocation;
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
	
	public static String getHelpMessage(String message) {
		return getMain().getConfig().getString("messages.help." + message);
	}
	
	public static String getGUIString(String string) {
		return getMain().getConfig().getString("gui." + string);		
	}
	
	public static List<String> getGuiStringList(String list) {
		return getMain().getConfig().getStringList("gui." + list);
	}
}
