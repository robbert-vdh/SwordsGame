package me.coolblinger.swordsgame;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.bukkitcontrib.player.ContribPlayer;
import org.bukkitcontrib.sound.SimpleSoundManager;

import java.util.Arrays;

public class SwordsGameClass {
	SwordsGame plugin;
	Player[] players = new Player[4];
	int playercount = 0;
	boolean isStarted = false;
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
		player.sendMessage(ChatColor.GREEN + "Game successfully created!");
		player.sendMessage(ChatColor.RED + "Unfortunaly, there are currently too few people, so you'll     have to wait until someone joins you.");
		addPlayer(player);
	}

	public boolean addPlayer(Player player) {
		for (int i = 0; i <= 3; i++) {
			if (players[i] == null) {
				players[i] = player;
				playercount++;
				plugin.players.put(player, new SwordsGamePlayerRestore(player, arenaName, plugin));
				toSpawn(players[i], true);
				messagePlayers(ChatColor.AQUA + players[i].getDisplayName() + ChatColor.GREEN + " has joined the game!");
				if (!isStarted && playercount >= 2) {
					BukkitScheduler bScheduler = plugin.getServer().getScheduler();
					bScheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							start();
						}
					}, 20); // This will start the game approximately 1 second after the second player joins.
				} else if (playercount >= 2 && isStarted) {
					playSound("http://dl.dropbox.com/u/677732/Minecraft/quakeplay.wav");
				}
				return true;
			}
		}
		return false;
	}

	public boolean removePlayer(Player player) {
		for (int i = 0; i <= 3; i++) {
			if (players[i] == player) {
				messagePlayers(ChatColor.AQUA + players[i].getDisplayName() + ChatColor.GREEN + " has left the game!");
				players[i] = null;
				playercount--;
				if (playercount < 2) {
					reset();
				}
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

	public void toSpawn(Player player, boolean message) {
		for (int i = 0; i <= 3; i++) {
			if (players[i] == player) {
				Vector spawnLoc = new Vector(spawns[i].getX() + 0.5, spawns[i].getY(), spawns[i].getZ() + 0.5);
				players[i].teleport(spawnLoc.toLocation(world));
				if (message) {
					player.sendMessage(ChatColor.RED + "You can leave using " + ChatColor.GOLD + "/sg leave" + ChatColor.RED + ".");
				}
			}
		}
	}

	public void toSpawnAll() {
		for (int i = 0; i <= 3; i++) {
			if (players[i] != null) {
				toSpawn(players[i], false);
			}
		}
	}

	public void playSound(String url) {
		SimpleSoundManager sManager = new SimpleSoundManager();
		for (int i = 0; i <= 3; i++) {
			if (players[i] != null) {
				ContribPlayer cPlayer = (ContribPlayer) players[i];
				sManager.playCustomSoundEffect(plugin, cPlayer, url, true);
			}
		}
	}

	public void messagePlayers(String message) {
		for (int i = 0; i <= 3; i++) {
			try {
				players[i].sendMessage(message);
			} catch (Exception e) {

			}
		}
	}

	public void start() {
		toSpawnAll();
		isStarted = true;
		playSound("http://dl.dropbox.com/u/677732/Minecraft/quakeplay.wav");
		messagePlayers(ChatColor.GOLD + "The game has been started, good luck!");
	}

	public void reset() {
		isStarted = false;
		messagePlayers(ChatColor.GOLD + "The game has been aborted because there is only one player left.");
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
