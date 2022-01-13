package fr.foxelia.proceduraldungeon.utilities.rooms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RoomsManager {
	
	private File folder;
	File propertiesFile;
	private Properties properties = new Properties();
	private List<Room> rooms = new ArrayList<>();
	
	public RoomsManager(File root) {
		folder = new File(root, "rooms");
		if(!folder.exists()) folder.mkdirs();			
		propertiesFile = new File(folder, "rooms.properties");
		try {
			if(!propertiesFile.exists()) propertiesFile.createNewFile();
			properties.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		restoreRooms();
	}
	
	private void restoreRooms() {
		for(String key : properties.stringPropertyNames()) {
			File keyFile = new File(folder, key + ".dungeon");
			if(!keyFile.exists()) continue;
			String[] values = (properties.getProperty(key).split(","));
			if(values.length < 4) continue;
			for(String str : values) {
				if(!isNumeric(str)) continue;
			}			
			Room dungeonRoom = new Room(keyFile, new Coordinate(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2])));
			dungeonRoom.setSpawnrate(Integer.parseInt(values[3]));
			this.getRooms().add(dungeonRoom);
		}
	}
	
	public File getFolder() {
		return this.folder;
	}

	public List<Room> getRooms() {
		return rooms;
	}
	
	public void reloadRooms() {
		this.restoreRooms();
	}
	
	public void addRoom(Room room) {
		this.getRooms().add(room);
		this.saveRooms();
	}
	
	public void saveRooms() {
		if(propertiesFile.exists()) propertiesFile.delete();
		try {
			propertiesFile.createNewFile();
			properties.clear();
			for(Room room : rooms) properties.setProperty(room.getFile().getName().replace(".dungeon", ""), room.getExit().getX() + "," + room.getExit().getY() + "," + room.getExit().getZ() + "," + room.getSpawnrate());
			properties.store(new FileOutputStream(propertiesFile), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        Double.parseDouble(strNum);
	    } catch (NumberFormatException e) {
	        return false;
	    }
	    return true;
	}

}
