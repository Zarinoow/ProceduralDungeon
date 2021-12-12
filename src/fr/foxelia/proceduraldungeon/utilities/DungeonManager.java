package fr.foxelia.proceduraldungeon.utilities;

import java.io.File;

import javax.management.InstanceAlreadyExistsException;

import org.bukkit.configuration.file.FileConfiguration;

import fr.foxelia.proceduraldungeon.Main;

public class DungeonManager {
	
	private String dungeonName;
	private File dungeonFolder;
	private DungeonConfig dungeonConfiguration;
	
	public DungeonManager(String folder) {
		this.dungeonFolder = new File(Main.getProceduralDungeon().getDataFolder(), "dungeons/" + folder);
	}
	
	public void createDungeon(String dungeonName) throws InstanceAlreadyExistsException, NullPointerException {
		this.dungeonName = dungeonName;
		if(this.exist()) throw new InstanceAlreadyExistsException("An instance of this dungeon (" + Main.getDungeons().get(dungeonFolder.getName()).getName() + ") already exists!");
		if(dungeonFolder == null) return;
		if(!dungeonFolder.exists()) dungeonFolder.mkdirs();
		this.dungeonConfiguration = new DungeonConfig(dungeonFolder.getName());
		if(getConfig() == null) throw new NullPointerException("An error occurred while generating the configuration file!");
		getConfig().set("name", dungeonName);
		Main.getDungeons().put(dungeonName.toLowerCase(), this);
	}
	
	public boolean restoreDungeon() {
		
		return false;
	}
	
	/*
	 * Getters 
	 */
	
	public File getDungeonFolder() {
		return dungeonFolder;
	}
	
	public FileConfiguration getConfig() {
		return dungeonConfiguration.getConfig();
	}
	
	public DungeonConfig getDungeonConfig() {
		return dungeonConfiguration;
	}
	
	public String getName() {
		return dungeonName;
	}
	
	/*
	 * Conditionals
	 */
	
	public boolean exist() {
		if(Main.getDungeons().containsKey(dungeonName.toLowerCase())) return true;
		return false;
	}
}
