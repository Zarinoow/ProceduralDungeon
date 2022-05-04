package fr.foxelia.proceduraldungeon.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.proceduraldungeon.utilities.rooms.Room;
import net.md_5.bungee.api.ChatColor;

public class GUIManager {
	
	private Inventory inventory;
	
	public GUI createDungeonGUI(DungeonManager dungeon) {
		inventory = Bukkit.createInventory(
				null,
				6*9,
				ChatColor.translateAlternateColorCodes('&', Main.getGUIString("dungeongui.title")
						.replace("%dungeon%", dungeon.getName())));
		
// Rename
		try {
			inventory.setItem(2, constructItem(
					Material.valueOf(Main.getGUIString("dungeongui.items.rename.material")),
					1,
					Main.getGUIString("dungeongui.items.rename.name"),
					Main.getGuiStringList("dungeongui.items.rename.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("dungeongui.items.rename.material") + " in the rename section. Attempt to retrieve the default value...");
			inventory.setItem(2, constructItem(Material.PAPER, 1, Main.getGUIString("dungeongui.items.rename.name"), Main.getGuiStringList("dungeongui.items.rename.lore")));
		}

// RoomCount
		try {
			inventory.setItem(4, constructItem(
					Material.valueOf(Main.getGUIString("dungeongui.items.roomcount.material")),
					dungeon.getConfig().getInt("roomcount"),
					Main.getGUIString("dungeongui.items.roomcount.name"),
					Main.getGuiStringList("dungeongui.items.roomcount.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("dungeongui.items.roomcount.material") + " in the roomcount section. Attempt to retrieve the default value...");
			inventory.setItem(4, constructItem(Material.OAK_DOOR, dungeon.getConfig().getInt("roomcount"), Main.getGUIString("dungeongui.items.roomcount.name"), Main.getGuiStringList("dungeongui.items.roomcount.lore")));
		}
		
// RoomRecyling
		try {
			if(dungeon.getConfig().getBoolean("roomrecyling")) {
				inventory.setItem(6, constructItem(
						Material.valueOf(Main.getGUIString("dungeongui.items.roomrecyling.truematerial")),
						1,
						Main.getGUIString("dungeongui.items.roomrecyling.name").replace("%boolean%", Main.getGUIString("dungeongui.boolean.true")),
						Main.getGuiStringList("dungeongui.items.roomrecyling.lore")));
			} else {
				inventory.setItem(6, constructItem(
						Material.valueOf(Main.getGUIString("dungeongui.items.roomrecyling.falsematerial")),
						1,
						Main.getGUIString("dungeongui.items.roomrecyling.name").replace("%boolean%", Main.getGUIString("dungeongui.boolean.false")),
						Main.getGuiStringList("dungeongui.items.roomrecyling.lore")));
			}
			
		} catch (IllegalArgumentException exception) {
			if(dungeon.getConfig().getBoolean("roomrecyling")) {
				Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("dungeongui.items.roomrecyling.truematerial") + " in the roomrecyling section. Attempt to retrieve the default value...");
				inventory.setItem(6, constructItem(
						Material.LIME_WOOL,
						1,
						Main.getGUIString("dungeongui.items.roomrecyling.name").replace("%boolean%", Main.getGUIString("dungeongui.boolean.true")),
						Main.getGuiStringList("dungeongui.items.roomrecyling.lore")));
			} else {
				Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("dungeongui.items.roomrecyling.falsematerial") + " in the roomrecyling section. Attempt to retrieve the default value...");
				inventory.setItem(6, constructItem(
						Material.RED_WOOL,
						1,
						Main.getGUIString("dungeongui.items.roomrecyling.name").replace("%boolean%", Main.getGUIString("dungeongui.boolean.false")),
						Main.getGuiStringList("dungeongui.items.roomrecyling.lore")));
			}
		}
		
// RoomSeparator
		try {
			for(int slot = 9; slot <= 17; slot++) {
				inventory.setItem(slot, constructItem(
						Material.valueOf(Main.getGUIString("dungeongui.items.roomseparator.material")),
						1,
						Main.getGUIString("dungeongui.items.roomseparator.name"),
						Main.getGuiStringList("dungeongui.items.roomseparator.lore")));
			}
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("dungeongui.items.roomseparator.material") + " in the roomseparator section. Attempt to retrieve the default value...");
			for(int slot = 9; slot <= 17; slot++) {
				inventory.setItem(slot, constructItem(
						Material.BLACK_STAINED_GLASS_PANE,
						1,
						Main.getGUIString("dungeongui.items.roomseparator.name"),
						Main.getGuiStringList("dungeongui.items.roomseparator.lore")));
			}
		}
		
// RoomItem
		try {
			int roomcount = dungeon.getDungeonRooms().getRooms().size() + 18;
			for(int slot = 18; slot <= 53; slot++) {
				if(slot == roomcount) break;
				inventory.setItem(slot, constructItem(
						Material.valueOf(Main.getGUIString("dungeongui.items.roomitem.material")),
						1,
						Main.getGUIString("dungeongui.items.roomitem.name").replace("%int%", String.valueOf(slot - 18)),
						Main.getGuiStringList("dungeongui.items.roomitem.lore")));
			}
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("dungeongui.items.roomitem.material") + " in the roomitem section. Attempt to retrieve the default value...");
			int roomcount = dungeon.getDungeonRooms().getRooms().size() + 18;
			for(int slot = 18; slot <= 53; slot++) {
				if(slot == roomcount) break;
				inventory.setItem(slot, constructItem(
						Material.PISTON,
						1,
						Main.getGUIString("dungeongui.items.roomitem.name").replace("%int%", String.valueOf(slot - 18)),
						Main.getGuiStringList("dungeongui.items.roomitem.lore")));
			}
		}
		
		return new GUI(inventory, dungeon, null, false);
	}
	
	public GUI createRoomGUI(DungeonManager dungeon, Room room) {
		inventory = Bukkit.createInventory(
				null,
				3*9,
				ChatColor.translateAlternateColorCodes('&', Main.getGUIString("roomgui.title")
						.replace("%room%", room.getFile().getName().replace(".dungeon", ""))));
		
// Icon
		try {
			inventory.setItem(0, constructItem(
					Material.valueOf(Main.getGUIString("roomgui.items.roomitem.material")),
					1,
					Main.getGUIString("roomgui.items.roomitem.name").replace("%int%", String.valueOf(dungeon.getDungeonRooms().getRooms().indexOf(room))),
					Main.getGuiStringList("roomgui.items.roomitem.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("roomgui.items.roomitem.material") + " in the roomitem section. Attempt to retrieve the default value...");
			inventory.setItem(0, constructItem(Material.PISTON, 1, Main.getGUIString("roomgui.items.roomitem.name").replace("%int%", String.valueOf(dungeon.getDungeonRooms().getRooms().indexOf(room))), Main.getGuiStringList("roomgui.items.roomitem.lore")));
		}
		
// SpawnRate Icon
		try {
			inventory.setItem(4, constructItem(
					Material.valueOf(Main.getGUIString("roomgui.items.spawnrate.material")),
					1,
					Main.getGUIString("roomgui.items.spawnrate.name").replace("%int%", String.valueOf(room.getSpawnrate())),
					Main.getGuiStringList("roomgui.items.spawnrate.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("roomgui.items.spawnrate.material") + " in the spawnrate section. Attempt to retrieve the default value...");
			inventory.setItem(4, constructItem(Material.DISPENSER, 1, Main.getGUIString("roomgui.items.spawnrate.name").replace("%int%", String.valueOf(room.getSpawnrate())), Main.getGuiStringList("roomgui.items.spawnrate.lore")));
		}
		
// Add SpawnRate
		try {
			inventory.setItem(12, constructItem(
					Material.valueOf(Main.getGUIString("roomgui.items.addspawnrate.material")),
					1,
					Main.getGUIString("roomgui.items.addspawnrate.name").replace("%int%", "1"),
					Main.getGuiStringList("roomgui.items.addspawnrate.lore")));
			inventory.setItem(21, constructItem(
					Material.valueOf(Main.getGUIString("roomgui.items.addspawnrate.material")),
					1,
					Main.getGUIString("roomgui.items.addspawnrate.name").replace("%int%", "10"),
					Main.getGuiStringList("roomgui.items.addspawnrate.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("roomgui.items.addspawnrate.material") + " in the addspawnrate section. Attempt to retrieve the default value...");
			inventory.setItem(12, constructItem(Material.LIME_STAINED_GLASS_PANE, 1, Main.getGUIString("roomgui.items.addspawnrate.name").replace("%int%", "1"), Main.getGuiStringList("roomgui.items.addspawnrate.lore")));
			inventory.setItem(21, constructItem(Material.LIME_STAINED_GLASS_PANE, 1, Main.getGUIString("roomgui.items.addspawnrate.name").replace("%int%", "10"), Main.getGuiStringList("roomgui.items.addspawnrate.lore")));
		}
		
// Lower SpawnRate
		try {
			inventory.setItem(14, constructItem(
					Material.valueOf(Main.getGUIString("roomgui.items.lowerspawnrate.material")),
					1,
					Main.getGUIString("roomgui.items.lowerspawnrate.name").replace("%int%", "1"),
					Main.getGuiStringList("roomgui.items.lowerspawnrate.lore")));
			inventory.setItem(23, constructItem(
					Material.valueOf(Main.getGUIString("roomgui.items.lowerspawnrate.material")),
					1,
					Main.getGUIString("roomgui.items.lowerspawnrate.name").replace("%int%", "10"),
					Main.getGuiStringList("roomgui.items.lowerspawnrate.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("roomgui.items.lowerspawnrate.material") + " in the lowerspawnrate section. Attempt to retrieve the default value...");
			inventory.setItem(14, constructItem(Material.RED_STAINED_GLASS_PANE, 1, Main.getGUIString("roomgui.items.lowerspawnrate.name").replace("%int%", "1"), Main.getGuiStringList("roomgui.items.lowerspawnrate.lore")));
			inventory.setItem(23, constructItem(Material.RED_STAINED_GLASS_PANE, 1, Main.getGUIString("roomgui.items.lowerspawnrate.name").replace("%int%", "10"), Main.getGuiStringList("roomgui.items.lowerspawnrate.lore")));
		}
		
// Delete
		try {
			inventory.setItem(8, constructItem(
					Material.valueOf(Main.getGUIString("roomgui.items.delete.material")),
					1,
					Main.getGUIString("roomgui.items.delete.name"),
					Main.getGuiStringList("roomgui.items.delete.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("roomgui.items.delete.material") + " in the delete section. Attempt to retrieve the default value...");
			inventory.setItem(8, constructItem(Material.BARRIER, 1, Main.getGUIString("roomgui.items.delete.name"), Main.getGuiStringList("roomgui.items.delete.lore")));
		}
		
		return new GUI(inventory, dungeon, room, false);
	}
	
	public GUI createRoomDeleteGUI(DungeonManager dungeon, Room room) {
		inventory = Bukkit.createInventory(
				null,
				9,
				ChatColor.translateAlternateColorCodes('&', Main.getGUIString("deletegui.title")
						.replace("%room%", room.getFile().getName().replace(".dungeon", ""))));
		
// Confirm
		try {
			inventory.setItem(3, constructItem(
					Material.valueOf(Main.getGUIString("deletegui.items.confirm.material")),
					1,
					Main.getGUIString("deletegui.items.confirm.name"),
					Main.getGuiStringList("deletegui.items.confirm.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("deletegui.items.confirm.material") + " in the confirm section. Attempt to retrieve the default value...");
			inventory.setItem(3, constructItem(Material.LIME_STAINED_GLASS_PANE, 1, Main.getGUIString("deletegui.items.confirm.name"), Main.getGuiStringList("deletegui.items.confirm.lore")));
		}
		
// Cancel
		try {
			inventory.setItem(5, constructItem(
					Material.valueOf(Main.getGUIString("deletegui.items.cancel.material")),
					1,
					Main.getGUIString("deletegui.items.cancel.name"),
					Main.getGuiStringList("deletegui.items.cancel.lore")));
		} catch (IllegalArgumentException exception) {
			Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("deletegui.items.cancel.material") + " in the cancel section. Attempt to retrieve the default value...");
			inventory.setItem(5, constructItem(Material.RED_STAINED_GLASS_PANE, 1, Main.getGUIString("deletegui.items.cancel.name"), Main.getGuiStringList("deletegui.items.cancel.lore")));
		}
		
		return new GUI(inventory, dungeon, room, true);
	}
	
	private ItemStack constructItem(Material material, int amount, String customName, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if(customName != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
		if(lore != null) {
			List<String> newLore = new ArrayList<String>();
			for(String str : lore) {
				newLore.add(ChatColor.translateAlternateColorCodes('&', str));
			}
			meta.setLore(newLore);
		}
		
		item.setItemMeta(meta);
		return item;
	}
	
}
