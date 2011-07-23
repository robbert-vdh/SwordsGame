package me.coolblinger.swordsgame;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class SwordsGameClass {
    Player[] players = new Player[4];
    int playercount = 0;
    Vector[] spawns = new Vector[4];
    String arenaname;
    String world;
    Vector[] arenaCorners = new Vector[2];


    public SwordsGameClass(Player player, SwordsGameArenaClass arenaClass) {
        arenaname = arenaClass.name;
        world = arenaClass.world;
        arenaCorners[0] = new Vector(arenaClass.cornerX[0], 0, arenaClass.cornerZ[0]);
        arenaCorners[1] = new Vector(arenaClass.cornerX[1], 128, arenaClass.cornerZ[1]);
        for (int i = 0; i <= 3; i++) {
            spawns[i] = new Vector(arenaClass.spawnX[i], arenaClass.spawnY[i], arenaClass.spawnZ[i]);
        }
        addPlayer(player);
        player.sendMessage(ChatColor.GREEN + "Game successfully created!");
    }

    public boolean addPlayer(Player player) {
        for (Player p : players) {
            if (p == null) {
                p = player;
                playercount++;
                return true;
            }
        }
        return false;
    }

    public boolean removePlayer(Player player) {
        for (Player p : players) {
            if (p == player) {
                p = null;
                playercount--;
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
}
