package fr.foxelia.proceduraldungeon.gui;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.proceduraldungeon.utilities.rooms.Room;
import fr.foxelia.tools.minecraft.ui.gui.GUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIManager {

	private static final Map<DungeonManager, List<DungeonGUI>> dungeonGUIs = new HashMap<>();
	private static final Map<Room, RoomGUI> roomGUIs = new HashMap<>();
	private static final Map<Room, RoomDeleteGUI> roomDeleteGUIs = new HashMap<>();

	/*
	 * Security methods
	 */

	/**
	 * Check if player can interact with the GUI and send a message if not
	 * @param player Player to check
	 * @return true if player has permission to edit
	 */
	public static boolean checkPermissionToEdit(HumanEntity player) {
		boolean hasPermission = player.hasPermission("proceduraldungeon.admin.edit");
		if(!hasPermission) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission").replace("%permission%", "proceduraldungeon.admin.edit")));
		}
		return hasPermission;
	}

	/**
	 * Check if the dungeon still exists and close the inventory if not
	 * @param gui GUI to check
	 * @param dungeon Dungeon to check
	 * @return true if dungeon exists
	 */
	public static boolean checkDungeonExists(GUI gui, DungeonManager dungeon) {
		boolean exists = dungeon.getDungeonFolder().exists();
		if(!exists) {
			gui.closeInventory();
		}
		return exists;
	}

	/**
	 * Check if the room still exists and close the inventory if not
	 * @param gui GUI to check
	 * @param room Room to check
	 * @return true if room exists
	 */
	public static boolean checkRoomExists(GUI gui, Room room) {
		boolean exists = room.getFile().exists();
		if(!exists) {
			gui.closeInventory();
		}
		return exists;
	}

	/*
	 * GUI Opening
	 */

	public static DungeonGUI openDungeonGUI(DungeonManager dungeon, HumanEntity player) {
		DungeonGUI gui = new DungeonGUI(dungeon);
		gui.openInventory(player);
		addDungeonGUI(dungeon, gui);
		return gui;
	}

	public static void openRoomGUI(DungeonManager dungeon, Room room, HumanEntity player) {
		if(roomGUIs.containsKey(room)) {
			player.openInventory(roomGUIs.get(room).getInventory());
		} else {
			RoomGUI gui = new RoomGUI(dungeon, room);
			gui.openInventory(player);
			roomGUIs.put(room, gui);
		}
	}

	public static void openRoomDeleteGUI(DungeonManager dungeon, Room room, HumanEntity player) {
		if(roomDeleteGUIs.containsKey(room)) {
			player.openInventory(roomDeleteGUIs.get(room).getInventory());
		} else {
			RoomDeleteGUI gui = new RoomDeleteGUI(dungeon, room);
			gui.openInventory(player);
			roomDeleteGUIs.put(room, gui);
		}
	}

	/*
	 * GUI Closing
	 */

	public static List<HumanEntity> closeAllRoomGUIOf(Room room) {
		List<HumanEntity> closed = new ArrayList<>();
		if(roomGUIs.containsKey(room)) {
			RoomGUI gui = roomGUIs.get(room);
			closed.addAll(gui.getInventory().getViewers());
			gui.closeInventory();
		}
		if(roomDeleteGUIs.containsKey(room)) {
			RoomDeleteGUI gui = roomDeleteGUIs.get(room);
			closed.addAll(gui.getInventory().getViewers());
			gui.closeInventory();
		}

		return closed;
	}

	public static List<HumanEntity> closeAllGUIOf(DungeonManager dungeon) {
		List<HumanEntity> closed = new ArrayList<>();
		for(Room room : dungeon.getDungeonRooms().getRooms()) {
			closed.addAll(closeAllRoomGUIOf(room));
		}
		if(dungeonGUIs.containsKey(dungeon)) {
			for(DungeonGUI gui : new ArrayList<>(dungeonGUIs.get(dungeon))) {
				closed.addAll(gui.getInventory().getViewers());
				gui.closeInventory();
			}
		}
		return closed;
	}

	/*
	 * GUI Updating
	 */

	public static void reopenDungeonGUI(DungeonManager dungeon) {
		Map<HumanEntity, Integer> pages = new HashMap<>();
		for(DungeonGUI gui : dungeonGUIs.get(dungeon)) {
			for(HumanEntity viewer : gui.getInventory().getViewers()) {
				pages.put(viewer, gui.getCurrentPage());
			}
			gui.closeInventory();
		}
		for(Map.Entry<HumanEntity, Integer> entry : pages.entrySet()) {
			DungeonGUI gui = openDungeonGUI(dungeon, entry.getKey());
			gui.goToPage(entry.getValue());
		}
	}

	public static void updateDungeonRoomCountSettings(DungeonManager dungeon) {
		if(!dungeonGUIs.containsKey(dungeon)) return;
		dungeonGUIs.get(dungeon).forEach(DungeonGUI::updateRoomCount);
	}

	public static void updateDungeonRoomRecyclingSettings(DungeonManager dungeon) {
		if(!dungeonGUIs.containsKey(dungeon)) return;
		dungeonGUIs.get(dungeon).forEach(DungeonGUI::updateRoomRecycling);
	}

	/*
	 * GUI Memory Management
	 */
	private static void addDungeonGUI(DungeonManager dungeon, DungeonGUI gui) {
		if(!dungeonGUIs.containsKey(dungeon)) {
			dungeonGUIs.put(dungeon, new ArrayList<>(List.of(gui)));
		} else {
			dungeonGUIs.get(dungeon).add(gui);
		}
	}

	private static void removeDungeonGUI(DungeonManager dungeon, DungeonGUI gui) {
		if(dungeonGUIs.containsKey(dungeon)) {
			List<DungeonGUI> list = dungeonGUIs.get(dungeon);
			if(list.contains(gui)) list.remove(gui);
			if(list.isEmpty()) dungeonGUIs.remove(dungeon);
		}
	}

	private static void removeRoomGUI(Room room) {
		if(roomGUIs.containsKey(room)) roomGUIs.remove(room);
	}

	private static void removeRoomDeleteGUI(Room room) {
		if(roomDeleteGUIs.containsKey(room)) roomDeleteGUIs.remove(room);
	}

	public static void removeGUI(GUI gui) {
		if(gui instanceof DungeonGUI dungeonGUI) {
			removeDungeonGUI(dungeonGUI.getDungeon(), dungeonGUI);
		} else if(gui instanceof RoomGUI roomGUI) {
			removeRoomGUI(roomGUI.getRoom());
		} else if(gui instanceof RoomDeleteGUI roomDeleteGUI) {
			removeRoomDeleteGUI(roomDeleteGUI.getRoom());
		}
	}
}
