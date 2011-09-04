package me.coolblinger.swordsgame.classes;

import me.coolblinger.swordsgame.SwordsGame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class SwordsGameClass {
	private SwordsGame plugin;
	private AppearanceManager aManager = SpoutManager.getAppearanceManager();
	private SoundManager sManager = SpoutManager.getSoundManager();
	public int maxPlayers;
	public Player[] players;
	public int[] weapon;
	private List<ItemStack> weaponList = new ArrayList<ItemStack>();
	public int playercount = 0;
	public boolean isStarted = false;
	public boolean isPlaying = false;
	public Vector[] spawns;
	public String arenaName;
	public World world;
	public Vector[] arenaCorners = new Vector[2];

	public SwordsGameClass(Player player, SwordsGameArenaClass arenaClass, SwordsGame swordsGame) {
		plugin = swordsGame;
		arenaName = arenaClass.name;
		world = plugin.toWorld(arenaClass.world);
		arenaCorners[0] = new Vector(arenaClass.cornerX[0], 0, arenaClass.cornerZ[0]);
		arenaCorners[1] = new Vector(arenaClass.cornerX[1], 128, arenaClass.cornerZ[1]);
		maxPlayers = plugin.arenas.get(arenaName).spawnCount;
		players = new Player[maxPlayers];
		weapon = new int[maxPlayers];
		spawns = new Vector[maxPlayers];
		for (int i = 0; i < maxPlayers; i++) {
			spawns[i] = new Vector(arenaClass.spawnX[i], arenaClass.spawnY[i], arenaClass.spawnZ[i]);
		}
		player.sendMessage(ChatColor.GREEN + plugin.local("games.created"));
		player.sendMessage(ChatColor.RED + plugin.local("games.createMessage"));
		addPlayer(player);
	}

	public boolean addPlayer(Player player) {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] == null) {
				players[i] = player;
				playercount++;
				plugin.players.put(player, new SwordsGamePlayerRestore(player, arenaName, plugin));
				toSpawn(players[i], true);
				messagePlayers(ChatColor.AQUA + players[i].getDisplayName() + ChatColor.GREEN + plugin.local("games.playerJoined"));
				if (!isStarted && playercount >= 2) {
					BukkitScheduler bScheduler = plugin.getServer().getScheduler();
					bScheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							start();
						}
					}, 100); // This will start the game approximately 5 seconds after the second player joins.
				} else if (playercount >= 2 && isStarted) {
					sManager.playCustomSoundEffect(plugin, (SpoutPlayer) players[i], "http://dl.dropbox.com/u/677732/Minecraft/quakeplay.wav", true);
					rankUp(players[i], true);
				}
				return true;
			}
		}
		return false;
	}

	public boolean removePlayer(Player player) {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] == player) {
				messagePlayers(ChatColor.AQUA + players[i].getDisplayName() + ChatColor.GREEN + plugin.local("games.playerLeft"));
				aManager.resetGlobalTitle(players[i]);
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

	public void toSpawn(Player player, boolean message) {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] == player) {
				Vector spawnLoc = new Vector(spawns[i].getX() + 0.5, spawns[i].getY(), spawns[i].getZ() + 0.5);
				players[i].teleport(spawnLoc.toLocation(world));
				if (message) {
					player.sendMessage(ChatColor.RED + plugin.local("games.leaveCommand") + ChatColor.GOLD + "/sg leave" + ChatColor.RED + ".");
				}
				break;
			}
		}
	}

	public void toSpawnAll() {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] != null) {
				toSpawn(players[i], false);
			}
		}
	}

	public void playSound(String url) {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] != null) {
				SpoutPlayer sPlayer = (SpoutPlayer) players[i];
				sManager.playCustomSoundEffect(plugin, sPlayer, url, true);
			}
		}
	}

	public void messagePlayers(String message) {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] != null) {
				players[i].sendMessage(message);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void start() {
		weapon = new int[4];
		weaponList.clear();
		if (plugin.configBoolean("ladder.custom")) {
			List<Integer> idList = plugin.configList("ladder.ladder");
			for (int id : idList) {
				weaponList.add(new ItemStack(id, 1));
			}
		} else {
			weaponList.add(new ItemStack(Material.DIAMOND_SWORD, 1));
			weaponList.add(new ItemStack(Material.IRON_SWORD, 1));
			weaponList.add(new ItemStack(Material.DIAMOND_AXE, 1));
			weaponList.add(new ItemStack(Material.IRON_AXE, 1));
			weaponList.add(new ItemStack(Material.GOLD_SWORD, 1));
			weaponList.add(new ItemStack(Material.GOLD_AXE, 1));
			weaponList.add(new ItemStack(Material.GOLD_PICKAXE, 1));
			weaponList.add(new ItemStack(Material.AIR));
		}
		toSpawnAll();
		isStarted = true;
		isPlaying = true;
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] != null) {
				players[i].setHealth(20);
				rankUp(players[i], false);
			}
		}
		playSound("http://dl.dropbox.com/u/677732/Minecraft/quakeplay.wav");
		messagePlayers(ChatColor.GOLD + plugin.local("games.started"));
	}

	public void stop() {
		isStarted = false;
		isPlaying = false;
		messagePlayers(ChatColor.GOLD + plugin.local("games.aborted"));
	}

	public boolean isFull() {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] == null) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void rankUp(Player player, boolean notify) {
		if (isPlaying) {
			for (int i = 0; i < maxPlayers; i++) {
				if (players[i] == player) {
					if (weapon[i] != weaponList.size()) {
						weapon[i]++;
						player.getInventory().clear();
						if (plugin.configBoolean("ladder.custom")) {
							List<Integer> idList = plugin.configList("ladder.sideItems");
							for (int id : idList) {
								player.getInventory().addItem(new ItemStack(id, 1));
							}
						} else {
							player.getInventory().addItem(new ItemStack(320, 1)); //Cooked porkchop
						}
						if (weaponList.get(weapon[i] - 1).getType() != Material.AIR) {
							player.getInventory().addItem(weaponList.get(weapon[i] - 1));
						}
						player.sendMessage(ChatColor.GREEN + plugin.local("games.rank") + ChatColor.AQUA + weapon[i] + ChatColor.GREEN + plugin.local("defining.list.outOf") + ChatColor.AQUA + weaponList.size() + ChatColor.GREEN + ".");
					} else {
						weapon[i]++;
						reset(player);
					}
					leadTitles();
					break;
				}
			}
		}
	}

	public void reset(Player winner) {
		isPlaying = false;
		toSpawnAll();
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] != null) {
				players[i].getInventory().clear();
				players[i].setHealth(20);
			}
		}
		messagePlayers(ChatColor.AQUA + winner.getDisplayName() + ChatColor.GOLD + " has won the match!");
		messagePlayers(ChatColor.GOLD + plugin.local("games.newMatch.15"));
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
				messagePlayers(ChatColor.GOLD + plugin.local("games.newMatch.5"));
			}
		}, 200);
	}

	@SuppressWarnings("unchecked")
	public void kill(Player killer, Player killed) {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] == killed) {
				players[i].setHealth(20);
				if (plugin.configBoolean("ladder.custom")) {
					List<Integer> idList = plugin.configList("ladder.sideItems");
					for (int id : idList) {
						if (!players[i].getInventory().contains(id)) {
							players[i].getInventory().addItem(new ItemStack(id, 1));
						}
					}
				} else {
					if (!players[i].getInventory().contains(320)) {
						players[i].getInventory().addItem(new ItemStack(320, 1)); //Cooked porkchop
					}
				}
				toSpawn(players[i], false);
				break;
			}
		}
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] == killer) {
				if (plugin.configBoolean("spawnOnKill")) {
					toSpawn(players[i], false);
				}
				rankUp(killer, true);
				break;
			}
		}
	}

	public void downRank(Player player) {
		for (int i = 0; i < maxPlayers; i++) {
			if (players[i] == player) {
				if (weapon[i] > 1 && isPlaying) {
					weapon[i]--;
					player.getInventory().clear();
					if (plugin.configBoolean("ladder.custom")) {
						List<Integer> idList = plugin.configList("ladder.sideItems");
						for (int id : idList) {
							players[i].getInventory().addItem(new ItemStack(id, 1));
						}
					} else {
						players[i].getInventory().addItem(new ItemStack(320, 1)); //Cooked porkchop
					}
					if (weaponList.get(weapon[i] - 1).getType() != Material.AIR) {
						players[i].getInventory().addItem(weaponList.get(weapon[i] - 1));
					}
					players[i].sendMessage(ChatColor.GREEN + plugin.local("games.downRank") + ChatColor.AQUA + weapon[i] + ChatColor.GREEN + plugin.local("defining.list.outOf") + ChatColor.AQUA + weaponList.size() + ChatColor.GREEN + ".");
					leadTitles();
				}
				players[i].setHealth(20);
				toSpawn(players[i], false);
				final Player finalPlayer = players[i];
				BukkitScheduler bScheduler = plugin.getServer().getScheduler();
				bScheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						finalPlayer.setFireTicks(0);
					}
				}, 2); // It won't work properly if not delayed.
			}
		}
	}

	public void leadTitles() {
		List<Player> leadPlayers = getLead();
		if (leadPlayers.size() == 1) {
			for (int i = 0; i < maxPlayers; i++) {
				if (players[i] == null) {
					continue;
				}
				aManager.resetGlobalTitle(players[i]);
				if (leadPlayers.contains(players[i])) {
					if (isPlaying) {
						aManager.setGlobalTitle(players[i], ChatColor.GOLD + plugin.local("games.BukkitContrib.lead") + ChatColor.WHITE + players[i].getDisplayName());
					} else {
						aManager.setGlobalTitle(players[i], ChatColor.GREEN + plugin.local("games.BukkitContrib.winner") + ChatColor.WHITE + players[i].getDisplayName());
					}
				}
			}
		} else if (leadPlayers.size() > 1) {
			for (int i = 0; i < maxPlayers; i++) {
				if (players[i] == null) {
					continue;
				}
				aManager.resetGlobalTitle(players[i]);
				if (leadPlayers.contains(players[i])) {
					aManager.setGlobalTitle(players[i], ChatColor.AQUA + plugin.local("games.BukkitContrib.tie") + ChatColor.WHITE + players[i].getDisplayName());
				}
			}
		}
	}

	public List<Player> getLead() {
		int highest = 0;
		List<Player> highestList = new ArrayList<Player>();
		for (int i = 0; i <= 3; i++) {
			if (weapon[i] == highest) {
				highestList.add(players[i]);
			}
			if (weapon[i] > highest) {
				highestList.clear();
				highestList.add(players[i]);
				highest = weapon[i];
			}
		}
		return highestList;
	}
}