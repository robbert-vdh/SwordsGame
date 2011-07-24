package me.coolblinger.swordsgame.listeners;

import me.coolblinger.swordsgame.SwordsGame;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class SwordsGameEntityListener extends EntityListener {
	private SwordsGame plugin;

	public SwordsGameEntityListener(SwordsGame instance) {
		plugin = instance;
	}

	public void onEntityDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
			if (edbeEvent.getEntity() instanceof HumanEntity && edbeEvent.getDamager() instanceof HumanEntity) {
				Entity damaged = edbeEvent.getEntity();
				Entity damager = edbeEvent.getDamager();
				if (plugin.players.containsKey((Player) damaged) && !plugin.players.containsKey((Player) damager)) {
					event.setCancelled(true);
				}
				if (plugin.players.containsKey((Player) damaged) && !plugin.players.containsKey((Player) damager)) {
					event.setCancelled(true);
				}
				if (plugin.players.containsKey(damaged) && plugin.players.containsKey(damager)) {
					if (plugin.players.get(damaged).arena == plugin.players.get(damager).arena) {
						if (((Player) damaged).getHealth() - edbeEvent.getDamage() < 1) {
							event.setDamage(0);
							plugin.games.get(plugin.players.get(damaged).arena).kill((Player) damager, (Player) damaged);
						}
					}
				}
			}
		}
	}
}