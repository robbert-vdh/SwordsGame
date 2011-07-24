package me.coolblinger.swordsgame;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SwordsGameBlockListener extends BlockListener {
	SwordsGame plugin;

	public SwordsGameBlockListener(SwordsGame instance) {
		plugin = instance;
	}

	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (plugin.getArena(event.getBlock().getLocation().toVector()) != null && !plugin.permissions.has(player, "swordsgame.define")) {
			event.setCancelled(true);
		} else if (plugin.players.containsKey(player)) {
			event.setCancelled(true);
		}
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (plugin.getArena(event.getBlock().getLocation().toVector()) != null && !plugin.permissions.has(player, "swordsgame.define")) {
			event.setCancelled(true);
		} else if (plugin.players.containsKey(player)) {
			event.setCancelled(true);
		}
	}
}
