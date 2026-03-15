# HidePlayer

HidePlayer is a lightweight and powerful Minecraft plugin for Spigot/Paper (1.20+) that allows administrators or specific players to hide their real identity by changing their nickname and skin in-game.

## Features
- **Identity Hiding**: Easily change your in-game nickname and skin.
- **Commands**: Simple commands to hide and reveal your true identity.
- **PlaceholderAPI Support**: Multiple placeholders to integrate with scoreboards, chat formatting, and tab lists.
- **Multilingual**: Built-in support for translations (`en` and `es` out of the box).
- **Update Checker**: Notifies server administrators about new plugin updates automatically.
- **Sounds**: Support for sounds to be played when the player is hidden or shown.
- **Admin Panel**: Graphical User Interface (GUI) to view and manage currently hidden players, with pagination and fully customizable items.

## Dependencies
- **Server Version**: Java 17+, Spigot/Paper 1.20+
- **Soft Dependencies**:
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (For placeholders)
  - [SkinsRestorer](https://skinsrestorer.net/) (For skin changing functionality)

## Commands & Permissions

| Command | Description | Permission |
|---------|-------------|------------|
| `/hideplayer hide <nickname> <skin>` | Hides your identity by changing your name and skin. | `hideplayer.hide` |
| `/hideplayer show` | Restores your original identity. | `hideplayer.show` |
| `/hideplayer reload` | Reloads the plugin configuration and languages. | `hideplayer.admin` |
| `/hideplayer panel` (or `admin`) | Opens the Admin Panel GUI to view all hidden players. | `hideplayer.admin` |

*(Alias: `/hp` can be used instead of `/hideplayer`)*

## Placeholders
If you have **PlaceholderAPI** installed, you can use the following placeholders:

- `%hideplayer_is_hidden%` - Returns `true` or `false` depending on if the player is hidden.
- `%hideplayer_original_name%` - Returns the player's true real name.
- `%hideplayer_nick%` - Returns the player's current fake nickname (or real name if not hidden).
- `%hideplayer_skin%` - Returns the player's current fake skin name.

## Configuration

**config.yml**
```yaml
prefix: "&8[&bHidePlayer&8] &r"
language: "en" # Available: en, es

# Partially shown config for Admin Panel:
admin-panel:
  title: "&cAdmin Panel - Page {page}"
  size: 54
  # Includes customizable items for page navigation and player heads
```

## Compilation
To compile this plugin yourself:
1. Clone this repository: `git clone https://github.com/staFF6773/hidePlayer.git`
2. Run Gradle build: `./gradlew build` (or `gradlew.bat build` on Windows)
3. The compiled `.jar` will be in the `build/libs/` directory.

---

### Español (Spanish)
HidePlayer es un plugin de Minecraft para Spigot/Paper (1.20+) que permite a los administradores ocultar su identidad real cambiando su apodo (nickname) y su skin dentro del juego.

- Usa `/hp hide <nick> <skin>` para ocultarte.
- Usa `/hp show` para volver a tu identidad original.
- Usa `/hp panel` (o `admin`) para abrir el panel GUI y ver a los jugadores ocultos.
- Para cambiar el idioma del plugin al español, cambia `language: "es"` en tu archivo `config.yml`.
