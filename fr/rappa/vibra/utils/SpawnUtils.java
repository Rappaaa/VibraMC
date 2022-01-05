package fr.rappa.vibra.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnUtils {

	public static void GiveSpawnObjects(Player p) {
		p.getInventory().clear();
		
		ItemStack menuselector = new ItemStack(Material.NETHER_STAR);
		ItemMeta menumeta = menuselector.getItemMeta();
		menumeta.setDisplayName("§7>> Menu §ejeux");
		List<String> lore = new ArrayList<String>();
		lore.add("§7Cliquez pour afficher");
		lore.add("§7le §emenu des jeux");
		menumeta.setLore(lore);
		menuselector.setItemMeta(menumeta);
		
		
		lore.clear();
		
		p.getInventory().setItem(0, menuselector);
	}
	
	public static void GiveLobbyObjects(Player p) {
		p.getInventory().clear();
		
		ItemStack leave = new ItemStack(Material.REDSTONE);
		ItemMeta leavemeta = leave.getItemMeta();
		leavemeta.setDisplayName("§c>> Retour au lobby");
		List<String> lore = new ArrayList<String>();
		lore.add("§7Cliquez pour §cquitter");
		lore.add("§7le jeu");
		leavemeta.setLore(lore);
		leave.setItemMeta(leavemeta);
		
		
		lore.clear();
		
		p.getInventory().setItem(8, leave);
	}
	
}
