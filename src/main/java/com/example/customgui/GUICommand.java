package com.example.customgui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * A dynamically registered command that opens a GUI when executed by a player.
 *
 * One instance of this class is created per GUI at startup (and on /customgui reload).
 */
public class GUICommand extends Command {

    private final CustomGUIPlugin plugin;
    private final String guiName;
    private final GUIConfig guiConfig;

    public GUICommand(CustomGUIPlugin plugin, String guiName, GUIConfig guiConfig) {
        super(
                guiConfig.getCommandId(),
                "Open the " + guiName + " GUI",
                "/" + guiConfig.getCommandId(),
                new ArrayList<>()
        );
        this.plugin = plugin;
        this.guiName = guiName;
        this.guiConfig = guiConfig;
        setPermission(guiConfig.getPermission());
        // Don't auto-send the no-permission message — we handle it ourselves for colour
        setPermissionMessage(org.bukkit.ChatColor.RED + "You don't have permission to use this GUI.");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "Only players can open GUIs.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(guiConfig.getPermission())) {
            player.sendMessage(org.bukkit.ChatColor.RED + "You don't have permission to use this GUI.");
            return true;
        }

        plugin.getGuiManager().openGUI(player, guiName);
        return true;
    }
}
