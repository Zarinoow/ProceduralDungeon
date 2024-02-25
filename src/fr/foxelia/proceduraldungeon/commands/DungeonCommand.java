package fr.foxelia.proceduraldungeon.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.gui.GUIManager;
import fr.foxelia.proceduraldungeon.utilities.ActionType;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.proceduraldungeon.utilities.Pair;
import fr.foxelia.proceduraldungeon.utilities.WorldEditSchematic;
import fr.foxelia.proceduraldungeon.utilities.rooms.Coordinate;
import fr.foxelia.proceduraldungeon.utilities.rooms.Room;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DungeonCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		
		if(args.length != 0) {
/*
 * Reload Command
 */
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("proceduraldungeon.admin.reload")) {
				Main.getMain().reloadConfig();
				List<DungeonManager> dms = List.copyOf(Main.getDungeons().values());
				Main.getDungeons().clear();
				for(DungeonManager dm : dms) {
					if(dm.getDungeonFolder().exists()) {
						dm.getDungeonConfig().reloadConfig();
						dm.restoreDungeon();
						if(Main.getDungeons().containsKey(dm.getName().toLowerCase())) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("duplicatename").replace("%folder%", dm.getDungeonFolder().getName())));
						} else Main.getDungeons().put(dm.getName().toLowerCase(), dm);						
					}
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("reload")));
				return true;
/*
 * Help Command
 */
			} else if(args[0].equalsIgnoreCase("help") && sender.hasPermission("proceduraldungeon.dungeon")) { 
				boolean isPlayer = (sender instanceof Player);
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helptop")));
				if(sender.hasPermission("proceduraldungeon.admin.reload")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon reload")
							.replace("%info%", Main.getHelpMessage("info.reload"))));
				}
				if(sender.hasPermission("proceduraldungeon.admin.create")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon create {dungeonname}")
							.replace("%info%", Main.getHelpMessage("info.create"))));
				}
				if(sender.hasPermission("proceduraldungeon.admin.remove")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon remove {dungeonname}")
							.replace("%info%", Main.getHelpMessage("info.remove"))));
				}
				if(sender.hasPermission("proceduraldungeon.admin.delete")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon delete {dungeonname}")
							.replace("%info%", Main.getHelpMessage("info.delete"))));
				}
				if(sender.hasPermission("proceduraldungeon.admin.edit") && isPlayer) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon edit {dungeonname} (roomnumber)")
							.replace("%info%", Main.getHelpMessage("info.edit"))));
				}
				if(sender.hasPermission("proceduraldungeon.admin.addroom") && isPlayer) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon addroom {dungeonname} (customname)")
							.replace("%info%", Main.getHelpMessage("info.addroom"))));
				}
				if(sender.hasPermission("proceduraldungeon.admin.addroom") && isPlayer) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon setexit")
							.replace("%info%", Main.getHelpMessage("info.setexit"))));
				}
				if(sender.hasPermission("proceduraldungeon.admin.generate")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon generate {dungeonname} {x} {y} {z} (World)")
							.replace("%info%", Main.getHelpMessage("info.generate"))));
				}
				if(sender.hasPermission("proceduraldungeon.admin.list")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
							.replace("%cmd%", "dungeon list")
							.replace("%info%", Main.getHelpMessage("info.list"))));
				}
			
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpcmd")
						.replace("%cmd%", "dungeon help")
						.replace("%info%", Main.getHelpMessage("info.help"))));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getHelpMessage("helpbottom")));
				return true;
