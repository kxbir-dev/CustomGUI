package com.example.customgui;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory representation of one GUI definition loaded from config.yml.
 */
public class GUIConfig {

    private String name;
    private String commandName;     // e.g. "/servers"
    private String permission;      // e.g. "gui.serverselector"
    private String title;           // e.g. "&6&lServer Selector"
    private String guiSlots;        // e.g. "9x3"
    private final List<GUIItem> items = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCommandName() { return commandName; }
    public void setCommandName(String commandName) { this.commandName = commandName; }

    /**
     * The bare command name (no leading slash) used for dynamic registration.
     */
    public String getCommandId() {
        if (commandName == null) return name;
        return commandName.startsWith("/") ? commandName.substring(1) : commandName;
    }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGuiSlots() { return guiSlots; }
    public void setGuiSlots(String guiSlots) { this.guiSlots = guiSlots; }

    public List<GUIItem> getItems() { return items; }

    /**
     * Parses "WxH" (e.g. "9x3") into the total slot count.
     * Minecraft only supports 9 columns, so width must always be 9.
     * Valid: 9x1, 9x2, 9x3, 9x4, 9x5, 9x6.
     * Falls back to 27 (9x3) on invalid input.
     */
    public int getSlots() {
        if (guiSlots == null) return 27;
        String[] parts = guiSlots.split("x");
        if (parts.length != 2) return 27;
        try {
            int horizontal = Integer.parseInt(parts[0].trim());
            int vertical = Integer.parseInt(parts[1].trim());
            if (horizontal != 9) return 27; // enforce 9 columns
            if (vertical < 1 || vertical > 6) return 27;
            return horizontal * vertical;
        } catch (NumberFormatException e) {
            return 27;
        }
    }
}
