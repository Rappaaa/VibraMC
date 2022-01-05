package fr.rappa.vibra.game.moba;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.rappa.vibra.game.Game;

public class Moba extends Game {

	public List<Player> redTeam = new ArrayList<Player>();
	public List<Player> blueTeam = new ArrayList<Player>();
	
	public Moba() {
		super("moba");
		
		
		maxPlayers = 8;
		playersToStart = 2;
		
		
	}
	
	@Override
	public void StartGame() {
		hasStarted = true;
		
		
		
	}

}
