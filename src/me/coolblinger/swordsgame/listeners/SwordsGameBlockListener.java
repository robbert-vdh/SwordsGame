package me.coolblinger.swordsgame.listeners;

import me.coolblinger.swordsgame.SwordsGame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SwordsGameBlockListener extends BlockListener {
	private final SwordsGame plugin;

	public SwordsGameBlockListener(SwordsGame instance) {
		plugin = instance;
	}

	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (plugin.hasPermissions(player, "swordsgame.define")) {
			if (plugin.players.containsKey(player)) {
				event.setCancelled(true);
			} else if (plugin.getLobby(event.getBlock().getLocation().toVector()) != null) {
				event.setCancelled(true);
			}
		} else if (plugin.getArena(event.getBlock().getLocation().toVector()) != null) {
			event.setCancelled(true);
		} else if (plugin.getLobby(event.getBlock().getLocation().toVector()) != null) {
			event.setCancelled(true);
		} else if (plugin.players.containsKey(player)) {
			event.setCancelled(true);
		}
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (plugin.hasPermissions(player, "swordsgame.define")) {
			if (plugin.players.containsKey(player)) {
				event.setCancelled(true);
			} else if (plugin.getLobby(event.getBlock().getLocation().toVector()) != null) {
				event.setCancelled(true);
			}
		} else if (plugin.getArena(event.getBlock().getLocation().toVector()) != null) {
			event.setCancelled(true);
		} else if (plugin.getLobby(event.getBlock().getLocation().toVector()) != null) {
			event.setCancelled(true);
		} else if (plugin.players.containsKey(player)) {
			event.setCancelled(true);
		}
	}
}
