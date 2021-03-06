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
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.services.permission.EGroupSubject;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupRename extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupRename(final EverPermissions plugin, final EPGroup command) {
        super(plugin, command, "rename");
        
        this.pattern = Args.builder()
    			.arg((source, args) -> this.getAllGroups())
    			.arg((source, args) -> Arrays.asList("name..."));
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
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_GROUP.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) {
		Args args = this.pattern.build(this.plugin, source, argsList);
		List<String> argsString = args.getArgs();
		
		if (argsString.size() != 2) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		return this.command(source, argsString.get(0), argsString.get(1));
	}

	private CompletableFuture<Boolean> command(final CommandSource player, final String oldGroupName, final String newGroupName) {		
		Optional<EGroupSubject> oldGroup = this.plugin.getService().getGroupSubjects().get(oldGroupName);
		// Groupe introuvable
		if (!oldGroup.isPresent()) {
			EAMessages.GROUP_NOT_FOUND.sender()
				.replace("{group}", oldGroup.get().getFriendlyIdentifier().orElse(oldGroupName))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<EGroupSubject> newGroup = this.plugin.getService().getGroupSubjects().get(newGroupName);
		// Groupe introuvable
		if (newGroup.isPresent()) {
			EPMessages.GROUP_RENAME_ERROR.sender()
				.replace("{group}", newGroup.get().getFriendlyIdentifier().orElse(newGroupName))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<String> oldName = oldGroup.get().getFriendlyIdentifier();
		if (oldName.isPresent() && oldName.get().equals(newGroupName)) {
			EPMessages.GROUP_RENAME_EQUALS.sender()
			.replace("{group}", oldGroup.get().getFriendlyIdentifier().orElse(oldGroupName))
			.sendTo(player);
		}
		
		// Le groupe n'a pas été supprimé
		return oldGroup.get().setFriendlyIdentifier(newGroupName)
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				EPMessages.GROUP_RENAME_STAFF.sender()
					.replace("{oldName}", oldName.orElse(oldGroupName))
					.replace("{newName}", newGroupName)
					.sendTo(player);
				return true;
			});
	}
}
