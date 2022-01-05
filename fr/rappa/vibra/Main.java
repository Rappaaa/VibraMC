package fr.rappa.vibra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.rappa.vibra.anticheat.VACListener;
import fr.rappa.vibra.commands.CommandManager;
import fr.rappa.vibra.event.EventListener;
import fr.rappa.vibra.event.MenuListener;
import fr.rappa.vibra.game.GameManager;
import fr.rappa.vibra.party.PartyManager;
import fr.rappa.vibra.player.PlayerVibra;
import fr.rappa.vibra.rank.Rank;
import fr.rappa.vibra.utils.LocationUtil;

public class Main extends JavaPlugin {

	public List<PlayerVibra> PlayerDatabase = new ArrayList<PlayerVibra>();
	public List<Rank> RankManager = new ArrayList<Rank>();
	
	
	public File databaseFolder = new File("plugins/VibraMC/database/");
	public File mainFolder = new File("plugins/VibraMC/");
	
	public static File configFile = new File("plugins/VibraMC/config.vmc");
	
	public static Location serverSpawn;
	public static Location lobbySpawn;
	public static Location murderSpawn;
	
	public static Main instance;
	public static PartyManager partyManager;
	public static GameManager gameManager;
	
	
	
	public static void setServerSpawn(Location loc) {
		//TEST
		serverSpawn = loc;
		SaveConfigFile();
		
	}
	
	public static void setLobbySpawn(Location loc) {
		
		lobbySpawn = loc;
		SaveConfigFile();
		
	}
	
	public static void setMurderSpawn(Location loc) {
		
		murderSpawn = loc;
		SaveConfigFile();
		
	}
	
	public static void SaveConfigFile() {
		List<String> lines = new ArrayList<String>();
		
		lines.add("spawn: " + LocationUtil.getString(serverSpawn));
		lines.add("lobby: " + LocationUtil.getString(lobbySpawn));
		lines.add("murder: " + LocationUtil.getString(murderSpawn));
		
		try {
			FileUtils.writeLines(configFile, lines);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}
	
	public static void LoadConfigFile() {
		
		try {
			List<String> lines = FileUtils.readLines(configFile);
			
			for (String line : lines) {
				
				String arg = line.split(":")[0].trim();
				String value = line.split(":")[1].trim();				
				
				switch (arg.toLowerCase()) {
				case "spawn":
					
					serverSpawn = LocationUtil.getLocation(value).add(0.5, 0, 0.5);
					
					break;
					
				case "lobby":
					
					lobbySpawn = LocationUtil.getLocation(value).add(0.5, 0, 0.5);
					
					break;
				case "murder":
					
					murderSpawn = LocationUtil.getLocation(value).add(0.5, 0, 0.5);
					
					break;
					
				default: break;
				}
			}
			
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
		
	}
	
	@Override
	public void onEnable() {
		
		instance = this;
		partyManager = new PartyManager();
		gameManager = new GameManager();
		
		if (!databaseFolder.exists()) {
			Log.info("Creation du dossier Database");
			databaseFolder.mkdirs();
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				Log.error(e.getMessage());
			}
		} else {
			if (!configFile.exists()) {
				try {
					configFile.createNewFile();
				} catch (IOException e) {
					Log.error(e.getMessage());
				}
			}
		}
		
		LoadConfigFile();
		
		if (!mainFolder.exists()) {
			Log.info("Creation du dossier main");
			mainFolder.mkdirs();
		}
		
		Log.info("Initialisation du plugin VibraMC");
		
		for (File file : mainFolder.listFiles()) {
			if (file.getName().endsWith((".rk"))) {
				Rank rk = new Rank(file);
				RankManager.add(rk);
				Log.info("Rank charge: " + rk.name);
			}
		}
		
		
		for (File file : databaseFolder.listFiles()) {
			PlayerVibra pv = new PlayerVibra(file);
			PlayerDatabase.add(pv);
			Log.info("Joueur charge: " + pv.username);
		}
		
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!IsInDatabase(player))
				PlayerDatabase.add(new PlayerVibra(player));
		}
		
		
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
		this.getServer().getPluginManager().registerEvents(new VACListener(), this);
		this.getCommand("history").setExecutor(new CommandManager());
		this.getCommand("party").setExecutor(new CommandManager());
		this.getCommand("setrank").setExecutor(new CommandManager());
		this.getCommand("setspawn").setExecutor(new CommandManager());
		this.getCommand("setlobby").setExecutor(new CommandManager());
		this.getCommand("spawn").setExecutor(new CommandManager());
		
		super.onEnable();
		
	}
	
	@Override
	public void onDisable() {
		
		for (PlayerVibra pv : PlayerDatabase) {
			pv.ResetUsername();
			System.out.println(pv.username);
			pv.SavePlayerFile();
		}
		
		super.onDisable();
	}
	
	
	
	public PlayerVibra getPlayerByName(String name) {
		for (PlayerVibra pv : PlayerDatabase) {
			if (name.equalsIgnoreCase(pv.username)) {
				return pv;
			}
		}
		
		return null;
	}
	
	public Rank getRank(int id) {
		for (Rank rk : RankManager) {
			if (rk.id == id) {
				return rk;
			}
		}
		
		return null;
	}
	
	
	
	public PlayerVibra getPlayer(Player p) {
		for (PlayerVibra pv : PlayerDatabase) {
			if (p.getUniqueId().compareTo(pv.id) == 0) {
				return pv;
			}
		}
		
		return null;
	}
	
	public boolean IsInDatabase(Player p) {
		for (PlayerVibra pv : PlayerDatabase) {
			if (p.getUniqueId().compareTo(pv.id) == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	 public Player getPlayerByUuid(UUID uuid) {
	      for(Player p : getServer().getOnlinePlayers()) {
	    	  if (p.getUniqueId().compareTo(uuid) == 0)
	              return p;
	      }
	      return null;
	 }
}
