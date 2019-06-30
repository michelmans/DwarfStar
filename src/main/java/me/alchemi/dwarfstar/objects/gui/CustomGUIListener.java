package me.alchemi.dwarfstar.objects.gui;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.plugin.java.JavaPlugin;

import me.alchemi.al.objects.GUI.GUIListener;

public class CustomGUIListener extends GUIListener {

	protected EditItemsGUI gui;
	
	public CustomGUIListener(JavaPlugin plugin, EditItemsGUI gui) {
		super(plugin, gui);
		this.gui = gui;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		
		if (!player.equals(gui.getPlayer().getPlayer()) 
				&& !(gui.getSender() instanceof Player && player.equals((Player)gui.getSender()))) return;
		
		if (e.getSlotType() != SlotType.OUTSIDE 
				&& e.getRawSlot() >= 0 && e.getRawSlot() < gui.getGuiSize()) {
			
			e.setCancelled(true);
			
			try {
				gui.onClicked(e);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| InstantiationException e1) {}
			
		} else if (e.getSlotType() != SlotType.OUTSIDE) {
			gui.onOutsideClick(e);
			e.setCancelled(true);
			
		}
		
		if (e.getCursor() != null && e.isCancelled()) {
			if (e.getClickedInventory().getItem(e.getSlot()) == null) {
				e.getClickedInventory().setItem(e.getSlot(), e.getCursor());
				e.setCursor(null);
				((Player)e.getWhoClicked()).updateInventory();
			}
		}
	}

}
