package me.coolblinger.swordsgame.classes;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.Serializable;

public class SwordsGameLobbyClass implements Serializable {
	public final double[] cornerX = new double[2];
	public final double[] cornerY = new double[2];
	public final double[] cornerZ = new double[2];

	public double portX;
	public double portY;
	public double portZ;

	public double signX;
	public double signY;
	public double signZ;

	public String face;

	public String arena;
	public String world; // World has to be a string, or else serialization would be hard.

	public SwordsGameLobbyClass(String _arena, Player player) {
		cornerX[0] = player.getLocation().getBlock().getLocation().toVector().subtract(new Vector(1, 0, 0)).getX();
		cornerY[0] = player.getLocation().getBlock().getLocation().toVector().getY();
		cornerZ[0] = player.getLocation().getBlock().getLocation().toVector().add(new Vector(0, 0, 1)).getZ();

		cornerX[1] = player.getLocation().getBlock().getLocation().toVector().add(new Vector(1, 0, 0)).getX();
		cornerY[1] = player.getLocation().getBlock().getLocation().toVector().add(new Vector(0, 3, 0)).getY();
		cornerZ[1] = player.getLocation().getBlock().getLocation().toVector().subtract(new Vector(0, 0, 1)).getZ();
		arena = _arena;
		world = player.getWorld().getName();
		int dir = Math.round(player.getLocation().getYaw() / 90);
		if (dir == 1 || dir == -3) {
			face = "NORTH";
			portX = cornerX[0];
			portY = cornerY[0] + 1;
			portZ = cornerZ[0] - 1;
			signX = portX + 1;
			signY = portY + 1;
			signZ = portZ - 1;
		} else if (dir == 2 || dir == -2) {
			face = "EAST";
			portX = cornerX[0] + 1;
			portY = cornerY[0] + 1;
			portZ = cornerZ[0] - 2;
			signX = portX + 1;
			signY = portY + 1;
			signZ = portZ + 1;
		} else if (dir == -1 || dir == 3) {
			face = "SOUTH";
			portX = cornerX[0] + 2;
			portY = cornerY[0] + 1;
			portZ = cornerZ[0] - 1;
			signX = portX - 1;
			signY = portY + 1;
			signZ = portZ + 1;
		} else if (dir == 0 || dir == 4) {
			face = "WEST";
			portX = cornerX[0] + 1;
			portY = cornerY[0] + 1;
			portZ = cornerZ[0];
			signX = portX - 1;
			signY = portY + 1;
			signZ = portZ - 1;
		} else {
			face = null;
		}
	}
}