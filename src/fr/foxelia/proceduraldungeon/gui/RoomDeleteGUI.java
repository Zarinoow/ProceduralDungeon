package fr.foxelia.proceduraldungeon.gui;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.proceduraldungeon.utilities.rooms.Room;
import fr.foxelia.tools.minecraft.ui.gui.GUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class RoomDeleteGUI extends GUI implements DungeonInterface, RoomInterface {


    /*
     * Constants
     */
    private final DungeonManager dungeon;
    private final Room room;
    private static final int inventorySize = 1;


    /*
     * Constructor
     */

    public RoomDeleteGUI(DungeonManager dungeon, Room room) {
        super("roomDelete");
        this.dungeon = dungeon;
        this.room = room;
    }

    /*
     * Foxelia Methods
     */
    @Override
    public void constructGUI() {
        // Update placeholders
        addPlaceholder("%room%", room.getFile().getName().replace(".dungeon", ""));
        addPlaceholder("%number%", String.valueOf(dungeon.getDungeonRooms().getRooms().indexOf(room)));

        createInventory(inventorySize, getPlaceholderReplacement()); // Create Inventory

        setItem(3, "confirm"); // Confirm
        setItem(5, "cancel"); // Cancel

        fillEmpty(generateItem("empty", getPlaceholderReplacement()));

    }

    /*
     * Events
     */
    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        HumanEntity human = event.getWhoClicked();

        // Check if player can interact with the GUI
        // Check if the dungeons is not removed
        // Check if the room still exists
        if(!GUIManager.checkPermissionToEdit(human) || !GUIManager.checkDungeonExists(this, dungeon) && !GUIManager.checkRoomExists(this, room)) return;

        if(!event.isLeftClick()) return;
        switch(event.getSlot()) {
             // Confirm
             case 3 -> {
                // Check if player still have permission
                if (!human.hasPermission("proceduraldungeon.admin.edit.deleteroom")) {
                      human.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("lackingpermission").replace("%permission%", "proceduraldungeon.admin.edit.deleteroom")));
                     break;
                }
                // Remember Task Time
                long tasktime = System.currentTimeMillis();

                // Remove the rooms
                dungeon.getDungeonRooms().getRooms().remove(room);
                room.getFile().delete();
                dungeon.getDungeonRooms().saveRooms();

                // Close all GUIs of the room
                List<HumanEntity> closedInventories = GUIManager.closeAllRoomGUIOf(room);
                // Update the main GUI of the dungeon
                GUIManager.reopenDungeonGUI(dungeon);

                 for (HumanEntity humanEntity : closedInventories) {
                     GUIManager.openDungeonGUI(dungeon, humanEntity);
                     if(humanEntity != human) humanEntity.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("roomdeleted").replace("%room%", room.getFile().getName().replace(".dungeon", ""))));
                 }

                tasktime = System.currentTimeMillis() - tasktime;
                human.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getTaskMessage("timetook").replace("%time%", String.valueOf(tasktime))));
            }
            // Cancel
            case 5 -> GUIManager.openRoomGUI(dungeon, room, human);
        }
    }

    /*
     * Interface
     */
    @Override
    public DungeonManager getDungeon() {
        return dungeon;
    }

    @Override
    public RoomDeleteGUI getGUI() {
        return this;
    }

    @Override
    public Room getRoom() {
        return room;
    }
}
