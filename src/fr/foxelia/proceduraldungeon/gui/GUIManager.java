package fr.foxelia.proceduraldungeon.gui;

import java.util.ArrayList;
import java.util.List;

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
	private GUI gui;
	
	public void createDungeonGUI(DungeonManager dungeon) {
		inventory = Bukkit.createInventory(
				null,
				6*9,
				ChatColor.translateAlternateColorCodes('&', Main.getGUIString("dungeongui.title")
						.replace("%dungeon%", dungeon.getName())));
		gui = new GUI(inventory, dungeon, null);
		inventory.setItem(1, constructItem(Material.PAPER, 1, null, null));
		
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
