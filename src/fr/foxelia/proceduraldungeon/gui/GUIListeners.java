package fr.foxelia.proceduraldungeon.gui;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
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
					DungeonManager dungeon = gui.getDungeon();
					Main.getRenaming().put(e.getWhoClicked(), dungeon);
					e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("renaming")));
					e.getWhoClicked().closeInventory();
					Bukkit.getScheduler().runTaskLater(Main.getProceduralDungeon(), () -> {
						if(Main.getRenaming().containsKey(e.getWhoClicked())) {
							Main.getRenaming().remove(e.getWhoClicked());
							
							for(GUI igui : Main.getGUIs()) {
								if(igui.getDungeon().equals(dungeon) && igui.getType().equals(GUIType.DUNGEON)) {
									e.getWhoClicked().openInventory(igui.getInventory());
									return;
								}
							}
							
							GUI igui = new GUIManager().createDungeonGUI(dungeon);
							Main.getGUIs().add(igui);
							e.getWhoClicked().openInventory(igui.getInventory());
						}
					}, 200);
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
				System.out.println(((AnvilInventory) gui.getInventory()).getRenameText()); // Debug
				break;
			default:
//				e.setCancelled(true);
				break;
			}
		}
		
	}
	
	@EventHandler
	public void onDungeonRename(AsyncPlayerChatEvent e) {
		if(!Main.getRenaming().containsKey(e.getPlayer())) return;
		DungeonManager dungeon = Main.getRenaming().get(e.getPlayer());
		e.setCancelled(true);
		
		String newName = e.getMessage().split(" ")[0];
		if(Main.getDungeons().containsKey(newName.toLowerCase())) {
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("alreadyexist")
					.replace("%dungeon%", Main.getDungeons().get(newName.toLowerCase()).getName())));
			return;
		}
		
		Main.getRenaming().remove(e.getPlayer());

		BukkitRunnable run = new BukkitRunnable() {
			@Override
			public void run() {
				Main.getDungeons().remove(dungeon.getName().toLowerCase());
				dungeon.setName(newName);
				Main.getDungeons().put(dungeon.getName().toLowerCase(), dungeon);

				GUI newgui = new GUIManager().createDungeonGUI(dungeon);
				GUI removegui = null;
				
				for(GUI opengui : Main.getGUIs()) {
					if(opengui.getDungeon().equals(dungeon) && opengui.getType().equals(GUIType.DUNGEON)) {
						for(HumanEntity p : new ArrayList<>(opengui.getInventory().getViewers())) {
							p.openInventory(newgui.getInventory());
						}
						removegui = opengui;
					}
				}
				
				if(removegui != null) Main.getGUIs().remove(removegui);
				Main.getGUIs().add(newgui);
				
				e.getPlayer().openInventory(newgui.getInventory());			
			}
		};
		
		run.runTask(Main.getProceduralDungeon());
	}
	
	
}
