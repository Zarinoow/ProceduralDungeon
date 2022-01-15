package fr.foxelia.proceduraldungeon.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;

public class DungeonCommandCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
		ArrayList<String> suggest = new ArrayList<>();
		
		if(args.length == 1) {
			// /dungeon
			if(sender.hasPermission("proceduraldungeon.admin.reload")) suggest.add("reload");
			if(sender.hasPermission("proceduraldungeon.admin.create")) suggest.add("create");
			if(sender.hasPermission("proceduraldungeon.admin.remove")) suggest.add("remove");
			if(sender.hasPermission("proceduraldungeon.admin.delete")) suggest.add("delete");
			if(sender.hasPermission("proceduraldungeon.admin.edit")) suggest.add("edit");
			if(sender.hasPermission("proceduraldungeon.admin.addroom") && sender instanceof Player) {
				suggest.add("addroom");
				suggest.add("setexit");
			}
			if(sender.hasPermission("proceduraldungeon.admin.generate")) suggest.add("generate"); // Not coded
			if(sender.hasPermission("proceduraldungeon.admin.list")) suggest.add("list");
			if(sender.hasPermission("proceduraldungeon.dungeon")) suggest.add("help");
			if(Main.getConfirmation().containsKey(sender)) suggest.add("confirm");
		} else if(args.length == 2) {
// /dungeon remove
			if(sender.hasPermission("proceduraldungeon.admin.remove") && args[0].equalsIgnoreCase("remove")) {
				for(DungeonManager dm : Main.getDungeons().values()) {
					suggest.add(dm.getName());
				}
// /dungeon delete
			} else if(sender.hasPermission("proceduraldungeon.admin.delete") && args[0].equalsIgnoreCase("delete")) {
				for(DungeonManager dm : Main.getDungeons().values()) {
					suggest.add(dm.getName());
				}
// /dungeon addroom
			} else if(sender.hasPermission("proceduraldungeon.admin.addroom") && args[0].equalsIgnoreCase("addroom") && sender instanceof Player) {
				for(DungeonManager dm : Main.getDungeons().values()) {
					suggest.add(dm.getName());
				}
// /dungeon edit
			} else if(sender.hasPermission("proceduraldungeon.admin.edit") && args[0].equalsIgnoreCase("edit") && sender instanceof Player) {
				for(DungeonManager dm : Main.getDungeons().values()) {
					suggest.add(dm.getName());
				}
			}
		}
		
		return suggest;
	}

}
