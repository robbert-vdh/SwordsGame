package me.coolblinger.swordsgame;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import me.coolblinger.swordsgame.classes.*;
import me.coolblinger.swordsgame.listeners.SwordsGameBlockListener;
import me.coolblinger.swordsgame.listeners.SwordsGameEntityListener;
import me.coolblinger.swordsgame.listeners.SwordsGamePlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SwordsGame extends JavaPlugin {
	public Logger log = Logger.getLogger("Minecraft");
	public boolean permissions3;
	public PermissionHandler permissions;
	public final SwordsGameLocalisation localisationConfig = new SwordsGameLocalisation(this);
	public ConcurrentHashMap<String, String> localisation = localisationConfig.getLocalisation();
	private SwordsGamePlayerListener playerListener = new SwordsGamePlayerListener(this);
	private SwordsGameBlockListener blockListener = new SwordsGameBlockListener(this);
	private SwordsGameEntityListener entityListener = new SwordsGameEntityListener(this);
	public ConcurrentHashMap<Player, SwordsGamePlayerRestore> players = new ConcurrentHashMap<Player, SwordsGamePlayerRestore>(); // Used for keeping track of who's in which game.
	public ConcurrentHashMap<String, SwordsGameClass> games = new ConcurrentHashMap<String, SwordsGameClass>();
	public ConcurrentHashMap<String, SwordsGameArenaClass> arenas = new ConcurrentHashMap<String, SwordsGameArenaClass>();
	public ConcurrentHashMap<String, SwordsGameLobbyClass> lobbies = new ConcurrentHashMap<String, SwordsGameLobbyClass>();
	public ConcurrentHashMap<Player, SwordsGameDefine> define = new ConcurrentHashMap<Player, SwordsGameDefine>();

	@Override
	public void onDisable() {
		// Saving arenas
		File arenaFile = new File("plugins" + File.separator + "SwordsGame" + File.separator + "arenas.dat");
		try {
			FileOutputStream arenaFileInputStream = new FileOutputStream(arenaFile);
			ObjectOutputStream arenaFileObjectInputStream = new ObjectOutputStream(arenaFileInputStream);
			arenaFileObjectInputStream.writeObject(arenas);
			arenaFileObjectInputStream.flush();
			arenaFileObjectInputStream.close();
		} catch (Exception e) {
			log.severe("'arenas.dat' could not be written to. (is it outdated?)");
		}
		// Saving lobbies
		File lobbyFile = new File("plugins" + File.separator + "SwordsGame" + File.separator + "lobbies.dat");
		try {
			FileOutputStream lobbyFileInputStream = new FileOutputStream(lobbyFile);
			ObjectOutputStream lobbyFileObjectInputStream = new ObjectOutputStream(lobbyFileInputStream);
			lobbyFileObjectInputStream.writeObject(lobbies);
			lobbyFileObjectInputStream.flush();
			lobbyFileObjectInputStream.close();
		} catch (Exception e) {
			log.severe("'lobbies.dat' could not be written to. (is it outdated?)");
		}
		for (SwordsGamePlayerRestore pRestore : players.values()) {
			pRestore.restore();
		}
		updateLobbySigns();
		PluginDescriptionFile pdFile = this.getDescription();
		log.info(pdFile.getName() + " unloaded!");
	}

	@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
	@Override
	public void onEnable() {
		File SwordsGameDirectory = new File("plugins" + File.separator + "SwordsGame");
		SwordsGameDirectory.mkdir();
		PluginDescriptionFile pdFile = this.getDescription();
		PluginManager pm = getServer().getPluginManager();
		// Autodownloading Spout
		if (pm.getPlugin("Spout") == null) {
			try {
				downloadSpout();
				pm.loadPlugin(new File("plugins" + File.separator + "Spout.jar"));
				pm.enablePlugin(pm.getPlugin("Spout"));
			} catch (Exception e) {
				log.warning("Failed to install Spout, you may have to restart your server or install it manually.");
			}
		}
		// Initialize permissions:
		Plugin permissionsPlugin = pm.getPlugin("Permissions");
		if (permissionsPlugin == null) {
			log.severe("Permissions3 was not found, " + pdFile.getName() + " will use SuperPerms instead.");
			permissions3 = false;
		} else {
			permissions = ((Permissions) permissionsPlugin).getHandler();
			permissions3 = true;
		}
		// Loading arenas
		File arenaFile = new File("plugins/SwordsGame/arenas.dat");
		if (!arenaFile.exists() || arenaFile.length() == 0) {
			try {
				arenaFile.createNewFile();
			} catch (Exception e) {
				log.severe("'arenas.dat' could not be made. Arena saving is disabled.");
			}
		} else {
			try {
				FileInputStream arenaFileInputStream = new FileInputStream(arenaFile);
				ObjectInputStream arenaFileObjectInputStream = new ObjectInputStream(arenaFileInputStream);
				arenas = (ConcurrentHashMap<String, SwordsGameArenaClass>) arenaFileObjectInputStream.readObject();
				arenaFileObjectInputStream.close();
			} catch (Exception e) {
				log.severe("'arenas.dat' could not be read (was it outdated?) and has been deleted.");
				try {
					arenaFile.delete();
					arenaFile.createNewFile();
				} catch (Exception ignored) {
					e.printStackTrace();
				}
			}
		}
		// Loading lobbies
		File lobbyFile = new File("plugins/SwordsGame/lobbies.dat");
		if (!lobbyFile.exists() || lobbyFile.length() == 0) {
			try {
				lobbyFile.createNewFile();
			} catch (Exception e) {
				log.severe("'lobbies.dat' could not be made. Lobby saving is disabled.");
			}
		} else {
			try {
				FileInputStream lobbyFileInputStream = new FileInputStream(lobbyFile);
				ObjectInputStream lobbyFileObjectInputStream = new ObjectInputStream(lobbyFileInputStream);
				lobbies = (ConcurrentHashMap<String, SwordsGameLobbyClass>) lobbyFileObjectInputStream.readObject();
				lobbyFileObjectInputStream.close();
			} catch (Exception e) {
				log.severe("'lobbies.dat' could not be read (was it outdated?) and has been deleted.");
				try {
					lobbyFile.delete();
					lobbyFile.createNewFile();
				} catch (Exception ignored) {
					e.printStackTrace();
				}
			}
		}
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.High, this);
		updateLobbySigns();
		// Check for updates
		try {
			updateCheck();
		} catch (Exception e) {
			log.severe("SwordsGame could not check for updates.");
		}
		log.info(pdFile.getName() + " version " + pdFile.getVersion() + " loaded!");
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		SwordsGameCommand cmd = new SwordsGameCommand(this);
		return cmd.execute(sender, command, commandLabel, args);
	}

	public void downloadSpout() throws IOException {
		File file = new File("plugins" + File.separator + "Spout.jar");
		URL url = new URL("http://dl.dropbox.com/u/49805/Spout.jar");
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

	public String getLobby(Vector vector) { // An easy way to see which arena a coordinate belongs to.
		List<SwordsGameLobbyClass> arenaList = new ArrayList<SwordsGameLobbyClass>(lobbies.values());
		for (SwordsGameLobbyClass lobby : arenaList) {
			if (clamp(vector.getX(), lobby.cornerX[0], lobby.cornerX[1]) == vector.getX() && clamp(vector.getY(), lobby.cornerY[0], lobby.cornerY[1]) == vector.getY() && clamp(vector.getZ(), lobby.cornerZ[0], lobby.cornerZ[1]) == vector.getZ()) {
				return lobby.arena;
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

	public String local(String path) { //Get the corrosponding localisation string
		return localisation.get(path);
	}

	public void createLobby(String name, Player player) {
		lobbies.put(name, new SwordsGameLobbyClass(name, player));
		String face = lobbies.get(name).face;
		if (face != null) {
			SwordsGameLobbyClass lobby = lobbies.get(name);
			Vector corner1 = new Vector(lobby.cornerX[0], lobby.cornerY[0], lobby.cornerZ[0]);
			for (double i = 0; i < 3; i++) {
				for (double n = 0; n < 3; n++) {
					corner1.toLocation(toWorld(lobby.world)).add(i, 0, -n).getBlock().setType(Material.GLASS);
				}
			}
			Vector port = new Vector(lobby.portX, lobby.portY, lobby.portZ);
			port.toLocation(toWorld(lobby.world)).subtract(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
			for (double i = 0; i < 3; i++) {
				if (lobby.face == "NORTH") {
					if (i == 0 || i == 1) {
						port.toLocation(toWorld(lobby.world)).add(0, i, 1).getBlock().setType(Material.STONE);
						port.toLocation(toWorld(lobby.world)).add(0, i, -1).getBlock().setType(Material.STONE);
					} else {
						port.toLocation(toWorld(lobby.world)).add(0, i, 0).getBlock().setType(Material.STONE);
					}
				} else if (lobby.face == "SOUTH") {
					if (i == 0 || i == 1) {
						port.toLocation(toWorld(lobby.world)).add(0, i, 1).getBlock().setType(Material.STONE);
						port.toLocation(toWorld(lobby.world)).add(0, i, -1).getBlock().setType(Material.STONE);
					} else {
						port.toLocation(toWorld(lobby.world)).add(0, i, 0).getBlock().setType(Material.STONE);
					}
				} else if (lobby.face == "WEST") {
					if (i == 0 || i == 1) {
						port.toLocation(toWorld(lobby.world)).add(1, i, 0).getBlock().setType(Material.STONE);
						port.toLocation(toWorld(lobby.world)).add(-1, i, 0).getBlock().setType(Material.STONE);
					} else {
						port.toLocation(toWorld(lobby.world)).add(0, i, 0).getBlock().setType(Material.STONE);
					}
				} else if (lobby.face == "EAST") {
					if (i == 0 || i == 1) {
						port.toLocation(toWorld(lobby.world)).add(1, i, 0).getBlock().setType(Material.STONE);
						port.toLocation(toWorld(lobby.world)).add(-1, i, 0).getBlock().setType(Material.STONE);
					} else {
						port.toLocation(toWorld(lobby.world)).add(0, i, 0).getBlock().setType(Material.STONE);
					}
				}
			}
			Vector sign = new Vector(lobby.signX, lobby.signY, lobby.signZ);
			if (lobby.face == "NORTH") {
				sign.toLocation(toWorld(lobby.world)).getBlock().setTypeIdAndData(68, (byte) 0x5, true);
			} else if (lobby.face == "SOUTH") {
				sign.toLocation(toWorld(lobby.world)).getBlock().setTypeIdAndData(68, (byte) 0x4, true);
			} else if (lobby.face == "WEST") {
				sign.toLocation(toWorld(lobby.world)).getBlock().setTypeIdAndData(68, (byte) 0x2, true);
			} else if (lobby.face == "EAST") {
				sign.toLocation(toWorld(lobby.world)).getBlock().setTypeIdAndData(68, (byte) 0x3, true);
			}
			Sign signSign = (Sign) sign.toLocation(toWorld(lobby.world)).getBlock().getState();
			signSign.setLine(0, ChatColor.DARK_RED + "-SwordsGame-");
			signSign.setLine(1, lobby.arena);
			if (games.containsKey(lobby.arena)) {
				signSign.setLine(3, ChatColor.DARK_GREEN + Integer.toString(games.get(lobby.arena).playercount) + "/4 " + local("lobby.players"));
			} else {
				signSign.setLine(3, ChatColor.DARK_GRAY + "0/4 " + local("lobby.players"));
			}
			player.sendMessage(ChatColor.GREEN + local("lobby.success"));
		} else {
			lobbies.remove(name);
			player.sendMessage(ChatColor.RED + local("errors.lobby.create.invalidLocation"));
		}
	}

	public void removeLobby(String name, Player player) {
		lobbies.remove(name); // TODO: Remove the lobby itself (and restore the original blocks, if modified)
		player.sendMessage(ChatColor.GREEN + local("lobby.remove1") + ChatColor.WHITE + name + ChatColor.GREEN + local("lobby.remove2"));
	}

	public void updateLobbySigns() {
		for (SwordsGameLobbyClass lobby : lobbies.values()) {
			Vector sign = new Vector(lobby.signX, lobby.signY, lobby.signZ);
			BlockState signState = sign.toLocation(toWorld(lobby.world)).getBlock().getState();
			if (signState instanceof Sign) {
				Sign signSign = (Sign) signState;
				if (games.containsKey(lobby.arena)) {
					signSign.setLine(3, ChatColor.DARK_GREEN + Integer.toString(games.get(lobby.arena).playercount) + "/4 " + local("lobby.players"));
				} else {
					signSign.setLine(3, ChatColor.DARK_GRAY + "0/4 " + local("lobby.players"));
				}
				signSign.update();
			}
		}
	}

	public void updateCheck() throws IOException {
		URL url = new URL("http://dl.dropbox.com/u/677732/uploads/SwordsGame.jar");
		int urlSize = url.openConnection().getContentLength();
		File pluginFile = new File("plugins" + File.separator + "SwordsGame.jar");
		if (!pluginFile.exists()) {
			log.severe("SwordsGame has not been installed correctly");
			return;
		}
		long pluginFileSize = pluginFile.length();
		if (urlSize != pluginFileSize) {
			BukkitScheduler bScheduler = this.getServer().getScheduler();
			bScheduler.scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					log.warning("There has been an update for SwordsGame.");
				}
			}, 600);
		}
	}

	private void initConfig() {
		org.bukkit.util.config.Configuration config = getConfiguration();
		config.load();
		if (config.getProperty("allowCommands") == null) {
			List<String> dummyList = new ArrayList<String>();
			dummyList.add("/time");
			dummyList.add("/help");
			config.setProperty("allowCommands", dummyList);
			config.save();
		}
		if (config.getProperty("spawnOnKill") == null) {
			config.setProperty("spawnOnKill", true);
			config.save();
		}
		if (config.getProperty("lobbyOnly") == null) {
			config.setProperty("lobbyOnly", false);
			config.save();
		}
		if (config.getProperty("ladder.custom") == null) {
			config.setProperty("ladder.custom", false);
			config.save();
		}
		if (config.getProperty("ladder.ladder") == null) {
			List<Integer> dummyList = new ArrayList<Integer>();
			dummyList.add(276);
			dummyList.add(267);
			dummyList.add(279);
			dummyList.add(258);
			dummyList.add(283);
			dummyList.add(286);
			dummyList.add(285);
			dummyList.add(0);
			config.setProperty("ladder.ladder", dummyList);
			config.save();
		}
		if (config.getProperty("ladder.sideItems") == null) {
			List<Integer> dummyList = new ArrayList<Integer>();
			dummyList.add(320);
			config.setProperty("ladder.sideItems", dummyList);
			config.save();
		}
	}

	public List configList(String path) {
		org.bukkit.util.config.Configuration config = getConfiguration();
		config.load();
		return config.getList(path);
	}

	public boolean configBoolean(String path) {
		org.bukkit.util.config.Configuration config = getConfiguration();
		config.load();
		return config.getBoolean(path, false);
	}

	public boolean hasPermissions(Player player, String permission) {
		if (permissions3) {
			if (permissions.has(player, permission)) {
				return true;
			} else {
				return false;
			}
		} else {
			if (player.hasPermission(permission)) {
				return true;
			} else {
				return false;
			}
		}
	}
}
