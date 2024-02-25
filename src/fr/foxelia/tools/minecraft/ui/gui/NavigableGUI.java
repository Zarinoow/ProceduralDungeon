package fr.foxelia.tools.minecraft.ui.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>Multi pages GUI system for Spigot servers designed by the Foxelia team.
 * <br>License: CC BY-SA 4.0
 * @author Foxelia, Zarinoow, ParadoxalUnivers
 * @version 1.0
 */
public abstract class NavigableGUI<ItemListed> extends GUI {

    /*
     * Constantes
     */
    private int page = 0;

    private final List<ItemListed> displayedList = new ArrayList<>();

    // Constructeur
    public NavigableGUI(String guiName) {
        super(guiName);
    }

    /*
     * Fonctions
     */

    /**
     * Add the navigation buttons to the inventory.
     * You must have initialized the inventory before calling this function.
     * <br>It will call the {@link #addNextButton(int)}, {@link #addPreviousButton(int)} and {@link #addCustomButtons(int)} functions.
     */
    public void addNavigationButtons() {
        if(getInventory() == null) throw new NullPointerException("You must set the inventory before adding navigation buttons");
        int bottomRow = getInventory().getSize() - 9;
        addNextButton(bottomRow);
        addPreviousButton(bottomRow);
        addCustomButtons(bottomRow);
    }

    /**
     * Add the next button to the inventory
     * <br>To correctly place the button, use the bottomRow + n.
     * <br>n is the slot number where you want to place the button.
     * @param bottomRow The starting int of the bottom row of the inventory
     */
    public abstract void addNextButton(int bottomRow);

    /**
     * Add the previous button to the inventory
     * <br>To correctly place the button, use the bottomRow + n.
     * <br>n is the slot number where you want to place the button.
     * @param bottomRow The starting int of the bottom row of the inventory
     */
    public abstract void addPreviousButton(int bottomRow);

    /**
     * Add your own custom buttons to the inventory
     * <br>To correctly place the button, use the bottomRow + n.
     * <br>n is the slot number where you want to place the button.
     * @param bottomRow The starting int of the bottom row of the inventory
     */
    public abstract void addCustomButtons(int bottomRow);

    /**
     * Use this function to initialize the displayedList and add the items to be processed.
     */
    public abstract void setupDisplayedList();

    /**
     * You can update the displayedList with this function.
     * First it will clear the displayedList, then it will call the {@link #setupDisplayedList()} function.
     */
    public void refreshDisplayedList() {
        displayedList.clear();
        setupDisplayedList();
    }

    /**
     * Use this function to treat individual items that will be displayed in the inventory.
     * @return An {@link ItemStack} which will be displayed in the inventory.
     */
    public abstract ItemStack processItem(ItemListed item);

    /**
     * This function is used to display items in the inventory.
     * It will call the {@link #processItem(ItemListed)} function for each item.
     * @param page The page of the inventory to display
     */
    public void goToPage(int page) {
        if(page < 0 || page == getCurrentPage()) return;
        if(page >= Math.ceil((double)displayedList.size() / (getInventory().getSize() - 9))) {
            goToPage(page - 1);
            return;
        }
        this.page = page;

        Inventory oldInventory = getInventory();
        constructGUI();
        for(HumanEntity viewer : new ArrayList<>(oldInventory.getViewers())) {
            viewer.openInventory(getInventory());
        }
    }

    /**
     * This function is used to display items in the inventory.
     * It will call the {@link #processItem(ItemListed)} function for each item.
     */
    public void fillInventory() {
        for(int i = 0; i < getInventory().getSize() - 9; i++) {
            int index = page * (getInventory().getSize() - 9) + i;
            if(index >= displayedList.size()) break;
            ItemStack item = processItem(displayedList.get(index));
            if(item == null) continue;
            getInventory().setItem(i, item);
        }
    }

    /**
     * Go to the next page of the GUI
     */
    public void goToNextPage() {
        goToPage(getCurrentPage() + 1);
    }

    /**
     * Go to the previous page of the GUI
     */
    public void goToPreviousPage() {
        goToPage(getCurrentPage() - 1);
    }

    /*
     * Getters
     */

    /**
     * Get the current page of the GUI
     * @return The current page of the GUI
     */
    public int getCurrentPage() {
        return page;
    }

    /**
     * Get the displayedList
     * @return The displayedList
     */
    public List<ItemListed> getDisplayedList() {
        return displayedList;
    }
}
