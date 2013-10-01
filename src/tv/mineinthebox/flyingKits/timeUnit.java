package tv.mineinthebox.flyingKits;

import java.io.File;
import java.util.Date;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class timeUnit {
	
	public static int getCommandCooldown() {
		try {
			File f = new File(flyingKit.getPlugin.getDataFolder() + File.separator + "kits.yml");
			if(f.exists()) {
				FileConfiguration con = YamlConfiguration.loadConfiguration(f);
				return con.getInt("timeUsageBetweenKit");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@SuppressWarnings("deprecation")
	public static Long getFixedTime(Long time) {
		Date date = new Date(time);
		date.setMinutes(date.getMinutes() + getCommandCooldown());
		return date.getTime();
	}
	
	public static boolean isOverTime(Long time) {
		Long fixedTime = getFixedTime(time);
		Long timeNow = System.currentTimeMillis();
		if(timeNow > fixedTime) {
			return true;
		}
		return false;
	}

}
