package me.coolblinger.swordsgame;

import org.bukkit.util.Vector;

import java.io.Serializable;

public class SwordsGameArenaClass implements Serializable {
	double[] cornerX = new double[2];
	double[] cornerY = new double[2]; // I seem to get an outofbounds exception when using 1.
	double[] cornerZ = new double[2];

	double[] spawnX = new double[3];
	double[] spawnY = new double[3];
	double[] spawnZ = new double[3];

	String world; // World has to be a string, or else serialization would be hard.

	public SwordsGameArenaClass(String _world, Vector _corner1, Vector _corner2) {
		cornerX[0] = _corner1.getX();
		cornerY[0] = _corner1.getY();
		cornerZ[0] = _corner1.getZ();

		cornerZ[1] = _corner2.getX();
		cornerY[1] = _corner2.getY();
		cornerZ[1] = _corner2.getZ();

		world = _world;
	}
}