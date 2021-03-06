package org.cubeville.cvracing.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.TrackStatus;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CloseTrack extends BaseCommand {
	JavaPlugin plugin;

	public CloseTrack(JavaPlugin plugin) {
		super("track close");
		addBaseParameter(new CommandParameterString());
		setPermission("cvracing.admin.close");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {
		FileConfiguration config = plugin.getConfig();

		String name = baseParameters.get(0).toString().toLowerCase();

		if (!config.contains("tracks." + name)) {
			throw new CommandExecutionException("Track with name " + baseParameters.get(0) + " does not exist!");
		}

		config.set("tracks." + name + ".isClosed", true);
		Track track = TrackManager.getTrack(name);
		track.setClosed(true);
		track.setStatus(TrackStatus.CLOSED);

		plugin.saveConfig();
		return new CommandResponse("&aThe track " + baseParameters.get(0) + " has been closed.");
	}
}
