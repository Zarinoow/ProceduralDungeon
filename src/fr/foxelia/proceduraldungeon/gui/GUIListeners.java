package fr.foxelia.proceduraldungeon.gui;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.tools.minecraft.ui.gui.GUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIListeners implements Listener {
	
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

				GUIManager.reopenDungeonGUI(dungeon); // Update the GUI for players who actually edit the dungeon
				GUIManager.openDungeonGUI(dungeon, e.getPlayer()); // Open the GUI for the player who renamed the dungeon
			}
		};
		
		run.runTask(Main.getProceduralDungeon());
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof DungeonInterface dungeonInterface) {
			DungeonManager dungeon = dungeonInterface.getDungeon();
			if(e.getInventory().getViewers().size() <= 1) {
				dungeon.getDungeonRooms().saveRooms();
				dungeon.getDungeonConfig().saveConfig();
			}
		}


	}
}
