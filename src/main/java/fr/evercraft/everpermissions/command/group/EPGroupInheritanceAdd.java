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
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Locatable;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupInheritanceAdd extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupInheritanceAdd(final EverPermissions plugin, final EPGroupInheritance parent) {
        super(plugin, parent, "add");
        
        this.pattern = Args.builder()
        		.value(EPCommand.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
    			.arg((source, args) -> {
    				String world = args.getValue(EPCommand.MARKER_WORLD).orElse(null);
    				if (world == null) {
						if (source instanceof Locatable) {
							world = ((Locatable) source).getWorld().getName();
						} else {
							world = this.plugin.getGame().getServer().getDefaultWorldName();
						}
    				}
    				return this.getAllGroups(world);
    			})
    			.arg((source, args) -> {
    				String world = args.getValue(EPCommand.MARKER_WORLD).orElse(null);
    				if (world == null) {
						if (source instanceof Locatable) {
							world = ((Locatable) source).getWorld().getName();
						} else {
							world = this.plugin.getGame().getServer().getDefaultWorldName();
						}
    				}
    				
    				Set<String> inheritances = this.getAllGroups(world);
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
		return this.pattern.suggest(source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EPCommand.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_GROUP.getString() + ">"
												 + " <" + EAMessages.ARGS_INHERITANCE.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) {
		Args args = this.pattern.build(argsList);
		List<String> argsString = args.getArgs();
		
		if (argsString.size() != 2) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		String world = args.getValue(EPCommand.MARKER_WORLD).orElse(null);
		if (world == null) {
			if (source instanceof Locatable) {
				world = ((Locatable) source).getWorld().getName();
			} else {
				world = this.plugin.getGame().getServer().getDefaultWorldName();
			}
		}
		
		return this.command(source, argsString.get(0), argsString.get(1), world);
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
		
		// Le groupe et l'inheritance sont égale
		if (group.get().equals(inheritance.get())) {
			EPMessages.GROUP_INHERITANCE_ADD_ERROR_EQUALS.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (group.get().getParents(type_group.get()).contains(inheritanceReference)) {
			EPMessages.GROUP_INHERITANCE_ADD_ERROR_HAVE.sender()
				.replace("{inheritance}", inheritance.get().getFriendlyIdentifier().orElse(inheritanceName))
				.replace("{group}", group.get().getFriendlyIdentifier().orElse(groupName))
				.replace("{type}", type_group.get())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// L'inheritance n'a pas été ajouté
		return group.get().getSubjectData().addParent(type_group.get(), inheritanceReference)
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				
				EPMessages.GROUP_INHERITANCE_ADD_STAFF.sender()
					.replace("{inheritance}", inheritance.get().getFriendlyIdentifier().orElse(inheritanceName))
					.replace("{group}", group.get().getFriendlyIdentifier().orElse(groupName))
					.replace("{type}", type_group.get())
					.sendTo(player);
				return true;
			});
	}
}
