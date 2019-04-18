package com.alchemi.dwarfstar;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dwarfstar.listeners.command.DwarfCommand;
import com.alchemi.dwarfstar.listeners.command.SmeltCommand;

public class main extends JavaPlugin {

	public static Messenger messenger;
	
	public static File CONFIG_FILE;
	public static File MESSAGES_FILE;
	public static File RECIPES_FOLDER;
	
	public static final int CONFIG_FILE_VERSION = 0;
	public static final int MESSAGES_FILE_VERSION = 0;
	
	public static main instance;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		CONFIG_FILE = new File(getDataFolder(), "config.yml");
		MESSAGES_FILE = new File(getDataFolder(), "messages.yml");
		RECIPES_FOLDER = new File(getDataFolder(), "recipes");
		
		messenger = new Messenger(this);
		
		if (!RECIPES_FOLDER.exists()) RECIPES_FOLDER.mkdir();
		
		try {
			Config.enable();
		} catch (IOException | InvalidConfigurationException e) {
			System.err.println("Unable to load configs! Disabling plugin...");
			Bukkit.getPluginManager().disablePlugin(this);
			e.printStackTrace();
		}
		
		registerCommands();
		
	}
	
	@Override
	public void onDisable() {
		Config.save();
	}
	
	private void registerCommands() {
		getServer().getPluginCommand("smelter").setExecutor(new SmeltCommand());
		getServer().getPluginCommand("dwarfstar").setExecutor(new DwarfCommand());
	}
	
}
