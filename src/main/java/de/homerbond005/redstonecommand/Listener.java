/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.redstonecommand;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class Listener implements org.bukkit.event.Listener {
	public static RedstoneCommand plugin;

	public Listener(RedstoneCommand redstoneCommand) {
		Listener.plugin = redstoneCommand;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.OAK_SIGN || event.getBlock().getType() == Material.OAK_WALL_SIGN) {
			Sign sign = (Sign) event.getBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase("[rsc]")) {
				event.getPlayer().sendMessage(ChatColor.RED + "Please remove this RSC via /rsc delete " + sign.getLine(1) + "!");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event) {
		Listener.plugin.reloadConfig();
		Player player = event.getPlayer();
		BlockState state = event.getBlock().getState();
		if (state instanceof Sign) {
			if (event.getLine(0).equalsIgnoreCase("[rsc]")) {
				if (!player.hasPermission("RSC.create") || !player.hasPermission("RSC.*")) {
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
					event.getBlock().setType(Material.AIR);
					ItemStack signpost = new ItemStack(Material.OAK_SIGN, 1);
					player.getInventory().addItem(signpost);
					return;
				}
				if (Listener.plugin.getConfig().getString("RedstoneCommands.Locations." + event.getLine(1)) != null) {
					player.sendMessage(ChatColor.RED + "The RSC " + ChatColor.GOLD + event.getLine(1) + ChatColor.RED + " already exists.");
					event.getBlock().setType(Material.AIR);
					ItemStack signpost = new ItemStack(Material.OAK_SIGN, 1);
					player.getInventory().addItem(signpost);
					return;
				}
				Listener.plugin.reloadConfig();
				int delay = 0;
				try {
					delay = Integer.parseInt(event.getLine(2));
				} catch (NumberFormatException error) {
					if (!event.getLine(2).isEmpty()) {
						player.sendMessage(ChatColor.GOLD + event.getLine(2) + ChatColor.RED + " is not a number! Saved with a delay of 0 sec.");
					}
					event.setLine(2, "0");
				}
				BlockFace direction;
				if (!event.getLine(3).isEmpty()) {
					try {
						direction = BlockFace.valueOf(event.getLine(3).toUpperCase());
					} catch (IllegalArgumentException e) {
						player.sendMessage(ChatColor.RED + "Wrong torch direction!");
						player.sendMessage(ChatColor.RED + "Possible values are: NORTH, EAST, SOUTH, WEST, UP, DOWN, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST");
						event.getBlock().setType(Material.AIR);
						player.getInventory().addItem(new ItemStack(Material.OAK_SIGN, 1));
						return;
					}
				} else if (Listener.plugin.getSignPlaceDirectionModeEnabled()) {
					@SuppressWarnings("deprecation")
					byte signdata = event.getBlock().getData();
					if (event.getBlock().getState().getType() == Material.OAK_WALL_SIGN) {
						if (signdata == 4)
							direction = BlockFace.NORTH;
						else if (signdata == 2)
							direction = BlockFace.EAST;
						else if (signdata == 5)
							direction = BlockFace.SOUTH;
						else if (signdata == 3)
							direction = BlockFace.WEST;
						else
							direction = BlockFace.NORTH;
					} else {
						if (signdata == 4)
							direction = BlockFace.NORTH;
						else if (signdata == 8)
							direction = BlockFace.EAST;
						else if (signdata == 12)
							direction = BlockFace.SOUTH;
						else if (signdata == 0)
							direction = BlockFace.WEST;
						else if (signdata > 0 && signdata < 4)
							direction = BlockFace.NORTH_WEST;
						else if (signdata > 4 && signdata < 8)
							direction = BlockFace.NORTH_EAST;
						else if (signdata > 8 && signdata < 12)
							direction = BlockFace.SOUTH_EAST;
						else if (signdata > 12)
							direction = BlockFace.SOUTH_WEST;
						else
							direction = BlockFace.NORTH;
					}
				} else {
					direction = BlockFace.NORTH;
				}
				player.sendMessage(ChatColor.GREEN + "Redstone torch will be placed: " + direction);
				Listener.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".X", event.getBlock().getX());
				Listener.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Y", event.getBlock().getY());
				Listener.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Z", event.getBlock().getZ());
				Listener.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".WORLD", event.getBlock().getWorld().getName());
				Listener.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Xchange", direction.getModX());
				Listener.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Ychange", direction.getModY());
				Listener.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".Zchange", direction.getModZ());
				Listener.plugin.getConfig().set("RedstoneCommands.Locations." + event.getLine(1) + ".DELAY", delay);
				if (delay != 0) {
					event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(direction.getModX(), direction.getModY(), direction.getModZ())).setType(Material.AIR);
				} else {
					event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(direction.getModX(), direction.getModY(), direction.getModZ())).setType(Material.REDSTONE_TORCH);
				}
				Listener.plugin.saveConfig();
				Listener.plugin.reloadRSCs();
				player.sendMessage(ChatColor.GREEN + "Successfully created RSC named " + ChatColor.GOLD + event.getLine(1));
			}
		}
	}
}
