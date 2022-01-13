package fr.foxelia.proceduraldungeon.utilities.rooms;

import java.io.File;

public class Room {
	
	private File file;
	private Coordinate exit;
	private int spawnrate;
	
	public Room(File file, Coordinate exit) {
		this.file = file;
		this.exit = exit;
		this.setSpawnrate(100);
	}

	public File getFile() {
		return file;
	}

	public Coordinate getExit() {
		return exit;
	}

	public int getSpawnrate() {
		return spawnrate;
	}
	
	public void addSpawnrate(int spawnrate) {
		if((this.spawnrate + spawnrate) > 100) {
			this.spawnrate = 100;
		} else if((this.spawnrate + spawnrate) < 0) {
			this.spawnrate = 0;
		} else this.spawnrate += spawnrate;
	}

	public void setSpawnrate(int spawnrate) {
		if(spawnrate > 100) {
			this.spawnrate = 100;
		} else if(spawnrate < 0) {
			this.spawnrate = 0;
		} else this.spawnrate = spawnrate;
	}

}
