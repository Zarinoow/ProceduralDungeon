package fr.foxelia.tools.minecraft.ui.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.foxelia.proceduraldungeon.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * GUI system for Spigot servers designed by the Foxelia team.
 * <br>License: CC BY-SA 4.0
 * @author Foxelia, Zarinoow, ParadoxalUnivers
 * @version 1.0
 */
public abstract class GUI implements InventoryHolder {

    /*
     * Constants
     */

    /**
     * The path to default GUIs configuration files
     */
    private static final String DEFAULT_CONFIG_FOLDER = "fr/foxelia/proceduraldungeon/gui/config"; // Change this to your package where you store the default files

    /**
     * The plugin instance
     */
    private static JavaPlugin plugin = null;

    /**
     * The GUIs folder
     */
    private static File guiFolder = null;

    /**
     * The inventory of the GUI
     */
    private Inventory guiInventory;

    /**
     * The folder where the GUI configuration files and specific language files are stored
     */
    private File guiFiles;
    private FileConfiguration guiConfig;
    private final Map<String, String> placeholderReplacement;

    /*
     * Constructors
     */

    public GUI(String guiName) {
        if(guiFolder == null) {
            throw new RuntimeException("The GUI system is not initialized");
        }
        this.guiFiles = new File(guiFolder, guiName);
        reloadConfig();
        placeholderReplacement = new HashMap<>();
    }

    /*
     * Getters
     */

    /**
     * @return An {@link Inventory} object representing the GUI
     */
    public Inventory getInventory() {
        return guiInventory;
    }

    public Map<String, String> getPlaceholderReplacement() {
        return new HashMap<>(placeholderReplacement);
    }

    public String getRemplacementOf(String placeholder) {
        if(placeholderReplacement.containsKey(placeholder)) return placeholderReplacement.get(placeholder);
        else return placeholder;
    }

    public String getStringVariable(String path) {
        return guiConfig.getString("variables." + path);
    }

    public List<String> getStringListVariable(String path) {
        return guiConfig.getStringList("variables." + path);
    }

    public boolean getBooleanVariable(String path) {
        return guiConfig.getBoolean("variables." + path);
    }

    /*
     * Setters
     */
    public void addPlaceholder(String placeholder, String replacement) {
        placeholderReplacement.put(placeholder, replacement);
    }

    public void removePlaceholder(String placeholder) {
        if(placeholderReplacement.containsKey(placeholder)) placeholderReplacement.remove(placeholder);
    }

    public void clearAllPlaceholder() {
        placeholderReplacement.clear();
    }

    public void addItems(String... names) {
        addItems(getPlaceholderReplacement(), names);
    }

    public void addItems(Map<String, String> placeholderReplacement, String... names) {
        for(String name : names) {
            guiInventory.addItem(generateItem(name, placeholderReplacement));
        }
    }

    public void addItems(ItemStack... items) {
        guiInventory.addItem(items);
    }

    public void setItem(int slot, String name) {
        setItem(slot, name, getPlaceholderReplacement());
    }

    public void setItem(int slot, String name, Map<String, String> placeholderReplacement) {
        try {
            setItem(slot, generateItem(name, placeholderReplacement));
        } catch (Exception e) {
            Main.getMain().getLogger().severe("Error while setting item " + name + " at slot " + slot + " in GUI " + guiFiles.getName() + ". Stack trace will be printed. Please fix this issue !");
            ItemStack errorItem = new ItemStack(Material.BARRIER);
            ItemMeta errorMeta = errorItem.getItemMeta();
            errorMeta.setDisplayName(ChatColor.RED + "Error : " + name);
            errorItem.setItemMeta(errorMeta);
            setItem(slot, errorItem);
        }

    }

    public void setItem(int slot, ItemStack item) {
        guiInventory.setItem(slot, item);
    }

