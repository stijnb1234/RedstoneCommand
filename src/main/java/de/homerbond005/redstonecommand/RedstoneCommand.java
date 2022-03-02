/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.redstonecommand;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedstoneCommand extends JavaPlugin {
	private final Listener blocklistener = new Listener(this);
	private Logger log;
	private Map<String, RSCSign> rscs;
	private boolean signPlaceDirectionModeEnabled;

	@Override
	public void onEnable() {
		log = getLogger();
		getCommand("rsc").setExecutor(new Command());
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(blocklistener, this);
		getConfig().addDefault("RedstoneCommands.Locations", new HashMap<String, Object>());
		getConfig().addDefault("RedstoneCommands.permissionsEnabled", true);
		getConfig().addDefault("RedstoneCommands.signPlaceDirectionModeEnabled", true);
		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadConfig();
		if (!getConfig().isBoolean("RedstoneCommands.permissionsEnabled")) {
			getConfig().set("RedstoneCommands.permissionsEnabled", true);
		}
		saveConfig();
		signPlaceDirectionModeEnabled = getConfig().getBoolean("RedstoneCommands.signPlaceDirectionModeEnabled");
		reloadRSCs();
		log.log(Level.INFO, "config.yml loaded.");
		log.log(Level.INFO, "is enabled!");
	}

	@Override
	public void onDisable() {
		log.log(Level.INFO, "is disabled!");
	}

	/**
	 * Reload all RSCs from the config
	 */
	public void reloadRSCs() {
		reloadConfig();
		rscs = new HashMap<>();
		ConfigurationSection sec = getConfig().getConfigurationSection("RedstoneCommands.Locations");
		Set<String> rscnames = sec.getKeys(false);
		for (String name : rscnames) {
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".Xchange", -1);
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".Ychange", 0);
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".Zchange", 0);
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".X", 0);
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".Y", 0);
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".Z", 0);
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".WORLD", "world");
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".DELAY", 0);
			getConfig().addDefault("RedstoneCommands.Locations." + name + ".MSG", true);
			getConfig().options().copyDefaults(true);
			saveConfig();
			ConfigurationSection rsc = sec.getConfigurationSection(name);
			rscs.put(name.toLowerCase(), new RSCSign(name, rsc.getInt("X"), rsc.getInt("Y"), rsc.getInt("Z"), getServer().getWorld(rsc.getString("WORLD")), rsc.getInt("DELAY"), rsc.getInt("Xchange"), rsc.getInt("Ychange"), rsc.getInt("Zchange"), rsc.getBoolean("MSG")));
		}
	}

	public Map<String, RSCSign> getRscs() {
		return rscs;
	}

	/**
	 * Check if the signPlaceDirectionMode is enabled
	 * 
	 * @return A boolean
	 */
	public boolean getSignPlaceDirectionModeEnabled() {
		return signPlaceDirectionModeEnabled;
	}
}
