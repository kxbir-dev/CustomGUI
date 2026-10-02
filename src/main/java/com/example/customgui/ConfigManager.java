package com.example.customgui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads and stores GUI configurations from config.yml.
 *
 * Each top-level key in config.yml is treated as one GUI definition.
 *
 * Supports both the new (recommended) item format:
 *   items:
 *     <name>:
 *       material: ...
 *       action-type: command|message|both|none
 *       command: "..."
 *       command-type: PLAYER|CONSOLE
 *       message: [...]
 *       ...
 *
 * And the legacy format for backwards compatibility:
 *   items:
 *     <name>:
 *       material: ...
 *       item-command: "[PLAYER]/spawn"
 */
public class ConfigManager {

    private final CustomGUIPlugin plugin;
    private final Map<String, GUIConfig> guis = new HashMap<>();

    public ConfigManager(CustomGUIPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        guis.clear();
        FileConfiguration config = plugin.getConfig();

        for (String guiName : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(guiName);
            if (section == null) continue;

            GUIConfig gui = new GUIConfig();
            gui.setName(guiName);
            gui.setCommandName(section.getString("command", "/" + guiName));
            gui.setPermission(section.getString("permission", "gui." + guiName));
            gui.setTitle(section.getString("title", guiName));
            gui.setGuiSlots(section.getString("gui-slots", "9x3"));

            ConfigurationSection itemsSection = section.getConfigurationSection("items");
            if (itemsSection != null) {
                for (String itemName : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
                    if (itemSection == null) continue;

                    GUIItem item = loadItem(itemName, itemSection);
                    gui.getItems().add(item);
                }
            }

            guis.put(guiName, gui);
        }

        plugin.getLogger().info("Loaded " + guis.size() + " GUI(s) from config.");
    }

    private GUIItem loadItem(String itemName, ConfigurationSection s) {
        GUIItem item = new GUIItem();
        item.setName(itemName);
        item.setMaterial(s.getString("material", "STONE"));
        item.setDisplayName(s.getString("name", itemName));
        item.setSlot(s.getInt("slot", 0));
        item.setEnchanted(s.getBoolean("enchanted", false));
        item.setCloseOnClick(s.getBoolean("close-on-click", true));

        // Lore: support both YAML list and legacy \n string
        if (s.isList("lore")) {
            item.setLoreLines(s.getStringList("lore"));
        } else if (s.isString("lore")) {
            String legacy = s.getString("lore", "");
            item.setLoreLines(splitLegacyLore(legacy));
        }

        // Message: support both list and single string
        if (s.isList("message")) {
            item.setMessage(s.getStringList("message"));
        } else if (s.isString("message")) {
            String msg = s.getString("message", "");
            if (!msg.isEmpty()) {
                item.setMessage(Arrays.asList(msg));
            }
        }

        // Action type: validate
        String actionType = s.getString("action-type", null);

        // Backwards-compat: if action-type is missing but the legacy item-command
        // field is present, derive action-type/command/command-type from it.
        if (actionType == null && s.contains("item-command")) {
            String legacy = s.getString("item-command", "");
            parseLegacyItemCommand(legacy, item);
            plugin.getLogger().warning(
                "Item '" + itemName + "' uses the legacy 'item-command' format. " +
                "Please migrate to action-type/command/command-type.");
        } else if (actionType == null) {
            item.setActionType("none");
        } else {
            String normalized = actionType.toLowerCase().trim();
            if (!normalized.equals("command") && !normalized.equals("message")
                    && !normalized.equals("both") && !normalized.equals("none")) {
                plugin.getLogger().warning(
                    "Item '" + itemName + "' has invalid action-type '" + actionType
                    + "'. Falling back to 'none'.");
                item.setActionType("none");
            } else {
                item.setActionType(normalized);
            }
        }

        // Command + command-type (only used if action-type is command or both)
        item.setCommand(s.getString("command", ""));
        String cmdType = s.getString("command-type", "PLAYER");
        if (!cmdType.equalsIgnoreCase("PLAYER") && !cmdType.equalsIgnoreCase("CONSOLE")) {
            plugin.getLogger().warning(
                "Item '" + itemName + "' has invalid command-type '" + cmdType
                + "'. Falling back to PLAYER.");
            item.setCommandType("PLAYER");
        } else {
            item.setCommandType(cmdType.toUpperCase());
        }

        return item;
    }

    /**
     * Splits a legacy lore string (with \n) into a list of lines.
     */
    private List<String> splitLegacyLore(String legacy) {
        List<String> out = new ArrayList<>();
        if (legacy == null || legacy.isEmpty()) return out;
        for (String line : legacy.split("\n")) {
            out.add(line);
        }
        return out;
    }

    /**
     * Parses the legacy item-command format "[PLAYER]/cmd" or "[CONSOLE]/cmd"
     * and writes the equivalent action-type, command, and command-type onto
     * the given GUIItem.
     */
    private void parseLegacyItemCommand(String legacy, GUIItem item) {
        if (legacy == null || legacy.isEmpty()) {
            item.setActionType("none");
            return;
        }
        String processed = legacy.trim();
        String cmdType = "PLAYER";
        if (processed.startsWith("[CONSOLE]")) {
            cmdType = "CONSOLE";
            processed = processed.substring("[CONSOLE]".length()).trim();
        } else if (processed.startsWith("[PLAYER]")) {
            cmdType = "PLAYER";
            processed = processed.substring("[PLAYER]".length()).trim();
        }
        if (processed.startsWith("/")) processed = processed.substring(1);
        item.setCommand(processed);
        item.setCommandType(cmdType);
        item.setActionType("command");
    }

    public Map<String, GUIConfig> getGUIs() { return guis; }
    public GUIConfig getGUI(String name) { return guis.get(name); }
}
