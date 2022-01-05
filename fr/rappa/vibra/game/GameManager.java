package fr.rappa.vibra.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.rappa.vibra.game.murder.Murder;

public class GameManager {

	private List<Game> games = new ArrayList<Game>();
	
	public List<Game> getGames() {
		return games;
	}
	
	public void AddGame(Game game) {
		games.add(game);
	}
	
	public int NumberOfPlayersInGame(Class<? extends Game> gameType) {
		int nbr = 0;
		for (Game game : games) {
			
			if (game.getClass().isAssignableFrom(gameType)) {
				if (game.hasStarted) {
					nbr+= game.playersInGame.size();
				}
			} 
			
		}
		
		return nbr;
	}
	
	public int NumberOfPlayersWaiting(Class<? extends Game> gameType) {
		int nbr = 0;
		System.out.println("type: " + gameType.getName());
		for (Game game : games) {
			
			if (game.getClass().isAssignableFrom(gameType)) {
				if (!game.hasStarted) {
					nbr+= game.playersInGame.size();
				}
			} 
			
		}
		
		return nbr;
	}
	
	public Game FindGame(Class<? extends Game> gameType, Player p) {
		for (Game game : games) {
			
			if (game.getClass().isAssignableFrom(gameType)) {
				if (!game.hasStarted && game.CanJoin(p)) {
					return game;
				}
			} 
			
		}
		
		return null;
	}
	
	public boolean isInGame(Player p) {
		for (Game game : games) {
			if (game.playersInGame.contains(p)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public Game GetGameByPlayer(Player p) {
		for (Game game : games) {
			if (game.playersInGame.contains(p)) {
				return game;
			}
		}
		
		return null;
	}

	public void RemoveGame(Murder murder) {
		games.remove(murder);
	}
}

