package com.alchemi.dwarfstar.listeners.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.alchemi.dwarfstar.objects.events.SmeltEvent;

public class MainListener implements Listener{

	@EventHandler
	public void onSmelt(SmeltEvent e) {
		if (!e.isCancelled()) e.getRecipe().smelt(e.getPlayer(), e.getSmeltTimes());
	}
	
}
