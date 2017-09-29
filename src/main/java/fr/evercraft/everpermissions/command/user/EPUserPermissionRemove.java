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
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.java.UtilsCompletableFuture;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.services.permission.EUserSubject;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.data.EPUserData;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPUserPermissionRemove extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPUserPermissionRemove(final EverPermissions plugin, final EPUserPermission parent) {
        super(plugin, parent, "remove");
        
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getUserSubjects().getWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllUsers(args.getArg(0).orElse(""), source))
        		.arg((source, args) -> {
        			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.getArg(0).orElse(""));
        			if (!player.isPresent()) return this.getAllPermissions();
        			
        			SubjectData data = player.get().getSubjectData();
        			if (!(data instanceof EPUserData)) return this.getAllPermissions();
        			
        			String typeUser = EPCommand.getTypeWorld(source, this.plugin.getService().getUserSubjects(), args.getWorld().getName());
        			return ((EPUserData) data).getPermissions(typeUser).keySet();
        		});
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_PERMISSION_REMOVE.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_PERMISSION_REMOVE_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_USER.getString() + ">"
												 + " <" + EAMessages.ARGS_PERMISSION.getString() + ">")
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
		String permission = argsString.get(1);
		String typeUser = EPCommand.getTypeWorld(source, this.plugin.getService().getUserSubjects(), world.getName());
		
		return UtilsCompletableFuture.anyOf(args.getArg(0, Args.USERS).orElse(Arrays.asList()).stream()
				.map(user -> this.command(source, user, permission, world.getName(), typeUser))
				.collect(Collectors.toList()));
	}
	
	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final String permission, final String worldName, final String typeUser) {
		return this.plugin.getService().getUserSubjects().load(user.getIdentifier())
			.exceptionally(e -> null)
			.thenCompose(subject -> {
				if (subject == null) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return CompletableFuture.completedFuture(false);
				}
				
				return this.command(staff, user, subject, permission, worldName, typeUser);
			});
	}

	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final EUserSubject subject, final String permission, 
			final String worldName, final String typeUser) {
		
		if (subject.getSubjectData().getPermissions(typeUser).get(permission) == null) {
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_PERMISSION_REMOVE_ERROR_EQUALS.sender()
					.replace("{player}", user.getName())
					.replace("{permission}", permission)
					.replace("{type}", typeUser)
					.sendTo(staff);
			} else {
				EPMessages.USER_PERMISSION_REMOVE_ERROR_STAFF.sender()
					.replace("{player}", user.getName())
					.replace("{permission}", permission)
					.replace("{type}", typeUser)
					.sendTo(staff);
			}
			return CompletableFuture.completedFuture(false);
		}
		
		return subject.getSubjectData().setPermission(typeUser, permission, Tristate.UNDEFINED)
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return false;
				}
				
				if (staff.getIdentifier().equals(user.getIdentifier())) {
					EPMessages.USER_PERMISSION_REMOVE_EQUALS.sender()
						.replace("{player}", user.getName())
						.replace("{permission}", permission)
						.replace("{type}", typeUser)
						.sendTo(staff);
					
					this.plugin.getService().broadcastMessage(staff,
						EPMessages.USER_PERMISSION_REMOVE_BROADCAST_EQUALS.sender()
							.replace("{staff}", staff.getName())
							.replace("{player}", user.getName())
							.replace("{permission}", permission)
							.replace("{type}", typeUser));
				} else {
					EPMessages.USER_PERMISSION_REMOVE_STAFF.sender()
						.replace("{player}", user.getName())
						.replace("{permission}", permission)
						.replace("{type}", typeUser)
						.sendTo(staff);
					
					this.plugin.getService().broadcastMessage(staff,
						EPMessages.USER_PERMISSION_REMOVE_BROADCAST_PLAYER.sender()
							.replace("{staff}", staff.getName())
							.replace("{player}", user.getName())
							.replace("{permission}", permission)
							.replace("{type}", typeUser));
				}
				return true;
			});
	}
}
