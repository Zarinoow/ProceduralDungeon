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
						Main.getGUIString("dungeongui.items.roomitem.name").replace("%int%", String.valueOf(slot - 17)),
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
						Main.getGUIString("dungeongui.items.roomitem.name").replace("%int%", String.valueOf(slot - 17)),
						Main.getGuiStringList("dungeongui.items.roomitem.lore")));
			}
		}
		
		return new GUI(inventory, dungeon, null);
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
