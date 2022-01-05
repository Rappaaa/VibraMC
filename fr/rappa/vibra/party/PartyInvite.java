package fr.rappa.vibra.party;

import org.bukkit.entity.Player;

public class PartyInvite {

	public long date;
	public Player player;
	public Party party;
	
	public PartyInvite(Player p, Party py) {
		date = System.currentTimeMillis();
		player = p;
		party = py;
	}
	
}
