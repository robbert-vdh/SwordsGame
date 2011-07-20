package me.coolblinger.swordsgame;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.Vector;

public class SwordsGamePlayerListener extends PlayerListener {
	SwordsGame plugin;

	public SwordsGamePlayerListener(SwordsGame instance) {
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (plugin.define.containsKey(player)) {
			if (event.getClickedBlock() != null) {
				Vector vector = event.getClickedBlock().getLocation().toVector();
				boolean corner = plugin.define.get(player).setCorner(vector, player.getWorld(), player);
				if (corner) {
					player.sendMessage(ChatColor.GREEN + "Second corner has been set to " + ChatColor.WHITE + vector.toString());
				} else if (!corner) {
					player.sendMessage(ChatColor.GREEN + "First corner has been set to " + ChatColor.WHITE + vector.toString());
				}
			}
		}
	}
}