/*
 * List Command
 */
			} else if(args[0].equalsIgnoreCase("list") && sender.hasPermission("proceduraldungeon.admin.list")) {
				File dungeonFolder = new File(Main.getProceduralDungeon().getDataFolder(), "dungeons");
				if(!dungeonFolder.exists() || dungeonFolder.listFiles().length == 0) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("nodungeon")));
					return false;
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("listtop")));
				List<String> loadeds= new ArrayList<>();
				for(DungeonManager dm : Main.getDungeons().values()) {
					loadeds.add(dm.getDungeonFolder().getName());
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("listcontent")
							.replace("%dungeon%", dm.getName())
							.replace("%status%", Main.getOthersMessage("liststatus.loaded"))));
				}
				
				for(File removed : dungeonFolder.listFiles()) {
					if(loadeds.contains(removed.getName()) || removed.isFile()) continue;
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("listcontent")
							.replace("%dungeon%", removed.getName())
							.replace("%status%", Main.getOthersMessage("liststatus.unloaded"))));
				}
				
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("listbottom")));
				return true;
				
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
				if(confirm.getFirst().equals(ActionType.IMPORT)) {
					if(!sender.hasPermission("proceduraldungeon.admin.create")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission")
								.replace("%permission%", "proceduraldungeon.admin.create")));
						return false;
					}
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("dungeonimport")));
					
					DungeonManager dm = new DungeonManager(confirm.getSecond().toLowerCase());
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
				} else if(confirm.getFirst().equals(ActionType.DELETE)) {
					if(!sender.hasPermission("proceduraldungeon.admin.delete")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission")
								.replace("%permission%", "proceduraldungeon.admin.delete")));
						return false;
					}
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("dungeondelete")));
					
					if(!Main.getDungeons().containsKey(confirm.getSecond().toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("doesnotexist").replace("%dungeon%", confirm.getSecond())));
						return false;
					}

					try {
						DungeonManager dm = Main.getDungeons().get(confirm.getSecond().toLowerCase());
						List<HumanEntity> closed = GUIManager.closeAllGUIOf(dm);
						Main.getDungeons().remove(confirm.getSecond().toLowerCase());
						Main.saveDungeons();
						delete(dm.getDungeonFolder());
						closed.forEach(human -> human.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("dungeondeleted"))));
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
 * AddRoom
 */
			} else if(args[0].equalsIgnoreCase("addroom") && sender.hasPermission("proceduraldungeon.admin.addroom")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("noconsole")));
					return false;					
				}
				
				if(args.length >= 2) {
					Player p = (Player) sender;
					
					if(!Main.getDungeons().containsKey(args[1].toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("doesnotexist").replace("%dungeon%", args[1])));
						return false;
					}
					
					Region pr;
					try {
						pr = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(p)).getSelection();
					} catch (IncompleteRegionException e) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("invalidregion")));
						return false;
					}
					
					if(!Main.getExitLocation().containsKey(p)) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("exitnotset")));
						return false;
					} else if(!isInNorthFace(pr, Main.getExitLocation().get(p))) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("exitincorectregion")));
						return false;
					} else if(!isInSouthFace(pr, p.getLocation().getBlock().getLocation())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("originincorectlocation")
								.replace("%z%", String.valueOf(Math.max(pr.getMinimumPoint().getBlockZ(), pr.getMaximumPoint().getBlockZ())))));
						return false;
					}	
					
					DungeonManager dungeon = Main.getDungeons().get(args[1].toLowerCase());
					if(dungeon.getDungeonRooms() == null) {
						Main.sendInternalError(sender);
						throw new NullPointerException("Room manager cannot be null");
					}
					WorldEditSchematic schematic = new WorldEditSchematic();
					File roomFile;
					if(args.length > 2) {
						roomFile = new File(dungeon.getDungeonRooms().getFolder(), args[2].replace(".dungeon", "") + ".dungeon");
						if(roomFile.getName().equals(".dungeon")) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("namenull")));
							return false;
						}
						if(roomFile.exists()) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("namealreadytaken")));
							return false;
						}
					} else {
						for(int i = 0; true; i++) {
							roomFile = new File(dungeon.getDungeonRooms().getFolder(), "room_" + i + ".dungeon");
							if(!roomFile.exists()) break;
						}
					}
					
					schematic.saveSchematic(p.getLocation().getBlock().getLocation(), WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(p)), roomFile);
					if(schematic.getSaveException() != null) {
						Main.sendInternalError(sender);
						schematic.getSaveException().printStackTrace();
						return false;
					}
					
					Coordinate coord = generateCoordinate(p.getLocation(), Main.getExitLocation().get(p));
					Room dungeonRoom = new Room(roomFile, coord);
					dungeon.getDungeonRooms().addRoom(dungeonRoom);
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getSuccessMessage("roomadded")
							.replace("%dungeon%", dungeon.getName())
							.replace("%file%", roomFile.getName())));

					GUIManager.reopenDungeonGUI(dungeon);
					return true;
				}
// Set Exit
			} else if(args[0].equalsIgnoreCase("setexit") && sender.hasPermission("proceduraldungeon.admin.addroom")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("noconsole")));
					return false;					
				}
				
				Player p = (Player) sender;
				
				Region pr;
				try {
					pr = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(p)).getSelection();
				} catch (IncompleteRegionException e) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("invalidregion")));
					return false;
				}
				
				if(!isInNorthFace(pr, p.getLocation().getBlock().getLocation())) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("exitincorectlocation")
							.replace("%z%", String.valueOf(Math.min(pr.getMinimumPoint().getBlockZ(), pr.getMaximumPoint().getBlockZ())))));
					return false;
				} else {
					Main.getExitLocation().put(p, p.getLocation().getBlock().getLocation());
					p.getWorld().spawnEntity(p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5), EntityType.FIREWORK);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getSuccessMessage("exitset")
							.replace("%x%", String.valueOf(p.getLocation().getBlockX()))
							.replace("%y%", String.valueOf(p.getLocation().getBlockY()))
							.replace("%z%", String.valueOf(p.getLocation().getBlockZ()))
							));
					return true;
				}
