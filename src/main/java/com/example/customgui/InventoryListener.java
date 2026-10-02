package com.example.customgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * Listens for clicks inside any inventory whose holder is a {@link GUIHolder}.
 *
 * Behavior per click (LEFT click only triggers actions):
 *   1. The click is always cancelled (items are decorative).
 *   2. If close-on-click is true, the inventory is closed.
 *   3. Based on action-type:
 *        command  → execute command (PLAYER or CONSOLE)
 *        message  → send message to player
 *        both     → send message, then execute command
 *        none     → nothing happens (decoration)
 *   4. Message + command run on the next server tick (1 tick later) so that
 *      opening a new GUI from a command doesn't conflict with the close.
 *
 * The <player> placeholder is replaced with the player's name in both
 * message and command text.
 */
public class InventoryListener implements Listener {

    private final CustomGUIPlugin plugin;

    public InventoryListener(CustomGUIPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() == null) return;
        if (!(event.getInventory().getHolder() instanceof GUIHolder)) return;

        event.setCancelled(true);

        if (event.getClick() != ClickType.LEFT) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0 || rawSlot >= event.getInventory().getSize()) {
            return; // clicked in the player's own inventory area
        }

        GUIHolder holder = (GUIHolder) event.getInventory().getHolder();
        String guiName = holder.getGuiName();
        GUIConfig guiConfig = plugin.getConfigManager().getGUI(guiName);
        if (guiConfig == null) return;

        GUIItem matched = null;
        for (GUIItem item : guiConfig.getItems()) {
            if (item.getSlot() == rawSlot) {
                matched = item;
                break;
            }
        }
        if (matched == null) return;

        // Step 1: optionally close
        boolean willClose = matched.isCloseOnClick();
        if (willClose) {
            player.closeInventory();
        }

        // Step 2: schedule the action 1 tick later (avoids conflicts with
        // openInventory when the command opens another GUI)
        final Player p = player;
        final GUIItem item = matched;
        Bukkit.getScheduler().runTaskLater(plugin, () -> runAction(p, item), 1L);
    }

    private void runAction(Player player, GUIItem item) {
        String actionType = item.getActionType() == null ? "none" : item.getActionType().toLowerCase();

        switch (actionType) {
            case "command":
                executeCommand(player, item);
                break;
            case "message":
                sendMessage(player, item);
                break;
            case "both":
                sendMessage(player, item);
                executeCommand(player, item);
                break;
            case "none":
            default:
                // decorative — do nothing
                break;
        }
    }

    /**
     * Sends the configured message lines to the player.
     * Replaces <player> with the player's name. Translates color codes.
     */
    private void sendMessage(Player player, GUIItem item) {
        List<String> lines = item.getMessage();
        if (lines == null || lines.isEmpty()) {
            plugin.getLogger().warning(
                "Item '" + item.getName() + "' action requires a message but none is defined.");
            return;
        }
        for (String line : lines) {
            if (line == null) continue;
            String processed = line.replace("<player>", player.getName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', processed));
        }
    }

    /**
     * Executes the configured command. The command-type (PLAYER or CONSOLE)
     * decides who runs it. <player> is replaced with the player's name.
     */
    private void executeCommand(Player player, GUIItem item) {
        String cmd = item.getCommand();
        if (cmd == null || cmd.isEmpty()) {
            plugin.getLogger().warning(
                "Item '" + item.getName() + "' action requires a command but none is defined.");
            return;
        }
        String processed = cmd.replace("<player>", player.getName()).trim();
        if (processed.startsWith("/")) processed = processed.substring(1);
        if (processed.isEmpty()) return;

        String type = item.getCommandType() == null ? "PLAYER" : item.getCommandType().toUpperCase();
        if ("CONSOLE".equals(type)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processed);
        } else {
            player.performCommand(processed);
        }
    }
}
