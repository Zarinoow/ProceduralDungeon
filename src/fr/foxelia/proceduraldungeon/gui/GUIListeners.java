package fr.foxelia.proceduraldungeon.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.proceduraldungeon.utilities.rooms.Room;
import net.md_5.bungee.api.ChatColor;

public class GUIListeners implements Listener {

	@EventHandler
	public void onGUIInteract(InventoryClickEvent e) {
		GUI gui = null;
// Check ClickedGUI
		for(GUI igui : Main.getGUIs()) {
			if(igui.getInventory().equals(e.getInventory())) {
				gui = igui;
				break;
			}
		}
		if(gui == null) return;
		e.setCancelled(true);
// Check if player can interact with the GUI
		if(!e.getWhoClicked().hasPermission("proceduraldungeon.admin.edit")) {
			e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission").replace("%permission%", "proceduraldungeon.admin.edit")));
			return;
		}
		
// Check if the dungeons is not removed
		if(!gui.getDungeon().getDungeonFolder().exists()) {
			for(HumanEntity view : new ArrayList<>(gui.getInventory().getViewers())) {
				view.closeInventory();
			}
		}
		
		if(gui.getRoom() != null && !gui.getRoom().getFile().exists()) {
			for(HumanEntity view : new ArrayList<>(gui.getInventory().getViewers())) {
				view.closeInventory();
			}
		}

/*
 * DungeonGUI menu
 */		
		
		if(gui.getType().equals(GUIType.DUNGEON)) {
			switch(e.getSlot()) {
// Rename Dungeon
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
// Change Room Amount
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
// Toggle Room Recyling
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
// Edit Room Properties
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
				if(e.isLeftClick()) {
					if(e.getCurrentItem() == null) break;
					
					Room room = gui.getDungeon().getDungeonRooms().getRooms().get(e.getSlot() - 18);
					
					for(GUI opengui : Main.getGUIs()) {
						if(opengui.getDungeon().equals(gui.getDungeon()) && opengui.getType().equals(GUIType.ROOM) && opengui.getRoom().equals(room)) {
							e.getWhoClicked().openInventory(opengui.getInventory());
							return;
						}
					}
					
					GUI newgui = new GUIManager().createRoomGUI(gui.getDungeon(), room);
					Main.getGUIs().add(newgui);
					e.getWhoClicked().openInventory(newgui.getInventory());
				}
				break;
// Others
			default:
				break;
			}
/*
 * RoomGUI menu
 */
		} else if(gui.getType().equals(GUIType.ROOM)) {
			switch(e.getSlot()) {
// Go backward
			case 0:
				if(e.isLeftClick()) {
					for(GUI opengui : Main.getGUIs()) {
						if(opengui.getDungeon().equals(gui.getDungeon()) && opengui.getType().equals(GUIType.DUNGEON)) {
							e.getWhoClicked().openInventory(opengui.getInventory());
							return;
						}
					}
					
					GUI newgui = new GUIManager().createDungeonGUI(gui.getDungeon());
					Main.getGUIs().add(newgui);
					e.getWhoClicked().openInventory(newgui.getInventory());
				}
				break;
// Delete Room
			case 8:
				if(e.isLeftClick()) {
					if(!e.getWhoClicked().hasPermission("proceduraldungeon.admin.edit.deleteroom")) {
						e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission").replace("%permission%", "proceduraldungeon.admin.edit.deleteroom")));
						break;
					}
					for(GUI opengui : Main.getGUIs()) {
						if(opengui.getDungeon().equals(gui.getDungeon()) && opengui.getType().equals(GUIType.ROOM_DELETE) && opengui.getRoom().equals(gui.getRoom())) {
							e.getWhoClicked().openInventory(opengui.getInventory());
							return;
						}
					}
					
					GUI newgui = new GUIManager().createRoomDeleteGUI(gui.getDungeon(), gui.getRoom());
					Main.getGUIs().add(newgui);
					e.getWhoClicked().openInventory(newgui.getInventory());
				}
				break;
// Change Room Spawn Percentage
			case 12:
			case 14:
			case 21:
			case 23:
				if(e.isLeftClick()) {
					int rate;
					if(e.getSlot() == 12) {
						rate = 1;
					} else if(e.getSlot() == 14) {
						rate = -1;
					} else if(e.getSlot() == 21) {
						rate = 10;
					} else rate = -10;
					gui.getRoom().addSpawnrate(rate);
					
					ItemStack item = e.getInventory().getItem(4);
					ItemMeta im = item.getItemMeta();
					im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getGUIString("roomgui.items.spawnrate.name").replace("%int%", String.valueOf(gui.getRoom().getSpawnrate()))));
					item.setItemMeta(im);
				}
				break;
// Others
			default:
				break;
			} 
/*
 * Delete Room Confirmation
 */
		} else if(gui.getType().equals(GUIType.ROOM_DELETE)) {
			switch(e.getSlot()) {
// Confirm
			case 3:
				if(e.isLeftClick()) {
	// Check if player steel have permission
					if(!e.getWhoClicked().hasPermission("proceduraldungeon.admin.edit.deleteroom")) {
						e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission").replace("%permission%", "proceduraldungeon.admin.edit.deleteroom")));
						break;
					}
	// Remember Task Time
					long tasktime = System.currentTimeMillis();
					
	// Remember Room Viewers
					List<HumanEntity> viewers = new ArrayList<>();
					
	// Get all viewers of delete confirmation
					for(HumanEntity view : gui.getInventory().getViewers()) {
						viewers.add(view);
					}
					
	// Get all viewers of room configuration
					for(GUI openGui : Main.getGUIs()) {
						if(openGui.getDungeon().equals(gui.getDungeon()) && openGui.getType().equals(GUIType.ROOM) && openGui.getRoom().equals(gui.getRoom())) {
							for(HumanEntity view : openGui.getInventory().getViewers()) {
								viewers.add(view);
							}
							break;
						}
					}
					
	// Close the inventory for all and send messsage
					for(HumanEntity view : viewers) {
						view.closeInventory();
						view.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("roomdelete").replace("%room%", gui.getRoom().getFile().getName().replace(".dungeon", ""))));
					}
					
	// Remove the rooms					
					gui.getDungeon().getDungeonRooms().getRooms().remove(gui.getRoom());
					gui.getRoom().getFile().delete();
					gui.getDungeon().getDungeonRooms().saveRooms();
					
	// Saving the player editing the dungeon
					List<HumanEntity> inConfig = new ArrayList<>();
					GUI inConfigGUI = null;
					for(GUI opengui : Main.getGUIs()) {
						if(opengui.getDungeon().equals(gui.getDungeon()) && opengui.getType().equals(GUIType.DUNGEON)) {
							for(HumanEntity view : opengui.getInventory().getViewers()) {
								inConfig.add(view);
							}
							inConfigGUI = opengui;
							break;
						}
					}
					if(inConfigGUI != null) Main.getGUIs().remove(inConfigGUI);
	// Open the main GUI for all viewers of the Dungeon
					GUI newgui = new GUIManager().createDungeonGUI(gui.getDungeon());
					Main.getGUIs().add(newgui);
					for(HumanEntity view : inConfig) {
						view.openInventory(newgui.getInventory());
					}
					tasktime = System.currentTimeMillis() - tasktime;
					for(HumanEntity view : viewers) {
						view.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("timetook").replace("%time%", String.valueOf(tasktime))));
						view.openInventory(newgui.getInventory());
					}
					
				}
				break;
// Cancel
			case 5:
				if(e.isLeftClick()) {
					for(GUI opengui : Main.getGUIs()) {
						if(opengui.getDungeon().equals(gui.getDungeon()) && opengui.getType().equals(GUIType.ROOM) && opengui.getRoom().equals(gui.getRoom())) {
							e.getWhoClicked().openInventory(opengui.getInventory());
							return;
						}
					}
					
					GUI newgui = new GUIManager().createRoomGUI(gui.getDungeon(), gui.getRoom());
					Main.getGUIs().add(newgui);
					e.getWhoClicked().openInventory(newgui.getInventory());
				}
				break;
// Others
			default: break;
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
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {	
		GUI gui = null;
		for(GUI igui : Main.getGUIs()) {
			if(igui.getInventory().equals(e.getInventory())) {
				gui = igui;
				break;
			}
		}
		if(gui == null) return;
		
		if(e.getInventory().getViewers().size() <= 1) {
			Main.getGUIs().remove(gui);
			gui.getDungeon().getDungeonRooms().saveRooms();
			gui.getDungeon().getDungeonConfig().saveConfig();
		}
	}	
}
