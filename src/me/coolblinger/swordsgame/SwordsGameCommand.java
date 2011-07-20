package me.coolblinger.swordsgame;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkitcontrib.player.ContribPlayer;

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
		if (plugin.permissions.has(player, "swordsgame.create")) {
			player.sendMessage(ChatColor.GOLD + "/sg create " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Create a new game");
		}
		if (plugin.permissions.has(player, "swordsgame.define")) {
			player.sendMessage(ChatColor.GOLD + "/sg define " + ChatColor.WHITE + "-" + ChatColor.AQUA + " Define a new arena");
		}
	}

	public void define(Player player, String[] args) {
		ContribPlayer cPlayer = (ContribPlayer) player;
		if (!plugin.define.containsKey(player)) {
			plugin.define.put(player, new SwordsGameDefine());
			cPlayer.sendNotification("Defining", "Click both corners.", Material.MAP);
			player.sendMessage(ChatColor.GREEN + "Right click at both corners of the arena. You can type          " + ChatColor.GOLD + "/sg define <name>" + ChatColor.GREEN + " when you're done, or right click to cancel.");
		} else {
			if (plugin.define.get(player).corner1 != null && plugin.define.get(player).corner2 != null) {
				if (args.length >= 2) {
					if (!plugin.arenas.containsKey(args[1])) {
						plugin.arenas.put(args[1], new SwordsGameArenaClass(args[1], plugin.define.get(player).world.getName(), plugin.define.get(player).corner1, plugin.define.get(player).corner2));
						player.sendMessage(ChatColor.GREEN + "Arena '" + ChatColor.WHITE + args[1] + ChatColor.GREEN + "' has been created!");
						plugin.define.remove(player);
					} else {
						player.sendMessage(ChatColor.RED + "An arena with that name already exists");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You need to specify a name for the new arena.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "Defining has been canceled");
				plugin.define.remove(player);
			}
		}
	}
}
