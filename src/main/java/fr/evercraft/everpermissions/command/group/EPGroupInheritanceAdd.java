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
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.SubjectReference;
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

public class EPGroupInheritanceAdd extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupInheritanceAdd(final EverPermissions plugin, final EPGroupInheritance parent) {
        super(plugin, parent, "add");
        
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllGroups(args.getWorld().getName()))
    			.arg((source, args) -> {
					Set<String> inheritances = this.getAllGroups(args.getWorld().getName());
    				inheritances.remove(args.getArg(0).orElse(""));
    				return inheritances;
    			});
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_INHERITANCE_ADD.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_INHERITANCE_ADD_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_GROUP.getString() + ">"
												 + " <" + EAMessages.ARGS_INHERITANCE.getString() + ">")
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
		
		return this.command(source, argsString.get(0), argsString.get(1), args.getWorld().getName());
	}

	private CompletableFuture<Boolean> command(final CommandSource player, final String groupName, final String inheritanceName, final String worldName) throws EMessageException {
		String typeGroup = EPCommand.getTypeWorld(player, this.plugin.getService().getGroupSubjects(), worldName);
		EGroupSubject group = EPCommand.getGroup(player, this.plugin.getService(), groupName, typeGroup);
		EGroupSubject inheritance = EPCommand.getGroup(player, this.plugin.getService(), inheritanceName, typeGroup);
		SubjectReference inheritanceReference = inheritance.asSubjectReference();
		
		// Le groupe et l'inheritance sont égale
		if (group.equals(inheritance)) {
			EPMessages.GROUP_INHERITANCE_ADD_ERROR_EQUALS.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (group.getParents(typeGroup).contains(inheritanceReference)) {
			EPMessages.GROUP_INHERITANCE_ADD_ERROR_HAVE.sender()
				.replace("{inheritance}", inheritance.getFriendlyIdentifier().orElse(inheritanceName))
				.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
				.replace("{type}", typeGroup)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// L'inheritance n'a pas été ajouté
		return group.getSubjectData().addParent(typeGroup, inheritanceReference)
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				
				EPMessages.GROUP_INHERITANCE_ADD_STAFF.sender()
					.replace("{inheritance}", inheritance.getFriendlyIdentifier().orElse(inheritanceName))
					.replace("{group}", group.getFriendlyIdentifier().orElse(groupName))
					.replace("{type}", typeGroup)
					.sendTo(player);
				return true;
			});
	}
}