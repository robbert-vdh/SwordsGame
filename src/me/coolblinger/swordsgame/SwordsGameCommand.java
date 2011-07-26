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
			sender.sendMessage(plugin.local("errors.consoleCommand"));
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
		if (plugin.permissions.has(player, "swordsgame.define") || player.hasPermission("swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg define " + ChatColor.WHITE + "- " + ChatColor.AQUA + plugin.local("commandDesc.define"));
		}
		if (plugin.permissions.has(player, "swordsgame.define") || player.hasPermission("swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg remove <arena> " + ChatColor.WHITE + "- " + ChatColor.AQUA + plugin.local("commandDesc.remove"));
		}
		if (plugin.permissions.has(player, "swordsgame.define") || player.hasPermission("swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg setspawns " + ChatColor.WHITE + "- " + ChatColor.AQUA + plugin.local("commandDesc.setspawns"));
		}
		if (plugin.permissions.has(player, "swordsgame.define") || player.hasPermission("swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg resetspawns <arena> " + ChatColor.WHITE + "- " + ChatColor.AQUA + plugin.local("commandDesc.resetspawns"));
		}
		if (plugin.permissions.has(player, "swordsgame.play") || player.hasPermission("swordsgame.play")) {
			player.sendMessage(ChatColor.GOLD + "/sg list <#> " + ChatColor.WHITE + "- " + ChatColor.AQUA + plugin.local("commandDesc.list"));
		}
		if (plugin.permissions.has(player, "swordsgame.play") || player.hasPermission("swordsgame.play")) {
			player.sendMessage(ChatColor.GOLD + "/sg game <arena> " + ChatColor.WHITE + "- " + ChatColor.AQUA + plugin.local("commandDesc.game"));
		}
		if (plugin.permissions.has(player, "swordsgame.play") || player.hasPermission("swordsgame.play")) {
			player.sendMessage(ChatColor.GOLD + "/sg leave " + ChatColor.WHITE + "- " + ChatColor.AQUA + plugin.local("commandDesc.leave"));
		}
	}

	public void define(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.define") || player.hasPermission("swordsgame.define")) {
			ContribPlayer cPlayer = (ContribPlayer) player;
			if (!plugin.define.containsKey(player)) {
				plugin.define.put(player, new SwordsGameDefine("define"));
				cPlayer.sendNotification(plugin.local("defining.defining.BukkitContrib.notificationTitle1"), plugin.local("defining.defining.BukkitContrib.notificationText1"), Material.MAP);
				player.sendMessage(ChatColor.GREEN + plugin.local("defining.defining.text1") + ChatColor.GOLD + "/sg define <name>" + ChatColor.GREEN + plugin.local("defining.defining.text2"));
			} else {
				if (plugin.define.get(player).mode == "setspawns") {
					player.sendMessage(ChatColor.RED + plugin.local("errors.defining.spawnsWhileDefining"));
				} else {
					if (plugin.define.get(player).corner1 != null && plugin.define.get(player).corner2 != null && plugin.define.get(player).mode == "define") {
						if (args.length >= 2) {
							if (!plugin.arenas.containsKey(args[1])) {
								plugin.arenas.put(args[1], new SwordsGameArenaClass(args[1], plugin.define.get(player).world.getName(), plugin.define.get(player).corner1, plugin.define.get(player).corner2));
								player.sendMessage(ChatColor.GREEN + "Arena '" + ChatColor.WHITE + args[1] + ChatColor.GREEN + plugin.local("defining.defining.arenaCreated"));
								player.sendMessage(ChatColor.GREEN + plugin.local("defining.defining.setSpawns") + ChatColor.GOLD + "/sg setspawns" + ChatColor.GREEN + ".");
								cPlayer.sendNotification(plugin.local("defining.defining.BukkitContrib.notificationTitle2"), "/sg setspawns", Material.MAP);
								plugin.define.remove(player);
							} else {
								player.sendMessage(ChatColor.RED + plugin.local("errors.defining.alreadyExists"));
							}
						} else {
							player.sendMessage(ChatColor.RED + plugin.local("errors.defining.noName"));
						}
					} else {
						player.sendMessage(ChatColor.RED + plugin.local("errors.defining.alreadyDefining"));
					}
				}
			}
		} else {
			printCommandList(player);
		}
	}

	public void remove(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.define") || player.hasPermission("swordsgame.define")) {
			if (args.length >= 2) {
				if (plugin.arenas.containsKey(args[1])) {
					plugin.arenas.remove(args[1]);
					player.sendMessage(ChatColor.GREEN + "Arena '" + ChatColor.WHITE + args[1] + ChatColor.GREEN + plugin.local("defining.removing.success"));
				} else {
					player.sendMessage(ChatColor.RED + plugin.local("errors.removing.invalidName"));
				}
			} else {
				player.sendMessage(ChatColor.RED + plugin.local("errors.removing.noName"));
			}
		} else {
			printCommandList(player);
		}
	}

	public void setSpawns(Player player) {
		ContribPlayer cPlayer = (ContribPlayer) player;
		if (plugin.permissions.has(player, "swordsgame.define") || player.hasPermission("swordsgame.define")) {
			if (!plugin.define.containsKey(player)) {
				plugin.define.put(player, new SwordsGameDefine("setspawns"));
				cPlayer.sendNotification(plugin.local("defining.settingSpawns.BukkitContrib.notificationTitle1"), plugin.local("defining.settingSpawns.BukkitContrib.notificationText1"), Material.MAP);
				player.sendMessage(ChatColor.GREEN + plugin.local("defining.settingSpawns.help"));
			} else {
				if (plugin.define.get(player).mode == "setspawns") {
					player.sendMessage(ChatColor.RED + plugin.local("errors.settingSpawns.alreadySettingSpawns"));
				} else {
					player.sendMessage(ChatColor.RED + plugin.local("errors.settingSpawns.alreadyDefiningArenas"));
				}
			}
		} else {
			printCommandList(player);
		}
	}

	public void resetSpawns(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.define") || player.hasPermission("swordsgame.define")) {
			if (args.length >= 2) {
				if (plugin.arenas.containsKey(args[1])) {
					plugin.arenas.get(args[1]).resetSpawns();
					player.sendMessage(ChatColor.GREEN + plugin.local("defining.resettingSpawns.success1") + ChatColor.WHITE + args[1] + ChatColor.GREEN + plugin.local("defining.resettingSpawns.success2"));
				} else {
					player.sendMessage(ChatColor.RED + plugin.local("errors.settingSpawns.invalidName"));
				}
			} else {
				player.sendMessage(ChatColor.RED + plugin.local("defining.settingSpawns.noName"));
			}
		} else {
			printCommandList(player);
		}
	}

	public void list(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.play") || player.hasPermission("swordsgame.play")) {
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
			player.sendMessage(ChatColor.GOLD + "SwordsGame arena " + plugin.local("defining.list.arenaList") + showingFrom + "-" + showingTo + plugin.local("defining.list.outOf") + arenaList.length + ")");
			player.sendMessage(ChatColor.GOLD + "-----------");
			for (int i = showingFrom - 1; i < showingFrom + 8; i++) {
				try {
					if (plugin.arenas.get(arenaList[i]).isPrepared()) {
						if (plugin.games.containsKey(arenaList[i])) {
							player.sendMessage(ChatColor.GOLD + "- " + ChatColor.WHITE + arenaList[i] + ChatColor.AQUA + " (" + ChatColor.WHITE + plugin.games.get(arenaList[i]).playercount + ChatColor.AQUA + " out of " + ChatColor.WHITE + "4" + ChatColor.AQUA + plugin.local("defining.list.players"));
						} else {
							player.sendMessage(ChatColor.GOLD + "- " + ChatColor.WHITE + arenaList[i] + ChatColor.AQUA + plugin.local("defining.list.empty"));
						}
					} else {
						player.sendMessage(ChatColor.GOLD + "- " + ChatColor.WHITE + arenaList[i] + ChatColor.AQUA + plugin.local("defining.list.missingSpawns"));
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
		if (plugin.permissions.has(player, "swordsgame.play") || player.hasPermission("swordsgame.play")) {
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
									player.sendMessage(ChatColor.RED + plugin.local("errors.game.full"));
								}
							}
						} else {
							player.sendMessage(ChatColor.RED + plugin.local("errors.game.alreadyInAGame"));
						}
					} else {
						player.sendMessage(ChatColor.RED + plugin.local("errors.game.missingSpawns"));
					}
				} else {
					player.sendMessage(ChatColor.RED + plugin.local("errors.game.invalidName"));
				}
			} else {
				player.sendMessage(ChatColor.RED + plugin.local("errors.game.noName"));
			}
		} else {
			printCommandList(player);
		}
	}

	public void leave(Player player) {
		if (plugin.permissions.has(player, "swordsgame.play") || player.hasPermission("swordsgame.play")) {
			if (plugin.players.containsKey(player)) {
				plugin.players.get(player).restore();
			} else {
				player.sendMessage(ChatColor.RED + plugin.local("errors.leave.notInAGame"));
			}
		} else {
			printCommandList(player);
		}
	}
}
