package me.alchemi.dwarfstar.listeners.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import me.alchemi.al.Library;
import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.objects.handling.InputRequest;
import me.alchemi.al.objects.handling.InputRequest.InputReceivedEvent;
import me.alchemi.al.objects.handling.InputRequest.RequestOption;
import me.alchemi.dwarfstar.Config;
import me.alchemi.dwarfstar.Config.Messages;
import me.alchemi.dwarfstar.Config.Options;
import me.alchemi.dwarfstar.Star;
import me.alchemi.dwarfstar.objects.SmeltRecipe;
import me.alchemi.dwarfstar.objects.SmeltRecipeBuilder;
import me.alchemi.dwarfstar.objects.gui.RecipeGUI;

public class DwarfCommand implements CommandExecutor {

	public static final List<String> createAliases = Arrays.asList("create", "createrecipe", "c", "cr");
	public static final List<String> deleteAliases = Arrays.asList("delete", "deleterecipe", "d", "dr", "del");
	public static final List<String> enableAliases = Arrays.asList("enable", "ea", "enablerecipe", "load");
	public static final List<String> disableAliases = Arrays.asList("disable", "da", "disablerecipe", "unload");
	public static final List<String> modifyAliases = Arrays.asList("modify", "edit", "m", "mod", "e");
	public static final List<String> defaultAliases = Arrays.asList("regenerate", "default", "defaults");
	public static final List<String> listAliases = Arrays.asList("list", "listrecipes", "l", "lr");
	public static final List<String> reloadAliases = Arrays.asList("reload", "r");
	
	final String createUsage = "&9/dwarfstar create <recipe_name>";
	final String deleteUsage = "&9/dwarfstar delete <recipe_name>";
	final String modifyUsage = "&9/dwarfstar modify <recipe_name>";
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			
			if (args.length >= 1) {
				if (createAliases.contains(args[0])) {
					
					create(sender, args);
					
				} else if (deleteAliases.contains(args[0])) {
					
					delete(sender, args);
					
				} else if (enableAliases.contains(args[0])) {
					
					enable(sender, args);
					
				} else if (disableAliases.contains(args[0])) {
					
					disable(sender, args);
					
				} else if (modifyAliases.contains(args[0])) {
					
					modify(sender, args);
					
				} else if (defaultAliases.contains(args[0])) {
					
					defaults(sender, args);
					
				} else if (listAliases.contains(args[0])) {
					
					list(sender, args);
					
				} else if (reloadAliases.contains(args[0])) {
					
					Star.getInstance().config.reload();
					
				}
			
			} else {
				
			}
		}
		return true;
	}
	
	private void create(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Material mainType = MaterialWrapper.getWrapper(player.getInventory().getItemInMainHand());
		
		if (mainType != null
				&& mainType != Material.AIR) {
			SmeltRecipeBuilder recipeBuilder = new SmeltRecipeBuilder()
					.input(new MaterialChoice(mainType));
			if (args.length > 1) {
				if (Config.hasRecipe(args[1]) || Config.hasRecipe(recipeBuilder.getKey())) {
					new InputRequest(Star.getInstance(), player, RequestOption.valueOf(Options.REQUEST_OPTION.asString()), Messages.COMMANDS_RECIPE_BAD_NAME.value());
					new InputListener(recipeBuilder, player);
					return;
				}
				recipeBuilder.key(args[1]);
			}
			
			if (Config.possibleMaterials().contains(mainType)) {
				Star.getInstance().getMessenger().sendMessage(Messages.COMMANDS_RECIPE_ERROR.value()
						.replace("$name$", recipeBuilder.getKey()), player);
				Star.getInstance().getMessenger().sendMessage(Messages.COMMANDS_RECIPE_MATERIAL_HAS_RECIPE.value(), player);
				return;
			}
			new RecipeGUI(player, player, recipeBuilder);
		}
	}
	
	private void delete(CommandSender sender, String[] args) {
		if (args.length > 1) {
			
			try {
				Star.getInstance().getMessenger().sendMessage(Messages.COMMANDS_RECIPE_DELETED.value()
						.replace("$name$", Config.getRecipe(args[1]).delete().getKey()), sender);
			} catch(IllegalArgumentException e) {
				Star.getInstance().getMessenger().sendMessage(e.getMessage(), sender);
			}
			
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			
			try{
				Star.getInstance().getMessenger().sendMessage(Messages.COMMANDS_RECIPE_DELETED.value()
						.replace("$name$", Config.getRecipe(player.getInventory().getItemInMainHand().getType()).delete().getKey()), sender);
			} catch(IllegalArgumentException e) {
				Star.getInstance().getMessenger().sendMessage(e.getMessage(), sender);
			}
					
				
		}
	}
	
	private void enable(CommandSender sender, String[] args) {

		if (args.length > 1) {
			
			try {
				Star.getInstance().getMessenger().sendMessage(Messages.COMMANDS_RECIPE_ENABLED.value()
						.replace("$name$", SmeltRecipe.load(args[1]).getKey()), sender);
			} catch(IllegalArgumentException e) {
				Star.getInstance().getMessenger().sendMessage(e.getMessage(), sender);
			}
			
		}
	}
	
	private void disable(CommandSender sender, String[] args) {
		
		if (args.length > 1) {
			
			try {
				Star.getInstance().getMessenger().sendMessage(Messages.COMMANDS_RECIPE_DISABLED.value()
						.replace("$name$", Config.getRecipe(args[1]).unload().getKey()), sender);
			} catch(IllegalArgumentException e) {
				Star.getInstance().getMessenger().sendMessage(e.getMessage(), sender);
			}
			
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			
			try{
				Star.getInstance().getMessenger().sendMessage(Messages.COMMANDS_RECIPE_DISABLED.value()
						.replace("$name$", Config.getRecipe(player.getInventory().getItemInMainHand().getType()).unload().getKey()), sender);
			} catch(IllegalArgumentException e) {
				Star.getInstance().getMessenger().sendMessage(e.getMessage(), sender);
			}
					
				
		}
		
	}
	
	private void modify(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if (args.length > 1) {
			
			try {
				
				new RecipeGUI(player, player, SmeltRecipeBuilder.fromRecipe(Config.getRecipe(args[1]).modify()));
				
			} catch(IllegalArgumentException e) {
				Star.getInstance().getMessenger().sendMessage(e.getMessage(), sender);
			}
			
		} else {
			
			try{
				new RecipeGUI(player, player, SmeltRecipeBuilder.fromRecipe(Config.getRecipe(player.getInventory().getItemInMainHand().getType()).modify()));
			} catch(IllegalArgumentException e) {
				Star.getInstance().getMessenger().sendMessage(e.getMessage(), sender);
			}
					
				
		}
	}
	
	private void defaults(CommandSender sender, String[] args) {
		Config.regenerateRecipes();
	}
	
	private void list(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Star.getInstance().getMessenger().sendMessages(player, Library.asSortedList(Config.getRecipeKeys()).toArray(new String[Config.getRecipeCount()]));
	}

	private class InputListener implements Listener{
		
		private SmeltRecipeBuilder recipeBuilder;
		private Player player;
		
		public InputListener(SmeltRecipeBuilder recipeBuilder, Player player) {
			this.recipeBuilder = recipeBuilder;
			this.player = player;
			Bukkit.getPluginManager().registerEvents(this, Star.getInstance());
		}
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onEvent(InputReceivedEvent e) {
			if (e.getPlayer().equals(player)) {
				recipeBuilder.key(e.getInput());
				HandlerList.unregisterAll(this);
				new RecipeGUI(player, player, recipeBuilder);
			}
		}
	}
	
}
