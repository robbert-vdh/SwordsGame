package me.coolblinger.swordsgame;

import org.bukkit.util.Vector;

import java.io.Serializable;

public class SwordsGameArenaClass implements Serializable {
	double corner1x;
	double corner1y;
	double corner1z;

	double corner2x;
	double corner2y;
	double corner2z;

	String name;
	String world; //World has to be a string, or else serialization would be hard.

	public SwordsGameArenaClass(String _name, String _world, Vector _corner1, Vector _corner2) {
		corner1x = _corner1.getX();
		corner1y = _corner1.getY();
		corner1z = _corner1.getZ();

		corner2x = _corner2.getX();
		corner2y = _corner2.getY();
		corner2z = _corner2.getZ();

		name = _name;
		world = _world;
	}
}
/*
public class SwordsGameArenaClass implements Serializable{
	Vector corner1;
	Vector corner2;
	String name;
	String world; //World has to be a string, or else serialization would be hard.

	public SwordsGameArenaClass(String _name, String _world, Vector _corner1, Vector _corner2) {
		corner1 = _corner1;
		corner2 = _corner2;
		name = _name;
		world = _world;
	}
}
*/