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

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupDefault extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupDefault(final EverPermissions plugin, final EPGroup command) {
        super(plugin, command, "default");
        
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
    			.arg((source, args) -> this.getAllGroups(args.getWorld().getName()))
    			.arg((source, args) -> Arrays.asList("true", "false"));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_DEFAULT.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_DEFAULT_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_GROUP.getString() + ">"
												 + " <true|false>")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) throws EMessageException {
		Args args = this.pattern.build(this.plugin, source, argsList);
		List<String> argsString = args.getArgs();
		
		if (argsString.size() != 2) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		return this.command(source, argsString.get(0), args.getArg(1, Args.BOOLEAN).orElse(false), args.getWorld().getName());
	}

	private CompletableFuture<Boolean> command(final CommandSource player, final String groupName, final boolean value, final String worldName) throws EMessageException {
		String typeGroup = EPCommand.getTypeWorld(player, this.plugin.getService().getGroupSubjects(), worldName);
		EGroupSubject group = EPCommand.getGroup(player, this.plugin.getService(), groupName, typeGroup);
		
		Optional<EGroupSubject> oldDefault = this.plugin.getService().getGroupSubjects().getDefaultGroup(typeGroup);
		if (oldDefault.isPresent()) {
			// C'est déjà le groupe par défaut
			if (value && oldDefault.get().equals(group)) {
				EPMessages.GROUP_DEFAULT_ERROR_EQUALS.sender()
					.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
					.replace("{type}", typeGroup)
					.sendTo(player);
				return CompletableFuture.completedFuture(false);
			
			// Le groupe n'a pas un groupe par défaut
			} else if (!value && !oldDefault.get().equals(group)) {
				EPMessages.GROUP_DEFAULT_ERROR_FALSE.sender()
					.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
					.replace("{type}", typeGroup)
					.sendTo(player);
				return CompletableFuture.completedFuture(false);
			
			} else if (value) {
				EPMessages.GROUP_DEFAULT_ERROR_TRUE.sender()
					.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
					.replace("{type}", typeGroup)
					.sendTo(player);
				return CompletableFuture.completedFuture(false);
			}
			
		// Il n'y a pas de groupe par défaut
		} else if (!value) {
			EPMessages.GROUP_DEFAULT_ERROR_FALSE.sender()
				.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
				.replace("{type}", typeGroup)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		return group.setDefault(typeGroup, value)
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				
				if (value) {
					EPMessages.GROUP_DEFAULT_TRUE.sender()
						.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
						.replace("{type}", typeGroup)
						.sendTo(player);
				} else {
					EPMessages.GROUP_DEFAULT_FALSE.sender()
						.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
						.replace("{type}", typeGroup)
						.sendTo(player);
				}
				return true;
			});
	}
}
