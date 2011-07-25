package me.coolblinger.swordsgame;

import me.coolblinger.swordsgame.classes.SwordsGameArenaClass;
import me.coolblinger.swordsgame.classes.SwordsGameClass;
import me.coolblinger.swordsgame.classes.SwordsGameDefine;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkitcontrib.player.ContribPlayer;

import java.util.Arrays;

public class SwordsGameCommand {
	SwordsGame plugin;

	public SwordsGameCommand(SwordsGame instance) {
		plugin = instance;
	}

	public boolean execute(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		if (command.getName().equalsIgnoreCase("sg")) {
			if (args.length == 0) {
				// Print a list of commands that 'sender' is allowed to execute.
				printCommandList(player);
				return true;
			} else if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("define")) {
					define(player, args);
					return true;
				} else if (args[0].equalsIgnoreCase("remove")) {
					remove(player, args);
					return true;
				} else if (args[0].equalsIgnoreCase("setspawns")) {
					setSpawns(player);
					return true;
				} else if (args[0].equalsIgnoreCase("resetspawns")) {
					resetSpawns(player, args);
					return true;
				} else if (args[0].equalsIgnoreCase("list")) {
					list(player, args);
					return true;
				} else if (args[0].equalsIgnoreCase("game")) {
					game(player, args);
					return true;
				} else if (args[0].equalsIgnoreCase("leave")) {
					leave(player);
					return true;
				} else {
					printCommandList(player);
					return true;
				}
			} else {
				printCommandList(player);
				return true;
			}
		}
		return true;
	}

	public void printCommandList(Player player) {
		player.sendMessage(ChatColor.GOLD + "SwordsGame:");
		player.sendMessage(ChatColor.GOLD + "-----------");
		if (plugin.permissions.has(player, "swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg define " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Define a new arena");
		}
		if (plugin.permissions.has(player, "swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg remove <arena> " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Remove an arena");
		}
		if (plugin.permissions.has(player, "swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg setspawns " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Set the spawns for an arena");
		}
		if (plugin.permissions.has(player, "swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg resetspawns <arena> " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Reset the spawns for an arena");
		}
		if (plugin.permissions.has(player, "swordsgame.play")) {
			player.sendMessage(ChatColor.GOLD + "/sg list <#> " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Arena list");
		}
		if (plugin.permissions.has(player, "swordsgame.play")) {
			player.sendMessage(ChatColor.GOLD + "/sg game <arena> " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Create or join a game in the specified     arena");
		}
		if (plugin.permissions.has(player, "swordsgame.play")) {
			player.sendMessage(ChatColor.GOLD + "/sg leave " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Leave the current game ");
		}
	}

	public void define(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.define")) {
			ContribPlayer cPlayer = (ContribPlayer) player;
			if (!plugin.define.containsKey(player)) {
				plugin.define.put(player, new SwordsGameDefine("define"));
				cPlayer.sendNotification("Defining", "Right click both corners", Material.MAP);
				player.sendMessage(ChatColor.GREEN + "Right click at both corners of the arena. You can type          " + ChatColor.GOLD + "/sg define <name>" + ChatColor.GREEN + " when you're done, or left click to cancel.");
			} else {
				if (plugin.define.get(player).mode == "setspawns") {
					player.sendMessage(ChatColor.RED + "You can't define arenas while setting spawns.");
				} else {
					if (plugin.define.get(player).corner1 != null && plugin.define.get(player).corner2 != null && plugin.define.get(player).mode == "define") {
						if (args.length >= 2) {
							if (!plugin.arenas.containsKey(args[1])) {
								plugin.arenas.put(args[1], new SwordsGameArenaClass(args[1], plugin.define.get(player).world.getName(), plugin.define.get(player).corner1, plugin.define.get(player).corner2));
								player.sendMessage(ChatColor.GREEN + "Arena '" + ChatColor.WHITE + args[1] + ChatColor.GREEN + "' has been created!");
								player.sendMessage(ChatColor.GREEN + "You should set four spawnpoints now, using " + ChatColor.GOLD + "/sg setspawns" + ChatColor.GREEN + ".");
								cPlayer.sendNotification("Setting spawnpoints", "/sg setspawns", Material.MAP);
								plugin.define.remove(player);
							} else {
								player.sendMessage(ChatColor.RED + "An arena with that name already exists.");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You need to specify a name for the new arena.");
						}
					}
				}
			}
		} else {
			printCommandList(player);
		}
	}

	public void remove(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.define")) {
			if (args.length >= 2) {
				if (plugin.arenas.containsKey(args[1])) {
					plugin.arenas.remove(args[1]);
					player.sendMessage(ChatColor.GREEN + "Arena '" + ChatColor.WHITE + args[1] + ChatColor.GREEN + "' has been removed.");
				} else {
					player.sendMessage(ChatColor.RED + "Invalid arena name specified.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You need to specify the name of the arena you want to         remove.");
			}
		} else {
			printCommandList(player);
		}
	}

	public void setSpawns(Player player) {
		ContribPlayer cPlayer = (ContribPlayer) player;
		if (plugin.permissions.has(player, "swordsgame.define")) {
			if (!plugin.define.containsKey(player)) {
				plugin.define.put(player, new SwordsGameDefine("setspawns"));
				cPlayer.sendNotification("Setting spawns", "Right click", Material.MAP);
				player.sendMessage(ChatColor.GREEN + "Right click inside an arena at the desired location. You can leftclick when you're done.");
			} else {
				if (plugin.define.get(player).mode == "setspawns") {
					player.sendMessage(ChatColor.RED + "You're already setting spawns.");
				} else {
					player.sendMessage(ChatColor.RED + "You can't set spawns while defining arenas.");
				}
			}
		} else {
			printCommandList(player);
		}
	}

	public void resetSpawns(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.define")) {
			if (args.length >= 2) {
				if (plugin.arenas.containsKey(args[1])) {
					plugin.arenas.get(args[1]).resetSpawns();
					player.sendMessage(ChatColor.GREEN + "Spawns for arena '" + ChatColor.WHITE + args[1] + ChatColor.GREEN + "' have been reset.");
				} else {
					player.sendMessage(ChatColor.RED + "Invalid arena name specified.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You need to specify the name of an arena.");
			}
		} else {
			printCommandList(player);
		}
	}

	public void list(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.play")) {
			Object[] arenaList = plugin.arenas.keySet().toArray();
			Arrays.sort(arenaList);
			int multiplier;
			try {
				multiplier = Integer.parseInt(args[1]);
			} catch (Exception e) {
				multiplier = 1;
			}
			if (multiplier < 1) {
				multiplier = 1;
			}
			int showingFrom = multiplier * 10 - 9;
			if (arenaList.length == 0) {
				showingFrom = 0;
			}
			int showingTo;
			if (showingFrom + 10 > arenaList.length) {
				showingTo = arenaList.length;
			} else {
				showingTo = showingFrom + 9;
			}
			player.sendMessage(ChatColor.GOLD + "SwordsGame arena list: (showing " + showingFrom + "-" + showingTo + " out of " + arenaList.length + ")");
			player.sendMessage(ChatColor.GOLD + "-----------");
			for (int i = showingFrom - 1; i < showingFrom + 8; i++) {
				try {
					if (plugin.arenas.get(arenaList[i]).isPrepared()) {
						if (plugin.games.containsKey(arenaList[i])) {
							player.sendMessage(ChatColor.GOLD + "- " + ChatColor.WHITE + arenaList[i] + ChatColor.AQUA + " (" + ChatColor.WHITE + plugin.games.get(arenaList[i]).playercount + ChatColor.AQUA + " out of " + ChatColor.WHITE + "4" + ChatColor.AQUA + " players)");
						} else {
							player.sendMessage(ChatColor.GOLD + "- " + ChatColor.WHITE + arenaList[i] + ChatColor.AQUA + " (empty)");
						}
					} else {
						player.sendMessage(ChatColor.GOLD + "- " + ChatColor.WHITE + arenaList[i] + ChatColor.AQUA + " (missing spawnpoints)");
					}
				} catch (Exception e) {
					return; // An easier way of checking if the value is null.
				}
			}
		} else {
			printCommandList(player);
		}
	}

	public void game(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.play")) {
			if (args.length >= 2) {
				if (plugin.arenas.containsKey(args[1])) {
					if (plugin.arenas.get(args[1]).isPrepared()) {
						if (!plugin.players.containsKey(player)) {
							if (!plugin.games.containsKey(args[1])) {
								plugin.games.put(args[1], new SwordsGameClass(player, plugin.arenas.get(args[1]), plugin));
							} else {
								if (!plugin.games.get(args[1]).isFull()) {
									plugin.games.get(args[1]).addPlayer(player);
								} else {
									player.sendMessage(ChatColor.RED + "This game is full.");
								}
							}
						} else {
							player.sendMessage(ChatColor.RED + "You're already in a game.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "The specified arena is missing spawnpoints.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "Invalid arena name.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You need to specify the name of the arena you want to play in.");
			}
		} else {
			printCommandList(player);
		}
	}

	public void leave(Player player) {
		if (plugin.permissions.has(player, "swordsgame.play")) {
			if (plugin.players.containsKey(player)) {
				plugin.players.get(player).restore();
			} else {
				player.sendMessage(ChatColor.RED + "You're not in a game.");
			}
		} else {
			printCommandList(player);
		}
	}
}
