# Changelog

All notable changes to this project will be documented in this file.

## [0.1] - Initial Release

### Added
- **Identity Hiding**: Core system that allows players or administrators to change their in-game nickname and skin.
- **Main Commands**:
  - `/hideplayer hide <nickname> <skin>`: Hides the player's real identity.
  - `/hideplayer show`: Restores the player's original identity.
  - `/hideplayer panel` (or `/hp admin`): Opens a graphical user interface (GUI) for administrators.
  - `/hideplayer reload`: Reloads the plugin configuration and language files.
  - Help menu and support for the short alias `/hp`.
- **Admin Panel (GUI)**:
  - Graphical interface to view all currently hidden players.
  - Built-in pagination system to handle large amounts of players.
  - Fully customizable menu items via the configuration file.
- **PlaceholderAPI Support**:
  - `%hideplayer_is_hidden%`: Returns `true` if the player is hidden, or `false` otherwise.
  - `%hideplayer_original_name%`: Displays the player's true real name.
  - `%hideplayer_nick%`: Displays the current fake nickname (or the real one if not hidden).
  - `%hideplayer_skin%`: Displays the name of the fake skin being used.
- **Multilingual Support**:
  - Integrated translation system with out-of-the-box support for English (`en`) and Spanish (`es`).
- **Update Checker**: Automatically notifies administrators when a new plugin version is available.
- **Sounds System**: Customizable sound playback when hiding or showing identity.
- **SkinsRestorer Integration**: Uses the SkinsRestorer API to apply skin changes seamlessly.
- **Permissions System**: Dedicated permissions (`hideplayer.hide`, `hideplayer.show`, `hideplayer.admin`) to control access to each feature.
- **Compatibility**: Full support for Spigot/Paper servers running version 1.20 and above (requires Java 17+).
