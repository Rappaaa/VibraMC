package fr.rappa.vibra.game.murder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.game.Game;
import fr.rappa.vibra.player.PlayerVibra;
import fr.rappa.vibra.utils.SpawnUtils;
import fr.rappa.vibra.utils.Utils;

@SuppressWarnings("deprecation")
public class Murder extends Game {

	public List<Player> innocents = new ArrayList<Player>(); 
	public List<Player> imposteurs = new ArrayList<Player>(); 

	public HashMap<Player, Integer> chestTaken = new HashMap<>(maxPlayers); 
	public HashMap<Player, Integer> playerPoints = new HashMap<>(maxPlayers); 
	
	public List<Location> lootedChests = new ArrayList<Location>();
	public ItemStack[] randomItems = new ItemStack[] {
		new ItemStack(Material.WOOD_SWORD),
		new ItemStack(Material.STONE_SWORD),
		new ItemStack(Material.BOW),
		new ItemStack(Material.ARROW, 32)
	};
	
	public boolean pvp;
	public boolean test;

	public boolean end = false;
	
	public void UpdateSidebar(Player p) {
		ScoreboardManager sb = Bukkit.getScoreboardManager();
		Scoreboard s = sb.getNewScoreboard();				
		Objective obj = s.registerNewObjective("murder", "ok");
		
		Team t = s.registerNewTeam("players");
		t.setNameTagVisibility(NameTagVisibility.NEVER);
		
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName("§f§o- §cImposteurs §f§o-");
		Score profil = obj.getScore("§b§lRôle");
		
		Score role;
		if (isInnocent(p)) role = obj.getScore("§f§o- §aInnocent");
		else if (isImposteur(p)) role = obj.getScore("§f§o- §cImposteur");
		else role = obj.getScore("§f§o- §7§oSpectateur");
		
		Score joueurs = obj.getScore("§e§lJoueurs restants");
		Score nombre = obj.getScore("§f§o- §f" + (imposteurs.size() + innocents.size()));
		
		Score end = obj.getScore("§avibramc.fr");
		
		profil.setScore(7);
		role.setScore(6);
		obj.getScore("  ").setScore(4);
		joueurs.setScore(3);
		nombre.setScore(2);
		obj.getScore(" ").setScore(1);
		end.setScore(0);
		
		p.setScoreboard(s);
	}
	
	public Murder() {
		super("murder-" + Math.round(1000+Math.random() * 9000));
		
		
		
		playersToStart = 2;
		maxPlayers = 24;
		
		pvp = false;
		test = false;
	}
	
	
	
	@Override
	public void StartGame() {
		
		pvp = false;
		test = false;
		end = false;
		
		for (Player ply : playersInGame) {
			chestTaken.put(ply, 0);
			playerPoints.put(ply, 0);
			ply.teleport(Main.murderSpawn);
			ply.setBedSpawnLocation(Main.murderSpawn);
			ply.getInventory().clear();
			ply.getInventory().setArmorContents(new ItemStack[4]);
		}
		
		broadcast("§7La partie vient de §acommencer");
		broadcast("§7>> Le pvp sera actif dans 45 secondes");
		broadcast("§b>> Récupérez un maximum d'objets!");
		broadcast("§c<!> §7Faîtes clic gauche si §7vous n'arrivez pas à ouvrir un coffre §c<!>");
		hasStarted = true;
		
		int numberOfPlayers = playersInGame.size();
		int numberOfImposteur = Math.floorDiv(numberOfPlayers, 4); 

		if (numberOfImposteur == 0) numberOfImposteur++;
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (Player pig : playersInGame) {
				
				
				if (!playersInGame.contains(player)) {
					player.hidePlayer(pig);
					pig.hidePlayer(player);
				}				
			}
		}
		
		
		Random rand = new Random();
		
		while (imposteurs.size() < numberOfImposteur) {
			
			Player imp = playersInGame.get(rand.nextInt(numberOfPlayers));
		
			while (imposteurs.contains(imp)) {
				imp = playersInGame.get(rand.nextInt(numberOfPlayers));
			}
			
			imposteurs.add(imp);
		}
		
		innocents.addAll(playersInGame);
		innocents.removeAll(imposteurs);
		
		for (Player ply : innocents) {
			Utils.sendTitle(ply, "§aINNOCENT", "§7>> Survivez le plus longtemps possible !");
			ply.sendMessage("§7>> Vous êtes §ainnocent.");
			Utils.playSound(ply, Sound.CAT_MEOW);
			UpdateSidebar(ply);
		}
		
		for (Player ply : imposteurs) {
			Utils.sendTitle(ply, "§cIMPOSTEUR", "§7>> Tuez les innocents sans vous faire repérer !");
			ply.sendMessage("§7>> Vous êtes §cimposteur.");
			Utils.playSound(ply, Sound.ENDERDRAGON_GROWL);
			UpdateSidebar(ply);
		}
		

		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				pvp = true;
				
				broadcast("§7>> §oVous avez reçu une épée §7§oen bois au cas où vous n'en aviez pas.");
				
