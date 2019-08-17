package me.alchemi.dwarfstar.listeners.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.Library;
import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.dwarfstar.Config;
import me.alchemi.dwarfstar.Config.Messages;
import me.alchemi.dwarfstar.Config.Options;
import me.alchemi.dwarfstar.objects.events.SmeltEvent;

public class SmeltCommand implements CommandExecutor {

	protected enum PROCESSTYPE{
	
		HAND, INVENTORY, AMOUNT;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		return perform(sender, command, args);
	
	}
	
	public boolean perform(CommandSender sender, Command command, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (player.hasPermission(command.getPermission())) {
				
				if (args.length > 1) {
					if (args[1].startsWith("{") && args[1].endsWith("}")) {
						
						RESULT res = parseArg(args[1]);
						
						if (res instanceof EXCLUSION) {
							if (args[0].equals("hand")) {
								
								process(PROCESSTYPE.HAND, player, 0, (EXCLUSION) res, null);
								
								return true;
								
							} else if (args[0].equals("all") || args[0].equals("inventory") || args[0].equals("inv")) {
								
								process(PROCESSTYPE.INVENTORY, player, 0, (EXCLUSION) res, null);
								
								return true;
							} else if (Library.testIfNumber(args[0])) {
							
								process(PROCESSTYPE.AMOUNT, player, Integer.valueOf(args[0]), (EXCLUSION) res, null);
								
								return true;
							}
						} else if (res instanceof INCLUSION) {
							if (args[0].equals("hand")) {
								
								process(PROCESSTYPE.HAND, player, 0, null, (INCLUSION) res);
								
								return true;
								
							} else if (args[0].equals("all") || args[0].equals("inventory") || args[0].equals("inv")) {
								
								process(PROCESSTYPE.INVENTORY, player, 0, null, (INCLUSION) res);
								
								return true;
							} else if (Library.testIfNumber(args[0])) {
							
								process(PROCESSTYPE.AMOUNT, player, Integer.valueOf(args[0]), null, (INCLUSION) res);
								
								return true;
							}
						}
						
					}
					return true;
				} else if (args.length == 1) {
					if (args[0].equals("hand")) {
						
						process(PROCESSTYPE.HAND, player);
						
						return true;
						
					} else if (args[0].equals("all") || args[0].equals("inventory") || args[0].equals("inv")) {
						
						process(PROCESSTYPE.INVENTORY, player);
						
						return true;
					} else if (Library.testIfNumber(args[0])) {
					
						process(PROCESSTYPE.AMOUNT, player, Integer.valueOf(args[0]), null, null);
						
						return true;
					}
				} else if (args.length == 0) {
					
					process(PROCESSTYPE.valueOf(Options.defaultSmeltType.asString()), player);
					
					return true;
				}
				
			}
			player.sendMessage(Messenger.formatString(Messages.COMMANDS_NO_PERMISSION.value()
					.replace("$sender$", command.getLabel())));
			
		}
		
		return true;
	}
	
	protected void process(PROCESSTYPE type, Player player) { process(type, player, 0, null, null); }
	
	protected void process(PROCESSTYPE type, Player player, int amount, EXCLUSION ex, INCLUSION in) {
		if (type.equals(PROCESSTYPE.HAND)) {
			
			ItemStack hand = player.getInventory().getItemInMainHand();
			Material itemType = MaterialWrapper.getFromItemStack(hand);
			
			if (Config.hasRecipe(itemType)
					&& (in == null || in.getMaterialList().contains(itemType))
					&& (ex == null || !ex.getMaterialList().contains(itemType))) {
				Bukkit.getPluginManager().callEvent(new SmeltEvent(Config.getRecipe(itemType), player, hand.getAmount()));
			} else {
				player.sendMessage(Messenger.formatString(Messages.NO_MELTING.value()));
			}
			return;
			
		} else if (type.equals(PROCESSTYPE.INVENTORY)) {
			
			int i = 0;
			for (ItemStack item : player.getInventory().getContents()) {
				if (item == null) continue;
				Material itemType = MaterialWrapper.getFromItemStack(item);
				if (!Arrays.asList(player.getInventory().getArmorContents()).contains(item) 
						&& Config.hasRecipe(itemType)
						&& (in == null || in.getMaterialList().contains(itemType))
						&& (ex == null || !ex.getMaterialList().contains(itemType))) {
					Bukkit.getPluginManager().callEvent(new SmeltEvent(Config.getRecipe(itemType), player, item.getAmount()));
					i++;
				}
			}
			if (i == 0) {
				 player.sendMessage(Messenger.formatString(Messages.NO_MELTING.value()));
			}
			return;
			
		} else if (type.equals(PROCESSTYPE.AMOUNT)) {
			
			ItemStack hand = player.getInventory().getItemInMainHand().clone();
			Material itemType = MaterialWrapper.getFromItemStack(hand);
			
			if (Config.hasRecipe(itemType) 
					&& player.getInventory().contains(itemType, amount)
					&& (in == null || in.getMaterialList().contains(itemType))
					&& (ex == null || !ex.getMaterialList().contains(itemType))) {
				Bukkit.getPluginManager().callEvent(new SmeltEvent(Config.getRecipe(itemType), player, amount));
			} else {
				player.sendMessage(Messenger.formatString(Messages.NO_MELTING.value()));
			}
			return;
			
		}
	}
	
	protected abstract class RESULT {
		
		private List<Material> mats;
		
		public RESULT(List<Material> mats) {
			this.mats = mats;
		}
		
		List<Material> getMaterialList() {
			return mats;
		}
		
	}
	
	protected class INCLUSION extends RESULT {
		
		public INCLUSION(List<Material> mats) {
			super(mats);
		}
		
	}
	
	protected class EXCLUSION extends RESULT {
		
		public EXCLUSION(List<Material> mats) {
			super(mats);
		}
		
	}

	protected RESULT parseArg(String arg) {
		if (arg.contains("exclude")) {
			arg = arg.replaceFirst("(^.+:)", "{");
			Matcher m = Pattern.compile("([A-z])\\w+").matcher(arg);

			List<Material> mats = new ArrayList<Material>();
			while (m.find()) {
				Material mat = Material.getMaterial(m.group().toUpperCase());
				if (mat == null) {
					throw new IllegalArgumentException(m.group() + " is not a material!");
				}
				mats.add(mat);
			}
			return new EXCLUSION(mats);
		} else if (arg.contains("include")) {
			arg = arg.replaceFirst("(^.+:)", "{");
			Matcher m = Pattern.compile("([A-z])\\w+").matcher(arg);

			List<Material> mats = new ArrayList<Material>();
			while (m.find()) {
				Material mat = Material.getMaterial(m.group().toUpperCase());
				if (mat == null) {
					throw new IllegalArgumentException(m.group() + " is not a material!");
				}
				mats.add(mat);
			}
			return new INCLUSION(mats);
		}
		throw new IllegalArgumentException("You must specify either include or exclude");
	}
	
}
