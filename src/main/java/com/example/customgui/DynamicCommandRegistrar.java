package com.example.customgui;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Registers and unregisters commands at runtime via the server's CommandMap.
 *
 * Required because plugin.yml only supports statically known commands, but
 * the user wants each GUI to dynamically define its own command on server
 * restart based on config.yml.
 *
 * Uses reflection to access CraftBukkit's commandMap field, which is the
 * standard technique for dynamic command registration on Spigot 1.8.8.
 */
public class DynamicCommandRegistrar {

    private final Plugin plugin;
    private final Map<String, Command> registeredCommands = new HashMap<>();
    private final CommandMap commandMap;

    public DynamicCommandRegistrar(Plugin plugin) {
        this.plugin = plugin;
        this.commandMap = resolveCommandMap();
    }

    private CommandMap resolveCommandMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());
        } catch (Exception e) {
            plugin.getLogger().severe("Could not access server commandMap: " + e.getMessage());
            return null;
        }
    }

    /**
     * Registers a command. The name may start with "/" — the slash is stripped
     * before registration. Each call replaces any previously registered command
     * with the same name.
     */
    public void registerCommand(String name, Command command) {
        if (commandMap == null) {
            plugin.getLogger().severe("Cannot register command '" + name + "' — commandMap unavailable.");
            return;
        }
        if (name == null || name.isEmpty()) {
            plugin.getLogger().severe("Cannot register command with empty name.");
            return;
        }
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        // unregister previous registration with the same name (for reloads)
        if (registeredCommands.containsKey(name.toLowerCase())) {
            Command old = registeredCommands.remove(name.toLowerCase());
            unregisterFromKnownCommands(old);
        }

        commandMap.register(plugin.getName(), command);
        registeredCommands.put(name.toLowerCase(), command);
        plugin.getLogger().info("Registered command '/" + name + "' for GUI.");
    }

    /**
     * Unregisters all commands previously registered via this registrar.
     */
    public void unregisterAll() {
        for (Command cmd : new java.util.ArrayList<>(registeredCommands.values())) {
            unregisterFromKnownCommands(cmd);
        }
        registeredCommands.clear();
    }

    @SuppressWarnings("unchecked")
    private void unregisterFromKnownCommands(Command cmd) {
        if (commandMap == null || cmd == null) return;
        try {
            cmd.unregister(commandMap);
            if (commandMap instanceof SimpleCommandMap) {
                Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
                f.setAccessible(true);
                Map<String, Command> known = (Map<String, Command>) f.get(commandMap);
                // Remove both the bare name and the "plugin:name" alias
                known.remove(cmd.getName());
                known.remove(plugin.getName().toLowerCase() + ":" + cmd.getName());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to unregister command '" + cmd.getName() + "': " + e.getMessage());
        }
    }
}
