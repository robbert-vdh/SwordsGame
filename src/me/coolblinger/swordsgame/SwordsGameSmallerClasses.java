package me.coolblinger.swordsgame;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkitcontrib.player.ContribPlayer;

//This file contains the smaller classes

class SwordsGameDefine {
	Vector corner1;
	Vector corner2;
	boolean secondcorner;
	World world;
	boolean showedWarning;

	public boolean setCorner(Vector vector, World _world, Player player) {
		if (_world != world) {
			world = _world;
			secondcorner = false;
			corner1 = null;
			corner2 = null;
		}
		if (secondcorner) {
			corner2 = vector;
			secondcorner = false;
			if (corner1 != null && corner2 != null && !showedWarning) {
				ContribPlayer cPlayer = (ContribPlayer) player;
				cPlayer.sendNotification("Defining", "/sg define <name>'", Material.MAP);
				showedWarning = true;
			}
			return true;
		} else {
			corner1 = vector;
			secondcorner = true;
			if (corner1 != null && corner2 != null && !showedWarning) {
				ContribPlayer cPlayer = (ContribPlayer) player;
				cPlayer.sendNotification("Help", "Type '/define <name>' to finish.", Material.MAP);
				showedWarning = true;
			}
			return false;
		}
	}
}