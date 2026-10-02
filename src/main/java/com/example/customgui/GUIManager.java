package com.example.customgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds and opens the Inventory for a configured GUI.
 */
public class GUIManager {

    private final CustomGUIPlugin plugin;

    public GUIManager(CustomGUIPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens the GUI named {@code guiName} for {@code player}.
     */
    public void openGUI(Player player, String guiName) {
        GUIConfig gui = plugin.getConfigManager().getGUI(guiName);
        if (gui == null) {
            player.sendMessage(ChatColor.RED + "GUI not found: " + guiName);
            return;
        }

        int slots = gui.getSlots();
        String title = ChatColor.translateAlternateColorCodes('&', gui.getTitle());

        GUIHolder holder = new GUIHolder(guiName);
        Inventory inv = Bukkit.createInventory(holder, slots, title);

        for (GUIItem item : gui.getItems()) {
            ItemStack stack = buildItemStack(item);
            int slot = item.getSlot();
            if (slot >= 0 && slot < slots) {
                inv.setItem(slot, stack);
            } else {
                plugin.getLogger().warning(
                        "GUI '" + guiName + "' item '" + item.getName()
                                + "' has slot " + slot + " out of range (0.." + (slots - 1) + ")");
            }
        }

        player.openInventory(inv);
    }

    private ItemStack buildItemStack(GUIItem item) {
        Material material = Material.matchMaterial(item.getMaterial());
        if (material == null) {
            plugin.getLogger().warning("Unknown material '" + item.getMaterial()
                    + "' for item '" + item.getName() + "', using STONE.");
            material = Material.STONE;
        }

        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            // Display name
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', item.getDisplayName()));

            // Lore (already a list)
            List<String> loreLines = item.getLoreLines();
            if (loreLines != null && !loreLines.isEmpty()) {
                List<String> translated = new ArrayList<>();
                for (String line : loreLines) {
                    translated.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(translated);
            }

            // Glow effect: add a fake enchantment and hide the enchantment tooltip
            // so the item glows but doesn't show "Unbreaking I" etc.
            if (item.isEnchanted()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                // Hide enchantment flag (Spigot 1.8.8 ItemMeta supports addItemFlags via Spigot)
                try {
                    meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                } catch (Throwable t) {
                    // HIDE_ENCHANTS exists in 1.8.8 Spigot — fallback ignored
                }
            }

            stack.setItemMeta(meta);
        }
        return stack;
    }
}
