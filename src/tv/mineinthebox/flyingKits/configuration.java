package tv.mineinthebox.flyingKits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class configuration {
	
	public static ArrayList<String> permissions = new ArrayList<String>();
	
	@SuppressWarnings("deprecation")
	public static void generateConfig() {
		try {
			File f = new File(flyingKit.getPlugin.getDataFolder() + File.separator + "kits.yml");
			if(!f.exists()) {
				FileConfiguration con = YamlConfiguration.loadConfiguration(f);
				FileConfigurationOptions opt = con.options();
				opt.header("This is a little schematic how these values are readable\ndata:subdatavalue={Amount of items}");
				con.set("timeUsageBetweenKit", 1440);
				con.set("kit.normal", Material.STONE_SWORD.getId() + "={1}," + Material.STONE_AXE.getId() + "={1}," + Material.STONE_PICKAXE.getId() + "={1}," + Material.STONE_SPADE.getId() + "={1}," + Material.MELON.getId() + "={10}");
				con.set("kit.anotherkit", Material.IRON_SWORD.getId() + "={1}," + Material.IRON_AXE.getId() + "={1}," + Material.IRON_PICKAXE.getId() +"={1}," + Material.IRON_SPADE.getId() + "={1},"+ Material.MELON.getId() +"={32}");
				con.set("kit.woolkit", Material.WOOL.getId() + ":0={64}," + Material.WOOL.getId() + ":1={64}," + Material.WOOL.getId() + ":2={64}," + Material.WOOL.getId() + ":3={64}," + Material.WOOL.getId() + ":4={64}," + Material.WOOL.getId() + ":5={64}," + Material.WOOL.getId() + ":6={64}," + Material.WOOL.getId() + ":7={64}," + Material.WOOL.getId() + ":8={64}," + Material.WOOL.getId() + ":9={64}");
				con.save(f);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createPermissionNodes() {
		try {
			File f = new File(flyingKit.getPlugin.getDataFolder() + File.separator + "kits.yml");
			if(f.exists()) {
				FileConfiguration con = YamlConfiguration.loadConfiguration(f);
				for(String permissionNode: con.getConfigurationSection("kit").getKeys(false)) {
					if(permissionNode.equalsIgnoreCase("help") || permissionNode.equalsIgnoreCase("reload")) {
						//skip this permission.
					} else {
						permissions.add("kit."+permissionNode);	
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isKit(String arg) {
		for(String permission : permissions) {
			if(permission.replace("kit.", "").equalsIgnoreCase(arg)) {
				return true;
			}
		}
		return false;
	}
	
	public static String convertToPermissionKey(String arg) {
		if(isKit(arg)) {
			return "kit." + arg;
		}
		return null;
	}
	
	public static int usageTime() {
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
	
	public static List<ItemStack> StackList(String kitPath) {
		try {
			File f = new File(flyingKit.getPlugin.getDataFolder() + File.separator + "kits.yml");
			if(f.exists()) {
				FileConfiguration con = YamlConfiguration.loadConfiguration(f);
				ArrayList<ItemStack> list = new ArrayList<ItemStack>();
				String[] items = con.get("kits." + kitPath).toString().split(",");
				for(String item : items) {
					try {
						Integer amount = Integer.parseInt(item.substring("{" .length()).replace("}", ""));	
						try {
							String materialName = item.replace("{.*?}", "");
							ItemStack stack = new ItemStack(Material.valueOf(materialName));
							stack.setAmount(amount);
							list.add(stack);
						} catch(IllegalArgumentException e) {
							System.out.println("cannot be converted to item stack..");
						}
					} catch(NumberFormatException e) {
						System.out.println("amount is not a numbers in config!");
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
