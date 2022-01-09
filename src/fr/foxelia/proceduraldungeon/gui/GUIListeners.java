package fr.foxelia.proceduraldungeon.gui;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.foxelia.proceduraldungeon.Main;
import net.md_5.bungee.api.ChatColor;

public class GUIListeners implements Listener {

	@EventHandler
	public void onGUIInteract(InventoryClickEvent e) {
		GUI gui = null;
		for(GUI igui : Main.getGUIs()) {
			if(igui.getInventory().equals(e.getInventory())) {
				gui = igui;
				break;
			}
		}
		if(gui == null) return;
		
		if(gui.getType().equals(GUIType.DUNGEON)) {
			e.setCancelled(true);
			switch(e.getSlot()) {
			case 2:
				if(e.isLeftClick()) {
					e.getWhoClicked().closeInventory();
					GUI renamegui = new GUIManager().createRenamingGUI(gui.getDungeon());
					Main.getGUIs().add(renamegui);
					e.getWhoClicked().openInventory(renamegui.getInventory());
				}
				break;
			case 4:
				if(e.isLeftClick()) {
					ItemStack item = e.getCurrentItem();
					if(item.getAmount() == 64) break;
					e.getCurrentItem().setAmount(item.getAmount() + 1);
					gui.getDungeon().getConfig().set("roomcount", item.getAmount());
				} else if(e.isRightClick()) {
					ItemStack item = e.getCurrentItem();
					if(item.getAmount() == 1) break;
					e.getCurrentItem().setAmount(item.getAmount() - 1);
					gui.getDungeon().getConfig().set("roomcount", item.getAmount());
				}
				break;
			case 6:
				if(e.isLeftClick()) {
					ItemStack item = e.getCurrentItem();
					if(gui.getDungeon().getConfig().getBoolean("roomrecyling")) {
						gui.getDungeon().getConfig().set("roomrecyling", false);
						try {
							item.setType(Material.valueOf(Main.getGUIString("dungeongui.items.roomrecyling.falsematerial")));
						} catch (Exception exception) {
							Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("dungeongui.items.roomrecyling.falsematerial") + " in the roomrecyling section. Attempt to retrieve the default value...");
							item.setType(Material.RED_WOOL);
						}
						ItemMeta im = item.getItemMeta();
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getGUIString("dungeongui.items.roomrecyling.name").replace("%boolean%", Main.getGUIString("dungeongui.boolean.false"))));
						item.setItemMeta(im);
					} else {
						gui.getDungeon().getConfig().set("roomrecyling", true);
						try {
							item.setType(Material.valueOf(Main.getGUIString("dungeongui.items.roomrecyling.truematerial")));
						} catch (Exception exception) {
							Main.getMain().getLogger().log(Level.WARNING, "Invalid material " + Main.getGUIString("dungeongui.items.roomrecyling.truematerial") + " in the roomrecyling section. Attempt to retrieve the default value...");
							item.setType(Material.LIME_WOOL);
						}
						ItemMeta im = item.getItemMeta();
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getGUIString("dungeongui.items.roomrecyling.name").replace("%boolean%", Main.getGUIString("dungeongui.boolean.true"))));
						item.setItemMeta(im);
					}
				}
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
			case 40:
			case 41:
			case 42:
			case 43:
			case 45:
			case 46:
			case 47:
			case 48:
			case 49:
			case 50:
			case 51:
			case 52:
			case 53:
				if(e.getCurrentItem() == null) break;
				break;
			default:
				break;
			}
		} else if(gui.getType().equals(GUIType.ROOM)) {
			e.setCancelled(true);			
		} else if(gui.getType().equals(GUIType.RENAMING)) {
			switch (e.getSlot()) {
			case 2:
				break;
			default:
				e.setCancelled(true);
				break;
			}
		}
		
	}
	
	@EventHandler
	public void onDungeonRename(PrepareAnvilEvent e) {
		GUI gui = null;
		for(GUI igui : Main.getGUIs()) {
			if(igui.getInventory().equals(e.getInventory())) {
				gui = igui;
				break;
			}
		}
		if(gui == null) return;
		
		Main.getGUIs().remove(gui);
		
		if(e.getResult() != null) {
			String newname = e.getResult().getItemMeta().getDisplayName();
			e.setResult(null);
			if(Main.getDungeons().containsKey(newname.toLowerCase())) {
				for(HumanEntity p : e.getViewers()) {
					p.closeInventory();
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("alreadyexist")
							.replace("%dungeon%", Main.getDungeons().get(newname.toLowerCase()).getName())));
				}
				return;
			}
			Main.getDungeons().remove(gui.getDungeon().getName().toLowerCase());
			gui.getDungeon().setName(newname);
			Main.getDungeons().put(gui.getDungeon().getName().toLowerCase(), gui.getDungeon());
		}
		
		GUI newgui = new GUIManager().createDungeonGUI(gui.getDungeon());
		
		for(GUI opengui : Main.getGUIs()) {
			if(opengui.getDungeon().equals(gui.getDungeon()) && opengui.getType().equals(GUIType.DUNGEON)) {
				for(HumanEntity p : opengui.getInventory().getViewers()) {
					p.openInventory(newgui.getInventory());
				}
				Main.getGUIs().remove(opengui);
				return;
			}
		}
		
		Main.getGUIs().add(newgui);
		for(HumanEntity p : e.getViewers()) {
			p.openInventory(newgui.getInventory());
		}
		
	}
	
	
}
