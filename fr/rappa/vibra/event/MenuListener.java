package fr.rappa.vibra.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.game.Game;
import fr.rappa.vibra.game.moba.Moba;
import fr.rappa.vibra.game.murder.Murder;
import fr.rappa.vibra.game.tft.TFT;

public class MenuListener implements Listener {

	@EventHandler
	public void OnClickInMenu(InventoryClickEvent e) {
		if (e == null || e.getWhoClicked() == null || e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		
		Player p = (Player) e.getWhoClicked();
		
		
		if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7>> §cImposteurs")) {
			e.setCancelled(true);
			Game game = Main.gameManager.FindGame(Murder.class, p);
			
			if (game == null) {
				game = new Murder();
				p.sendMessage("§7Pas de partie trouvée");
				p.sendMessage("§a>> Création d'une partie");
				Main.gameManager.AddGame(game);
			} 
			
			game.OnPlayerJoin(p);
		}
		
		if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7>> §3Kill The Guardian §8(4v4)")) {
			e.setCancelled(true);
			Game game = Main.gameManager.FindGame(Moba.class, p);
			
			if (game == null) {
				game = new Moba();
				p.sendMessage("§7Pas de partie trouvée");
				p.sendMessage("§a>> Création d'une partie");
				Main.gameManager.AddGame(game);
			} 
			
			game.OnPlayerJoin(p);
			
		}
		
		if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7>> §2TFT")) {
			e.setCancelled(true);
			Game game = Main.gameManager.FindGame(TFT.class, p);
			
			if (game == null) {
				game = new TFT();
				p.sendMessage("§7Pas de partie trouvée");
				p.sendMessage("§a>> Création d'une partie");
				Main.gameManager.AddGame(game);
			} 
			
			game.OnPlayerJoin(p);
		}
	}
	
}