    /*
     * Functions
     */
    public static void init(JavaPlugin plugin) {
        if(GUI.plugin != null) {
            throw new RuntimeException("The GUI system is already initialized");
        }
        GUI.plugin = plugin;
        guiFolder = new File(plugin.getDataFolder(), "GUIs");
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    public abstract void constructGUI();

    public abstract void onClick(InventoryClickEvent event);

    /**
     * Generate the GUI with a size and a title
     * @param size The size of the GUI (1 to 6)
     * @param title The title of the GUI (no default placeholder)
     */
    public void createInventory(int size, String title) {
        guiInventory = Bukkit.createInventory(this, size * 9, ChatColor.translateAlternateColorCodes('&', title));
    }

    /**
     * Generate the GUI with a size, a title and a custom placeholder replacement
     * @param size The size of the GUI (1 to 6)
     * @param title The title of the GUI (no default placeholder)
     * @param placeholderReplacement The custom placeholder replacement (if you want to use the default placeholder, use {@link #getPlaceholderReplacement()})
     */
    public void createInventory(int size, String title, Map<String, String> placeholderReplacement) {
        createInventory(size, replacePlaceholder(title, placeholderReplacement));
    }

    /**
     * Generate the GUI with a size and a title loaded from the configuration file (no default placeholder)
     * @param size The size of the GUI (1 to 6)
     */
    public void createInventory(int size) {
        createInventory(size, new HashMap<>());
    }

    /**
     * Generate the GUI with a size and a title loaded from the configuration file with a custom placeholder replacement
     * @param size The size of the GUI (1 to 6)
     * @param placeholderReplacement The custom placeholder replacement (if you want to use the default placeholder, use {@link #getPlaceholderReplacement()})
     */
    public void createInventory(int size, Map<String, String> placeholderReplacement) {
        if(guiConfig.get("title") == null) {
            createInventory(size, ChatColor.RED + "Error : No title defined");
        } else {
            createInventory(size, replacePlaceholder(guiConfig.getString("title"), placeholderReplacement));
        }
    }

    /**
     * Generate the GUI with a size and a title loaded from the configuration file (no default placeholder)
     * @param size The size of the GUI (1 to 6)
     * @param titleID The ID of the title in the configuration file (The path of the title is title.{"title0, title1, title2, ..."})
     */
    public void createInventory(int size, int titleID) {
        createInventory(size, titleID, new HashMap<>());
    }

    /**
     * Generate the GUI with a size and a title loaded from the configuration file with a custom placeholder replacement.
     * @param size The size of the GUI (1 to 6)
     * @param titleID The ID of the title in the configuration file (The path of the title is title.{"title0, title1, title2, ..."})
     * @param placeholderReplacement The custom placeholder replacement (if you want to use the default placeholder, use {@link #getPlaceholderReplacement()})
     */

    public void createInventory(int size, int titleID, Map<String, String> placeholderReplacement) {
        if(guiConfig.get("title.") + "title" + titleID == null) {
            createInventory(size, ChatColor.RED + "Error : No title defined");
        } else {
            createInventory(size, guiConfig.getString("title." + "title" + titleID), placeholderReplacement);
        }
    }

    public void reloadConfig() {
        File configFile = new File(guiFiles, "config.yml");
        if(!guiFiles.exists()) {
            guiFiles.mkdirs();
        }
        if(!configFile.exists()) {
            InputStream is = plugin.getResource(DEFAULT_CONFIG_FOLDER + "/" + guiFiles.getName() + "/config.yml");
            if (is == null) return;
            guiConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8));
            try {
                guiConfig.save(configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else guiConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    public void openInventory(HumanEntity human) {
        constructGUI();
        if(guiInventory == null) {
            createInventory(1, ChatColor.RED + "Error : GUI not initialized");
        }
        human.openInventory(guiInventory);
    }

    public void closeInventory() {
        for(HumanEntity viewer : new ArrayList<>(guiInventory.getViewers())) {
            viewer.closeInventory();
        }
    }

    public final void fillEmpty(ItemStack item) {
        for(int i = 0; i < guiInventory.getSize(); i++) {
            if(guiInventory.getItem(i) == null) {
                guiInventory.setItem(i, item);
            }
        }
    }

    public ItemStack generateItem(String item, Map<String, String> placeholderReplacement) {
        String itemPath = "items." + item;
        if(guiConfig.get(itemPath + ".material") == null) return null;
        ItemStack generatedItem = null;
        try {
            generatedItem = new ItemStack(Material.valueOf(guiConfig.getString(itemPath + ".material")));
        } catch (IllegalArgumentException e) {
            Main.getMain().getLogger().severe("Invalid material " + guiConfig.getString(itemPath + ".material") + " at " + itemPath + " section. Stack trace will be printed. Please fix this issue !");
            throw new RuntimeException(e);
        }

        if(guiConfig.get(itemPath + ".amount") != null) {
            generatedItem.setAmount(guiConfig.getInt(itemPath + ".amount"));
        }

        ItemMeta itemMeta = generatedItem.getItemMeta();

        if(guiConfig.get(itemPath + ".name") != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', replacePlaceholder(guiConfig.getString(itemPath + ".name"), placeholderReplacement)));
        }

        if(guiConfig.get(itemPath + ".lore") != null) {
            List<String> itemLore = new ArrayList<>();
            for(String line : guiConfig.getStringList(itemPath + ".lore")) {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', replacePlaceholder(line, placeholderReplacement)));
            }
            itemMeta.setLore(itemLore);
        }
        if(guiConfig.get(itemPath + ".custommodeldata") != null) {
            itemMeta.setCustomModelData(guiConfig.getInt(itemPath + ".custommodeldata"));
        }

        generatedItem.setItemMeta(itemMeta);

        if(guiConfig.get(itemPath + ".data") != null){
            if(generatedItem.getType().equals(Material.PLAYER_HEAD)) {
                String data = replacePlaceholder(guiConfig.getString(itemPath + ".data"), placeholderReplacement);
                SkullMeta sm = (SkullMeta) generatedItem.getItemMeta();
                if(data.contains("{PLAYER}:")) {
                    data = data.replace("{PLAYER}:", "");
                    UUID ofUUID = UUID.fromString(data);
                    if(ofUUID != null) {
                        OfflinePlayer ofPlayer = Bukkit.getOfflinePlayer(ofUUID);
                        sm.setOwningPlayer(ofPlayer);
                    }
                } else {
                    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                    profile.getProperties().put("textures", new Property("textures", data));
                    Field profileField = null;
                    try {
                        profileField = sm.getClass().getDeclaredField("profile");
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    profileField.setAccessible(true);
                    try {
                        profileField.set(sm, profile);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                generatedItem.setItemMeta(sm);
            }
        }
        return generatedItem;
    }

    public ItemStack copyDisplayMeta(ItemStack source, ItemStack target) {
        ItemStack targetItem = target.clone();
        ItemMeta sourceMeta = source.getItemMeta();
        ItemMeta targetMeta = target.getItemMeta();
        if(sourceMeta.hasDisplayName()) targetMeta.setDisplayName(sourceMeta.getDisplayName());
        if(sourceMeta.hasLore()) targetMeta.setLore(sourceMeta.getLore());
        if(sourceMeta.hasCustomModelData()) targetMeta.setCustomModelData(sourceMeta.getCustomModelData());
        targetItem.setItemMeta(targetMeta);
        return targetItem;
    }

    /*
     * Private functions
     */

    private static String replacePlaceholder(String toReplace, Map<String, String> placeholderReplacement) {
        String str = toReplace;
        for(Map.Entry<String, String> en : placeholderReplacement.entrySet()){
            str = str.replace(en.getKey(), en.getValue());
        }
        return str;
    }
}

/**
 * <br>GUI system for Spigot servers designed by the Foxelia team.
 * <br>License: CC BY-SA 4.0
 * @author Foxelia, Zarinoow, ParadoxalUnivers
 * @version 1.0
 */
class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().getHolder() instanceof GUI) {
            GUI gui = (GUI) event.getInventory().getHolder();
            if(event.getClickedInventory() == null || !event.getClickedInventory().equals(gui.getInventory())) return;
            gui.onClick(event);
        }
    }
}
