package com.example.customgui;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main plugin class for CustomGUI.
 *
 * Loads GUI definitions from config.yml, dynamically registers
 * one command + one permission per GUI, and routes inventory
 * clicks to the configured item-commands.
 */
public class CustomGUIPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private GUIManager guiManager;
    private DynamicCommandRegistrar commandRegistrar;

    @Override
    public void onEnable() {
        // Save default config.yml if none exists
        saveDefaultConfig();

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.guiManager = new GUIManager(this);
        this.commandRegistrar = new DynamicCommandRegistrar(this);

        // Register dynamic commands + permissions for every GUI in config
        registerGUICommands();

        // Register the inventory click listener
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        // Built-in /closemenu command: just close the player's inventory
        getCommand("closemenu").setExecutor((sender, label, cmd, args) -> {
            if (sender instanceof Player) {
                ((Player) sender).closeInventory();
            }
            return true;
        });

        // Built-in /customgui reload command
        getCommand("customgui").setExecutor(this::onAdminCommand);

        getLogger().info("CustomGUI enabled! Loaded " + configManager.getGUIs().size() + " GUI(s).");
    }

    private boolean onAdminCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!sender.hasPermission("customgui.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /customgui reload");
            return true;
        }

        // Unregister old dynamic commands
        commandRegistrar.unregisterAll();

        // Remove old dynamic permissions (only those we created)
        List<Permission> toRemove = new ArrayList<>();
        for (Permission p : getServer().getPluginManager().getPermissions()) {
            // GUI permissions are stored in each GUIConfig; remove any that match the pattern
            // We use a marker prefix "gui." plus the GUI name to identify them
            if (p.getName().startsWith("gui.")) {
                toRemove.add(p);
            }
        }
        for (Permission p : toRemove) {
            getServer().getPluginManager().removePermission(p);
        }

        // Reload config from disk
        reloadConfig();
        configManager.load();

        // Re-register commands + permissions for new config
        registerGUICommands();

        sender.sendMessage(ChatColor.GREEN + "CustomGUI reloaded! "
                + configManager.getGUIs().size() + " GUI(s) loaded.");
        return true;
    }

    /**
     * Dynamically registers a command and a permission for each GUI in config.
     * Called on enable and on /customgui reload.
     */
    private void registerGUICommands() {
        for (Map.Entry<String, GUIConfig> entry : configManager.getGUIs().entrySet()) {
            String guiName = entry.getKey();
            GUIConfig gui = entry.getValue();

            // Register the permission (idempotent)
            Permission perm = new Permission(gui.getPermission(), PermissionDefault.OP);
            try {
                getServer().getPluginManager().addPermission(perm);
            } catch (IllegalArgumentException ignored) {
                // permission already registered; fine
            }

            // Register the command dynamically via the server's CommandMap
            GUICommand guiCommand = new GUICommand(this, guiName, gui);
            commandRegistrar.registerCommand(gui.getCommandName(), guiCommand);
        }
    }

    @Override
    public void onDisable() {
        if (commandRegistrar != null) {
            commandRegistrar.unregisterAll();
        }
        getLogger().info("CustomGUI disabled.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }
}