// Edit
			} else if(args[0].equalsIgnoreCase("edit") && sender.hasPermission("proceduraldungeon.admin.edit")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("noconsole")));
					return false;					
				}
				
				if(args.length == 2) {
					Player p = (Player) sender;
					
					if(!Main.getDungeons().containsKey(args[1].toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("doesnotexist").replace("%dungeon%", args[1])));
						return false;
					}

					GUIManager.openDungeonGUI(Main.getDungeons().get(args[1].toLowerCase()), p);
					return true;
				} 
				if (args.length >= 3) {
					Player p = (Player) sender;
					
					DungeonManager dungeon = Main.getDungeons().get(args[1].toLowerCase());
					try {
						Room room = dungeon.getDungeonRooms().getRooms().get(Integer.valueOf(args[2]));
						GUIManager.openRoomGUI(dungeon, room, p);
					} catch(IndexOutOfBoundsException | NumberFormatException e) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("roomnotexist").replace("%room%", args[2])));
					}
					return true;
				}
/*
 * Generate
 * dungeon generate dungeon x y z world
 * cmd     0        1       2 3 4 5
 * total   6
 */
			} else if(args[0].equalsIgnoreCase("generate") && sender.hasPermission("proceduraldungeon.admin.generate")) {
				
				if(args.length >= 5) {
// Check Dungeon Exist
					if(!Main.getDungeons().containsKey(args[1].toLowerCase())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("doesnotexist").replace("%dungeon%", args[1])));
						return false;
					}
					
					boolean isPlayer = false;
					if(sender instanceof Player) isPlayer = true;
					
// Check if coordinate are Numeric
					for(int i = 2; i <= 4; i++) {
						if(!isNumeric(args[i])) {
							if(isPlayer && args[i].equals("~")) continue;
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("invalidcoordinates")));
							return false;
						}
					}
					
// Check World is correct
					if(args.length == 5 && !isPlayer) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("missingworld")));
						return false;
					}
					
					World world;
					if(args.length >= 6) {
						world = Bukkit.getWorld(args[5]);
						if(world == null) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("invalidworld")));
							return false;
						}
					} else {
						world = ((Player) sender).getWorld();
					}
					
// Init usefull variables
					double x;
					double y;
					double z;
					if(args[2].equals("~")) {
						x = ((Player) sender).getLocation().getBlockX();
					} else {
						x = Double.parseDouble(args[2]);
					}
					
					if(args[3].equals("~")) {
						y = ((Player) sender).getLocation().getBlockY();
					} else {
						y = Double.parseDouble(args[3]);
					}
					
					if(args[4].equals("~")) {
						z = ((Player) sender).getLocation().getBlockZ();
					} else {
						z = Double.parseDouble(args[4]);
					}
					
					Location dungeonLoc = new Location(world, x, y, z);
					DungeonManager dungeon = Main.getDungeons().get(args[1].toLowerCase());
					
					if(dungeon.getDungeonRooms().getRooms().size() == 0) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("norooms")));
						return false;
					}
					
// Start gen task
					Long taskTime = System.currentTimeMillis();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("dungeongeneration")));
					
					if(!DungeonManager.generateDungeon(dungeon, dungeonLoc)) {
						Main.sendInternalError(sender);
						return false;
					}
					
// Generation Success
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("timetook")
							.replace("%time%", String.valueOf(System.currentTimeMillis() - taskTime))));
					return true;
				}
				
			}
/*
 * End
 */
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
	
	private boolean isInNorthFace(Region selection, Location loc) {		
		BlockVector3 pos = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
		
		double zcoord = Math.min(selection.getMinimumPoint().getBlockZ(), selection.getMaximumPoint().getBlockZ());
		Region area = new CuboidRegion(
				BlockVector3.at(selection.getMinimumPoint().getBlockX(), selection.getMinimumPoint().getBlockY(), zcoord), 
				BlockVector3.at(selection.getMaximumPoint().getBlockX(), selection.getMaximumPoint().getBlockY(), zcoord));
		
		if(area.contains(pos)) {
			return true;
		} else return false;
	}
	
	private boolean isInSouthFace(Region selection, Location loc) {		
		BlockVector3 pos = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
		
		double zcoord = Math.max(selection.getMinimumPoint().getBlockZ(), selection.getMaximumPoint().getBlockZ());
		Region area = new CuboidRegion(
				BlockVector3.at(selection.getMinimumPoint().getBlockX(), selection.getMinimumPoint().getBlockY(), zcoord), 
				BlockVector3.at(selection.getMaximumPoint().getBlockX(), selection.getMaximumPoint().getBlockY(), zcoord));
		
		if(area.contains(pos)) {
			return true;
		} else return false;
	}
	
	private Coordinate generateCoordinate(Location origin, Location exit) {
		return new Coordinate(exit.getBlockX() - origin.getBlockX(), exit.getBlockY() - origin.getBlockY(), exit.getBlockZ() - origin.getBlockZ());
	}
	
	private boolean isNumeric(String strNum) {
	    if(strNum == null) {
	        return false;
	    }
	    try {
	        Double.parseDouble(strNum);
	    } catch (NumberFormatException e) {
	        return false;
	    }
	    return true;
	}
}
