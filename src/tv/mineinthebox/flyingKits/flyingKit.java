package tv.mineinthebox.flyingKits;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class flyingKit extends JavaPlugin {

	public static flyingKit getPlugin = null;
	private static Logger logger = Logger.getLogger("Minecraft");

	public void onEnable() {
		getPlugin = this;
		setLogMessage("has been enabled!", logType.info);
		configuration.generateConfig();
		configuration.createPermissionNodes();
		getCommand("kit").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(new BlockChestEvent(), this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("kit")) {
			if(args.length == 0) {
				//shows help
				sender.sendMessage(ChatColor.GOLD + ".oO___[kits help]___Oo.");
				sender.sendMessage(ChatColor.GREEN + "while using the kit command, you will see fall a little box out from the sky\n this will be your kit!");
				sender.sendMessage(ChatColor.GOLD + "==[Available kits]==");
				for(String perm : configuration.permissions) {
					if(sender.hasPermission(perm)) {
						sender.sendMessage(ChatColor.DARK_GRAY + "Default: " + ChatColor.GRAY + "/kit " + perm.replace("kit.", "") + ChatColor.WHITE + " : gives you a kit");
					}
				}
			} else if(args.length == 1) {
				if(sender instanceof Player) {
					if(args[0].equalsIgnoreCase("help")) {
						//shows help
						sender.sendMessage(ChatColor.GOLD + ".oO___[kits help]___Oo.");
						sender.sendMessage(ChatColor.GREEN + "while using the kit command, you will see fall a little box out from the sky\n this will be your kit!");
						sender.sendMessage(ChatColor.GOLD + "==[Available kits]==");
						for(String perm : configuration.permissions) {
							if(sender.hasPermission(perm)) {
								sender.sendMessage(ChatColor.DARK_GRAY + "Default: " + ChatColor.GRAY + "/kit " + perm.replace("kit.", "") + ChatColor.WHITE + " : gives you a kit");
							}
						}
					} else if(args[0].equalsIgnoreCase("reload")) {
						BlockChestEvent.disableChests();
						configuration.permissions.clear();
						configuration.createPermissionNodes();
						sender.sendMessage(ChatColor.GREEN + "FlyingChest reload successfully");
					} else {
						Player p = (Player) sender;
						if(configuration.isKit(args[0])) {
							if(sender.hasPermission(configuration.convertToPermissionKey(args[0]))) {
								try {
									File f = new File(flyingKit.getPlugin.getDataFolder() + File.separator + "players" + File.separator + p.getName() + ".yml");
									if(f.exists()) {
										FileConfiguration con = YamlConfiguration.loadConfiguration(f);
										if(timeUnit.isOverTime(con.getLong("time"))) {
											f.delete();
										} else {
											sender.sendMessage(ChatColor.RED + "you need to wait when " + timeUnit.getCommandCooldown() + " minuts are passed!");
											return false;
										}
									}
								} catch(Exception e) {
									e.printStackTrace();
								}
								sender.sendMessage(ChatColor.GREEN + "youve successfully spawned the " + ChatColor.GRAY + args[0] + ChatColor.GREEN + " kit!, look above the sky!");
								Location loc = p.getLocation();
								loc.setY(120);
								FallingBlock block = p.getWorld().spawnFallingBlock(loc, Material.CHEST, (byte) 0);
								BlockChestEvent.playerChestLocation.put(sender.getName(), block.getLocation());
								BlockChestEvent.lastCommand.put(sender.getName(), args[0]);
								try {
									File f = new File(flyingKit.getPlugin.getDataFolder() + File.separator + "players" + File.separator + p.getName() + ".yml");
									if(f.exists()) {
										FileConfiguration con = YamlConfiguration.loadConfiguration(f);
										con.set("user", p.getName());
										con.set("time", System.currentTimeMillis());
										con.save(f);
									} else {
										FileConfiguration con = YamlConfiguration.loadConfiguration(f);
										con.set("user", p.getName());
										con.set("time", System.currentTimeMillis());
										con.save(f);
									}
								} catch(Exception e) {
									e.printStackTrace();
								}
							} else {
								sender.sendMessage(ChatColor.RED + "you don't have permission for this kit, " + ChatColor.GRAY + "permission: " + configuration.convertToPermissionKey(args[0]));
							}
						} else {
							sender.sendMessage(ChatColor.RED + "this kit does not exist!");
						}
					}
				}
			} else if(args.length == 2) {
				Player p = Bukkit.getPlayer(args[0]);
				if(p instanceof Player) {
					if(configuration.isKit(args[1])) {
						if(sender.hasPermission(configuration.convertToPermissionKey(args[1]))) {
							p.sendMessage(ChatColor.GREEN + sender.getName() + " successfully spawned the " + ChatColor.GRAY + args[1] + ChatColor.GREEN + " kit!, look above the sky!");
							Location loc = p.getLocation();
							loc.setY(120);
							FallingBlock block = p.getWorld().spawnFallingBlock(loc, Material.CHEST, (byte) 0);
							BlockChestEvent.playerChestLocation.put(p.getName(), block.getLocation());
							BlockChestEvent.lastCommand.put(p.getName(), args[1]);
						} else {
							sender.sendMessage(ChatColor.RED + "you don't have permission for this kit, " + ChatColor.GRAY + "permission: " + configuration.convertToPermissionKey(args[0]));
						}
					} else {
						sender.sendMessage(ChatColor.RED + "this kit does not exist!");
					}
				}
			}
		}
		return false;
	}

	public void onDisable() {
		setLogMessage("has been disabled!", logType.info);
		BlockChestEvent.disableChests();
	}

	public static void setLogMessage(String message, logType type) {
		if(type == logType.info) {
			logger.info(getPlugin.getName() + " " + getPlugin.getDescription().getVersion() + message);
		} else if(type == logType.servere) {
			logger.severe(getPlugin.getName() + " " + getPlugin.getDescription().getVersion() + message);
		}
	}
}
