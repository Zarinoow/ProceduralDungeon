package fr.foxelia.proceduraldungeon.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.management.InstanceAlreadyExistsException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.ActionType;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import net.md_5.bungee.api.ChatColor;
import oshi.util.tuples.Pair;

public class DungeonCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
		if(args.length != 0) {
/*
 * Reload Command
 */
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("proceduraldungeon.admin.reload")) {
				Main.getMain().reloadConfig();
/*
 * Create Command
 */
			} else if(args[0].equalsIgnoreCase("create") && sender.hasPermission("proceduraldungeon.admin.create")) {
				if(args.length >= 2) {
					Long delay = System.currentTimeMillis();
					DungeonManager manager = new DungeonManager(args[1].toLowerCase());
					DungeonManager checker = manager.checkExists();
					if(checker == null && Main.getDungeons().containsKey(args[1].toLowerCase())) checker = Main.getDungeons().get(args[1].toLowerCase());
// Instance Already Exist
					if(checker != null) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("alreadyexist")
								.replace("%dungeon%", checker.getName())));
						return false;
// Folder Already Exist
					} else if(manager.getDungeonFolder().exists()) {
						if(Main.getConfirmation().containsKey(sender)) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("importantaction")));
							return false;
						} else {
							Pair<ActionType, String> action = new Pair<ActionType, String>(ActionType.IMPORT, args[1]);
							Main.getConfirmation().put(sender, action);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("importdungeon")));
							Bukkit.getScheduler().runTaskLater(Main.getProceduralDungeon(), () -> {
								if(Main.getConfirmation().containsKey(sender)) {
									Main.getConfirmation().remove(sender);
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("actioncanceled")));
								}
							}, 200);
							return true;
						}
// Creation
					} else {
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
/*
 * Remove Command
 */
			} else if(args[0].equalsIgnoreCase("remove") && sender.hasPermission("proceduraldungeon.admin.remove")) {
				if(args.length >= 2) {
					if(!Main.getDungeons().containsKey(args[1].toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("doesnotexist").replace("%dungeon%", args[1])));
						return false;
					}
					String dungeonName = Main.getDungeons().get(args[1].toLowerCase()).getName();
					Main.getDungeons().remove(args[1].toLowerCase());
					Main.saveDungeons();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getSuccessMessage("removedfromconfig").replace("%dungeon%", dungeonName)));
					return true;
				}
/*
 * Delete Command
 */
			} else if(args[0].equalsIgnoreCase("delete") && sender.hasPermission("proceduraldungeon.admin.delete")) {
				if(args.length >= 2) {
					if(!Main.getDungeons().containsKey(args[1].toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("doesnotexist").replace("%dungeon%", args[1])));
						return false;
					}
					if(Main.getConfirmation().containsKey(sender)) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("importantaction")));
						return false;
					} else {
						Pair<ActionType, String> action = new Pair<ActionType, String>(ActionType.DELETE, args[1]);
						Main.getConfirmation().put(sender, action);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("deletedungeon").replace("%dungeon%", Main.getDungeons().get(args[1].toLowerCase()).getName())));
						Bukkit.getScheduler().runTaskLater(Main.getProceduralDungeon(), () -> {
							if(Main.getConfirmation().containsKey(sender)) {
								Main.getConfirmation().remove(sender);
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("actioncanceled")));
							}
						}, 200);
						return true;
					}
				}
/*
 * Confirm Command
 */
			} else if(args[0].equalsIgnoreCase("confirm") && Main.getConfirmation().containsKey(sender)){
				Long delay = System.currentTimeMillis();
				
				Pair<ActionType, String> confirm = Main.getConfirmation().get(sender);
				Main.getConfirmation().remove(sender);
				
// Import Action
				if(confirm.getA().equals(ActionType.IMPORT)) {
					if(!sender.hasPermission("proceduraldungeon.admin.create")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission")
								.replace("%permission%", "proceduraldungeon.admin.create")));
						return false;
					}
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("dungeonimport")));
					
					DungeonManager dm = new DungeonManager(confirm.getB().toLowerCase());
					try {
						dm.restoreDungeon();
					} catch (Exception e) {
						e.printStackTrace();
						Main.sendInternalError(sender);
						return false;
					}
					if(Main.getDungeons().containsKey(dm.getName().toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("alreadyexist")
								.replace("%dungeon%", Main.getDungeons().get(dm.getName().toLowerCase()).getName())));
						return false;
					}
					Main.getDungeons().put(dm.getName().toLowerCase(), dm);
					Main.saveDungeons();
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("timetook")
							.replace("%time%", String.valueOf(System.currentTimeMillis() - delay))));
					return true;
// Delete Action
				} else if(confirm.getA().equals(ActionType.DELETE)) {
					if(!sender.hasPermission("proceduraldungeon.admin.delete")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission")
								.replace("%permission%", "proceduraldungeon.admin.delete")));
						return false;
					}
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("dungeondelete")));
					
					if(!Main.getDungeons().containsKey(confirm.getB().toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("doesnotexist").replace("%dungeon%", confirm.getB())));
						return false;
					}
					
					try {
						DungeonManager dm = Main.getDungeons().get(confirm.getB().toLowerCase());
						Main.getDungeons().remove(confirm.getB().toLowerCase());
						Main.saveDungeons();
						this.delete(dm.getDungeonFolder());
						
					} catch (Exception e) {
						e.printStackTrace();
						Main.sendInternalError(sender);
						return false;
					}
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("timetook")
							.replace("%time%", String.valueOf(System.currentTimeMillis() - delay))));
					return true;
				}
/*
 * End
 */
			}
		}
		
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("invalidcmd")));
		return false;
	}
	
	private void delete(File file) throws IOException {
		if(file.isDirectory()) {
			for(File subfiles : file.listFiles()) delete(subfiles);
		}
		if(!file.delete()) throw new FileNotFoundException("Failed to delete file: " + file);
	}
}
