package com.example.customgui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Custom InventoryHolder used to tag inventories created by this plugin.
 *
 * We tag every GUI inventory with one of these so the InventoryListener can:
 *   1. Recognise that the click happened in one of our GUIs.
 *   2. Look up which GUI by name (so we can find the matching GUIItem).
 */
public class GUIHolder implements InventoryHolder {

    private final String guiName;

    public GUIHolder(String guiName) {
        this.guiName = guiName;
    }

    public String getGuiName() {
        return guiName;
    }

    @Override
    public Inventory getInventory() {
        // Not used; required by the interface.
        return null;
    }
}
