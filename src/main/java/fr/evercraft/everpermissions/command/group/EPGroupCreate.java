/*
 * This file is part of EverPermissions.
 *
 * EverPermissions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverPermissions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverPermissions.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.everpermissions.command.group;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Locatable;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.EParentCommand;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupCreate extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupCreate(final EverPermissions plugin, final EParentCommand<EverPermissions> command) {
        super(plugin, command, "create");
        
        this.pattern = Args.builder()
        		.value(EPCommand.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
    			.arg((source, args) -> Arrays.asList("name..."));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_ADD.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_ADD_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EPCommand.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_GROUP.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) {
		Args args = this.pattern.build(argsList);
		List<String> argsString = args.getArgs();
		
		if (argsString.size() != 1) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<String> world = args.getValue(EPCommand.MARKER_WORLD);
		if (world.isPresent()) {
			return this.command(source, argsString.get(0), world.get());
		} else {
			if (source instanceof Locatable) {
				return this.command(source, argsString.get(0), ((Locatable) source).getWorld().getName());
			} else {
				return this.command(source, argsString.get(0), this.plugin.getGame().getServer().getDefaultWorldName());
			}
		}
	}

	private CompletableFuture<Boolean> command(final CommandSource player, final String groupName, final String worldName) {
		Optional<String> typeGroup = this.plugin.getService().getGroupSubjects().getTypeWorld(worldName);
		// Monde introuvable
		if (!typeGroup.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("{world}", worldName)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<EGroupSubject> group = this.plugin.getService().getGroupSubjects().get(groupName);
		// Groupe existant
		if (group.isPresent() && group.get().hasTypeWorld(typeGroup.get())) {
			EPMessages.GROUP_ADD_ERROR.sender()
				.replace("{group}", groupName)
				.replace("{type}", typeGroup.get())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		return this.plugin.getService().getGroupSubjects().register(groupName, typeGroup.get())
			.exceptionally(result -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				
				EPMessages.GROUP_ADD_STAFF.sender()
					.replace("{group}", groupName)
					.replace("{type}", typeGroup.get())
					.sendTo(player);
				return true;
			});
	}
}
