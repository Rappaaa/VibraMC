package fr.rappa.vibra.game.tft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.rappa.vibra.game.Game;

public class TFT extends Game {
	
	public TFT() {
		super("tft");
		
		
		maxPlayers = 16;
		playersToStart = 2;
		
		
	}
	
	@Override
	public void StartGame() {
		hasStarted = true;
		
		
		
	}

}

