package org.cubeville.cvracing;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.RaceSign;

import java.util.UUID;

public class EventHandlers implements Listener {

	JavaPlugin plugin;
	long countdownFreeze;

	public EventHandlers(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null &&
			event.getAction() == Action.RIGHT_CLICK_BLOCK &&
			SignManager.signMaterials.contains(event.getClickedBlock().getType())
		) {
			RaceSign sign = SignManager.getSign(event.getClickedBlock().getLocation());
			if (sign == null) {
				return;
			}
			sign.onRightClick(event.getPlayer());
		}
	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) {
		if (RaceManager.checkpointTriggerTypes.contains(event.getBlock().getType())) {
			if (RaceManager.racingVehicles.contains(event.getEntityType())) {
				for ( Entity entity : event.getEntity().getPassengers()) {
					if (entity.getType() == EntityType.PLAYER) {
						RaceManager.advanceCheckpoints((Player) entity);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent event) {
		for (Player p : RaceManager.getRacingPlayers()) {
			if (p.getWorld() == event.getBlock().getWorld()) {
				if (p.getLocation().distance(event.getBlock().getLocation()) < 1.5) {
					RaceManager.advanceCheckpoints(p);
				}
			}
		}
	}

	@EventHandler
	public void onExit(VehicleExitEvent e) {
		if (e.getExited().getType() == EntityType.PLAYER && RaceManager.racingVehicles.contains(e.getVehicle().getType())) {
			// will cancel race if the player left their vehicle in a race
			Player p = (Player) e.getExited();
			Race r = RaceManager.getRace(p);
			if (r != null) {
				RaceManager.cancelRace(p, "You left the vehicle during the race.");
				e.getVehicle().remove();
			}
		}
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent e) {
		if (RaceManager.racingVehicles.contains(e.getVehicle().getType()) && e.getVehicle().getPassengers().size() > 0 && e.getVehicle().getPassengers().get(0).getType() == EntityType.PLAYER) {
			// will cancel race if the player exited their boat in a race
			Player p = (Player) e.getVehicle().getPassengers().get(0);
			Race r = RaceManager.getRace(p);
			if (r != null) {
				RaceManager.cancelRace(p, "Your vehicle was destroyed during the race.");
				e.getVehicle().remove();
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (RaceManager.racingVehicles.contains(e.getEntity().getType()) && e.getEntity().getPassengers().size() > 0 && e.getEntity().getPassengers().get(0).getType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity().getPassengers().get(0);
			Race r = RaceManager.getRace(p);
			if (r != null) {
				RaceManager.cancelRace(p, "Your vehicle was killed during the race.");
				e.getEntity().remove();
			}
		}
	}


	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Race r = RaceManager.getRace(p);
		if (r != null) {
			// remove them from a queue if they're in it
			TrackManager.clearPlayerFromQueues(p);
			RaceManager.cancelRace(p, "You left the game during the race.");
			if (p.getVehicle() != null) {
				p.getVehicle().remove();
			}
			p.teleport(r.getTrack().getExit());
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity();
			if (p.getHealth() - e.getDamage() < 1) {
				Race r = RaceManager.getRace(p);
				if (r != null) {
					RaceManager.cancelRace(p, "You died during the race.");
					e.setCancelled(true);
					if (p.getVehicle() != null) {
						p.getVehicle().remove();
					}
					p.setInvulnerable(true);
					countdownFreeze = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						() -> p.setInvulnerable(false), 10L);
					p.teleport(r.getTrack().getExit());
					p.setHealth(20.0);
				}
			}
		}
	}
}