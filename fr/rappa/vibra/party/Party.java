package fr.rappa.vibra.party;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

public class Party {

	private List<Player> players = new ArrayList<Player>();
	private List<PartyInvite> invitations = new ArrayList<PartyInvite>();
	private Player leader; 
	
	public Party(Player leader) {
		this.leader = leader;
		AddPlayer(leader);
	}
	
	public Player getLeader() {
		return leader;
	}
	
	public void InvitePlayer(Player p) {
		invitations.add(new PartyInvite(p, this));
		UpdateInvitations();
	}
	
	public void RemoveInvitation(Player p) {
		Invitation(p).date = 0;
		UpdateInvitations();
	}
	
	public void AddPlayer(Player p) {
		players.add(p);
		if (IsInvited(p)) invitations.remove(Invitation(p));
		UpdateInvitations();
	}
	
	public PartyInvite Invitation(Player p) {
		for (PartyInvite pinv : invitations)  {
			if (pinv.player.getUniqueId().compareTo(p.getUniqueId()) == 0) return pinv;
		}
		
		return null;
	}
	
	public void UpdateInvitations() {
		Iterator<PartyInvite> inv = invitations.iterator();
		
		while (inv.hasNext()) {
			PartyInvite pinv = inv.next();
			if (Math.abs(pinv.date - System.currentTimeMillis()) >= 120 * 1000L) {
				inv.remove();
			}
		}
	}
	
	public boolean IsInvited(Player p) {
		return Invitation(p) != null;
	}
	
	public void RemovePlayer(Player p) {
		players.remove(p);
		UpdateInvitations();
	}
	
	public void SetLeader(Player p) {
		this.leader = p;
	}
	
	public boolean IsLeader(Player p) {
		return p.getUniqueId().compareTo(leader.getUniqueId()) == 0;
	}
	
	public boolean hasPlayer(Player p) {
		for (Player ply : players) {
			if (p.getUniqueId().compareTo(ply.getUniqueId()) == 0)  {
				return true;
			}
		}
		
		return false;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
}
