package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterOnlinePlayer;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.HostedRace;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostRemovePlayers extends Command {

	public HostRemovePlayers() {
		super("host players remove");
		addBaseParameter(new CommandParameterOnlinePlayer());

		setPermission("cvracing.host.players.edit");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		Track track = TrackManager.getTrackHostedBy(player);
		if (track == null) {
			throw new CommandExecutionException("You are not currently hosting a track.");
		}
		HostedRace hostedRace = track.getHostedRace();

		Player removingPlayer  = (Player) baseParameters.get(0);

		if (!hostedRace.hasPlayer(removingPlayer)) {
			throw new CommandExecutionException("This lobby does not include " + removingPlayer.getDisplayName() + "!");
		}

		hostedRace.removePlayer(removingPlayer);

		return new CommandResponse("Removed " + removingPlayer.getDisplayName() + " from the race");
	}
}
