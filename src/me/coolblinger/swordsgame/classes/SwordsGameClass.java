package me.coolblinger.swordsgame.classes;

import me.coolblinger.swordsgame.SwordsGame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.bukkitcontrib.player.ContribPlayer;
import org.bukkitcontrib.sound.SimpleSoundManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwordsGameClass {
	private SwordsGame plugin;
	public Player[] players = new Player[4];
	private int[] weapon = new int[4];
	private List<ItemStack> weaponList = new ArrayList<ItemStack>();
	public int playercount = 0;
	public boolean isStarted = false;
	public Vector[] spawns = new Vector[4];
	public String arenaName;
	public World world;
	public Vector[] arenaCorners = new Vector[2];

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
					}, 100); // This will start the game approximately 5 seconds after the second player joins.
				} else if (playercount >= 2 && isStarted) {
					playSound("http://dl.dropbox.com/u/677732/Minecraft/quakeplay.wav");
					rankUp(players[i], true);
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
				weapon[i] = 0;
				playercount--;
				if (playercount < 2) {
					stop();
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
				break;
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
			if (players[i] != null) {
				players[i].sendMessage(message);
			}
		}
	}

	public void start() {
		weapon = new int[4];
		weaponList.clear();
		weaponList.add(new ItemStack(Material.DIAMOND_SWORD, 1));
		weaponList.add(new ItemStack(Material.IRON_SWORD, 1));
		weaponList.add(new ItemStack(Material.DIAMOND_AXE, 1));
		weaponList.add(new ItemStack(Material.IRON_AXE, 1));
		weaponList.add(new ItemStack(Material.GOLD_SWORD, 1));
		weaponList.add(new ItemStack(Material.GOLD_AXE, 1));
		weaponList.add(new ItemStack(Material.GOLD_PICKAXE, 1));
		weaponList.add(new ItemStack(Material.AIR));
		toSpawnAll();
		for (int i = 0; i <= 3; i++) {
			if (players[i] != null) {
				players[i].setHealth(20);
				rankUp(players[i], false);
			}
		}
		isStarted = true;
		playSound("http://dl.dropbox.com/u/677732/Minecraft/quakeplay.wav");
		messagePlayers(ChatColor.GOLD + "The game has been started, good luck!");
	}

	public void stop() {
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

	public void rankUp(Player player, boolean notify) {
		for (int i = 0; i <= 3; i++) {
			if (players[i] == player) {
				if (weapon[i] != weaponList.size()) {
					weapon[i]++;
					player.getInventory().clear();
					player.getInventory().addItem(new ItemStack(320, 1)); //Cooked porkchop
					if (weaponList.get(weapon[i] - 1).getType() != Material.AIR) {
						player.getInventory().addItem(weaponList.get(weapon[i] - 1));
					}
					player.sendMessage(ChatColor.GREEN + "Rank " + ChatColor.AQUA + weapon[i] + ChatColor.GREEN + " out of " + ChatColor.AQUA + weaponList.size() + ChatColor.GREEN + ".");
				} else {
					reset(player);
				}
				break;
			}
		}
	}

	public void reset(Player winner) {
		toSpawnAll();
		for (int i = 0; i <= 3; i++) {
			if (players[i] != null) {
				players[i].getInventory().clear();
				players[i].setHealth(20);
			}
		}
		messagePlayers(ChatColor.AQUA + winner.getDisplayName() + ChatColor.GOLD + " has won the match!");
		messagePlayers(ChatColor.GOLD + "A new match will start in fifteen seconds.");
		BukkitScheduler bScheduler = plugin.getServer().getScheduler();
		bScheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				start();
			}
		}, 300);
		bScheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				messagePlayers(ChatColor.GOLD + "Five seconds left!");
			}
		}, 200);
	}

	public void kill(Player killer, Player killed) {
		for (int i = 0; i <= 3; i++) {
			if (players[i] == killed) {
				players[i].setHealth(20);
				toSpawn(players[i], false);
				break;
			}
		}
		for (int i = 0; i <= 3; i++) {
			if (players[i] == killer) {
				toSpawn(players[i], false);
				rankUp(killer, true);
				break;
			}
		}
	}
}
