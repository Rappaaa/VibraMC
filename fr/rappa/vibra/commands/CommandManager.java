package fr.rappa.vibra.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.party.Party;
import fr.rappa.vibra.player.PlayerVibra;
import fr.rappa.vibra.rank.Rank;
import fr.rappa.vibra.utils.SpawnUtils;
import fr.rappa.vibra.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandManager implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof ConsoleCommandSender) {
			
			ConsoleCommandSender s = (ConsoleCommandSender) sender;
			
			if (label.equals("setrank")) {
				if (args.length == 2) {
					
					PlayerVibra p_arg = Main.instance.getPlayerByName(args[0].trim());
					int id = Integer.parseInt(args[1]);
					
					if (p_arg == null) {
						s.sendMessage("§c>> Joueur introuvable (" + args[0] + ")");
						return false;
					} else {
						
						Rank rk = Main.instance.getRank(id);
						p_arg.rank = rk;
						p_arg.SavePlayerFile();
						s.sendMessage("§8>> Le rank de §6" + p_arg.username + " §8est maintenant : " + rk.prefix);
					
						Bukkit.getPlayer(p_arg.id).sendMessage("§a>> Rank mis à jour. §8Vous êtes maintenant " + rk.prefix);
					}
				} else {
					s.sendMessage("§c>> /setrank <joueur> <rankid>");
					return false;
				}
			}
			
			return false;
		}
		
		Player p = (Player) sender;
		
		PlayerVibra pv = Main.instance.getPlayer(p);
		
		if (label.equalsIgnoreCase("hub") || label.equalsIgnoreCase("spawn")) {
			p.teleport(Main.serverSpawn);
			p.setGameMode(GameMode.SURVIVAL);
			SpawnUtils.GiveSpawnObjects(p);
			Utils.DisplayHubSidebar(p);
			if (Main.gameManager.isInGame(p)) {
				Main.gameManager.GetGameByPlayer(p).OnPlayerDisconnect(p);				
			}
		}
		
		if ((label.equalsIgnoreCase("setspawn") || label.equalsIgnoreCase("sethub")) && (p.hasPermission("admin"))) {
			
			if (args.length == 0) {
				Main.setServerSpawn(p.getLocation());
				p.sendMessage("§7>> Spawn §amis à jour!");
				return false;
			} else {
				switch (args[0].toLowerCase()) {
					case "murder":
						Main.setMurderSpawn(p.getLocation());
						p.sendMessage("§7>> Spawn du §1murder §amis à jour!");
						break;
					
					default: break;
				}
				
				return false;
			}
			
			
		}
		
		if ((label.equalsIgnoreCase("setlobby")) && (p.hasPermission("admin"))) {
			
			Main.setLobbySpawn(p.getLocation());
			p.sendMessage("§7>> Lobby §amis à jour!");
			return false;
			
		}
		
		if (label.equalsIgnoreCase("party") || label.equalsIgnoreCase("p") || label.equalsIgnoreCase("g") || label.equalsIgnoreCase("group")) {
			Party party = Main.partyManager.GetPartyFromPlayer(p);
			
			if (args.length == 0 && !Main.partyManager.isInParty(p)) {
				Main.partyManager.CreateParty(p);
				p.sendMessage("§a>> Groupe créé");
				return false;
			}
			
			if (args.length >= 1) {
				
				
				switch (args[0].toLowerCase()) {
				case "create":
					Main.partyManager.CreateParty(p);
					p.sendMessage("§a>> Groupe créé");
					
					break;
				case "help":
					p.sendMessage("§7>> Liste des §2commandes");
					p.sendMessage("§7>> §2/party §7-> Permet de créer un groupe");
					p.sendMessage("§7>> §2/party create §7-> Permet de créer un groupe");
					p.sendMessage("§7>> §2/party delete §7-> Permet de supprimer un groupe");
					p.sendMessage("§7>> §2/party lead <joueur> §7-> Permet de changer de chef de groupe");
					p.sendMessage("§7>> §2/party leave §7-> Permet de quitter un groupe");
					p.sendMessage("§7>> §2/party <joueur> §7-> Permet d'inviter un joueur");
					p.sendMessage("§7>> §2/party invite <joueur> §7-> Permet d'inviter un joueur");
					p.sendMessage("§7>> §2/party kick <joueur> §7-> Permet de retirer un joueur");
					p.sendMessage("§7>> §2/party accept <joueur> §7-> Accepte l'invitation d'un joueur");
					p.sendMessage("§7>> §2/party decline <joueur> §7-> Refuse l'invitation d'un joueur");
					break;
				case "invite":
					if (args.length <= 1) {
						p.sendMessage("§c>> /party invite <joueur>");
						break;
					} else {
					
						if (party == null) {
							p.sendMessage("§c>> Vous n'êtes pas dans un groupe!");
							break;
						} else {
							Player p_arg = Bukkit.getPlayer(args[1]);
							if (p_arg == null || !p_arg.isOnline()) {
								p.sendMessage("§c>> Joueur introuvable ou hors-ligne");
							} else {
								if (p_arg.getUniqueId().compareTo(p.getUniqueId()) == 0) {
									p.sendMessage("§c>> Vous êtes déjà dans votre groupe...");
									break;
								} else if (Main.partyManager.isInParty(p_arg)) {
									p.sendMessage("§c>> Ce joueur est déjà dans un groupe.");
									break;
								} else {
									party.UpdateInvitations();
									if (party.IsInvited(p_arg)) {
										p.sendMessage("§c>> Ce joueur a déjà été invité(e) il y a moins de 2 minutes.");
										break;	
									} else {
										
										party.InvitePlayer(p_arg);
										
										p.sendMessage("§7>> Vous avez invité §d" + p_arg.getDisplayName() + " §7dans votre groupe.");
										p_arg.sendMessage("§7>> Vous avez reçu une invitation pour rejoindre le groupe de §d" + p.getDisplayName());
										TextComponent msg = new TextComponent("§a[Accepter]");
										msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + p.getDisplayName()));
										p_arg.spigot().sendMessage(msg);
										TextComponent msg2 = new TextComponent("§c[Refuser]");
										msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party decline " + p.getDisplayName()));
										p_arg.spigot().sendMessage(msg2);
									}
								}
							}
						}
						

					}
					
					break;
					
				case "accept":
					if (args.length <= 1) {
						p.sendMessage("§c>> /party accept <joueur>");
						break;
					} else {
						Player p_arg = Bukkit.getPlayer(args[1]);
						if (p_arg == null || !p_arg.isOnline()) {
							p.sendMessage("§c>> Joueur introuvable ou hors-ligne");
						} else {
							if (p_arg.getUniqueId().compareTo(p.getUniqueId()) == 0) {
								p.sendMessage("§c>> Vous êtes déjà dans votre groupe...");
								break;
							} else if (!Main.partyManager.isInParty(p_arg)) {
								p.sendMessage("§c>> Ce joueur n'est pas dans un groupe.");
								break;
							} else { 
								
								Party pt = Main.partyManager.GetPartyFromPlayer(p_arg);
								
								if (pt == null) {
									p.sendMessage("§c>> Ce joueur n'est pas dans un groupe.");
									break;
								} else {
									
									if (pt.IsInvited(p)) {
										
										pt.AddPlayer(p);
										p.sendMessage("§7>> Vous avez rejoint le groupe de §d" + pt.getLeader().getDisplayName());
										for (Player ply : pt.getPlayers()) {
											ply.sendMessage("§7>> §d" + p.getDisplayName() + " §7a rejoint le groupe.");
										}
										
									} else {
										p.sendMessage("§c>> Vous n'avez pas reçu d'invitation.");
										break;
									}
									
								}
								
							}
						}
					}
					
					break;
					
				case "decline":
					if (args.length <= 1) {
						p.sendMessage("§c>> /party decline <joueur>");
						break;
					} else {
						Player p_arg = Bukkit.getPlayer(args[1]);
						if (p_arg == null || !p_arg.isOnline()) {
							p.sendMessage("§c>> Joueur introuvable ou hors-ligne");
						} else {
							Party pt = Main.partyManager.GetPartyFromPlayer(p_arg);
							
							if (pt == null) {
								p.sendMessage("§c>> Ce joueur n'est pas dans un groupe.");
								break;
							} else {
								
								if (pt.IsInvited(p)) {
									
									pt.RemoveInvitation(p);
									p.sendMessage("§7>> Vous avez décliné l'invitation de §d" + pt.getLeader().getDisplayName());
									for (Player ply : pt.getPlayers()) {
										ply.sendMessage("§7>> §d" + p.getDisplayName() + " §7a déclinée l'invitation au groupe.");
									}
									
								} else {
									p.sendMessage("§c>> Vous n'avez pas reçu d'invitation.");
									break;
								}
							}
						}
					}
					
					break;				
				case "lead":
					if (args.length <= 1) {
						p.sendMessage("§c>> /party lead <joueur>");
						break;
					} else {
						
						if (party == null) {
							p.sendMessage("§c>> Vous n'êtes pas dans un groupe!");
							break;
						} else {
							if (party.IsLeader(p)) {
								Player p_arg = Bukkit.getPlayer(args[1]);
								if (p_arg == null || !party.hasPlayer(p_arg)) {
									p.sendMessage("§c>> Ce joueur n'est pas dans votre groupe");
								} else {
									if (p_arg.getUniqueId().compareTo(p.getUniqueId()) == 0) {
										p.sendMessage("§c>> Vous êtes deja le chef du groupe!");
										break;
									}
									
									
									party.SetLeader(p_arg);
									
									for (Player ply : party.getPlayers()) {
										ply.sendMessage("§7>> §6" + p_arg.getDisplayName() + " §7a été promu chef du groupe.");
									}
								}
							} else {
								p.sendMessage("§c>> Vous n'êtes pas chef du groupe!");
								break;
							}
						}
						
					}
					
					break;
				case "leave":
					if (args.length != 1) {
						p.sendMessage("§c>> /party leave");
						break;
					} else {
						if (party == null) {
							p.sendMessage("§c>> Vous n'êtes pas dans un groupe!");
							break;
						} else {
							if (party.IsLeader(p)) {
								p.sendMessage("§c>> Vous devez choisir un nouveau chef de groupe avant de §cquitter votre partie");
								break;
							} else {
								
								p.sendMessage("§7>> Vous avez quitté le groupe de §d" + party.getLeader().getDisplayName());
								party.RemovePlayer(p);
								
								for (Player ply : party.getPlayers()) {
									ply.sendMessage("§7>> §c" + p.getDisplayName() + " §7a quitté le groupe.");
								}
								
							}
						}
					}
					
					break;
				case "list":
					if (party == null) {
						p.sendMessage("§c>> Vous n'êtes pas dans un groupe!");
						break;
					} else {
						p.sendMessage("§7Liste des membres de votre §agroupe");
						for (Player ply : party.getPlayers()) {
							p.sendMessage("§a> " + ply.getDisplayName());
						}
						break;
					}
				case "kick":
					if (args.length <= 1) {
						p.sendMessage("§c>> /party kick <joueur>");
						break;
					}
						
					if (party == null) {
						p.sendMessage("§c>> Vous n'êtes pas dans un groupe!");
						break;
					} else {
						if (party.IsLeader(p)) {
							
							Player p_arg = Bukkit.getPlayer(args[1]);
							if (p_arg == null || !party.hasPlayer(p_arg)) {
								p.sendMessage("§c>> Ce joueur n'est pas dans votre groupe");
							} else {
								if (p_arg.getUniqueId().compareTo(p.getUniqueId()) == 0) {
									p.sendMessage("§c>> Faîtes /party delete si vous voulez supprimer votre groupe.");
									break;
								}
								
								party.RemovePlayer(p_arg);
								p.sendMessage("§7>> Vous avez exclu(e) §c" + p_arg.getDisplayName() + " §7de votre groupe.");
								p_arg.sendMessage("§7>> Vous avez été §cexclu(e) §7du groupe.");
							}
							
							break;
						} else {
							p.sendMessage("§c>> Vous n'êtes pas chef du groupe!");
							break;
						}
					}	
				case "delete":
					if (party == null) {
						p.sendMessage("§c>> Vous n'êtes pas dans un groupe!");
						break;
					} else {
						if (party.IsLeader(p)) {
							for (Player ply : party.getPlayers()) {
								if (!party.IsLeader(ply)) {
									ply.sendMessage("§c>> Votre groupe a été supprimé");
								}
							}
							
							Main.partyManager.RemoveParty(party);
							
							if (!Main.partyManager.isInParty(p)) {
								p.sendMessage("§c>> Groupe supprimé");
								break;
							}
							
						} else {
							p.sendMessage("§c>> Vous n'êtes pas chef du groupe!");
							break;
						}
					}
					break;	
				default:

					if (party == null) {
						Main.partyManager.CreateParty(p);
						p.sendMessage("§a>> Groupe créé");
					}

					party = Main.partyManager.GetPartyFromPlayer(p);
					
					Player p_arg = Bukkit.getPlayer(args[0]);
					if (p_arg == null || !p_arg.isOnline()) {
						p.sendMessage("§c>> Joueur introuvable ou hors-ligne");
					} else {
						if (p_arg.getUniqueId().compareTo(p.getUniqueId()) == 0) {
							p.sendMessage("§c>> Vous êtes déjà dans votre groupe...");
							break;
						} else if (Main.partyManager.isInParty(p_arg)) {
							p.sendMessage("§c>> Ce joueur est déjà dans un groupe.");
							break;
						} else {
							party.UpdateInvitations();
							if (party.IsInvited(p_arg)) {
								p.sendMessage("§c>> Ce joueur a déjà été invité(e) il y a moins de 2 minutes.");
								break;	
							} else {
								
								party.InvitePlayer(p_arg);
								
								p.sendMessage("§7>> Vous avez invité §d" + p_arg.getDisplayName() + " §7dans votre groupe.");
								p_arg.sendMessage("§7>> Vous avez reçu une invitation pour rejoindre le groupe de §d" + p.getDisplayName());
								TextComponent msg = new TextComponent("§a[Accepter]");
								msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + p.getDisplayName()));
								p_arg.spigot().sendMessage(msg);
								TextComponent msg2 = new TextComponent("§c[Refuser]");
								msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party decline " + p.getDisplayName()));
								p_arg.spigot().sendMessage(msg2);
							}
						}
					}				
					break;
				}
				
				return false;
			}
		}
		
		
		if (label.equals("setrank") && (p.isOp() || pv.hasPermission("admin"))) {
			if (args.length == 2) {
				
				PlayerVibra p_arg = Main.instance.getPlayerByName(args[0].trim());
				int id = Integer.parseInt(args[1]);
				
				if (p_arg == null) {
					p.sendMessage("§c>> Joueur introuvable (" + args[0] + ")");
					return false;
				} else {
					
					Rank rk = Main.instance.getRank(id);
					p_arg.rank = rk;
					p_arg.SavePlayerFile();
					p.sendMessage("§8>> Le rank de §6" + p_arg.username + " §8est maintenant : " + rk.prefix);
				
					Bukkit.getPlayer(p_arg.id).sendMessage("§a>> Rank mis à jour. §8Vous êtes maintenant " + rk.prefix);
				}
			} else {
				p.sendMessage("§c>> /setrank <joueur> <rankid>");
				return false;
			}
		}
		
		if ((label.equals("history") || label.equals("h") || label.equals("hst")) && pv.hasPermission("moderation")) {
			if (args.length == 1) {
				
				PlayerVibra p_arg = Main.instance.getPlayerByName(args[0].trim());
				
				if (p_arg == null) {
					p.sendMessage("§c>> Joueur introuvable (" + args[0] + ")");
					return false;
				} else {
					p.sendMessage("§8>> §6Info sur " + p_arg.username);
					p.sendMessage("§8RankId: §6" + p_arg.rank.id);
					p.sendMessage("§8RankName: §6" + p_arg.rank.name);
					p.sendMessage("§8Permissions:");
					
					for (String s : p_arg.rank.permissions) {
						p.sendMessage("§6> " + s);
					}
					
					return false;
				}
			} else {
				p.sendMessage("§c>> /history <joueur>");
			}
			
		}
		
		return false;
	}

}
