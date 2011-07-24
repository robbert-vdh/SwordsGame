package me.coolblinger.swordsgame;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import me.coolblinger.swordsgame.classes.SwordsGameArenaClass;
import me.coolblinger.swordsgame.classes.SwordsGameClass;
import me.coolblinger.swordsgame.classes.SwordsGameDefine;
import me.coolblinger.swordsgame.classes.SwordsGamePlayerRestore;
import me.coolblinger.swordsgame.listeners.SwordsGameBlockListener;
import me.coolblinger.swordsgame.listeners.SwordsGameEntityListener;
import me.coolblinger.swordsgame.listeners.SwordsGamePlayerListener;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class SwordsGame extends JavaPlugin {
	public Logger log = Logger.getLogger("Minecraft");
	public PermissionHandler permissions;
	private SwordsGamePlayerListener playerListener = new SwordsGamePlayerListener(this);
	private SwordsGameBlockListener blockListener = new SwordsGameBlockListener(this);
	private SwordsGameEntityListener entityListener = new SwordsGameEntityListener(this);
	private boolean saving;
	public HashMap<Player, SwordsGamePlayerRestore> players = new HashMap<Player, SwordsGamePlayerRestore>(); // Used for keeping track of who's in which game.
	public HashMap<String, SwordsGameClass> games = new HashMap<String, SwordsGameClass>();
	public HashMap<String, SwordsGameArenaClass> arenas = new HashMap<String, SwordsGameArenaClass>();
	public HashMap<Player, SwordsGameDefine> define = new HashMap<Player, SwordsGameDefine>();

	@Override
	public void onDisable() {
		// Saving arenas
		File arenaFile = new File("plugins" + File.separator + "SwordsGame" + File.separator + "arenas.dat");
		if (saving) {
			try {
				FileOutputStream arenaFileInputStream = new FileOutputStream(arenaFile);
				ObjectOutputStream arenaFileObjectInputStream = new ObjectOutputStream(arenaFileInputStream);
				arenaFileObjectInputStream.writeObject(arenas);
				arenaFileObjectInputStream.flush();
				arenaFileObjectInputStream.close();
			} catch (Exception e) {
				log.severe("'arenas.dat' could not be written to. (is it outdated?)");
			}
		}
		for (SwordsGamePlayerRestore pRestore : players.values()) {
			pRestore.restore();
		}
		PluginDescriptionFile pdFile = this.getDescription();
		log.info(pdFile.getName() + " unloaded!");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		File SwordsGameDirectory = new File("plugins" + File.separator + "SwordsGame");
		SwordsGameDirectory.mkdir();
		PluginDescriptionFile pdFile = this.getDescription();
		PluginManager pm = getServer().getPluginManager();
		// Autodownloading BukkitContrib
		if (pm.getPlugin("BukkitContrib") == null) {
			try {
				downloadBukkitContrib();
				pm.loadPlugin(new File("plugins" + File.separator + "BukkitContrib.jar"));
				pm.enablePlugin(pm.getPlugin("BukkitContrib"));
			} catch (Exception e) {
				log.warning("Failed to install BukkitContrib, you may have to restart your server or install it manually.");
			}
		}
		// Initialize permissions:
		Plugin permissionsPlugin = pm.getPlugin("Permissions");
		if (permissionsPlugin == null) {
			log.severe("Permissions was not found, " + pdFile.getName() + " will disable itself.");
			this.setEnabled(false);
			return;
		}
		permissions = ((Permissions) permissionsPlugin).getHandler();
		// Loading arenas
		File arenaFile = new File("plugins/SwordsGame/arenas.dat");
		if (!arenaFile.exists() || arenaFile.length() == 0) {
			try {
				arenaFile.createNewFile();
				saving = true;
			} catch (Exception e) {
				log.severe("'arenas.dat' could not be made. Arena saving is disabled.");
				saving = false;
			}
		} else {
			try {
				FileInputStream arenaFileInputStream = new FileInputStream(arenaFile);
				ObjectInputStream arenaFileObjectInputStream = new ObjectInputStream(arenaFileInputStream);
				arenas = (HashMap<String, SwordsGameArenaClass>) arenaFileObjectInputStream.readObject();
				arenaFileObjectInputStream.close();
				saving = true;
			} catch (Exception e) {
				log.severe("'arenas.dat' could not be read (is it outdated?), arena loading and saving has been disabled.");
				saving = false;
			}
		}
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.High, this);
		log.info(pdFile.getName() + " version " + pdFile.getVersion() + " loaded!");
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		SwordsGameCommand cmd = new SwordsGameCommand(this);
		return cmd.execute(sender, command, commandLabel, args);
	}

	public void downloadBukkitContrib() throws IOException {
		File file = new File("plugins" + File.separator + "BukkitContrib.jar");
		URL url = new URL("http://dl.dropbox.com/u/49805/BukkitContrib.jar");
		if (!file.getParentFile().exists())
			file.getParentFile().mkdir();
		if (file.exists())
			file.delete();
		file.createNewFile();
		final int size = url.openConnection().getContentLength();
		log.info("Downloading " + file.getName() + " (" + size / 1024 + "kb) ...");
		final InputStream in = url.openStream();
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		final byte[] buffer = new byte[1024];
		int len, downloaded = 0, msgs = 0;
		final long start = System.currentTimeMillis();
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
			downloaded += len;
			if ((int) ((System.currentTimeMillis() - start) / 500) > msgs) {
				log.info((int) ((double) downloaded / (double) size * 100d) + "%");
				msgs++;
			}
		}
		in.close();
		out.close();
		log.info("Download finished");
	}

	public Double clamp(Double value, Double compare1, Double compare2) {
		double min;
		double max;
		if (compare2 < compare1) {
			max = compare1;
			min = compare2;
		} else {
			max = compare2;
			min = compare1;
		}
		if (value.compareTo(max) > 0) {
			return max;
		} else if (value.compareTo(min) < 0) {
			return min;
		} else {
			return value;
		}
	}

	public String getArena(Vector vector) { // An easy way to see which arena a coordinate belongs to.
		List<SwordsGameArenaClass> arenaList = new ArrayList<SwordsGameArenaClass>(arenas.values());
		for (SwordsGameArenaClass arena : arenaList) {
			if (clamp(vector.getX(), arena.cornerX[0], arena.cornerX[1]) == vector.getX() && clamp(vector.getZ(), arena.cornerZ[0], arena.cornerZ[1]) == vector.getZ()) {
				return arena.name;
			}
		}
		return null;
	}

	public World toWorld(String worldString) {
		World world = this.getServer().getWorld(worldString);
		if (world != null) {
			return world;
		} else {
			return null;
		}
	}
}
