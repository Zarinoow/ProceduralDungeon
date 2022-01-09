package fr.foxelia.proceduraldungeon.gui;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.proceduraldungeon.utilities.rooms.Room;

public class GUI {

	private Inventory inventory;
	private DungeonManager dungeon;
	private Room room;
	private GUIType type;
	
	public GUI(Inventory inventory, DungeonManager dungeon, Room room) {
		this.inventory = inventory;
		this.dungeon = dungeon;
		this.room = room;
		
		if(inventory.getType().equals(InventoryType.ANVIL)) {
			this.type = GUIType.RENAMING;
		} else if(room == null) {
			this.type = GUIType.DUNGEON;
		} else if(dungeon != null & room != null) {
			this.type = GUIType.ROOM;
		}
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public DungeonManager getDungeon() {
		return this.dungeon;
	}
	
	public Room getRoom() {
		return this.room;
	}
	
	public GUIType getType() {
		return this.type;
	}
	
	
	
}
