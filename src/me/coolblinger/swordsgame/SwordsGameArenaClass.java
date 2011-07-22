package me.coolblinger.swordsgame;

import org.bukkit.util.Vector;

import java.io.Serializable;

public class SwordsGameArenaClass implements Serializable {
	double[] cornerX = new double[2]; // I seem to get an outofbounds exception when using 1.
	double[] cornerZ = new double[2];

	double[] spawnX = new double[4]; // Same
	double[] spawnY = new double[4];
	double[] spawnZ = new double[4];

	String name;
	String world; // World has to be a string, or else serialization would be hard.

	public SwordsGameArenaClass(String _name, String _world, Vector _corner1, Vector _corner2) {
		cornerX[0] = _corner1.getX();
		cornerZ[0] = _corner1.getZ();

		cornerX[1] = _corner2.getX();
		cornerZ[1] = _corner2.getZ();

		name = _name;
		world = _world;
	}

	public int setSpawn(Vector vector) {
		for (int i = 0; i <= 3; i++) {
			if (spawnX[i] == 0 && spawnY[i] == 0 && spawnZ[i] == 0) // I can't think of another way to check if it's in use already, since doubles can't be null.
			{
				spawnX[i] = vector.getX();
				spawnY[i] = vector.getY() + 1;
				spawnZ[i] = vector.getZ();
				return i + 1;
			}
		}
		return 0;
	}

	public void resetSpawns() {
		for (int i = 0; i <= 3; i++) {
			spawnX[i] = 0;
			spawnY[i] = 0;
			spawnZ[i] = 0;
		}
	}

	public boolean isPrepared() { //Will return true when all four spawnpoints have been set
		for (int i = 0; i <= 3; i++) {
			if (spawnX[i] == 0 && spawnY[i] == 0 && spawnZ[i] == 0) {
				return false;
			}
		}
		return true;
	}
}