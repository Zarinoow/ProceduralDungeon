package fr.foxelia.proceduraldungeon;

import org.bukkit.plugin.java.JavaPlugin;

import fr.foxelia.proceduraldungeon.commands.DungeonCommand;
import fr.foxelia.proceduraldungeon.commands.DungeonCommandCompleter;

public class Main extends JavaPlugin {
	
	/*
	 * Todo list
	 * 
	 * 
	 */
	
	@Override
	public void onEnable() {
		/*
		 * Init message
		 */
		System.out.println("§2======");
		System.out.println("§2ProceduralDungeon");
		System.out.println("§2Initializing...");
		System.out.println("");
		System.out.println("§2Version 1.0.0");
//		System.out.println("§c/!\\ Attention Pré-release /!\\");
//		System.out.println("§c/!\\ Attention ReleaseCandidate /!\\");
//		System.out.println("§cCe build n'est pas terminée");
//		System.out.println("§cIl peut donc contenir des bugs");
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
		
		getCommand("dungeon").setExecutor(new DungeonCommand(this));
		getCommand("dungeon").setTabCompleter(new DungeonCommandCompleter());
		
	}
	
	@Override
	public void onDisable() {
		
	}
}
