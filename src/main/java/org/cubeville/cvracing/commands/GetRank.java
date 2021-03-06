package org.cubeville.cvracing.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.ScoreManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetRank extends Command {

	public GetRank() {
		super("rank");
		addBaseParameter(new CommandParameterString());
		addOptionalBaseParameter(new CommandParameterString());
		setPermission("cvracing.citizen.rank");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters,
		List<Object> baseParameters) throws CommandExecutionException {
		Track t = TrackManager.getTrack((String) baseParameters.get(0));
		if (t == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}
		if (baseParameters.size() > 1) {
			String opName = (String) baseParameters.get(1);
			OfflinePlayer op = Bukkit.getOfflinePlayer((String) baseParameters.get(1));
			if (op.hasPlayedBefore()) {
				if (ScoreManager.getScore(op.getUniqueId(), t) != null) {
					return new CommandResponse(
						"§6" + op.getName() + "\'s rank on §e§l" + t.getName() + "§6 is §e§l#" + ScoreManager.getScorePlacement(t,
							op.getUniqueId()) + "!");
				}
				return new CommandResponse("§6" + op.getName() + " does not have a registered time on §e§l" + t.getName() + "§6.");
			}
			throw new CommandExecutionException("Cannot find a player with the name " + opName + ".");

		} else {
			if (ScoreManager.getScore(player.getUniqueId(), t) != null) {
				return new CommandResponse(
					"§6Your rank on §e§l" + t.getName() + "§6 is §e§l#" + ScoreManager.getScorePlacement(
						t, player.getUniqueId()) + "§6 !");
			}
			return new CommandResponse("§6You do not have a registered time on §e§l" + t.getName() + "§6.");
		}
	}

}
