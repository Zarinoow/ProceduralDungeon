package fr.foxelia.proceduraldungeon.commands;

import javax.management.InstanceAlreadyExistsException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import net.md_5.bungee.api.ChatColor;

public class DungeonCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
		if(args.length != 0) {
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("proceduraldungeon.admin.reload")) {
				Main.getMain().reloadConfig();
			} else if(args[0].equalsIgnoreCase("create") && sender.hasPermission("proceduraldungeon.admin.create")) {
				if(args.length >= 2) {
					DungeonManager manager = new DungeonManager(args[1].toLowerCase());
					if(Main.getDungeons().containsKey(args[1].toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("alreadyexist")
								.replace("%dungeon%", Main.getDungeons().get(args[1].toLowerCase()).getName())));
						return false;
					}
					Long delay = System.currentTimeMillis();  
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("dungeoncreation")));
					try {
						manager.createDungeon(args[1]);
					} catch (InstanceAlreadyExistsException | NullPointerException e) {
						e.printStackTrace();
						Main.sendInternalError(sender);
						return false;
					}
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("timetook")
							.replace("%time%", String.valueOf(System.currentTimeMillis() - delay))));
					return true;
				}	
			}
		}
		
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("invalidcmd")));
		return false;
	}

}
