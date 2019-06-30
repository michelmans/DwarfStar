package me.alchemi.dwarfstar.objects.gui;

import java.util.Arrays;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.GUI.GUIBase;
import me.alchemi.al.objects.GUI.GUIListener;
import me.alchemi.al.objects.handling.ItemFactory;
import me.alchemi.al.objects.handling.SexyRunnable;
import me.alchemi.dwarfstar.main;
import me.alchemi.dwarfstar.objects.SmeltRecipeBuilder;
import me.alchemi.dwarfstar.objects.gui.EditItemsGUI.EDITTYPE;

public class RecipeGUI extends GUIBase{

	private SmeltRecipeBuilder recipeBuilder;
	private ListIterator<Material> inputIter;
	private ListIterator<ItemStack> outputIter;
	
	private BukkitTask inputTask;
	private BukkitTask outputTask;
	
	private boolean keepOpen = false;
	
	public RecipeGUI(Player player, Player sender, SmeltRecipeBuilder recipeBuilder) {
		super(main.getInstance(), Messenger.formatString("&8Recipe: &9" + recipeBuilder.getKey()), 9, player, sender);
		new GUIListener(plugin, this);
		
		this.recipeBuilder = recipeBuilder;
		
		setContents();
		setCommands();
		openGUI();
	}

	@Override
	public void setContents() {
		
		if (recipeBuilder.getInput().getChoices().size() == 1) {
			contents.put(2, new ItemFactory(recipeBuilder.getInput().getItemStack())
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Input items"));
		} else {
			contents.put(2, new ItemFactory(recipeBuilder.getInput().getItemStack())
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Input items"));
			
			inputIter = recipeBuilder.getInput().getChoices().listIterator();
			
			inputTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				
				@Override
				public void run() {
					if (!inputIter.hasNext()) inputIter = recipeBuilder.getInput().getChoices().listIterator();
					ItemStack cont = gui.getItem(2);
					Material next = inputIter.next();
					if (cont == null || next == null) return;
					
					cont.setType(next);
					gui.setItem(2, cont);
					
					
				}
			}, 0, 20);
		}
		
		contents.put(4, new ItemFactory(MaterialWrapper.COAL_BLOCK.getMaterial()).setName(" "));
		if (recipeBuilder.getOutput().length == 0) {
			contents.put(6, new ItemFactory(MaterialWrapper.BARRIER.getMaterial())
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Output items"));
		}
		else if (recipeBuilder.getOutput().length == 1) {
			ItemStack out = recipeBuilder.getOutput()[0];
			if (out == null || out.equals(new ItemStack(MaterialWrapper.AIR.getMaterial()))) {
				out = new ItemStack(MaterialWrapper.BARRIER.getMaterial());
			}
			contents.put(6, new ItemFactory(out)
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Output items"));
		} else {
			contents.put(6, new ItemFactory(recipeBuilder.getOutput()[0])
					.setLore(Arrays.asList("&6Click to edit")).setName("&9Output items"));

			outputIter = Arrays.asList(recipeBuilder.getOutput()).listIterator();
			outputTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				
				@Override
				public void run() {
					if (!outputIter.hasNext()) outputIter = Arrays.asList(recipeBuilder.getOutput()).listIterator();
					if (gui.getItem(6) == null) return;
					
					ItemFactory cont = new ItemFactory(gui.getItem(6));
					ItemFactory next = new ItemFactory(outputIter.next());
					
					gui.setItem(6, next.setLore(cont.getLore()));
					
				}
			}, 0, 20);
		}
		
		for (int i = 0; i < 9; i++) {
			
			if (!contents.containsKey(i)) contents.put(i, new ItemStack(MaterialWrapper.BLACK_STAINED_GLASS_PANE.getMaterial()));
			
		}
		
	}

	@Override
	public void setCommands() {
		commands.put(2, new SexyRunnable() {
			
			@Override
			public void run(Object... args) {
				
				keepOpen = true;
				sender.closeInventory();
				new EditItemsGUI(owningPlayer, sender, recipeBuilder, EDITTYPE.INPUT);
				
			}
		});
		
		commands.put(6, new SexyRunnable() {
			
			@Override
			public void run(Object... args) {
				
				keepOpen = true;
				sender.closeInventory();
				new EditItemsGUI(owningPlayer, sender, recipeBuilder, EDITTYPE.OUTPUT);
				
			}
		});
		
	}

	@Override
	public void onClose() {
		if (inputTask != null) inputTask.cancel();
		if (outputTask != null) outputTask.cancel();
		
		if (!keepOpen) recipeBuilder.register(true).create().save();
	}
	
}
