package fr.rappa.vibra.anticheat;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.player.PlayerVibra;

public class VACListener implements Listener {

	public double sommeVel = 0;
	public int numberOfVel = 0;
	public double averageVelocity = 0;
	
	/**@EventHandler
	public void OnPlayerVel(EntityDamageByEntityEvent e) {
		if (e.getEntityType() != EntityType.PLAYER) return;
		
		Player p = (Player) e.getEntity();
		int ping = ((CraftPlayer) p).getHandle().ping;
		
		if (ping > 50) return;
		
		Location L1 = p.getLocation();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Location L2 = p.getLocation();
				Vector vel = new Vector(L1.getX() - L2.getX(), L1.getY() - L2.getY(), L1.getZ() - L2.getZ());
				
				sommeVel += vel.length();
				numberOfVel++;
				
				averageVelocity = sommeVel / numberOfVel;

				if (vel.length() < averageVelocity - (averageVelocity * 0.3))  {
					double delta = averageVelocity - vel.length();
					for (PlayerVibra pv : Main.instance.PlayerDatabase) {
						if (pv.player != null && pv.hasPermission("moderation")) {
							pv.player.sendMessage("§8§o* §4§oVAC §8§o* §c" + p.getDisplayName() + " §7utilise peut-être un §c§oAnti-KB §e(vel/avg/delta = " + vel.length() + " §e/ " + averageVelocity + " §e/ " + delta + "§e)");
						}
					}
					
				}
			}
		}.runTaskLater(Main.instance, 5);

	}*/
	
}
