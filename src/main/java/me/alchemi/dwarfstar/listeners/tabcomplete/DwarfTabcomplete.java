package me.alchemi.dwarfstar.listeners.tabcomplete;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.alchemi.dwarfstar.Config;
import me.alchemi.dwarfstar.main;
import me.alchemi.dwarfstar.listeners.command.DwarfCommand;

public class DwarfTabcomplete implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> tabSuggest = new ArrayList<>();
		List<String> list = new ArrayList<>();
		
		if (!(sender instanceof Player && sender.hasPermission(command.getPermission())))
			return tabSuggest;
		
		if (args.length == 1) {
			
			list.add(DwarfCommand.createAliases.get(0));
			list.add(DwarfCommand.deleteAliases.get(0));
			list.add(DwarfCommand.enableAliases.get(0));
			list.add(DwarfCommand.disableAliases.get(0));
			list.add(DwarfCommand.modifyAliases.get(0));
			list.add(DwarfCommand.defaultAliases.get(0));
			list.add(DwarfCommand.listAliases.get(0));
				
		} else if (args.length == 2) {
			
			List<String> pArgs = new ArrayList<String>();
			pArgs.addAll(DwarfCommand.deleteAliases);
			pArgs.addAll(DwarfCommand.disableAliases);
			pArgs.addAll(DwarfCommand.modifyAliases);
			
			if (pArgs.contains(args[0])) {
				for (String key : Config.getRecipeKeys()) {
					list.add(key);
				}
			} else if (DwarfCommand.enableAliases.contains(args[0])) {
				for (File file : main.RECIPES_FOLDER.listFiles()) {
					String name = file.getName();
					if (name.endsWith(".yml")) {
						name = name.replace(".yml", "");
						if (!Config.getRecipeKeys().contains(name)) {
							list.add(name);
						}
					}
				}
			}

		}

		for (int i = list.size() - 1; i >= 0; i--)
			if(list.get(i).startsWith(args[args.length - 1]))
				tabSuggest.add(list.get(i));

		Collections.sort(tabSuggest);
		return tabSuggest;
	}
	
}
