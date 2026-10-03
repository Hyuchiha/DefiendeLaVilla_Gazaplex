# Defiende la villa!

This repo contains the code base for **Defiende la Villa Plugin** and it was designed for **Gazaplex Network**, now becomes an open source hoping it can be used in another servers and be maintained by the community.

# Features
- Support for Vault
- Support for SQLite, MySQL, MongoDB
- BungeeCord / Waterfall / Velocity support (`config.yml` → `proxy:`): `enabled` (default false) gives a "return to lobby" compass, `lobby-server` is the proxy's lobby server, `send-on-end` (default true) also sends players there when the game ends or they leave an arena. A config without `proxy.enabled` keeps reading the old keys `EnableBungeeComunication` / `ServerToConnect` / `AutoReturnToLobby` (auto-send off). Velocity needs `bungee-plugin-message-channel = true` (its default). If the lobby is down the player just stays on this server's main lobby.
- Multi-Configurable Arenas
- Customizable Messages
- Support for Minecraft 1.9 - 1.15
- Multiple Kits for players (9)
- Stats commands
- Custom Mobs (Can be better)
- Scoreboards
- Configurable In Game Shops
- Configurable Mobs difficulty (Recommended dont move)
- Configurable Mob Gems Drop
- Added custom functionality for Random Events (Random Pots, Random HP, Random Mobs)

## Permissions

All the permissions required for running this plugin are listed here:
 
 - **VD.Player.spect** add permission for Spect games
 - **VD.Class.[kit]** add permission to users for use a kit

## Commands

This are the commands a player can execute in game

- **/villagedefense** for view all the commands
- **/stats [kills, deaths, wins, losses, nexus_damage]** list the stats of a category for a player
- **/top [kills, deaths, wins, losses, nexus_damage]** list top stats for a giver category

## Configure a map
For now the only way to configure a map is adding all the map data to **maps.yml** using the example format and placing the map in the directory called **maps**

