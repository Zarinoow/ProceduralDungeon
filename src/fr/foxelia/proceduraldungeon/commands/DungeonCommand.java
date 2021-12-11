package fr.foxelia.proceduraldungeon.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.foxelia.proceduraldungeon.DungeonManager;
import fr.foxelia.proceduraldungeon.Main;
import net.md_5.bungee.api.ChatColor;

public class DungeonCommand implements CommandExecutor {
	
	private Main main;

	public DungeonCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
		if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("proceduraldungeon.admin.reload")) {
			main.reloadConfig();
		} else if(args[0].equalsIgnoreCase("create") && sender.hasPermission("proceduraldungeon.admin.create")) {
			DungeonManager manager = new DungeonManager();
		}
		
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.errors.invalidcmd")));
		return false;
	}

}
