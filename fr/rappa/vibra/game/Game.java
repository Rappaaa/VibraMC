package fr.rappa.vibra.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.party.Party;
import fr.rappa.vibra.utils.SpawnUtils;
import fr.rappa.vibra.utils.Utils;
@SuppressWarnings("deprecation")
public class Game {

	private String name;
	
	public List<Player> playersInGame = new ArrayList<Player>();
	public int maxPlayers;
	public int playersToStart;
	
	public boolean hasStarted = false;
	public boolean begin = false;
	public boolean allowSpectator = false;
	public boolean allowDestroy = false;
	
	private BukkitRunnable runnable;
	
	public Game(String name) {
		this.name = name;
	}
	
	public void StartGame() {}
	
	public void EndGame() {}
	
	public void OnPlayerJoin(Player p) {
		if (Main.gameManager.isInGame(p)) {
			p.sendMessage("§c>> Vous êtes déjà dans une partie!");
			return;
		}
		
		if (!hasStarted) {
			if (Main.partyManager.isInParty(p)) {
				Party pty = Main.partyManager.GetPartyFromPlayer(p);
				
				if (pty.IsLeader(p)) {
					if (playersInGame.size() < 1 + maxPlayers - pty.getPlayers().size()) {
						
						for (Player ply : pty.getPlayers()) {
							
							if(!ply.isOnline()) continue;
							
							if(Main.gameManager.isInGame(ply)) {
								
								Main.gameManager.GetGameByPlayer(ply).OnPlayerDisconnect(ply);
								ply.sendMessage("§c>> Vous avez quitté votre ancienne partie");
							}
							
							broadcast("§7>> §d" + ply.getDisplayName() + " §7a rejoint.");
							
							playersInGame.add(ply);
							ply.sendMessage("§7>> Votre §dgroupe §7a rejoint la partie §a[" + name + "§a]");							
							
							SpawnUtils.GiveLobbyObjects(ply);
							
							ply.teleport(Main.lobbySpawn);
						}
						
						CheckStart();
						return;
					} else {

						p.sendMessage("§c>> Vous ne pourrez pas rejoindre la §cpartie avec tout votre groupe.");
						return;
					}
				}
				
			}
			
			if (playersInGame.size() < maxPlayers) {
				
				broadcast("§7>> §d" + p.getDisplayName() + " §7a rejoint.");
				
				
				playersInGame.add(p);
				p.sendMessage("§7>> Vous avez rejoint la partie §a[" + name + "§a]");
				p.teleport(Main.lobbySpawn);
				SpawnUtils.GiveLobbyObjects(p);
				
				
				CheckStart();
				return;
			}
		} else {
			if (allowSpectator) OnJoinSpectator(p);
		}
	}
	
	private void CheckStart() {
		if (playersInGame.size() >= playersToStart) {
			if (!begin) {
				begin = true;
				runnable = new BukkitRunnable() {
					
					int s = 15;
					
					@Override
					public void run() {
						if (s <= 0) {
							StartGame();
							runnable.cancel();
							return;
						}
						
						if (s == 15) {
							broadcast("§7>> La partie commence dans §e" + s + " §eseconde(s)");							
							for (Player p : playersInGame) {
								Utils.playSound(p, Sound.SUCCESSFUL_HIT);
							}
						}
						
						if (s <= 5) {
							broadcast("§7>> La partie commence dans §e" + s + " §eseconde(s)");
							for (Player p : playersInGame) {
								Utils.playSound(p, Sound.SUCCESSFUL_HIT);
							}
						}
						
						
						
						s--;					
					}
				};
				
				runnable.runTaskTimer(Main.instance, 0, 1*20);
			}			
		} else {
			if (begin) {
				for (Player ply : playersInGame) {
					ply.sendMessage("§c>> Pas assez de joueurs pour commencer");
				}
				
				runnable.cancel();
				begin = false;
			}
		}
	}
	
	public void broadcast(String msg) {
		for (Player ply : playersInGame) {
			ply.sendMessage(msg);
		}
	}
	
	public boolean CanJoin(Player p) {
		
		if (Main.partyManager.isInParty(p)) {
			Party pty = Main.partyManager.GetPartyFromPlayer(p);
			
			if (pty.IsLeader(p)) {
				if (playersInGame.size() < 1 + maxPlayers - pty.getPlayers().size())
					return true;
				else 
					return false;
			}
		}
		
		if (playersInGame.size() < maxPlayers) {
			return true;
		}
		
		return false;
	}
	
	public void OnPlayerLeave(Player p) {		
		playersInGame.remove(p);
		
		for (Player ply : playersInGame) {
			ply.sendMessage("§7>> §c" + p.getDisplayName() + " §7a quitté la partie.");
		}
		
		p.sendMessage("§c>> Vous avez quitté la file d'attente pour §c[" + name + "§c]");
		
		CheckStart();
	}
	
	public void OnPlayerReconnect(Player p) {}
	
	public void OnPlayerDisconnect(Player p) {
		if (!hasStarted) OnPlayerLeave(p);
	}
	
	public void OnPlayerDeath(PlayerDeathEvent e) {}

	public void OnJoinSpectator(Player p) {}

	public void OnPlayerDamage(EntityDamageEvent e) {}

	public void OnInteractEvent(PlayerInteractEvent e) {}
	
	public void OnMoveEvent(PlayerMoveEvent e) {}
	
	public void OnDropEvent(PlayerDropItemEvent e) {}
	
	public void OnChat(PlayerChatEvent e) {
		e.setCancelled(true);
		
		broadcast("§f§l* §b" + e.getPlayer().getDisplayName() + " §7>> " + e.getMessage());
	}
}
