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
package fr.evercraft.everpermissions.command.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.java.UtilsCompletableFuture;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.data.EUserData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPUserSubGroupRemove extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPUserSubGroupRemove(final EverPermissions plugin, final EPUserSubGroup parent) {
        super(plugin, parent, "remove");
        
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getUserSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllUsers(args.getArg(0).orElse(""), source))
        		.arg((source, args) -> {
        			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.getArg(0).orElse(""));
        			if (!player.isPresent()) return this.getAllGroups(args.getWorld().getName());
        			
        			SubjectData data = player.get().getSubjectData();
        			if (!(data instanceof EUserData)) return this.getAllGroups(args.getWorld().getName());
        			
        			String typeUser = EPCommand.getTypeWorld(source, this.plugin.getService().getUserSubjects(), args.getWorld().getName());
        			return ((EUserData) data).getSubGroup(typeUser).stream()
        					.map(subgroup -> subgroup.resolve().join().getFriendlyIdentifier().orElse(subgroup.getSubjectIdentifier()))
        					.collect(Collectors.toSet());
        		});
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_SUBGROUP_REMOVE.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_SUBGROUP_REMOVE_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_USER.getString() + ">"
												 + " <" + EAMessages.ARGS_SUBGROUP.getString() + ">")
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
		
		World world = args.getWorld();
		String typeUser = EPCommand.getTypeWorld(source, this.plugin.getService().getUserSubjects(), world.getName());
		String typeGroup = EPCommand.getTypeWorld(source, this.plugin.getService().getGroupSubjects(), world.getName());
		EGroupSubject group = EPCommand.getGroup(source, this.plugin.getService(), argsString.get(1), typeGroup);
		
		return UtilsCompletableFuture.anyOf(args.getArg(0, Args.USERS).orElse(Arrays.asList()).stream()
				.map(user -> this.command(source, user, group, world.getName(), typeUser))
				.collect(Collectors.toList()));
	}
	
	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final EGroupSubject group, final String worldName, final String typeUser) {
		return this.plugin.getService().getUserSubjects().load(user.getIdentifier())
			.exceptionally(e -> null)
			.thenCompose(subject -> {
				if (subject == null) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return CompletableFuture.completedFuture(false);
				}
				
				return this.command(staff, user, subject, group, worldName, typeUser);
			});
	}

	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final EUserSubject subject, final EGroupSubject group, 
			final String worldName, final String typeUser) {
		String groupName = group.getName();
		
		if (!subject.getSubjectData().getSubGroup(typeUser).contains(group.asSubjectReference())) {
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_SUBGROUP_REMOVE_ERROR_EQUALS.sender()
					.replace("{player}", user.getName())
					.replace("{group}", groupName)
					.replace("{type}", typeUser)
					.sendTo(staff);
			} else {
				EPMessages.USER_SUBGROUP_REMOVE_ERROR_STAFF.sender()
					.replace("{player}", user.getName())
					.replace("{group}", groupName)
					.replace("{type}", typeUser)
					.sendTo(staff);
			}
			return CompletableFuture.completedFuture(false);
		}
		
		return subject.getSubjectData().removeParent(typeUser, group.asSubjectReference())
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return false;
				}
				
				if (staff.getIdentifier().equals(user.getIdentifier())) {
					EPMessages.USER_SUBGROUP_REMOVE_EQUALS.sender()
						.replace("{player}", user.getName())
						.replace("{group}", groupName)
						.replace("{type}", typeUser)
						.sendTo(staff);
					
					this.plugin.getService().broadcastMessage(staff,
						EPMessages.USER_SUBGROUP_REMOVE_BROADCAST_EQUALS.sender()
							.replace("{staff}", staff.getName())
							.replace("{player}", user.getName())
							.replace("{group}", groupName)
							.replace("{type}", typeUser));
				} else {
					EPMessages.USER_SUBGROUP_REMOVE_STAFF.sender()
						.replace("{player}", user.getName())
						.replace("{group}", groupName)
						.replace("{type}", typeUser)
						.sendTo(staff);
					
					 EPMessages.USER_SUBGROUP_REMOVE_PLAYER.sender()
						.replace("{staff}", staff.getName())
						.replace("{group}", groupName)
						.replace("{type}", typeUser)
						.sendTo(user);
					
					this.plugin.getService().broadcastMessage(staff, user.getUniqueId(), 
						EPMessages.USER_SUBGROUP_REMOVE_BROADCAST_PLAYER.sender()
							.replace("{staff}", staff.getName())
							.replace("{player}", user.getName())
							.replace("{group}", groupName)
							.replace("{type}", typeUser));
				}
				return true;
			});
	}
}