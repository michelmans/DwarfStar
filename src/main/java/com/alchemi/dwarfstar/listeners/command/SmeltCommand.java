package com.alchemi.dwarfstar.listeners.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.Library;
import com.alchemi.dwarfstar.Config;
import com.alchemi.dwarfstar.objects.events.SmeltEvent;

public class SmeltCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		return perform(sender, command, args);
		
	}
	
	public boolean perform(CommandSender sender, Command command, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (Library.hasPermission(player, command.getPermission())) {
				
				if (args.length >= 1) {
					if (args[0].equals("hand")) {
						ItemStack hand = player.getInventory().getItemInMainHand();
						if (Config.hasRecipe(hand.getType())) {
							Config.getRecipe(hand.getType()).smelt(player, hand.getAmount());
						}
						return true;
					} else if (args[0].equals("all") || args[0].equals("inventory") || args[0].equals("inv")) {
						
						for (ItemStack item : player.getInventory().getContents()) {
							if (!Arrays.asList(player.getInventory().getArmorContents()).contains(item) && Config.hasRecipe(item.getType())) {
								Bukkit.getPluginManager().callEvent(new SmeltEvent(Config.getRecipe(item.getType()), player, item.getAmount()));
							}
						}
						
						return true;
					} else if (Library.testIfInt(args[0])) {
					
						ItemStack hand = player.getInventory().getItemInMainHand().clone();
						int amount = Integer.valueOf(args[0]);
						
						if (Config.hasRecipe(hand.getType()) && player.getInventory().contains(hand.getType(), amount)) {
							Bukkit.getPluginManager().callEvent(new SmeltEvent(Config.getRecipe(hand.getType()), player, amount));
						}
						
						return true;
					}
				}
				
			}
			
		}
		
		return true;
	}

}
