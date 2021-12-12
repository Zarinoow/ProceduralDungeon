package fr.foxelia.proceduraldungeon;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import fr.foxelia.proceduraldungeon.commands.DungeonCommand;
import fr.foxelia.proceduraldungeon.commands.DungeonCommandCompleter;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	
	/*
	 * Todo list
	 * 
	 * % chance d'une salle
	 * Générée le donjon
	 * Crée une room
	 * Reload la config
	 * 
	 */
	private static Main mainclass;
	
	private static Map<String, DungeonManager> dungeons = new HashMap<>(); 
	
	@Override
	public void onEnable() {
		Main.mainclass = this;
		/*
		 * Init message
		 */
		System.out.println("§2======");
		System.out.println("§2ProceduralDungeon");
		System.out.println("§2Initializing...");
		System.out.println("");
		System.out.println("§2Version 1.0.0");
		System.out.println("§c/!\\ Attention Pré-release /!\\");
//		System.out.println("§c/!\\ Attention ReleaseCandidate /!\\");
		System.out.println("§cCe build n'est pas terminée");
		System.out.println("§cIl peut donc contenir des bugs");
		System.out.println("");
		System.out.println("§2By Foxelia Server");
		System.out.println("§2======");
		
		/*
		 * Load Config
		 */
		saveDefaultConfig();
		
		/*
		 * Register Class (Listeners, Command & Code)
		 */
		
		getCommand("dungeon").setExecutor(new DungeonCommand());
		getCommand("dungeon").setTabCompleter(new DungeonCommandCompleter());
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	/*
	 * Statics Methods
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
	
	public static void sendInternalError(CommandSender receiver) {
		receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', getMain().getConfig().getString("messages.errors.internalerror")));
	}
	
	public static String getErrorMessage(String message) {
		return getMain().getConfig().getString("messages.errors." + message);
	}
	
	public static String getTaskMessage(String message) {
		return getMain().getConfig().getString("messages.tasks." + message);
	}
}
