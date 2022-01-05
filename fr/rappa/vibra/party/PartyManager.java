package fr.rappa.vibra.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class PartyManager {

	private List<Party> partyList = new ArrayList<Party>();

	public void AddParty(Party p) {
		partyList.add(p);
	}
	
	public void CreateParty(Player leader) {
		Party p = new Party(leader);
		partyList.add(p);
	}
	
	public void RemoveParty(Party p) {
		partyList.remove(p);
	}
	
	public Party GetPartyFromPlayer(Player p) {
		for (Party party : partyList) {
			if (party.hasPlayer(p))
				return party;
		}
		
		return null;
	}
	
	public boolean isInParty(Player p) {
		
		for (Party party : partyList) {
			if (party.hasPlayer(p))
				return true;
		}
		
		return false;
	}
	
}
