package com.alchemi.dwarfstar.listeners.command;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.configurations.SexyConfiguration;
import com.alchemi.dwarfstar.main;
import com.alchemi.dwarfstar.objects.SmeltRecipe;
import com.alchemi.dwarfstar.objects.gui.RecipeGUI;

public class DwarfCommand implements CommandExecutor {

	List<String> createAliases = Arrays.asList("create", "createrecipe", "c", "cr");
	List<String> deleteAliases = Arrays.asList("delete", "deleterecipe", "d", "dr", "del");
	List<String> modifyAliases = Arrays.asList("modify", "edit", "m", "mod", "e");
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			new RecipeGUI(player, sender, SmeltRecipe.load(SexyConfiguration.loadConfiguration(new File(main.RECIPES_FOLDER, "glass.yml")), false));
			
			if (args.length >= 1) {
				
			}
		}
		return false;
	}
}
