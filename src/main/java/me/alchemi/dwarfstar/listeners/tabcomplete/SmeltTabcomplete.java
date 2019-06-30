package me.alchemi.dwarfstar.listeners.tabcomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.alchemi.al.api.MaterialWrapper;

public class SmeltTabcomplete implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> tabSuggest = new ArrayList<>();
		List<String> list = new ArrayList<>();
		
		if (!(sender instanceof Player && sender.hasPermission(command.getPermission())))
			return tabSuggest;
		
		Player player = (Player) sender;
		
		if (args.length == 1) {
			
			list.add("hand");
			list.add("inventory");
			list.add("64");
			if (player.getInventory().getItemInMainHand().getType() != MaterialWrapper.AIR.getMaterial()) {
				list.add(String.valueOf(player.getInventory().getItemInMainHand().getAmount()));
			}
				
		}

		for (int i = list.size() - 1; i >= 0; i--)
			if(list.get(i).startsWith(args[args.length - 1]))
				tabSuggest.add(list.get(i));

		Collections.sort(tabSuggest);
		return tabSuggest;
	}
	
}
