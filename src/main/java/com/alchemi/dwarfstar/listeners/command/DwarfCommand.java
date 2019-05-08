package com.alchemi.dwarfstar.listeners.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.dwarfstar.Config;
import com.alchemi.dwarfstar.objects.SmeltRecipe;
import com.alchemi.dwarfstar.objects.gui.RecipeGUI;

public class DwarfCommand implements CommandExecutor {

	List<String> createAliases = Arrays.asList("create", "createrecipe", "c", "cr");
	List<String> deleteAliases = Arrays.asList("delete", "deleterecipe", "d", "dr", "del");
	List<String> modifyAliases = Arrays.asList("modify", "edit", "m", "mod", "e");
	List<String> defaultAliases = Arrays.asList("defaults", "default", "generate");	
	
	final String createUsage = "&9/dwarfstar create <recipe_name>";
	final String deleteUsage = "&9/dwarfstar delete <recipe_name>";
	final String modifyUsage = "&9/dwarfstar modify <recipe_name>";
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (args.length >= 1) {
				if (createAliases.contains(args[0])) {
					if (player.getInventory().getItemInMainHand() != null) {
						SmeltRecipe recipe = new SmeltRecipe(player.getInventory().getItemInMainHand().getType());
						if (args.length > 1) recipe.setKey(args[1]);
						new RecipeGUI(player, sender, recipe);
					}
				} else if (defaultAliases.contains(args[0])) {
					Config.regenerateRecipes();
				}
			} else {
				
			}
		}
		return false;
	}
}
