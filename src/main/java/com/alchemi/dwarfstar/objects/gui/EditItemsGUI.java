package com.alchemi.dwarfstar.objects.gui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.objects.ItemFactory;
import com.alchemi.al.objects.SexyRunnable;
import com.alchemi.al.objects.GUI.GUIBase;
import com.alchemi.al.objects.GUI.GUIListener2;
import com.alchemi.dwarfstar.main;
import com.alchemi.dwarfstar.objects.SmeltRecipe;
import com.alchemi.dwarfstar.objects.events.ItemsEditEvent;

public class EditItemsGUI extends GUIBase{

	private final List<ItemStack> items;
	private final UUID parent;
	private final SmeltRecipe smeltRecipe;
	 
	public EditItemsGUI(OfflinePlayer player, CommandSender sender, UUID parent, SmeltRecipe smeltRecipe, List<ItemStack> items) {
		super(main.instance, Messenger.cc("&1Item editing"), 27, player, sender);
		new GUIListener2(plugin, this);
		
		this.items = items;
		this.parent = parent;
		this.smeltRecipe = smeltRecipe;
		
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
					
					@Override
					public void run(Object... args) {
						
						contents.remove((Integer)args[0]);
						gui.setItem((Integer)args[0], null);
						
					}
				});
				
				putArgument(i, i);
			}
			
		} 
		
	}

	@Override
	public void onClose() {
		
		new RecipeGUI(player, sender, smeltRecipe);
		Bukkit.getPluginManager().callEvent(new ItemsEditEvent(parent, items, Arrays.asList(gui.getContents()).stream()
				.filter(ItemStack -> ItemStack != null && !ItemStack.getType().equals(Material.BLACK_STAINED_GLASS_PANE))
				.collect(Collectors.toList())));
		
	}

	
	
}
