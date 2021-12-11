package fr.foxelia.proceduraldungeon.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class DungeonCommandCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
		if(!(sender instanceof Player)) return null;
		ArrayList<String> suggest = new ArrayList<>();
		
		if(args.length == 1) {
			// /dungeon
			if(sender.hasPermission("proceduraldungeon.admin.reload")) suggest.add("reload");
			if(sender.hasPermission("proceduraldungeon.admin.create")) suggest.add("create");
			if(sender.hasPermission("proceduraldungeon.admin.addroom")) suggest.add("addroom");
			if(sender.hasPermission("proceduraldungeon.admin.generate")) suggest.add("generate");
		}
		
		return suggest;
	}

}
