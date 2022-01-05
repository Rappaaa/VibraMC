package fr.rappa.vibra.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.rank.Rank;

public class PlayerVibra {

	public String username;
	public UUID id;
	public Rank rank;
	public int points;
	public Player player;
	
	public PlayerVibra(File file) {
		id = UUID.fromString(file.getName().split("\\.")[0].trim());
		LoadPlayer(file);
		
		if (rank == null) {
			rank = Main.instance.getRank(0);
		}
		

		player = Bukkit.getPlayer(id);
		if (player == null) player = Main.instance.getPlayerByUuid(id);
	}
	
	public void ResetUsername() {
		Player p = Bukkit.getPlayer(id);
		if (p == null) p = Main.instance.getPlayerByUuid(id);
		
		if (p == null) return;
		
		username = p.getDisplayName();
	}
	
	public void OnQuitGame() {
		
	}
	
	public PlayerVibra(Player player) {
		id = player.getUniqueId();
		username = player.getDisplayName();
		rank = Main.instance.getRank(0);
		points = 0;
		this.player = player;
	}
	
	public boolean hasPermission(String perm) {
		return rank.permissions.contains(perm);
	}
	
	public void LoadPlayer(File f) {
		
		List<String> args;
		try {
			
			args = FileUtils.readLines(f);
			
			for (String l : args) {
				
				String cmd = l.split(":")[0].toLowerCase().trim();
				String vl = l.split(":")[1].trim();
				
				switch (cmd) {
				case "name":
					
					username = vl;
					
					break;
				case "rankid":
					
					rank = Main.instance.getRank(Integer.parseInt(vl));
					
					break;
				case "points":
					
					points = Integer.parseInt(vl);
					
					break;				
				default:break;
				}
			}
		} catch (IOException e) {
			return;
		}
	}
	
	public void SavePlayerFile() {
		File databaseFolder = Main.instance.databaseFolder;
		File playerFile = new File(databaseFolder.getAbsolutePath() + "/" + id + ".vp");
		
		if (!playerFile.exists()) {
			try {
				playerFile.createNewFile();
			} catch (IOException e1) {
				Log.error(e1.getMessage());
			}
		}

		
		List<String> lines2Write = new ArrayList<String>();
		lines2Write.add("name: " + username);
		lines2Write.add("rankid: " + rank.id);
		lines2Write.add("points: " + points);
		
		try {
			FileUtils.writeLines(playerFile, lines2Write);
			Log.info("Joueur sauvegarde : " + username);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public void givePoints(int i) {
		points += i;
	}
	
}
