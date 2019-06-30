package me.alchemi.dwarfstar;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.base.PluginBase;
import me.alchemi.dwarfstar.listeners.command.DwarfCommand;
import me.alchemi.dwarfstar.listeners.command.SmeltCommand;
import me.alchemi.dwarfstar.listeners.event.MainListener;
import me.alchemi.dwarfstar.listeners.tabcomplete.DwarfTabcomplete;
import me.alchemi.dwarfstar.listeners.tabcomplete.SmeltTabcomplete;

public class main extends PluginBase {

	public static File RECIPES_FOLDER;
	
	private static main instance;
	
	public Config config;
	
	@Override
	public void onEnable() {
		
		instance = this;

		RECIPES_FOLDER = new File(getDataFolder(), "recipes");
		
		setMessenger(new Messenger(this));
		
		if (!RECIPES_FOLDER.exists()) RECIPES_FOLDER.mkdir();
		
		try {
			config = new Config();
		} catch (IOException | InvalidConfigurationException e) {
			System.err.println("Unable to load configs! Disabling plugin...");
			Bukkit.getPluginManager().disablePlugin(this);
			e.printStackTrace();
		}
		
		registerCommands();
		
		Bukkit.getPluginManager().registerEvents(new MainListener(), this);
		
	}
	
	@Override
	public void onDisable() {
	}
	
	private void registerCommands() {
		getServer().getPluginCommand("smelter").setExecutor(new SmeltCommand());
		getServer().getPluginCommand("smelter").setTabCompleter(new SmeltTabcomplete());
		getServer().getPluginCommand("dwarfstar").setExecutor(new DwarfCommand());
		getServer().getPluginCommand("dwarfstar").setTabCompleter(new DwarfTabcomplete());
	}
	
	public static main getInstance() {
		return instance;
	}
	
}
