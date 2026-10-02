package com.example.customgui;

import java.util.ArrayList;
import java.util.List;

/**
 * One clickable item inside a GUI.
 *
 * Fields:
 *   name          — internal identifier (config key)
 *   material      — Bukkit material name (e.g. "DIAMOND_SWORD")
 *   displayName   — shown under the item (color codes & work)
 *   slot          — 0 .. (totalSlots - 1)
 *   loreLines     — list of lore lines (YAML list). Backwards compatible:
 *                   if config supplies a single string with \n, it is split.
 *   enchanted     — if true, the item glows (fake enchantment, no visible name)
 *   closeOnClick  — if true, the GUI closes when this item is clicked
 *   actionType    — one of: command, message, both, none
 *   message       — message lines sent to the player (used when actionType
 *                   is "message" or "both"). Can be empty.
 *   command       — command text without prefix (e.g. "spawn", "give <player> x")
 *   commandType   — PLAYER or CONSOLE (used when actionType is "command" or "both")
 */
public class GUIItem {

    private String name;
    private String material;
    private String displayName;
    private int slot;
    private List<String> loreLines = new ArrayList<>();
    private boolean enchanted = false;
    private boolean closeOnClick = true;
    private String actionType = "none";       // default safest
    private List<String> message = new ArrayList<>();
    private String command = "";
    private String commandType = "PLAYER";    // PLAYER or CONSOLE

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }

    public List<String> getLoreLines() { return loreLines; }
    public void setLoreLines(List<String> loreLines) {
        this.loreLines = loreLines == null ? new ArrayList<>() : loreLines;
    }

    public boolean isEnchanted() { return enchanted; }
    public void setEnchanted(boolean enchanted) { this.enchanted = enchanted; }

    public boolean isCloseOnClick() { return closeOnClick; }
    public void setCloseOnClick(boolean closeOnClick) { this.closeOnClick = closeOnClick; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public List<String> getMessage() { return message; }
    public void setMessage(List<String> message) {
        this.message = message == null ? new ArrayList<>() : message;
    }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command == null ? "" : command; }

    public String getCommandType() { return commandType; }
    public void setCommandType(String commandType) { this.commandType = commandType; }
}
