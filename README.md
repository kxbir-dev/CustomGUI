# CustomGUI

Create fully custom Minecraft GUI menus — straight from `config.yml`. No code, no recompiles.

![Version](https://img.shields.io/badge/Minecraft-1.8.8-blue)
![Platform](https://img.shields.io/badge/Platform-Spigot%20%7C%20Bukkit-yellow)
![Java](https://img.shields.io/badge/Java-8%2B-orange)
![License](https://img.shields.io/badge/License-MIT-green)

Build server-selector menus, kit claimers, admin panels, warp menus, and more — without touching a single `.java` file.

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Screenshots / Visual Mockups](#screenshots--visual-mockups)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Configuration Reference](#configuration-reference)
  - [Per-GUI Fields](#per-gui-fields)
  - [Per-Item Fields](#per-item-fields)
  - [Action Types](#action-types)
  - [Placeholders](#placeholders)
  - [Legacy Format (Backwards Compatible)](#legacy-format-backwards-compatible)
- [Full Example Config](#full-example-config)
- [Commands & Permissions](#commands--permissions)
- [Building from Source](#building-from-source)
  - [Prerequisites](#prerequisites)
  - [Build Steps](#build-steps)
- [Project Structure](#project-structure)
- [Architecture (For Developers)](#architecture-for-developers)
- [FAQ / Troubleshooting](#faq--troubleshooting)
- [Compatibility Notes](#compatibility-notes)
- [License](#license)
- [Credits](#credits)

---

## Overview

**CustomGUI** is a lightweight Spigot/Bukkit plugin for Minecraft **1.8.8** that lets server owners design unlimited custom inventory-based GUI menus purely through `config.yml`. There is no need to write Java, compile a plugin, or restart with custom code — you define menus, items, slots, actions, and placeholders in a single YAML file, and CustomGUI handles the rest at runtime.

Whether you're building a hub server-selector, a kit claim interface, an admin control panel, or a warp menu, CustomGUI gives you the building blocks without the boilerplate.

---

## Key Features

- **100% Config-Driven** — Define every GUI, item, and action in `config.yml`.
- **Unlimited GUIs** — Create as many menus as you want; each is addressable by name.
- **Custom Inventory Sizes** — 9, 18, 27, 36, 45, or 54 slots.
- **Rich Item Customization** — Material, data value, amount, display name, lore, enchantments, and NBT-style flags.
- **Powerful Action System** — Run commands, open other GUIs, send messages, close inventories, give items, and more.
- **Placeholder Support** — Built-in placeholders plus optional PlaceholderAPI integration.
- **Permission Per GUI & Per Item** — Lock menus and items behind permissions.
- **Click Type Awareness** — Distinguish left-click, right-click, shift-click, etc.
- **Legacy Format Support** — Old configs continue to work alongside the new format.
- **Lightweight** — Minimal performance overhead; menus are cached and re-rendered only when needed.

---

## Screenshots / Visual Mockups

```
┌─────────────────────────────────────┐
│        Server Selector              │
├─────┬─────┬─────┬─────┬─────┬─────┬─────┤
│ [A] │ [B] │ [C] │     │     │     │     │
├─────┼─────┼─────┼─────┼─────┼─────┼─────┤
│     │     │     │     │     │     │     │
├─────┼─────┼─────┼─────┼─────┼─────┼─────┤
│     │     │     │     │     │     │ [X] │
└─────┴─────┴─────┴─────┴─────┴─────┴─────┘
```

```
┌─────────────────────────────────────┐
│        Kit Claim Menu               │
├─────┬─────┬─────┬─────┬─────┬─────┬─────┤
│     │ [S] │ [W] │ [A] │     │     │     │
├─────┼─────┼─────┼─────┼─────┼─────┼─────┤
│     │     │     │     │     │     │     │
└─────┴─────┴─────┴─────┴─────┴─────┴─────┘
```

*Visual mockups shown for layout reference. In-game appearance depends on your item choices.*

---

## Requirements

- **Minecraft:** 1.8.8 (Spigot or CraftBukkit)
- **Java:** 8 or higher
- **Server Software:** Spigot 1.8.8 / Bukkit 1.8.8 (other 1.8.x versions may work)
- **Optional:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for extended placeholders

---

## Installation

1. Download `CustomGUI.jar` from the releases page.
2. Drop the JAR into your server's `plugins/` directory.
3. Restart or reload the server (`/reload` is not recommended for production).
4. Edit `plugins/CustomGUI/config.yml` to define your GUIs.
5. Run `/customgui reload` to apply changes without a restart.

---

## Quick Start

The fastest way to see CustomGUI in action:

1. Open `plugins/CustomGUI/config.yml`.
2. Add a minimal GUI:

```yaml
guis:
  example:
    title: "&6Example Menu"
    size: 27
    items:
      diamond:
        material: DIAMOND
        slot: 13
        name: "&bClick me!"
        lore:
          - "&7This is an example item."
        actions:
          - "[MESSAGE] &aYou clicked the diamond!"
```

3. Save the file.
4. In-game, run `/customgui open example`.
5. You should see a 27-slot menu with a diamond in the center that messages you when clicked.

---

## Configuration Reference

### Per-GUI Fields

| Field | Type | Description |
|---|---|---|
| `title` | String | Inventory title. Supports color codes and placeholders. |
| `size` | Integer | Number of slots. Must be a multiple of 9 (9–54). |
| `permission` | String (optional) | Permission required to open this GUI. |
| `items` | Section | Map of item keys → item definitions. |
| `open-command` | String (optional) | Registers a command that opens this GUI (e.g., `shop`). |
| `fill` | Section (optional) | Default filler item applied to all empty slots. |

### Per-Item Fields

| Field | Type | Description |
|---|---|---|
| `material` | String | Bukkit material name (e.g., `DIAMOND`, `STAINED_GLASS_PANE`). |
| `data` | Integer (optional) | Data/durability value (e.g., `7` for light gray pane). |
| `amount` | Integer (optional) | Stack size (default: `1`). |
| `slot` | Integer or List | Slot index, or list of slots. |
| `name` | String (optional) | Display name. Supports color codes and placeholders. |
| `lore` | List<String> (optional) | Lore lines. Supports color codes and placeholders. |
| `enchantments` | Map (optional) | Map of enchantment name → level. |
| `glowing` | Boolean (optional) | Adds enchant glow without an enchantment. |
| `permission` | String (optional) | Permission required to see/use this item. |
| `actions` | List<String> | Actions executed on click. See [Action Types](#action-types). |

### Action Types

Actions are written as strings in the format `[TYPE] value`.

| Action | Syntax | Description |
|---|---|---|
| Message | `[MESSAGE] &aHello!` | Sends a chat message to the player. |
| Command (player) | `[COMMAND] spawn` | Runs a command as the player. |
| Command (console) | `[CONSOLE] give %player% diamond 1` | Runs a command from the console. |
| Open GUI | `[OPEN] other_gui` | Opens another CustomGUI menu. |
| Close | `[CLOSE]` | Closes the current inventory. |
| Give Item | `[GIVE] DIAMOND 1` | Gives the player an item. |
| Sound | `[SOUND] CLICK 1 1` | Plays a sound (name, volume, pitch). |
| Broadcast | `[BROADCAST] &e%player% clicked!` | Broadcasts a message to all players. |
| Title | `[TITLE] &6Title;&eSubtitle` | Sends a title/subtitle (1.8+). |
| Action Bar | `[ACTIONBAR] &eHello!` | Sends an action bar message (1.8+). |

### Placeholders

| Placeholder | Description |
|---|---|
| `%player%` | The player's name. |
| `%displayname%` | The player's display name. |
| `%world%` | The player's current world. |
| `%online%` | Number of online players. |
| `%balance%` | Player's balance (requires Vault). |
| `%papi_<identifier>%` | Any PlaceholderAPI placeholder (if PAPI is installed). |

### Legacy Format (Backwards Compatible)

Old configs using the flat format are still supported:

```yaml
example:
  title: "&6Legacy Menu"
  size: 9
  items:
    - material: DIAMOND
      slot: 4
      name: "&bLegacy"
      actions:
        - "[MESSAGE] &aLegacy still works!"
```

CustomGUI automatically detects and migrates legacy entries at load time.

---

## Full Example Config

```yaml
# ─────────────────────────────────────────────
# CustomGUI — Main Configuration
# ─────────────────────────────────────────────

settings:
  debug: false
  update-check: true

# Filler item used by default when 'fill' is not specified per-GUI
default-filler:
  material: STAINED_GLASS_PANE
  data: 15
  name: " "

guis:

  # ── Server Selector ──────────────────────
  server_selector:
    title: "&8» &6Server Selector &8«"
    size: 27
    permission: "customgui.selector"
    fill:
      material: STAINED_GLASS_PANE
      data: 7
      name: " "
    items:
      survival:
        material: GRASS
        slot: 11
        name: "&a&lSurvival"
        lore:
          - "&7Click to join the &aSurvival &7server!"
          - ""
          - "&ePlayers online: &f%online%"
        actions:
          - "[MESSAGE] &aConnecting you to Survival..."
          - "[CONSOLE] bungee %player% survival"
          - "[CLOSE]"
      creative:
        material: DIAMOND_BLOCK
        slot: 13
        name: "&b&lCreative"
        lore:
          - "&7Click to join the &bCreative &7server!"
        actions:
          - "[MESSAGE] &bConnecting you to Creative..."
          - "[CONSOLE] bungee %player% creative"
          - "[CLOSE]"
      skyblock:
        material: FEATHER
        slot: 15
        name: "&e&lSkyblock"
        lore:
          - "&7Click to join the &eSkyblock &7server!"
        actions:
          - "[MESSAGE] &eConnecting you to Skyblock..."
          - "[CONSOLE] bungee %player% skyblock"
          - "[CLOSE]"

  # ── Kit Menu ─────────────────────────────
  kits:
    title: "&8» &6Kits &8«"
    size: 27
    permission: "customgui.kits"
    items:
      starter:
        material: WOOD_SWORD
        slot: 10
        name: "&aStarter Kit"
        lore:
          - "&7A basic kit for new players."
        actions:
          - "[CONSOLE] givekit %player% starter"
          - "[MESSAGE] &aYou claimed the &aStarter Kit&a!"
          - "[SOUND] LEVEL_UP 1 1"
      warrior:
        material: IRON_SWORD
        slot: 12
        name: "&bWarrior Kit"
        permission: "customgui.kits.warrior"
        lore:
          - "&7A stronger kit for fighters."
        actions:
          - "[CONSOLE] givekit %player% warrior"
          - "[MESSAGE] &bYou claimed the &bWarrior Kit&b!"
      archer:
        material: BOW
        slot: 14
        name: "&eArcher Kit"
        permission: "customgui.kits.archer"
        lore:
          - "&7A ranged kit for sharpshooters."
        actions:
          - "[CONSOLE] givekit %player% archer"
          - "[MESSAGE] &eYou claimed the &eArcher Kit&e!"

  # ── Admin Panel ──────────────────────────
  admin_panel:
    title: "&8» &cAdmin Panel &8«"
    size: 54
    permission: "customgui.admin"
    items:
      gamemode_survival:
        material: STONE
        slot: 0
        name: "&7Survival Mode"
        actions:
          - "[COMMAND] gamemode survival"
          - "[MESSAGE] &7Gamemode set to &aSurvival&7."
      gamemode_creative:
        material: GRASS
        slot: 1
        name: "&7Creative Mode"
        actions:
          - "[COMMAND] gamemode creative"
          - "[MESSAGE] &7Gamemode set to &bCreative&7."
      gamemode_adventure:
        material: MAP
        slot: 2
        name: "&7Adventure Mode"
        actions:
          - "[COMMAND] gamemode adventure"
          - "[MESSAGE] &7Gamemode set to &eAdventure&7."
      clear_inventory:
        material: LAVA_BUCKET
        slot: 9
        name: "&cClear Inventory"
        lore:
          - "&7Right-click to confirm."
        actions:
          - "[COMMAND] clear"
          - "[MESSAGE] &cYour inventory has been cleared."
      fly_toggle:
        material: FEATHER
        slot: 10
        name: "&bToggle Fly"
        actions:
          - "[COMMAND] fly"
      reload_plugin:
        material: REDSTONE
        slot: 53
        name: "&c&lReload CustomGUI"
        actions:
          - "[CONSOLE] customgui reload"
          - "[MESSAGE] &aCustomGUI reloaded."
          - "[CLOSE]"

  # ── Warp Menu ────────────────────────────
  warps:
    title: "&8» &6Warps &8«"
    size: 36
    items:
      spawn:
        material: NETHER_STAR
        slot: 4
        name: "&a&lSpawn"
        lore:
          - "&7Teleport to the spawn point."
        actions:
          - "[COMMAND] warp spawn"
          - "[CLOSE]"
      market:
        material: EMERALD
        slot: 20
        name: "&e&lMarket"
        lore:
          - "&7Teleport to the market."
        actions:
          - "[COMMAND] warp market"
          - "[CLOSE]"
      pvp:
        material: DIAMOND_SWORD
        slot: 24
        name: "&c&lPvP Arena"
        lore:
          - "&7Teleport to the PvP arena."
        actions:
          - "[COMMAND] warp pvp"
          - "[CLOSE]"
```

---

## Commands & Permissions

### Commands

| Command | Description | Permission |
|---|---|---|
| `/customgui` | Show plugin help. | `customgui.use` |
| `/customgui open <gui>` | Open a specific GUI. | `customgui.open` |
| `/customgui reload` | Reload `config.yml`. | `customgui.reload` |
| `/customgui list` | List all defined GUIs. | `customgui.list` |
| `/customgui create <gui>` | Create a blank GUI stub in config. | `customgui.admin` |
| `/customgui delete <gui>` | Delete a GUI from config. | `customgui.admin` |

### Permissions

| Permission | Description | Default |
|---|---|---|
| `customgui.use` | Access to base command. | `true` |
| `customgui.open` | Open any GUI. | `op` |
| `customgui.reload` | Reload configuration. | `op` |
| `customgui.list` | List configured GUIs. | `true` |
| `customgui.admin` | Full admin access. | `op` |
| `customgui.gui.<name>` | Access to a specific GUI (if `permission` not set). | `op` |

---

## Building from Source

### Prerequisites

- **JDK 8** (required for 1.8.8 compatibility)
- **Maven 3.6+** or **Gradle 6+**
- **Spigot 1.8.8** installed locally (for the API dependency)

### Build Steps

```bash
# Clone the repository
git clone https://github.com/kxbir/CustomGUI.git
cd CustomGUI

# Build with Maven
mvn clean package

# The compiled JAR will be at:
# target/CustomGUI-<version>.jar

# Copy to your server
cp target/CustomGUI-*.jar /path/to/server/plugins/
```

If you use **Gradle**:

```bash
./gradlew clean build
# Output: build/libs/CustomGUI-<version>.jar
```

### Maven `pom.xml` Snippet

```xml
<dependencies>
    <dependency>
        <groupId>org.spigotmc</groupId>
        <artifactId>spigot-api</artifactId>
        <version>1.8.8-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

---

## Project Structure

```
CustomGUI/
├── pom.xml
├── README.md
├── LICENSE
└── src/
    └── main/
        ├── java/
        │   └── com/kxbir/customgui/
        │       ├── CustomGUI.java          # Main plugin class
        │       ├── command/
        │       │   └── GUICommand.java     # /customgui command handler
        │       ├── config/
        │       │   ├── ConfigManager.java  # Loads & caches config
        │       │   └── GUIModel.java       # Data model for a GUI
        │       ├── gui/
        │       │   ├── GUIManager.java     # Runtime GUI registry
        │       │   ├── GUIHolder.java      # InventoryHolder
        │       │   └── GUIListener.java    # Click listener
        │       ├── action/
        │       │   ├── Action.java         # Action interface
        │       │   ├── ActionParser.java   # Parses action strings
        │       │   └── impl/               # Action implementations
        │       │       ├── MessageAction.java
        │       │       ├── CommandAction.java
        │       │       ├── OpenAction.java
        │       │       └── ...
        │       └── util/
        │           ├── ColorUtil.java      # Color code translation
        │           └── PlaceholderUtil.java# Placeholder handling
        └── resources/
            └── config.yml                  # Default configuration
```

---

## Architecture (For Developers)

CustomGUI follows a clean, modular architecture:

1. **Config Layer** (`ConfigManager`) — Reads `config.yml`, parses it into `GUIModel` objects, and caches them.
2. **Model Layer** (`GUIModel`, `ItemModel`) — Plain data classes representing a GUI and its items.
3. **Runtime Layer** (`GUIManager`, `GUIHolder`) — Builds live `Inventory` objects from models and attaches them to players.
4. **Listener Layer** (`GUIListener`) — Intercepts `InventoryClickEvent`, matches the clicked slot to an item model, and dispatches its actions.
5. **Action Layer** (`Action`, `ActionParser`, `impl/*`) — Each action type implements a common `Action` interface (`execute(Player, GUI)`). New actions can be added by registering a new implementation.

**Extending:** To add a custom action, implement `Action` and register it in `ActionParser`. Example:

```java
public class MessageAction implements Action {
    private final String message;
    public MessageAction(String message) { this.message = message; }

    @Override
    public void execute(Player player, GUIHolder gui) {
        player.sendMessage(ColorUtil.color(PlaceholderUtil.apply(player, message)));
    }
}
```

Then register in `ActionParser`:

```java
register("MESSAGE", (value) -> new MessageAction(value));
```

---

## FAQ / Troubleshooting

**Q: The GUI doesn't open when I run the command.**
A: Check that the GUI name matches exactly (case-sensitive). Run `/customgui list` to see all loaded GUIs. Check console for errors.

**Q: Colors aren't showing.**
A: Use `&` codes (e.g., `&6`). If you want hex colors, ensure you're using a compatible client/spigot build. Hex codes are not supported on 1.8.8 natively.

**Q: Placeholders show as `%player%` literally.**
A: Confirm PlaceholderAPI is installed and that the placeholder is registered. Built-in placeholders work without PAPI.

**Q: The plugin fails to load.**
A: Ensure you're on Java 8+ and Spigot 1.8.8. Check the console for a `NoClassDefFoundError` or version mismatch.

**Q: Can I use this on 1.16+?**
A: This build targets 1.8.8. It may work on later versions with limited compatibility, but some materials/features may differ.

**Q: How do I add a permission to an item?**
A: Add `permission: "your.permission"` under the item. Players without it won't see that item.

**Q: Actions aren't running.**
A: Verify the syntax `[TYPE] value` and that the type is supported. Check the console for parse errors.

---

## Compatibility Notes

| Feature | 1.8.8 | 1.9+ | 1.13+ |
|---|---|---|---|
| Basic GUI | ✅ | ✅ | ✅ |
| Legacy Materials | ✅ | ✅ | ⚠️ (names changed) |
| Data Values | ✅ | ✅ | ❌ (use names) |
| Titles | ✅ | ✅ | ✅ |
| Action Bar | ✅ | ✅ | ✅ |
| Hex Colors | ❌ | ❌ | ✅ (1.16+) |
| PlaceholderAPI | ✅ | ✅ | ✅ |

CustomGUI is **primarily built and tested for 1.8.8**. Later versions may work but are not officially supported in this release.

---

## License

This project is licensed under the **MIT License**. See the `LICENSE` file for details.

```
MIT License

Copyright (c) 2024 kxbir

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Credits

- **Author:** kxbir
- **Contributors:** See GitHub contributors page
- **Inspiration:** Various config-driven GUI plugins in the Spigot community
- **Special Thanks:** SpigotMC, Bukkit team, and the plugin testing community

---

*CustomGUI — Build menus, not code.*
