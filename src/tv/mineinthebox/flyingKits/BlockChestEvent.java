package tv.mineinthebox.flyingKits;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class BlockChestEvent implements Listener {

	public static HashMap<String, Location> playerChestLocation = new HashMap<String, Location>();
	public static HashMap<String, String> lastCommand = new HashMap<String, String>();
	public static HashMap<String, Chest> chests = new HashMap<String, Chest>();
	public static HashMap<String, Chest> chests2 = new HashMap<String, Chest>();

	@EventHandler
	public void checkBlock(EntityChangeBlockEvent e) {
		if(e.getEntity() instanceof FallingBlock) {
			FallingBlock fblock = (FallingBlock) e.getEntity();
			if(fblock.getMaterial() == Material.CHEST) {
				e.getBlock().setType(Material.CHEST);
				if(e.getBlock().getType() == Material.CHEST) {
					setItemsInChest();
					e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.SMOKE, 100);
					playRespectedSound(Sound.AMBIENCE_CAVE, e.getBlock().getLocation());
					playRespectedSound(Sound.AMBIENCE_RAIN, e.getBlock().getLocation());
					playRespectedSound(Sound.ANVIL_BREAK, e.getBlock().getLocation());
					playRespectedSound(Sound.AMBIENCE_THUNDER, e.getBlock().getLocation());
					playRespectedSound(Sound.WOLF_DEATH, e.getBlock().getLocation());
					e.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void setItemsInChest() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(playerChestLocation.containsKey(p.getName())) {
				Block block = p.getWorld().getHighestBlockAt(playerChestLocation.get(p.getName()));
				if(block.getType() == Material.CHEST) {
					Chest chest = (Chest) block.getState();
					if(lastCommand.containsKey(p.getName())) {
						try {
							File f = new File(flyingKit.getPlugin.getDataFolder() + File.separator + "kits.yml");
							if(f.exists()) {
								FileConfiguration con = YamlConfiguration.loadConfiguration(f);
								if(con.isSet("kit."+lastCommand.get(p.getName()))) {
									String[] Itemarguments = con.getString("kit."+lastCommand.get(p.getName())).split(",");
									for(int i = 0; i < Itemarguments.length; i++) {
										String[] type = Itemarguments[i].split("=");
										String material_andData = type[0];
										String Amount = type[1].replace("{", "").replace("}", "");
										if(material_andData.contains(":")) {
											try {
												String[] material = material_andData.split(":");
												String materialName = material[0];
												String data = material[1];
												ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(materialName)));
												item.setAmount(Integer.valueOf(Amount));
												item.setDurability(Byte.parseByte(data));
												chest.getInventory().addItem(item);
												chest.update();
											} catch(NumberFormatException e) {
												p.getPlayer().sendMessage(ChatColor.RED + "Exception: this kit is corrupt, one of the data values is not a number or isn't a item id");
											}
										} else {
											try {
												String materialName = material_andData;
												ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(materialName)));
												item.setAmount(Integer.valueOf(Amount));
												chest.getInventory().addItem(item);
												chest.update();
											} catch(NumberFormatException e) {
												p.getPlayer().sendMessage(ChatColor.RED + "Exception: this kit is corrupt, one of the data values is not a number.");
											}
										}
									}
								}
								chests.put(p.getName(), chest);
								chests2.put(p.getName(), chest);
								lastCommand.remove(p.getName());
								playerChestLocation.remove(p.getName());
							}
						} catch(Exception e) {
							p.sendMessage(ChatColor.RED + "kit exception, kit not found! make sure your kit is case sensitive");
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void openChest(final InventoryOpenEvent e) {
		if(e.getInventory().getType() == InventoryType.CHEST) {
			if(e.getInventory().getHolder() instanceof Chest) {
				Chest Eventchest = (Chest) e.getInventory().getHolder();
				if(chests2.containsKey(e.getPlayer().getName())) {
					final Chest chest = chests2.get(e.getPlayer().getName());
					if(Eventchest.getLocation().equals(chest.getLocation())) {
						final Player p = (Player) e.getPlayer();
						e.setCancelled(true);
						Bukkit.getScheduler().scheduleSyncDelayedTask(flyingKit.getPlugin, new Runnable() {

							@Override
							public void run() {
								p.sendMessage(ChatColor.GREEN + "opening crate 5");
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 0, 1);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_CLOSE, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
							}

						}, 50);
						Bukkit.getScheduler().scheduleSyncDelayedTask(flyingKit.getPlugin, new Runnable() {

							@Override
							public void run() {
								p.sendMessage(ChatColor.GREEN + "opening crate 4");
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 0, 1);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_CLOSE, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
							}

						}, 100);
						Bukkit.getScheduler().scheduleSyncDelayedTask(flyingKit.getPlugin, new Runnable() {

							@Override
							public void run() {
								p.sendMessage(ChatColor.GREEN + "opening crate 3");
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 0, 1);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_CLOSE, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
							}


						}, 150);
						Bukkit.getScheduler().scheduleSyncDelayedTask(flyingKit.getPlugin, new Runnable() {

							@Override
							public void run() {
								p.sendMessage(ChatColor.GREEN + "opening crate 2");
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 0, 1);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_CLOSE, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
							}

						}, 200);
						Bukkit.getScheduler().scheduleSyncDelayedTask(flyingKit.getPlugin, new Runnable() {

							@Override
							public void run() {
								p.sendMessage(ChatColor.GREEN + "opening crate 1");
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 0, 1);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_CLOSE, 1, 0);
								p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
							}

						}, 250);
						Bukkit.getScheduler().scheduleSyncDelayedTask(flyingKit.getPlugin, new Runnable() {

							@Override
							public void run() {
								try {
									p.sendMessage(ChatColor.GREEN + "opening crate...");
									p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 0, 1);
									p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
									p.playSound(chest.getLocation(), Sound.CHEST_CLOSE, 1, 0);
									p.playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 0);
									if(e.isCancelled()) {
										e.setCancelled(false);
										chests2.remove(p.getName());
										p.openInventory(e.getInventory());
									}	
								} catch(NullPointerException e) {
									//supress this
								}
							}

						}, 300);
					}
				}
			}
		}
	}

	@EventHandler
	public void closeChest(InventoryCloseEvent e) {
		if(e.getInventory().getType() == InventoryType.CHEST) {
			if(e.getInventory().getHolder() instanceof Chest) {
				Chest Eventchest = (Chest) e.getInventory().getHolder();
				if(chests.containsKey(e.getPlayer().getName())) {
					Chest chest = chests.get(e.getPlayer().getName());
					if(Eventchest.getLocation().equals(chest.getLocation())) {
						chest.getBlock().breakNaturally();
						chests.remove(e.getPlayer().getName());
					}
				}
				if(chests2.containsKey(e.getPlayer().getName())) {
					chests2.remove(e.getPlayer().getName());
				}
			}
		}
	}

	@EventHandler
	public void ClearChest(PlayerQuitEvent e) {
		if(chests.containsKey(e.getPlayer().getName())) {
			Chest chest = chests.get(e.getPlayer().getName());
			chest.getBlock().setType(Material.AIR);
			chest.update();
			chests.remove(e.getPlayer().getName());
		}
		if(chests2.containsKey(e.getPlayer().getName())) {
			chests2.remove(e.getPlayer().getName());
		}
		if(lastCommand.containsKey(e.getPlayer().getName())) {
			lastCommand.remove(e.getPlayer().getName());
		}
	}

	@EventHandler
	public void ClearChest(PlayerKickEvent e) {
		if(chests.containsKey(e.getPlayer().getName())) {
			Chest chest = chests.get(e.getPlayer().getName());
			chest.getBlock().setType(Material.AIR);
			chest.update();
			chests.remove(e.getPlayer().getName());
		}
		if(chests2.containsKey(e.getPlayer().getName())) {
			chests2.remove(e.getPlayer().getName());
		}
		if(lastCommand.containsKey(e.getPlayer().getName())) {
			lastCommand.remove(e.getPlayer().getName());
		}
	}

	public static void disableChests() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(chests.containsKey(p.getPlayer().getName())) {
				Chest chest = chests.get(p.getPlayer().getName());
				chest.getBlock().setType(Material.AIR);
				chest.update();
				chests.remove(p.getPlayer().getName());
			}
			if(chests2.containsKey(p.getName())) {
				chests2.remove(p.getName());
			}
			if(lastCommand.containsKey(p.getPlayer().getName())) {
				lastCommand.remove(p.getPlayer().getName());
			}
		}
	}

	public static void playRespectedSound(final Sound sound, final Location loc) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(flyingKit.getPlugin, new Runnable() {

			@Override
			public void run() {
				loc.getWorld().playSound(loc, sound, 3, 3);
			}

		}, 15);
	}
}
