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
import java.util.stream.Collectors;

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
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupInheritanceRemove extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupInheritanceRemove(final EverPermissions plugin, final EPGroupInheritance parent) {
        super(plugin, parent, "remove");
        
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllGroups(args.getWorld().getName()))
    			.arg((source, args) -> {
					String worldName = args.getWorld().getName();
					Optional<String> typeGroup = this.plugin.getService().getGroupSubjects().getTypeWorld(worldName);
    				if (!typeGroup.isPresent()) return this.getAllGroups(worldName);

    				Optional<EGroupSubject> group = this.plugin.getService().getGroupSubjects().get(args.getArg(0).orElse(""));
    				if (!group.isPresent()) return this.getAllGroups(worldName);
    				
    				return group.get().getSubjectData().getParents(typeGroup.get()).stream()
    						.map(subject -> subject.resolve().join().getFriendlyIdentifier().orElse(subject.getSubjectIdentifier()))
    						.collect(Collectors.toSet());
    			});
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_INHERITANCE_REMOVE.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_INHERITANCE_REMOVE_DESCRIPTION.getText();
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

	private CompletableFuture<Boolean> command(final CommandSource player, final String groupName, final String inheritanceName, final String worldName) {
		Optional<String> type_group = this.plugin.getService().getGroupSubjects().getTypeWorld(worldName);
		// Monde introuvable
		if (!type_group.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("{world}", worldName)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<EGroupSubject> group = this.plugin.getService().getGroupSubjects().get(groupName);
		// Groupe existant
		if (!group.isPresent() || !group.get().hasTypeWorld(type_group.get())) {
			EPMessages.GROUP_NOT_FOUND_WORLD.sender()
				.replace("{group}", groupName)
				.replace("{type}", type_group.get())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<EGroupSubject> inheritance = this.plugin.getService().getGroupSubjects().get(inheritanceName);
		// Groupe existant
		if (!inheritance.isPresent() || !inheritance.get().hasTypeWorld(type_group.get())) {
			EPMessages.GROUP_NOT_FOUND_WORLD.sender()
				.replace("{group}", inheritanceName)
				.replace("{type}", type_group.get())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		SubjectReference inheritanceReference = inheritance.get().asSubjectReference();
		
		if (!group.get().getParents(type_group.get()).contains(inheritanceReference)) {
			EPMessages.GROUP_INHERITANCE_REMOVE_ERROR.sender()
				.replace("{inheritance}", inheritance.get().getFriendlyIdentifier().orElse(inheritanceName))
				.replace("{group}", group.get().getFriendlyIdentifier().orElse(groupName))
				.replace("{type}", type_group.get())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// L'inheritance n'a pas été supprimé
		return group.get().getSubjectData().removeParent(type_group.get(), inheritanceReference)
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				
				EPMessages.GROUP_INHERITANCE_REMOVE_STAFF.sender()
					.replace("{inheritance}", inheritance.get().getFriendlyIdentifier().orElse(inheritanceName))
					.replace("{group}", group.get().getFriendlyIdentifier().orElse(groupName))
					.replace("{type}", type_group.get())
					.sendTo(player);
				return true;
			});
	}
}
