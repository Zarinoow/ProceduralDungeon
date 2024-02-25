package fr.foxelia.proceduraldungeon.gui;

import fr.foxelia.proceduraldungeon.Main;
import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.proceduraldungeon.utilities.rooms.Room;
import fr.foxelia.tools.minecraft.ui.gui.NavigableGUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DungeonGUI extends NavigableGUI<Room> implements DungeonInterface {

    /*
     * Constants
     */
    private final DungeonManager dungeon;
    private static final int inventorySize = 4;

    /*
     * Constructor
     */

    public DungeonGUI(DungeonManager dungeon) {
        super("dungeon");
        this.dungeon = dungeon;
        setupDisplayedList();
    }

    /*
     * Foxelia Methods
     */

    @Override
    public void constructGUI() {
        addPlaceholder("%dungeon%", dungeon.getName());
        refreshPagesPlaceholders();
        createInventory(inventorySize, getPlaceholderReplacement());

        addNavigationButtons();
        fillInventory();
        fillEmpty(generateItem("empty", getPlaceholderReplacement()));

    }

    @Override
    public void addNextButton(int bottomRow) {
        setItem(bottomRow + 8, "navbar.next");
    }

    @Override
    public void addPreviousButton(int bottomRow) {
        setItem(bottomRow, "navbar.previous");
    }

    @Override
    public void addCustomButtons(int bottomRow) {
        // Rename
        setItem(bottomRow + 2, "navbar.rename");
        // Room Count
        updateRoomCount();
        // Room Recycling
        updateRoomRecycling();
    }

    @Override
    public void setupDisplayedList() {
        for(Room room : dungeon.getDungeonRooms().getRooms()) {
            getDisplayedList().add(room);
        }
    }

    @Override
    public ItemStack processItem(Room room) {
        addPlaceholder("%number%", String.valueOf(getIndexOf(room)));
        addPlaceholder("%room%", room.getFile().getName().replace(".dungeon", ""));
        return generateItem("roomitem", getPlaceholderReplacement());
    }

    public void refreshPagesPlaceholders() {
        addPlaceholder("%page%", String.valueOf(getCurrentPage() + 1));
        addPlaceholder("%maxpage%", String.valueOf((int) Math.ceil((double) getDisplayedList().size() / (inventorySize * 9 - 9))));
    }

    @Nullable
    public Room getSelected(int slot) {
        int index = slot + getCurrentPage() * ((inventorySize - 1) * 9);
        if(index >= getDisplayedList().size()) return null;
        return getDisplayedList().get(index);
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
        if(!GUIManager.checkPermissionToEdit(human) || !GUIManager.checkDungeonExists(this, dungeon)) return;

        switch (event.getSlot()) {
            // Previous button
            case inventorySize * 9 - 9 -> goToPreviousPage();
            // Next button
            case inventorySize * 9 - 1 -> goToNextPage();
            // Rename
            case inventorySize * 9 - 7 -> {
                if(event.isLeftClick()) {
                    if(Main.getRenaming().containsKey(human)) {
                        human.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getErrorMessage("alreadyrenaming")));
                        return;
                    }
                    Main.getRenaming().put(human, dungeon);
                    human.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getOthersMessage("renaming")));
                    human.closeInventory();
                    Bukkit.getScheduler().runTaskLater(Main.getProceduralDungeon(), () -> {
                        if(Main.getRenaming().containsKey(human)) {
                            Main.getRenaming().remove(human);
                            new DungeonGUI(dungeon).openInventory(human);
                        }
                    }, 200);
                }
            }
            // Room Count
            case inventorySize * 9 - 5 -> {
                int roomCount = event.getCurrentItem().getAmount();
                if(event.isLeftClick()) {
                    if(roomCount >= 64) break;
                    dungeon.getConfig().set("roomcount", ++roomCount);
                } else if(event.isRightClick()) {
                    if(roomCount <= 1) break;
                    dungeon.getConfig().set("roomcount", --roomCount);
                }
                GUIManager.updateDungeonRoomCountSettings(dungeon);
            }
            // Room Recycling
            case inventorySize * 9 - 3 -> {
                if(event.isLeftClick()) {
                    if(dungeon.getConfig().getBoolean("roomrecyling")) {
                        dungeon.getConfig().set("roomrecyling", false);
                    } else {
                        dungeon.getConfig().set("roomrecyling", true);
                    }
                    GUIManager.updateDungeonRoomRecyclingSettings(dungeon);
                }
            }
            // Other
            default -> {
                if(event.isLeftClick() && event.getSlot() < inventorySize * 9 - 9 && event.getSlot() >= 0) {
                    interactWith(event.getSlot(), human);
                }
            }
        }

    }

    /*
     * Other Methods
     */
    public void interactWith(int slot, HumanEntity human) {
        Room room = getSelected(slot);
        if(room == null) return;
        GUIManager.openRoomGUI(dungeon, room, human);
    }

    public int getIndexOf(Room room) {
        return dungeon.getDungeonRooms().getRooms().indexOf(room);
    }

    public void updateRoomCount() {
        ItemStack roomCountIcon = generateItem("navbar.roomcount", getPlaceholderReplacement());
        int roomCountValue = dungeon.getConfig().getInt("roomcount");
        if(roomCountValue > 64) {
            roomCountValue = 64;
        } else if(roomCountValue < 1) roomCountValue = 1;
        roomCountIcon.setAmount(roomCountValue);
        setItem(inventorySize * 9 - 5, roomCountIcon);
    }

    public void updateRoomRecycling() {
        setItem(inventorySize * 9 - 3, "navbar.roomrecyling.r" + dungeon.getConfig().getBoolean("roomrecyling"));
    }

    /*
     * Interface
     */
    @Override
    public DungeonManager getDungeon() {
        return dungeon;
    }

    @Override
    public DungeonGUI getGUI() {
        return this;
    }
}