				for (Player ply : playersInGame) {
					ply.getInventory().addItem(randomItems[0]);
					
					Utils.sendTitle(ply, "§bLe PvP est actif", "§7Bonne chance à tous");
				}
				broadcast("§a>> PvP actif!");

			}
			
		}.runTaskLater(Main.instance, 45*20L);
		
	}
	
	@Override
	public void OnPlayerDeath(PlayerDeathEvent e) {
		e.setDeathMessage("");
		e.setKeepLevel(true);
		e.setKeepInventory(true);
		
		
		Player p = e.getEntity();
		Player killer = p.getKiller();
		p.spigot().respawn();
		p.getInventory().clear();
		
		int pts = 0;
		
		if (isImposteur(p)) {
			if (killer != null) {
				if (isImposteur(killer)) {
					pts = -5;
					Utils.playSound(killer, Sound.WITHER_DEATH);
					killer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 10));
					killer.sendMessage("§7>> Ne §ctuez surtout pas §7vos coéquipiers!");
				} else {
					pts = 5;
				}
			}
			
			
			imposteurs.remove(p);
			//broadcast("§7>> Un §cimposteur §7est mort.");

		} else if (isInnocent(p)) {
			if (killer != null) {
				if (isInnocent(killer)) {
					pts = -5;
					Utils.playSound(killer, Sound.WITHER_DEATH);
					killer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 10));
					killer.sendMessage("§7>> Ne §ctuez surtout pas §7vos coéquipiers!");
				} else {
					pts = 1;
				}
			}
			innocents.remove(p);
			
		}
		
		broadcast("§7>> Un §7joueur §7est mort.");
		
		p.teleport(Main.murderSpawn);
		p.setGameMode(GameMode.SPECTATOR);
		p.setFlying(true);
		
		if (killer != null) {
			PlayerVibra killerv = Main.instance.getPlayer(killer);
			if (pts >= 0) {
				killer.sendMessage("§6>> "+ pts +" §6points");
			} else {
				killer.sendMessage("§4§l>> "+ pts +" §4§lpoints");
			}
			
			killerv.givePoints(pts);
		}
		
		for (Player ply : playersInGame) {
			UpdateSidebar(ply);
		}
		
		CheckWin();
		super.OnPlayerDeath(e);
	}
	
	public void CheckWin() {
		if (innocents.size() <= 0) {
			end = true;
			for (Player ply : playersInGame) {
				PlayerVibra pv = Main.instance.getPlayer(ply);
				pv.givePoints(10);
				Utils.playSound(ply, Sound.ANVIL_LAND);
				ply.sendMessage("§6>> +10 points");
				Utils.sendTitle(ply, "§7VICTOIRE DES §CIMPOSTEURS", "§7Bien joué à eux!");
			}
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					EndGame();
				}
				
			}.runTaskLater(Main.instance, 5*20L);
			
			return;
		}
		
		if (imposteurs.size() <= 0) {
			end = true;
			for (Player ply : playersInGame) {
				PlayerVibra pv = Main.instance.getPlayer(ply);
				pv.givePoints(10);
				Utils.playSound(ply, Sound.ANVIL_LAND);
				ply.sendMessage("§6>> +10 points");
				Utils.sendTitle(ply, "§7VICTOIRE DES §AINNOCENTS", "§7Bien joué à eux!");
			}
			
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					EndGame();
				}
				
			}.runTaskLater(Main.instance, 10*20L);
			
			
			return;
		}
		
		
	}
	
	public boolean isInnocent(Player p) {
		return innocents.contains(p);
	}
	
	public boolean isImposteur(Player p) {
		return imposteurs.contains(p);
	}
	
	public void RemoveImposteur(Player p) {
		imposteurs.remove(p);
		CheckWin();
	}
	
	public void RemoveInnocent(Player p) {
		innocents.remove(p);
		CheckWin();
	}
	
	@Override
	public void OnPlayerDisconnect(Player p) {
		super.OnPlayerDisconnect(p);
		
		if (hasStarted) {
			if (isImposteur(p)) {
				RemoveImposteur(p);
				
				broadcast("§7>> §c" + p.getDisplayName() + " §7a quitté la partie.");
			} else if (isInnocent(p)) {
				RemoveInnocent(p);
				
				broadcast("§7>> §a" + p.getDisplayName() + " §7a quitté la partie.");
			}
			
			
			playersInGame.remove(p);
		}
	}
	
	@Override
	public void EndGame() {
		
		hasStarted = false;
		begin = false;
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (Player pig : playersInGame) {
				if (!Main.gameManager.isInGame(player)) player.showPlayer(pig);
				pig.showPlayer(player);				
			}
		}
		
		for (Player ply : playersInGame) {
			ply.teleport(Main.serverSpawn);
			ply.setGameMode(GameMode.SURVIVAL);
			SpawnUtils.GiveSpawnObjects(ply);
			Utils.DisplayHubSidebar(ply);
			ply.sendMessage("§7>> Retour au spawn");
			ply.setHealth(20);
		}
		
		playersInGame.clear();
		lootedChests.clear();
		imposteurs.clear();
		innocents.clear();
		playerPoints.clear();
		chestTaken.clear();
		pvp = false;
		test = false;
		Main.gameManager.RemoveGame(this);
	}
	
	@Override
	public void OnPlayerDamage(EntityDamageEvent e) {
		if (!pvp || end) {
			e.setCancelled(true);
		}
		super.OnPlayerDamage(e);
	}
	
	@Override
	public void OnMoveEvent(PlayerMoveEvent e) {
		if (!hasStarted) return;
		
		Player p = e.getPlayer();
		
		if (p.getLocation().getY() < 50) {
			if (!end && (isInnocent(p) || isImposteur(p))) {
				p.setHealth(0);
				p.spigot().respawn();
				p.teleport(Main.murderSpawn);
				p.setGameMode(GameMode.SPECTATOR);
				p.setFlying(true);
			} else {
				p.teleport(Main.murderSpawn);
			}
		}
	}
	
	@Override
	public void OnDropEvent(PlayerDropItemEvent e) {
		e.setCancelled(true);
		super.OnDropEvent(e);
	}
	
	@Override
	public void OnChat(PlayerChatEvent e) {
		
		if (hasStarted) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			
			if (isImposteur(p) && e.getMessage().startsWith("!")) {
				for (Player ply : imposteurs) {
					ply.sendMessage("§7<§4Imposteur§7> §0> §c§o" + e.getMessage());
				}
				
				return;
			}
			
			for (Player ply : playersInGame) {
				if (!isImposteur(p) && !isInnocent(p))  {
					if (!isImposteur(ply) && !isInnocent(ply)) {
						ply.sendMessage("§f§o* §8" + p.getDisplayName() + " §7>> §l§7" + e.getMessage());
					}
				} else {
					ply.sendMessage("§f§o* §a" + p.getDisplayName() + " §7>> §l§7" + e.getMessage());
				}
			}
		} else {
			super.OnChat(e);
		}
		

	}
	
	@Override
	public void OnInteractEvent(PlayerInteractEvent e) {
		
		if (e.getClickedBlock() != null)
		{

			e.setCancelled(true);
			
			if ((e.getClickedBlock().getType() == Material.CHEST) && !pvp) {
				e.setCancelled(true);
				Player p = e.getPlayer();
				
				if (chestTaken.get(p).intValue() > 3) {
					p.sendMessage("§c>> Vous ne pouvez pas prendre plus de 4 coffres!");
					return;
				}
				
				
				if (lootedChests.contains(e.getClickedBlock().getLocation())) {
					p.sendMessage("§c>> Coffre déjà ouvert");
					
					return;
				}

				Random r = new Random();
				int rn = r.nextInt(randomItems.length);
				while (p.getInventory().contains(randomItems[rn])) {
					if (p.getInventory().contains(randomItems[rn], 32)) { // ARROW
						break;
					}
					rn = r.nextInt(randomItems.length);
				}
				p.getInventory().addItem(randomItems[rn]);
				lootedChests.add(e.getClickedBlock().getLocation());
				p.sendMessage("§a>> Coffre ouvert");
				
				
				if (chestTaken.containsKey(p)) {
					chestTaken.replace(p, chestTaken.get(p).intValue()+1);
				}
			}
			
			if (e.getClickedBlock().getType() == Material.STONE_BUTTON) {
				e.setCancelled(true);
				if (test) return;
				
				
				test = true;
				
				Location b1 = new Location(e.getPlayer().getWorld(), -44, 86, 14);
				Location b2 = new Location(e.getPlayer().getWorld(), -44, 86, 13);
				Location b3 = new Location(e.getPlayer().getWorld(), -44, 87, 14);
				Location b4 = new Location(e.getPlayer().getWorld(), -44, 87, 13);
				
				broadcast("§7>> Un test est en cours...");
				
				BukkitRunnable task = new BukkitRunnable() {

					@Override
					public void run() {
						for (Player ply : playersInGame) {
							
							
							ply.sendBlockChange(b1, Material.STAINED_GLASS, (byte) 14);
							ply.sendBlockChange(b2, Material.STAINED_GLASS, (byte) 14);
							ply.sendBlockChange(b3, Material.STAINED_GLASS, (byte) 14);
							ply.sendBlockChange(b4, Material.STAINED_GLASS, (byte) 14);
						}
					}
					
				};
				
				task.runTaskTimer(Main.instance, 0, 1);
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						boolean imposteur = false;
						
						task.cancel();
						for (Player ply : playersInGame) {
							
							if (ply.getWorld().getBlockAt(ply.getLocation().subtract(0, 2, 0)).getType() == Material.SPONGE)
							{
								if (isImposteur(ply)) {
									imposteur = true;
								}
							}
							
							
							ply.sendBlockChange(b1, Material.AIR, (byte) 0);
							ply.sendBlockChange(b2, Material.AIR, (byte) 0);
							ply.sendBlockChange(b3, Material.AIR, (byte) 0);
							ply.sendBlockChange(b4, Material.AIR, (byte) 0);
						}
						
						
						if (imposteur) {
							broadcast("§7>> Un §cimposteur §7a été détecté au test!");
						}
						
						test = false;
					}
				}.runTaskLater(Main.instance, 3*20L);
				
			}
			
			
		}
	}
}
