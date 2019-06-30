package me.alchemi.dwarfstar.objects.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.GUI.GUIBase;
import me.alchemi.al.objects.handling.ItemFactory;
import me.alchemi.al.objects.handling.SexyRunnable;
import me.alchemi.dwarfstar.main;
import me.alchemi.dwarfstar.objects.SmeltRecipeBuilder;

public class EditItemsGUI extends GUIBase{

	protected List<ItemStack> items = new ArrayList<ItemStack>();
	protected SmeltRecipeBuilder recipeBuilder;
	protected final EDITTYPE type;
	 
	public EditItemsGUI(Player player, Player sender, SmeltRecipeBuilder recipeBuilder, EDITTYPE type) {
		super(main.getInstance(), Messenger.formatString("&1Item editing"), 27, player, sender);
		new CustomGUIListener(plugin, this);
		
		this.type = type;
		this.recipeBuilder = recipeBuilder;
		
		switch(type) {
		case INPUT:
			this.items.addAll(recipeBuilder.getInput().getChoices().stream().map(Material -> new ItemStack(Material)).collect(Collectors.toList()));
			break;
		case OUTPUT:
			this.items.addAll(Arrays.asList(recipeBuilder.getOutput()));
			break;
		default:
			this.items = new ArrayList<ItemStack>();
			break;
		}
		
		setContents();
		setCommands();
		openGUI();
		
	}

	@Override
	public void setContents() {
		
		contents.clear();
		Iterator<ItemStack> iter = items.iterator();
		System.out.println(items);
		for (int i = 0; i < guiSize; i++) {
			
			if (!contents.containsKey(i) && iter.hasNext()) contents.put(i, new ItemFactory(iter.next()).setLore(Arrays.asList("", "Click to remove")));
			else if (!iter.hasNext()) break;
			
		}
		
	}

	@Override
	public void setCommands() {
		
		commands.clear();
		for (int i = 0; i < guiSize; i++) {
			
			if (contents.containsKey(i)) {
				commands.put(i, new SexyRunnable() {
					
					@Override
					public void run(Object... args) {
						
						items.remove((int)args[0]);
						setContents();
						setCommands();
						updateGUI();
						
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
			recipeBuilder.input(new MaterialChoice(items.stream()
					.map(ItemStack -> MaterialWrapper.getFromItemStack(ItemStack))
					.collect(Collectors.toList())));
			break;
		case OUTPUT:
			recipeBuilder.output(items);
			break;
		}
		new RecipeGUI(owningPlayer, sender, recipeBuilder);
		
	}
	
	@Override
	public void onOutsideClick(InventoryClickEvent e) {
		
		Inventory view = e.getClickedInventory();
		int slot = e.getSlot();
		
		if (view.getItem(slot) != null 
				&& view.getItem(slot).getType() != MaterialWrapper.AIR.getMaterial()) {
			
			int s = getItem(view.getItem(slot).clone());
			ItemStack item = view.getItem(slot).clone();
			
			if (s > -1 && e.isShiftClick()) {
				item = items.get(s).clone();
				item.setAmount(item.getAmount() + view.getItem(slot).getAmount());
				items.remove(s);
			} else if (s > -1) {
				item = items.get(s).clone();
				item.setAmount(item.getAmount() + 1);
				items.remove(s);
			} else if (!e.isShiftClick()) {
				item.setAmount(1);
			}
			
			items.add(item);
			
			setContents();
			setCommands();
			updateGUI();
		}
		
	}
	
	public int getItem(ItemStack item) {
		for (ItemStack slot : items) {
			if (slot.isSimilar(item)) {
				return items.indexOf(slot);
			}
		}
		
		return -1;
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
