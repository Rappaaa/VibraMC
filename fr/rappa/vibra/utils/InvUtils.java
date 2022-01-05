package fr.rappa.vibra.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.game.moba.Moba;
import fr.rappa.vibra.game.murder.Murder;
import fr.rappa.vibra.game.tft.TFT;

public class InvUtils {

	public static Inventory GameMenuInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, "§7>> Menu des §ejeux");

		ItemStack murderIcon = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta murdermeta = murderIcon.getItemMeta();
		murdermeta.setDisplayName("§7>> §cImposteurs");
		List<String> lore = new ArrayList<String>();

		int nbrWaiting = Main.gameManager.NumberOfPlayersWaiting(Murder.class);
		murderIcon.setAmount(nbrWaiting);
		lore.add("§7Le but du jeu varie selon votre role");
		lore.add("§7Les §cimposteurs §7doivent tuer les §ainnocents");
		lore.add("§7§osans se faire démasquer");
		lore.add("§7Les §ainnocents §7doivent tuer les §cimposteurs");
		lore.add("§7§osans dommages colatéraux");
		lore.add(" ");

		lore.add("§7>> Cliquez pour §arejoindre");
		lore.add("§e" + nbrWaiting + " §ejoueur(s) en attente");
		lore.add("§a" + Main.gameManager.NumberOfPlayersInGame(Murder.class) + " §ajoueur(s) en partie");
		murdermeta.setLore(lore);
		murderIcon.setItemMeta(murdermeta);

		inv.addItem(murderIcon);

		ItemStack mobaIcon = new ItemStack(Material.PRISMARINE_SHARD);
		ItemMeta mobameta = mobaIcon.getItemMeta();
		mobameta.setDisplayName("§7>> §3Kill The Guardian §8(4v4)");
		lore = new ArrayList<String>();

		nbrWaiting = Main.gameManager.NumberOfPlayersWaiting(Moba.class);
		mobaIcon.setAmount(nbrWaiting);

		lore.add("§7Description à faire");
		lore.add(" ");

		lore.add("§7>> Cliquez pour §arejoindre");
		lore.add("§e" + nbrWaiting + " §ejoueur(s) en attente");
		lore.add("§a" + Main.gameManager.NumberOfPlayersInGame(Moba.class) + " §ajoueur(s) en partie");
		mobameta.setLore(lore);
		mobaIcon.setItemMeta(mobameta);

		inv.addItem(mobaIcon);

		ItemStack tftIcon = new ItemStack(Material.GOLD_SWORD);
		ItemMeta tftmeta = tftIcon.getItemMeta();
		tftmeta.setDisplayName("§7>> §2TFT");
		lore = new ArrayList<String>();

		nbrWaiting = Main.gameManager.NumberOfPlayersWaiting(TFT.class);
		tftIcon.setAmount(nbrWaiting);

		lore.add("§7NTM Matheo");
		lore.add(" ");

		lore.add("§7>> Cliquez pour §arejoindre");
		lore.add("§e" + nbrWaiting + " §ejoueur(s) en attente");
		lore.add("§a" + Main.gameManager.NumberOfPlayersInGame(TFT.class) + " §ajoueur(s) en partie");
		tftmeta.setLore(lore);
		tftIcon.setItemMeta(tftmeta);

		inv.addItem(tftIcon);

		return inv;
	}

}
