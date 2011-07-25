package me.coolblinger.swordsgame.listeners;

import me.coolblinger.swordsgame.SwordsGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;

public class SwordsGamePlayerListener extends PlayerListener {
	SwordsGame plugin;

	public SwordsGamePlayerListener(SwordsGame instance) {
		plugin = instance;
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		if (plugin.players.containsKey(event.getPlayer())) {
			plugin.players.get(event.getPlayer()).restore();
		}
	}

	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (plugin.getArena(event.getTo().subtract(0.5, 0, 0.5).toVector()) == null && plugin.players.containsKey(player)) {
			plugin.games.get(plugin.players.get(player).arena).toSpawn(player, false);
		}
	}

	@SuppressWarnings("unchecked")
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String[] split = event.getMessage().split(" ");
		if (plugin.players.containsKey(event.getPlayer())) {
			List<String> allowedCommands = plugin.config.readList("allowCommands");
			allowedCommands.add("/sg");
			allowedCommands.add("/swordsgame");
			if (containsIgnoreCase(allowedCommands, split[0])) {

			} else {
				event.getPlayer().sendMessage(ChatColor.RED + "You can't execute this command while in a SwordsGame game.");
				event.setCancelled(true);
			}
		}
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (plugin.define.containsKey(player)) {
			if (plugin.define.get(player).mode == "define") {
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (event.getClickedBlock() != null) {
						Vector vector = event.getClickedBlock().getLocation().toVector();
						if (plugin.define.get(player).setCorner(vector, player.getWorld(), player)) {
							player.sendMessage(ChatColor.GREEN + "Second corner has been set to " + ChatColor.WHITE + vector.toString() + ChatColor.GREEN + ".");
						} else {
							player.sendMessage(ChatColor.GREEN + "First corner has been set to " + ChatColor.WHITE + vector.toString() + ChatColor.GREEN + ".");
						}
					}
				} else {
					plugin.define.remove(player);
					player.sendMessage(ChatColor.RED + "Defining has been canceled.");
				}
			} else if (plugin.define.get(player).mode == "setspawns") {
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (event.getClickedBlock() != null) {
						Vector vector = event.getClickedBlock().getLocation().toVector();
						String arenaName = plugin.getArena(vector);
						if (arenaName != null) {
							int setSpawns = plugin.arenas.get(arenaName).setSpawns(vector);
							if (setSpawns != 0) {
								player.sendMessage(ChatColor.GREEN + "Added spawn " + ChatColor.WHITE + setSpawns + ChatColor.GREEN + " out of " + ChatColor.WHITE + "4" + ChatColor.GREEN + " for arena '" + ChatColor.WHITE + arenaName + ChatColor.GREEN + "'.");
							} else {
								player.sendMessage(ChatColor.RED + "You've already created four spawns.");
							}
						} else {
							player.sendMessage("This is not a valid arena.");
						}
					}
				} else {
					plugin.define.remove(player);
					player.sendMessage(ChatColor.RED + "Setting spawns has been canceled.");
				}
			}
		}
	}

	public boolean containsIgnoreCase(List<String> list, String string) {
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			if (it.next().equalsIgnoreCase(string))
				return true;
		}
		return false;
	}
}
