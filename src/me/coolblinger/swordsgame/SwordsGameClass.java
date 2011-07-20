package me.coolblinger.swordsgame;

import org.bukkit.entity.Player;

public class SwordsGameClass {
	Player[] players = new Player[3];

	public boolean AddPlayer(Player player) {
		for (Player p : players) {
			if (p == null) {
				p = player;
				return true;
			}
		}
		return false;
	}

	public boolean RemovePlayer(Player player) {
		for (Player p : players) {
			if (p == player) {
				p = null;
				return true;
			}
		}
		return false;
	}
}
