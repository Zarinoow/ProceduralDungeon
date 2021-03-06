package fr.foxelia.proceduraldungeon.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import javax.management.InstanceAlreadyExistsException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.rooms.Room;
import fr.foxelia.proceduraldungeon.utilities.rooms.RoomsManager;

public class DungeonManager {
	
	private String dungeonName;
	private File dungeonFolder;
	private RoomsManager dungeonRooms;
	private DungeonConfig dungeonConfiguration;
	
	public DungeonManager(String folder) {
		this.dungeonFolder = new File(Main.getProceduralDungeon().getDataFolder(), "dungeons/" + folder);
	}
	
	public void createDungeon(String dungeonName) throws InstanceAlreadyExistsException, NullPointerException {
		this.dungeonName = dungeonName;
		DungeonManager checker = this.checkExists();
		if(checker != null) throw new InstanceAlreadyExistsException("An instance of " + checker.getName() + " dungeon already exists! Folder: " + checker.getDungeonFolder().getName());
		if(dungeonFolder == null) return;
		if(!dungeonFolder.exists()) dungeonFolder.mkdirs();
		this.dungeonConfiguration = new DungeonConfig(dungeonFolder.getName());
		if(getConfig() == null) throw new NullPointerException("An error occurred while generating the configuration file!");
		getConfig().set("name", dungeonName);
		getDungeonConfig().saveConfig();
		this.dungeonRooms = new RoomsManager(dungeonFolder);
		Main.getDungeons().put(dungeonName.toLowerCase(), this);
		Main.saveDungeons();
	}
	
	public void restoreDungeon() {
		if(!dungeonFolder.exists())	throw new NullPointerException("The dungeon " + dungeonFolder + " doesn't exists!");
		if(this.dungeonConfiguration == null) this.dungeonConfiguration = new DungeonConfig(dungeonFolder.getName());
		if(this.dungeonConfiguration == null) throw new NullPointerException("The configuration of the dungeon " + dungeonFolder.getName() + " doesn't exists!");
		if(this.dungeonConfiguration.getConfig().get("name") == null) throw new NullPointerException("The config is invalid!");
		this.dungeonName = dungeonConfiguration.getConfig().getString("name");
		this.dungeonRooms = new RoomsManager(dungeonFolder);
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
	
	public RoomsManager getDungeonRooms() {
		return dungeonRooms;
	}
	
	public DungeonManager checkExists() {
		for(DungeonManager dm : Main.getDungeons().values()) {
			if(dm.getDungeonFolder().getName().equals(this.getDungeonFolder().getName())) return dm;
		}
		return null;
	}
	
	/*
	 * Setters
	 */
	
	public void setName(String newName) {
		this.dungeonName = newName;
		getConfig().set("name", newName);
		getDungeonConfig().saveConfig();
	}
	
	/*
	 * Conditionals
	 */
	
	public boolean exist() {
		for(DungeonManager dm : Main.getDungeons().values()) {
			if(dm.getDungeonFolder().getName().equals(this.getDungeonFolder().getName())) return true;
		}
		return false;
	}
	
	/*
	 * Statics
	 */
	
	public static boolean generateDungeon(DungeonManager dungeon, Location spawnLoc) {
		spawnLoc.add(0, 0, -1);
		Random rand = new Random();
		List<Room> used = new ArrayList<>();
		List<Room> masked = new ArrayList<>();
		
		for(int i = 0; i < dungeon.getConfig().getInt("roomcount"); i++) {
			boolean roomfound = false;
			Room roomchoosen = null;
			do {
				if(dungeon.getDungeonRooms().getRooms().size() <= 0 || masked.size() >= dungeon.getDungeonRooms().getRooms().size()) {
					Main.getMain().getLogger().log(Level.SEVERE, "Unable to generate " + dungeon.getName() + ". No generatable rooms left. Aborting.");
					return false;
				}
				if(used.size() >= dungeon.getDungeonRooms().getRooms().size()) used.clear();
				int choose = rand.nextInt(0, dungeon.getDungeonRooms().getRooms().size());
				roomchoosen = dungeon.getDungeonRooms().getRooms().get(choose);
				if(!dungeon.getConfig().getBoolean("roomrecyling") && used.contains(roomchoosen)) continue;
				if(!roomchoosen.getFile().exists()) {
					Main.getMain().getLogger().log(Level.WARNING, "The file \"" + roomchoosen.getFile().getName() + "\" was previously incorrectly deleted. Please, prefer supression through /dungeon edit " + dungeon.getName() + ". The file will be removed from memory automagically.");
					dungeon.getDungeonRooms().saveRooms();
					dungeon.getDungeonRooms().getRooms().remove(roomchoosen);
					continue;
				}
				if(roomchoosen.getSpawnrate() <= 0) {
					used.add(roomchoosen);
					if(!masked.contains(roomchoosen)) masked.add(roomchoosen);
				}
				choose = rand.nextInt(1, 101);
				if(roomchoosen.getSpawnrate() >= choose) {
					used.add(roomchoosen);
					roomfound = true;
				}
			} while(!roomfound);	
			
			WorldEditSchematic schematic = new WorldEditSchematic();
			schematic.loadSchematic(roomchoosen.getFile());
			schematic.pasteSchematic(spawnLoc);
			spawnLoc.add(roomchoosen.getExit().getX(), roomchoosen.getExit().getY(), roomchoosen.getExit().getZ() - 1);
		}
		return true;
	}
}
