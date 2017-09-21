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
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupRemove extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupRemove(final EverPermissions plugin, final EPGroup command) {
        super(plugin, command, "remove");
        
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllGroups(args.getWorld().getName()));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_REMOVE.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_REMOVE_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_GROUP.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) throws EMessageException {
		Args args = this.pattern.build(this.plugin, source, argsList);
		List<String> argsString = args.getArgs();
		
		if (argsString.size() != 1) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		return this.command(source, argsString.get(0), args.getWorld().getName());
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
		// Groupe introuvable
		if (!group.isPresent() || !group.get().hasTypeWorld(typeGroup.get())) {
			EPMessages.GROUP_NOT_FOUND_WORLD.sender()
				.replace("{group}", group.get().getFriendlyIdentifier().orElse(groupName))
				.replace("{type}", typeGroup.get())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le groupe n'a pas été supprimé
		return group.get().clear(typeGroup.get())
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				EPMessages.GROUP_REMOVE_STAFF.sender()
					.replace("{group}", group.get().getFriendlyIdentifier().orElse(groupName))
					.replace("{type}", typeGroup.get())
					.sendTo(player);
				return true;
			});
	}
}
