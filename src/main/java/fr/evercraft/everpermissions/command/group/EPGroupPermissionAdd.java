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
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.services.permission.EGroupSubject;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupPermissionAdd extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupPermissionAdd(final EverPermissions plugin, final EPGroupPermission parent) {
        super(plugin, parent, "add");
        
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllGroups(args.getWorld().getName()))
    			.arg((source, args) -> this.getAllPermissions())
    			.arg((source, args) -> Arrays.asList("true", "false"));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_PERMISSION_ADD.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_PERMISSION_ADD_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_GROUP.getString() + ">"
												 + " <" + EAMessages.ARGS_PERMISSION.getString() + ">"
												 + " <true|false>")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) throws EMessageException {
		Args args = this.pattern.build(this.plugin, source, argsList);
		List<String> argsString = args.getArgs();
		
		if (argsString.size() != 3) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		return this.command(source, argsString.get(0), argsString.get(1), args.getArg(2, Args.BOOLEAN).get(), args.getWorld().getName());
	}

	private CompletableFuture<Boolean> command(final CommandSource player, final String groupName, final String permission, final boolean value, final String worldName) throws EMessageException {
		String typeGroup = EPCommand.getTypeWorld(player, this.plugin.getService().getGroupSubjects(), worldName);
		EGroupSubject group = EPCommand.getGroup(player, this.plugin.getService(), groupName, typeGroup);
		
		Boolean oldValue = group.getSubjectData().getPermissions(typeGroup).get(permission);
		if (oldValue != null) {
			if (oldValue && value) {
				EPMessages.GROUP_PERMISSION_ADD_ERROR_TRUE.sender()
					.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
					.replace("{permission}", permission)
					.replace("{type}", typeGroup)
					.sendTo(player);
				return CompletableFuture.completedFuture(false);
			} else if (!oldValue && !value) {
				EPMessages.GROUP_PERMISSION_ADD_ERROR_FALSE.sender()
					.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
					.replace("{permission}", permission)
					.replace("{type}", typeGroup)
					.sendTo(player);
				return CompletableFuture.completedFuture(false);
			}
		}
		
		// La permission n'a pas été ajouté
		return group.getSubjectData().setPermission(typeGroup, permission, Tristate.fromBoolean(value))
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				
				// Permission : True
				if (value) {
					EPMessages.GROUP_PERMISSION_ADD_TRUE.sender()
						.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
						.replace("{permission}", permission)
						.replace("{type}", typeGroup)
						.sendTo(player);
				// Permission : False
				} else {
					EPMessages.GROUP_PERMISSION_ADD_FALSE.sender()
						.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
						.replace("{permission}", permission)
						.replace("{type}", typeGroup)
						.sendTo(player);
				}
				return true;
			});
	}
}
