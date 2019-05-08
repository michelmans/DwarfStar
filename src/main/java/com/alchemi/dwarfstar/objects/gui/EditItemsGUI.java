package com.alchemi.dwarfstar.objects.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.objects.ItemFactory;
import com.alchemi.al.objects.SexyRunnable;
import com.alchemi.al.objects.GUI.GUIBase;
import com.alchemi.dwarfstar.main;
import com.alchemi.dwarfstar.objects.SmeltRecipe;

public class EditItemsGUI extends GUIBase{

	protected final List<ItemStack> items;
	protected final SmeltRecipe recipe;
	protected final EDITTYPE type;
	 
	public EditItemsGUI(OfflinePlayer player, CommandSender sender, SmeltRecipe smeltRecipe, EDITTYPE type) {
		super(main.instance, Messenger.cc("&1Item editing"), 27, player, sender);
		new CustomGUIListener(plugin, this);
		
		this.type = type;
		this.recipe = smeltRecipe;
		
		switch(type) {
		case INPUT:
			this.items = smeltRecipe.getInput().stream().map(Material -> new ItemStack(Material)).collect(Collectors.toList());
			break;
		case OUTPUT:
			this.items = smeltRecipe.getOutput();
			break;
		default:
			this.items = null;
			break;
		}
		
		setContents();
		setCommands();
		openGUI(player.getPlayer());
		
	}

	@Override
	public void setContents() {
		
		for (int i : Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,19,20,21,22,23,24,25,26)) {
			contents.put(i, new ItemFactory(Material.BLACK_STAINED_GLASS_PANE).setName("&1&oItem editing!"));
		}
		
		Iterator<ItemStack> iter = items.iterator();
		for (int i = 0; i < guiSize; i++) {
			
			if (!contents.containsKey(i) && iter.hasNext()) contents.put(i, new ItemFactory(iter.next()).setLore(Arrays.asList("", "Click to remove")));
			else if (!iter.hasNext()) break;
			
		}
		
	}

	@Override
	public void setCommands() {
		
		for (int i = 0; i < guiSize; i++) {
			
			if (contents.containsKey(i) 
					&& !contents.get(i).isSimilar(new ItemFactory(Material.BLACK_STAINED_GLASS_PANE).setName("&1&oItem editing!"))) {
				commands.put(i, new SexyRunnable() {
					
					@SuppressWarnings("unlikely-arg-type")
					@Override
					public void run(Object... args) {
						
						contents.remove((Integer)args[0]);
						gui.setItem((Integer)args[0], null);
						items.remove((Integer)args[0]);
						commands.remove((Integer)args[0]);
						arguments.remove((Integer)args[0]);
						
					}
				});
				
				putArgument(i, i);
			}
			
		} 
		
	}

	@Override
	public void onClose() {
		
		switch(type) {
		case INPUT:
			recipe.setInput(items.stream().map(ItemStack::getType).collect(Collectors.toList()));
			break;
		case OUTPUT:
			recipe.setOutput(items);
			break;
		}
		new RecipeGUI(player, sender, recipe);
		
	}
	
	public void onOutsideClick(int slot, InventoryView view) {
		
		if (slot >= guiSize && 
				!(view.getItem(slot) == null || view.getItem(slot).getType() == Material.AIR)) {
			items.add(view.getItem(slot).clone());
			System.out.println(items);
			
			int newSlot = gui.firstEmpty();
			ItemStack item = new ItemFactory(view.getItem(slot).clone()).setLore(Arrays.asList("", "Click to remove"));
			contents.put(newSlot, item);
			gui.setItem(newSlot, item);
			player.getPlayer().updateInventory();
			commands.put(newSlot, new SexyRunnable() {
					
					@SuppressWarnings("unlikely-arg-type")
					@Override
					public void run(Object... args) {
						
						contents.remove((Integer)args[0]);
						gui.setItem((Integer)args[0], null);
						items.remove((Integer)args[0]);
						commands.remove((Integer)args[0]);
						arguments.remove((Integer)args[0]);
						
					}
			});
			
			putArgument(newSlot, newSlot);
		}
		
	}
	
	@Override
	public void onClicked(int slot, Player pl, ClickType click)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		super.onClicked(slot, pl, click);
	}

	/**
	 * @return the type
	 */
	public EDITTYPE getType() {
		return type;
	}

	public enum EDITTYPE{
		INPUT, OUTPUT;
	}
	
}
