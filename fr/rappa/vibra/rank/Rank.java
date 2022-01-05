package fr.rappa.vibra.rank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

public class Rank {

	public int id;
	public String prefix;
	public String name;
	public List<String> permissions = new ArrayList<String>();;
	
	public Rank(File file) {
		
		permissions = new ArrayList<String>();
		
		List<String> args;
		try {
			
			args = FileUtils.readLines(file);
			
			for (String l : args) {
				
				String cmd = l.split(":")[0].toLowerCase().trim();
				String vl = l.split(":")[1].trim();
				
				switch (cmd) {
				case "id":
					
					id = Integer.parseInt(vl);
					
					break;
				case "name":
					
					name = vl;
					
					break;
				case "perm":
					
					permissions.add(vl);
					
					break;
				case "prefix":
					
					prefix = vl.replace("$", "§");
					
					break;
					
				default: break;	
				}
				
			}
			
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}
	
}
