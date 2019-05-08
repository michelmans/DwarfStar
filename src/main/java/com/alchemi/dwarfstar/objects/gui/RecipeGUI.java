package com.alchemi.dwarfstar.objects.gui;

import java.util.Arrays;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.objects.ItemFactory;
import com.alchemi.al.objects.SexyRunnable;
import com.alchemi.al.objects.GUI.GUIBase;
import com.alchemi.al.objects.GUI.GUIListener2;
import com.alchemi.dwarfstar.main;
import com.alchemi.dwarfstar.objects.SmeltRecipe;
import com.alchemi.dwarfstar.objects.gui.EditItemsGUI.EDITTYPE;

public class RecipeGUI extends GUIBase{

	private SmeltRecipe recipe;
	private ListIterator<Material> iter;
	private ListIterator<ItemStack> iterV2;
	
	private BukkitTask task;
	private BukkitTask task2;
	
	public RecipeGUI(OfflinePlayer player, CommandSender sender, SmeltRecipe recipe) {
		super(main.instance, Messenger.cc("&8Recipe editor"), 9, player, sender);
		new GUIListener2(plugin, this);
		
		this.recipe = recipe;
		
		main.messenger.broadcast(recipe.getOutput().toString());
		
		setContents();
		setCommands();
		openGUI(player.getPlayer());
	}

	@Override
	public void setContents() {
		
		if (recipe.getInput().size() == 1) {
			contents.put(2, new ItemFactory(recipe.getInput().get(0))
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Input items"));
		} else {
			contents.put(2, new ItemFactory(recipe.getInput().get(0))
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Input items"));
			
			iter = recipe.getInput().listIterator();
			
			task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				
				@Override
				public void run() {
					if (!iter.hasNext()) iter = recipe.getInput().listIterator();
					ItemStack cont = gui.getItem(2);
					Material next = iter.next();
					if (cont == null || next == null) return;
					
					cont.setType(next);
					gui.setItem(2, cont);
					
					
				}
			}, 0, 20);
		}
		
		contents.put(4, new ItemFactory(Material.COAL_BLOCK).setName(" "));
		if (recipe.getOutput().size() == 0) {
			contents.put(6, new ItemFactory(Material.BARRIER)
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Output items"));
		}
		else if (recipe.getOutput().size() == 1) {
			ItemStack out = recipe.getOutput().get(0);
			if (out == null || out.equals(new ItemStack(Material.AIR))) {
				out = new ItemStack(Material.BARRIER);
			}
			contents.put(6, new ItemFactory(out)
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Output items"));
		} else {
			contents.put(6, new ItemFactory(recipe.getOutput().get(0))
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Output items"));

			iterV2 = recipe.getOutput().listIterator();
			task2 = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				
				@Override
				public void run() {
					if (!iterV2.hasNext()) iterV2 = recipe.getOutput().listIterator();
					if (gui.getItem(6) == null) return;
					
					ItemFactory cont = new ItemFactory(gui.getItem(6));
					ItemFactory next = new ItemFactory(iterV2.next());
					
					gui.setItem(6, next.setLore(cont.getLore()));
					
				}
			}, 0, 20);
		}
		
		for (int i = 0; i < 9; i++) {
			
			if (!contents.containsKey(i)) contents.put(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
			
		}
		
	}

	@Override
	public void setCommands() {
		commands.put(2, new SexyRunnable() {
			
			@Override
			public void run(Object... args) {
				
				player.getPlayer().closeInventory();
				new EditItemsGUI(player, sender, recipe, EDITTYPE.INPUT);
				
			}
		});
		
		commands.put(6, new SexyRunnable() {
			
			@Override
			public void run(Object... args) {
				
				player.getPlayer().closeInventory();
				new EditItemsGUI(player, sender, recipe, EDITTYPE.OUTPUT);
				
			}
		});
		
	}

	@Override
	public void onClose() {
		if (task != null) task.cancel();
		if (task2 != null) task2.cancel();
	}
	
}
