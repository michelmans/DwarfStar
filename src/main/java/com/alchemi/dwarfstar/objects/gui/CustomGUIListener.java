package com.alchemi.dwarfstar.objects.gui;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.objects.GUI.GUIListener2;
import com.alchemi.dwarfstar.main;

public class CustomGUIListener extends GUIListener2 {

	protected EditItemsGUI gui;
	
	public CustomGUIListener(JavaPlugin plugin, EditItemsGUI gui) {
		super(plugin, gui);
		this.gui = gui;
	}
	
	@Override
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		
		main.messenger.print(e.getRawSlot());
		
		if (!player.equals(gui.getPlayer().getPlayer()) 
				&& !(gui.getSender() instanceof Player && player.equals((Player)gui.getSender()))) return;
		
		if (e.getSlotType() != SlotType.OUTSIDE 
				&& e.getSlotType() != SlotType.QUICKBAR
				&& e.getRawSlot() >= 0 && e.getRawSlot() < gui.getGuiSize()) {
			
			e.setCancelled(true);
			
			try {
				gui.onClicked(e.getSlot(), player, e.getClick());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| InstantiationException e1) {}
		} else if (e.getSlotType() != SlotType.OUTSIDE) {
			
			gui.onOutsideClick(e.getRawSlot(), e.getView());
			e.setCancelled(true);
			
		}
	}

}
