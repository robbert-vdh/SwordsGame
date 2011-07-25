package me.coolblinger.swordsgame.classes;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkitcontrib.player.ContribPlayer;

public class SwordsGameDefine {
	public Vector corner1;
	public Vector corner2;
	private boolean secondcorner;
	public World world;
	private boolean showedWarning;
	public String mode;

	public SwordsGameDefine(String _mode) {
		mode = _mode;
	}

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
				cPlayer.sendNotification("Defining", "/sg define <name>", Material.MAP);
				showedWarning = true;
			}
			return true;
		} else {
			corner1 = vector;
			secondcorner = true;
			if (corner1 != null && corner2 != null && !showedWarning) {
				ContribPlayer cPlayer = (ContribPlayer) player;
				cPlayer.sendNotification("Defining", "/sg define <name>", Material.MAP);
				showedWarning = true;
			}
			return false;
		}
	}
}