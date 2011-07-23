package me.coolblinger.swordsgame;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class SwordsGameClass {
    SwordsGame plugin;
    Player[] players = new Player[4];
    int playercount = 0;
    Vector[] spawns = new Vector[4];
    String arenaName;
    World world;
    Vector[] arenaCorners = new Vector[2];

    public SwordsGameClass(Player player, SwordsGameArenaClass arenaClass, SwordsGame swordsGame) {
        plugin = swordsGame;
        arenaName = arenaClass.name;
        world = plugin.toWorld(arenaClass.world);
        arenaCorners[0] = new Vector(arenaClass.cornerX[0], 0, arenaClass.cornerZ[0]);
        arenaCorners[1] = new Vector(arenaClass.cornerX[1], 128, arenaClass.cornerZ[1]);
        for (int i = 0; i <= 3; i++) {
            spawns[i] = new Vector(arenaClass.spawnX[i], arenaClass.spawnY[i], arenaClass.spawnZ[i]);
        }
        addPlayer(player);
        player.sendMessage(ChatColor.GREEN + "Game successfully created!");
        player.sendMessage(ChatColor.RED + "Unfortunaly, there are currently too few people, so you'll     have to wait until someone joins you.");
    }

    public boolean addPlayer(Player player) {
        for (int i = 0; i <= 3; i++) {
            if (players[i] == null) {
                players[i] = player;
                playercount++;
                plugin.players.put(player, new SwordsGamePlayerRestore(player, arenaName, plugin));
                toSpawn(players[i]);
                return true;
            }
        }
        return false;
    }

    public boolean removePlayer(Player player) {
        for (int i = 0; i <= 3; i++) {
            if (players[i] == player) {
                players[i] = null;
                playercount--;
                if (playercount == 0) {
                    plugin.games.remove(arenaName);
                }
                return true;
            }
        }
        return false;
    }

    public boolean checkPlayer(Player player) {
        if (Arrays.asList(players).contains(player)) {
            return true;
        } else {
            return false;
        }
    }

    public void toSpawn(Player player) {
        for (int i = 0; i <= 3; i++) {
            if (players[i] == player) {
                Vector spawnLoc = new Vector(spawns[i].getX() + 0.5, spawns[i].getY(), spawns[i].getZ() + 0.5);
                players[i].teleport(spawnLoc.toLocation(world));
            }
        }
    }

    public boolean isFull() {
        for (int i = 0; i <= 3; i++) {
            if (players[i] == null) {
                return false;
            }
        }
        return true;
    }
}
