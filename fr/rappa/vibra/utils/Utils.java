package fr.rappa.vibra.utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import fr.rappa.vibra.Main;
import fr.rappa.vibra.player.PlayerVibra;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class Utils {

    public static void sendTitle(Player p, String title, String subtitle)
    {
            PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
            IChatBaseComponent titleJSON = ChatSerializer.a("{'text':'" + title + "'}");
            IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text':'" + subtitle + "'}");
            PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON);
            PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
            connection.sendPacket(titlePacket);
            connection.sendPacket(subtitlePacket);
    }
   
    public static void sendActionBar(Player p, String msg)
    {
            IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + msg + "\"}");
            PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(ppoc);
    }
   
    public static void sendHeaderAndFooter(Player p, String head, String foot)
    {
            PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
            IChatBaseComponent header = ChatSerializer.a("{'color':'" + "', 'text':'" + head + "'}");
            IChatBaseComponent footer = ChatSerializer.a("{'color':'" + "', 'text':'" + foot + "'}");
            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
            try
            {
                    Field headerField = packet.getClass().getDeclaredField("a");
                    headerField.setAccessible(true);
                    headerField.set(packet, header);
                    headerField.setAccessible(!headerField.isAccessible());
                   

                    Field footerField = packet.getClass().getDeclaredField("b");
                    footerField.setAccessible(true);
                    footerField.set(packet, footer);
                    footerField.setAccessible(!footerField.isAccessible());
            }
            catch(Exception e){e.printStackTrace();}
            connection.sendPacket(packet);
    }
    
    public static void playSound(Player p, Sound sound) {
    	p.playSound(p.getLocation(), sound, 1, 1);
    }
	
    
	public static void DisplayHubSidebar(Player p) {
		PlayerVibra pv = Main.instance.getPlayer(p);
		
		if (pv == null) return;
		
		int players = Bukkit.getOnlinePlayers().size();
		
		ScoreboardManager sb = Bukkit.getScoreboardManager();
		Scoreboard s = sb.getNewScoreboard();				
		Objective obj = s.registerNewObjective("test", "ok");
		
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName("§f§o- §c§lVibraMC §f§o-");
		Score profil = obj.getScore("§7§lMon profil");
		Score rang = obj.getScore("§f§o- " + pv.rank.prefix);
		Score point = obj.getScore("§f§o- §b§l" + pv.points + " point(s)");
		Score serveur = obj.getScore("§a§lServeur");
		Score region = obj.getScore("§f§o- §8Région FR");
		Score connectes = obj.getScore("§f§o- §e§l" + players + " connecté(s)");
		
		Score end = obj.getScore("§avibramc.fr");
		
		profil.setScore(8);
		rang.setScore(7);
		point.setScore(6);
		obj.getScore("  ").setScore(5);
		serveur.setScore(4);
		region.setScore(3);
		connectes.setScore(2);
		obj.getScore(" ").setScore(1);
		end.setScore(0);
		
		p.setScoreboard(s);
	}

    
}
