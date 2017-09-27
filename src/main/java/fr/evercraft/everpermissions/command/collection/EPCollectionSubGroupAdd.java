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
package fr.evercraft.everpermissions.command.collection;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPCollectionSubGroupAdd extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPCollectionSubGroupAdd(final EverPermissions plugin, final EPCollectionSubGroup parent) {
        super(plugin, parent, "add");
        
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getUserSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> EPCommand.getAllCollections(this.plugin.getService()))
        		.arg((source, args) -> EPCommand.getAllSubjects(this.plugin.getService(), args.getArg(0).orElse("")))
    			.arg((source, args) -> this.getAllGroups(args.getWorld().getName()));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.COLLECTION_SUBGROUP_ADD.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.COLLECTION_SUBGROUP_ADD_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_COLLECTION.getString() + ">"
												 + " <" + EAMessages.ARGS_SUBJECT.getString() + ">"
												 + " <" + EAMessages.ARGS_SUBGROUP.getString() + ">")
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
		
		World world = args.getWorld();
		EUserSubject subject = EPCommand.getSubject(source, this.plugin.getService(), argsString.get(0), argsString.get(1));
		String typeUser = EPCommand.getTypeWorld(source, this.plugin.getService().getUserSubjects(), world.getName());
		String typeGroup = EPCommand.getTypeWorld(source, this.plugin.getService().getGroupSubjects(), world.getName());
		EGroupSubject group = EPCommand.getGroup(source, this.plugin.getService(), argsString.get(2), typeGroup);
		
		return this.command(source, subject, group, world.getName(), typeUser);
	}

	private CompletableFuture<Boolean> command(final CommandSource staff, final EUserSubject subject, final EGroupSubject group, 
			final String worldName, final String typeUser) {
		String groupName = group.getName();
		
		if (subject.getSubjectData().getSubGroup(typeUser).contains(group.asSubjectReference())) {
			EPMessages.COLLECTION_SUBGROUP_ADD_ERROR.sender()
				.replace("{subject}", subject.getIdentifier())
				.replace("{collection}", subject.getCollectionIdentifier())
				.replace("{group}", groupName)
				.replace("{type}", typeUser)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		return subject.getSubjectData().addParent(typeUser, group.asSubjectReference())
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return false;
				}
				
				EPMessages.COLLECTION_SUBGROUP_ADD.sender()
					.replace("{subject}", subject.getIdentifier())
					.replace("{collection}", subject.getCollectionIdentifier())
					.replace("{group}", groupName)
					.replace("{type}", typeUser)
					.sendTo(staff);
				
				this.plugin.getService().broadcastMessage(staff, 
					EPMessages.COLLECTION_SUBGROUP_ADD_BROADCAST.sender()
						.replace("{staff}", staff.getName())
						.replace("{subject}", subject.getIdentifier())
						.replace("{collection}", subject.getCollectionIdentifier())
						.replace("{group}", groupName)
						.replace("{type}", typeUser));
				return true;
			});
	}
}
