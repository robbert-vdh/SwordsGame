package me.coolblinger.swordsgame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkitcontrib.player.ContribPlayer;

//This file contains the smaller classes

class SwordsGameDefine {
    Vector corner1;
    Vector corner2;
    boolean secondcorner;
    World world;
    boolean showedWarning;
    String mode;

    public SwordsGameDefine(String _mode) {
        mode = _mode;
    }

    public boolean setCorner(Vector vector, World _world, Player player) {
        if (_world != world) {
            world = _world;
            secondcorner = false;
            corner1 = null;
            corner2 = null;
        }
        if (secondcorner) {
            corner2 = vector;
            secondcorner = false;
            if (corner1 != null && corner2 != null && !showedWarning) {
                ContribPlayer cPlayer = (ContribPlayer) player;
                cPlayer.sendNotification("Defining", "/sg define <name>", Material.MAP);
                showedWarning = true;
            }
            return true;
        } else {
            corner1 = vector;
            secondcorner = true;
            if (corner1 != null && corner2 != null && !showedWarning) {
                ContribPlayer cPlayer = (ContribPlayer) player;
                cPlayer.sendNotification("Defining", "/sg define <name>", Material.MAP);
                showedWarning = true;
            }
            return false;
        }
    }
}

class SwordsGamePlayerRestore {
    Location location;
    ItemStack[] inventory;
    Player player;
    SwordsGame plugin;
    String arena;

    public SwordsGamePlayerRestore(Player _player, String _arena, SwordsGame _plugin) {
        player = _player;
        location = player.getLocation();
        inventory = player.getInventory().getContents();
        arena = _arena;
        plugin = _plugin;
        player.getInventory().clear();
    }

    public void restore() {
        player.getInventory().setContents(inventory);
        player.teleport(location);
        plugin.players.remove(player);
        plugin.games.get(arena).removePlayer(player);
    }
}