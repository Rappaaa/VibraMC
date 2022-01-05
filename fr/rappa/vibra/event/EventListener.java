package fr.rappa.vibra.event;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.game.Game;
import fr.rappa.vibra.party.Party;
import fr.rappa.vibra.player.PlayerVibra;
import fr.rappa.vibra.utils.InvUtils;
import fr.rappa.vibra.utils.SpawnUtils;
import fr.rappa.vibra.utils.Utils;

@SuppressWarnings("deprecation")
public class EventListener implements Listener {

	@EventHandler
	public void OnChat(PlayerChatEvent e) {
		
		Player p = e.getPlayer();
		PlayerVibra pv = Main.instance.getPlayer(e.getPlayer());
		e.setCancelled(true);
		
		Party pt = Main.partyManager.GetPartyFromPlayer(e.getPlayer());
		
		if (e.getMessage().startsWith("@") && pt != null)
		{
			for (Player ply : pt.getPlayers()) {
				ply.sendMessage("§f§l* §8<§dGroupe§8> §d" + pv.username + " §e-> " + e.getMessage().replaceFirst("@", ""));
			}
		} else {
			
			if (Main.gameManager.isInGame(p)) {
				Main.gameManager.GetGameByPlayer(p).OnChat(e);
			} else {
				for (Player ply : Bukkit.getOnlinePlayers()) {
					if (!Main.gameManager.isInGame(ply)) {
						ply.sendMessage("§f§l* §b§l[" + pv.points + "§b§l] " + pv.rank.prefix + " " + pv.username + " §7>> " + e.getMessage());
					}
				}
			}			
		}
	}
	
	@EventHandler
	public void OnWeatherChange(WeatherChangeEvent e) {
		if (e.toWeatherState()) e.setCancelled(true); // Pour annuler la pluie et l'orage
	}
	
	@EventHandler
	public void OnQuitEvent(PlayerQuitEvent e) {
		Player p = e.getPlayer();
	
		e.setQuitMessage("");
		
		if (Main.gameManager.isInGame(p)) {
			Main.gameManager.GetGameByPlayer(p).OnPlayerDisconnect(p);
		}
	}
	
	@EventHandler
	public void OnJoinEvent(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();	
		p.teleport(Main.serverSpawn);
		
		e.setJoinMessage("");
		
		if (!Main.instance.IsInDatabase(p)) {
			PlayerVibra pv = new PlayerVibra(p);
			Main.instance.PlayerDatabase.add(pv);
			e.setJoinMessage("§3>> §7Bienvenue a §3" + p.getDisplayName());
			pv.SavePlayerFile();
		}
		
		if (Main.gameManager.isInGame(p)) {
			Main.gameManager.GetGameByPlayer(p).OnPlayerReconnect(p);
		}
			
		SpawnUtils.GiveSpawnObjects(e.getPlayer());
		e.getPlayer().teleport(Main.serverSpawn);
		
		
		Utils.DisplayHubSidebar(p);
		
	}
	
	@EventHandler
	public void OnDropEvent(PlayerDropItemEvent e) {
		if (e.getPlayer().getWorld() == Main.serverSpawn.getWorld() && !e.getPlayer().isOp()) e.setCancelled(true);
	
		if (Main.gameManager.isInGame(e.getPlayer())) {
			Main.gameManager.GetGameByPlayer(e.getPlayer()).OnDropEvent(e);
		}
	}
	
	@EventHandler
	public void OnBlockPlace(BlockPlaceEvent e) {
		if (e.getPlayer().getWorld() == Main.serverSpawn.getWorld() && !e.getPlayer().isOp()) e.setCancelled(true);
	
		Player p = e.getPlayer();
		
		if (Main.gameManager.isInGame(p)) {
			
			Game g = Main.gameManager.GetGameByPlayer(p);
			
			if (!g.allowDestroy) e.setCancelled(true);
			
		}
	}
	
	@EventHandler
	public void OnInteractEvent(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if (Main.gameManager.isInGame(p)) {
			
			Game g = Main.gameManager.GetGameByPlayer(p);
			
			g.OnInteractEvent(e);
			
		}
		
		
		if (e == null || e.getPlayer() == null || e.getPlayer().getItemInHand() == null || !e.getPlayer().getItemInHand().hasItemMeta() || e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == null) return;
		
		if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§7>> Menu §ejeux")) {
			e.setCancelled(true);
			
			e.getPlayer().openInventory(InvUtils.GameMenuInventory());
		}
		
		if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§c>> Retour au lobby")) {
			e.setCancelled(true);
		
			p.teleport(Main.serverSpawn);
			p.setGameMode(GameMode.SURVIVAL);
			SpawnUtils.GiveSpawnObjects(p);
			Utils.DisplayHubSidebar(p);
			if (Main.gameManager.isInGame(p)) {
				Main.gameManager.GetGameByPlayer(p).OnPlayerDisconnect(p);				
			}	
		
		}
		
	}
	
	@EventHandler
	public void OnBlockBreak(BlockBreakEvent e) {
		if (e.getPlayer().getWorld() == Main.serverSpawn.getWorld() && !e.getPlayer().isOp()) e.setCancelled(true);
	
		Player p = e.getPlayer();
		
		if (Main.gameManager.isInGame(p)) {
			
			Game g = Main.gameManager.GetGameByPlayer(p);
			
			if (!g.allowDestroy) e.setCancelled(true);
			
		}
	}
	
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		if (Main.gameManager.isInGame(p)) {
			
			Game g = Main.gameManager.GetGameByPlayer(p);
			
			g.OnMoveEvent(e);
			
		}
		
		if (p.getLocation().getY() < 40 && p.getWorld() == Main.serverSpawn.getWorld()) {
			if (Main.gameManager.isInGame(p)) {
				
				p.teleport(Main.lobbySpawn);
			} else {
				p.teleport(Main.serverSpawn);
			}
			
		}
	}
	
	@EventHandler
	public void OnFoodChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
		
		if (e.getFoodLevel() < 20) e.setFoodLevel(20);
	}
	
	@EventHandler
	public void OnDeathEvent(PlayerDeathEvent e) {
		Player p = e.getEntity();
		
		if (Main.gameManager.isInGame(p)) {
			
			Game g = Main.gameManager.GetGameByPlayer(p);
			
			g.OnPlayerDeath(e);
			
			System.out.println("OK DEATHEVENT GAME");
		} else {
			System.out.println("OK DEATHEVENT PAS GAME");
		}
	}
	
	@EventHandler
 	public void OnAttackEvent(EntityDamageEvent e) {
		if (e.getEntity().getWorld() == Main.serverSpawn.getWorld()) e.setCancelled(true);
	
		if (e.getEntityType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity();
			
			if (Main.gameManager.isInGame(p)) {
				
				Game g = Main.gameManager.GetGameByPlayer(p);
				
				g.OnPlayerDamage(e);
				
			}
		}
	}
	
}
