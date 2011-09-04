package me.coolblinger.swordsgame.classes;

import org.bukkit.util.Vector;

import java.io.Serializable;

public class SwordsGameArenaClass implements Serializable {
	public double[] cornerX = new double[2];
	public double[] cornerZ = new double[2];

	double[] spawnX = new double[20];
	double[] spawnY = new double[20];
	double[] spawnZ = new double[20];
	public int spawnCount = 0;

	public String name;
	public String world; // World has to be a string, or else serialization would be hard.

	public SwordsGameArenaClass(String _name, String _world, Vector _corner1, Vector _corner2) {
		cornerX[0] = _corner1.getX();
		cornerZ[0] = _corner1.getZ();

		cornerX[1] = _corner2.getX();
		cornerZ[1] = _corner2.getZ();

		name = _name;
		world = _world;
	}

	public int setSpawns(Vector vector) {
		for (int i = 0; i <= 19; i++) {
			if (spawnX[i] == 0 && spawnY[i] == 0 && spawnZ[i] == 0) // I can't think of another way to check if it's in use already, since doubles can't be null.
			{
				spawnX[i] = vector.getX();
				spawnY[i] = vector.getY() + 1;
				spawnZ[i] = vector.getZ();
				spawnCount++;
				return i + 1;
			}
		}
		return 0;
	}

	public void resetSpawns() {
		for (int i = 0; i <= 19; i++) {
			spawnX[i] = 0;
			spawnY[i] = 0;
			spawnZ[i] = 0;
		}
		spawnCount = 0;
	}
}