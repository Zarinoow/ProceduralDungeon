package fr.foxelia.proceduraldungeon.utilities.rooms;

import java.io.File;

public class Room {
	
	private File file;
	private Coordinate exit;
	
	public Room(File file, Coordinate exit) {
		this.file = file;
		this.exit = exit;
	}

	public File getFile() {
		return file;
	}

	public Coordinate getExit() {
		return exit;
	}

}
