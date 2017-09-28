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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.java.UtilsCompletableFuture;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.subject.EPUserSubject;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPUserClear extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPUserClear(final EverPermissions plugin, final EPUser parent) {
        super(plugin, parent, "clear");
        
        this.pattern = Args.builder()
        		.empty(Args.MARKER_CONFIRMATION,
        				(source, args) -> args.getArgs().size() >= 1)
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getUserSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllUsers(args.getArg(0).orElse(""), source));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_CLEAR.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_CLEAR_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_USER.getString() + ">")
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
		
		if (!args.isOption(Args.MARKER_CONFIRMATION)) {
			for (EUser user : args.getArg(0, Args.USERS).orElse(Arrays.asList())) {
				this.commandNoConfirmation(source, user);
			}
			return CompletableFuture.completedFuture(false);
		} else {
			return UtilsCompletableFuture.anyOf(args.getArg(0, Args.USERS).orElse(Arrays.asList()).stream()
					.map(user -> this.commandConfirmation(source, user))
					.collect(Collectors.toList()));
		}
	}
	
	private void commandNoConfirmation(final CommandSource staff, final EUser user) {
		EPMessages.USER_CLEAR_CONFIRMATION.sender()
			.replace("{player}", user.getName())
			.replace("{confirmation}", this.getButtonConfirmation(user.getName()))
			.sendTo(staff);
	}
	
	public Text getButtonConfirmation(String name) {
		return EPMessages.USER_CLEAR_CONFIRMATION_BUTTON.getFormat()
					.toText("{player}", name).toBuilder()
					.onHover(TextActions.showText(EPMessages.USER_CLEAR_CONFIRMATION_BUTTON_HOVER.getFormat()
						.toText("{player}", name)))
					.onClick(TextActions.runCommand("/" + this.getName() + " \"" + name + "\" " + Args.MARKER_CONFIRMATION))
					.build();
	}
	
	
	private CompletableFuture<Boolean> commandConfirmation(final CommandSource staff, final EUser user) {
		return this.plugin.getService().getUserSubjects().load(user.getIdentifier())
			.exceptionally(e -> null)
			.thenCompose(subject -> {
				if (subject == null) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return CompletableFuture.completedFuture(false);
				}
				
				return this.commandConfirmation(staff, user, subject);
			});
	}
	
	private CompletableFuture<Boolean> commandConfirmation(final CommandSource staff, final EUser user, final EPUserSubject subject) {
		return subject.clear()
				.exceptionally(e -> false)
				.thenApply(result -> {
					if (!result) {
						EAMessages.COMMAND_ERROR.sender()
							.prefix(EPMessages.PREFIX)
							.sendTo(staff);
						return false;
					}
					
					if (staff.getIdentifier().equals(user.getIdentifier())) {
						EPMessages.USER_CLEAR_EQUALS.sender()
							.replace("{player}", user.getName())
							.sendTo(staff);
						this.plugin.getService().broadcastMessage(staff,
							EPMessages.USER_CLEAR_BROADCAST_EQUALS.sender()
								.replace("{staff}", staff.getName())
								.replace("{player}", user.getName()));
					} else {
						EPMessages.USER_CLEAR_STAFF.sender()
							.replace("{player}", user.getName())
							.sendTo(staff);
						EPMessages.USER_CLEAR_PLAYER.sender()
							.replace("{staff}", staff.getName())
							.replace("{player}", user.getName())
							.sendTo(user);
						this.plugin.getService().broadcastMessage(staff, user.getUniqueId(),
							EPMessages.USER_CLEAR_BROADCAST_PLAYER.sender()
								.replace("{staff}", staff.getName())
								.replace("{player}", user.getName()));
					}
					return true;
				});
	}
}
