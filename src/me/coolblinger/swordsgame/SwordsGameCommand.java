package me.coolblinger.swordsgame;

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
			sender.sendMessage("This command is only executable by players."); // TODO: Do this for indevidual commands.
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
				} else if (args[0].equalsIgnoreCase("list")) {
					list(player, args);
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
			player.sendMessage(ChatColor.GOLD + "/sg remove <name> " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Remove an arena");
		}
		if (plugin.permissions.has(player, "swordsgame.play")) {
			player.sendMessage(ChatColor.GOLD + "/sg list <#> " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Arena list");
		}
	}

	public void define(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.define")) {
			ContribPlayer cPlayer = (ContribPlayer) player;
			if (!plugin.define.containsKey(player)) {
				plugin.define.put(player, new SwordsGameDefine());
				cPlayer.sendNotification("Defining", "Right click both corners", Material.MAP);
				player.sendMessage(ChatColor.GREEN + "Right click at both corners of the arena. You can type          " + ChatColor.GOLD + "/sg define <name>" + ChatColor.GREEN + " when you're done, or left click to cancel.");
			} else {
				if (plugin.define.get(player).corner1 != null && plugin.define.get(player).corner2 != null) {
					if (args.length >= 2) {
						if (!plugin.arenas.containsKey(args[1])) {
							plugin.arenas.put(args[1], new SwordsGameArenaClass(plugin.define.get(player).world.getName(), plugin.define.get(player).corner1, plugin.define.get(player).corner2));
							player.sendMessage(ChatColor.GREEN + "Arena '" + ChatColor.WHITE + args[1] + ChatColor.GREEN + "' has been created!");
							plugin.define.remove(player);
						} else {
							player.sendMessage(ChatColor.RED + "An arena with that name already exists.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You need to specify a name for the new arena.");
					}
				} else {
					plugin.define.remove(player);
					player.sendMessage(ChatColor.RED + "Defining has been canceled");
				}
			}
		} else {
			printCommandList(player);
		}
	}

	public void remove(Player player, String[] args) {
		if (plugin.permissions.has(player, "swordsgame.define")) {
			if (args[1] != null) {
				if (plugin.arenas.containsKey(args[1])) {
					plugin.arenas.remove(args[1]);
					player.sendMessage(ChatColor.GREEN + "Arena '" + ChatColor.WHITE + args[1] + ChatColor.GREEN + "' has been removed.");
				} else {
					player.sendMessage(ChatColor.RED + "Invalid arena name specified.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You need to specify the name of the arena you want to remove.");
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
					player.sendMessage(ChatColor.GOLD + "- " + ChatColor.WHITE + arenaList[i]);
				} catch (Exception e) {
					return; // An easier way of checking if the value is null.
				}
			}
		} else {
			printCommandList(player);
		}
	}
}
