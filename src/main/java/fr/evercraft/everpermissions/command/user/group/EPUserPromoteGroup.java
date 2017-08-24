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
package fr.evercraft.everpermissions.command.user.group;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EPUserPromoteGroup extends ECommand<EverPermissions> {
	
	public EPUserPromoteGroup(final EverPermissions plugin) {
        super(plugin, "permupromote", "manpromote");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_PROMOTE_GROUP.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_PROMOTE_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permupromote <" + EAMessages.ARGS_PLAYER.getString() + "> "
										+ "<" + EAMessages.ARGS_GROUP.getString() + "> "
										+ "[" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/permupromote "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.getAllUsers(args.get(0), source);
		} else if (args.size() == 2) {
			return this.getAllGroups();
		} else if (args.size() == 3) {
			return this.getAllWorlds();
		}
		return Arrays.asList();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le monde
		if (args.size() == 2) {
			Optional<EUser> optUser = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optUser.isPresent()){
				// Si la source est un joueur
				if (source instanceof EPlayer) {
					return this.command(source, optUser.get(), args.get(1), ((EPlayer) source).getWorld().getName());
				// La source n'est pas un joueur
				} else {
					return this.command(source, optUser.get(), args.get(1), this.plugin.getGame().getServer().getDefaultWorldName());
				}
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EPMessages.PREFIX)
					.replace("<player>", args.get(0))
					.sendTo(source);
			}
		// On connait le monde
		} else if (args.size() == 3) {
			Optional<EUser> optPlayer = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()){
				return this.command(source, optPlayer.get(), args.get(1), args.get(2));
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EPMessages.PREFIX)
					.replace("<player>", args.get(0))
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final String group_name, final String world_name) {
		return this.plugin.getService().getUserSubjects().load(user.getIdentifier())
			.exceptionally(e -> null)
			.thenCompose(subject -> {
				if (subject == null) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return CompletableFuture.completedFuture(false);
				}
				
				return this.command(staff, user, subject, group_name, world_name);
			});
	}
	
	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final EUserSubject subject, final String group_name, final String world_name) {
		Optional<String> type_user = this.plugin.getService().getUserSubjects().getTypeWorld(world_name);
		Optional<String> type_group = this.plugin.getService().getGroupSubjects().getTypeWorld(world_name);
		// Monde existant
		if (!type_user.isPresent() || !type_group.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<EGroupSubject> group = this.plugin.getService().getGroupSubjects().get(group_name);
		// Groupe existant
		if (!group.isPresent() || !group.get().hasTypeWorld(type_group.get())) {
			EPMessages.GROUP_NOT_FOUND_WORLD.sender()
				.replace("<group>", group_name)
				.replace("<type>", type_group.get())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		Set<Context> contexts = EContextCalculator.of(world_name);
		Optional<SubjectReference> oldGroup = subject.getSubjectData().getGroup(type_user.get());
		
		if (oldGroup.isPresent()) {
			// Le groupe du joueur est égale au nouveau groupe
			if (oldGroup.get().equals(group.get().asSubjectReference())) {
				if (staff.getIdentifier().equals(user.getIdentifier())) {
					EPMessages.USER_PROMOTE_ERROR_EQUALS.sender()
						.replace("<player>", user.getName())
						.replace("<group>", group.get().getFriendlyIdentifier().orElse(group_name))
						.replace("<type>", type_user.get())
						.sendTo(staff);
				} else {
					EPMessages.USER_PROMOTE_ERROR_STAFF.sender()
						.replace("<player>", user.getName())
						.replace("<group>", group.get().getIdentifier())
						.replace("<type>", type_user.get())
						.sendTo(staff);
				}
				return CompletableFuture.completedFuture(false);
			}
			
			// Le groupe est supérieur au groupe actuelle du joueur
			if (oldGroup.isPresent() && oldGroup.get().resolve().join().isChildOf(contexts, group.get().asSubjectReference())) {
				if (staff.getIdentifier().equals(user.getIdentifier())) {
					EPMessages.USER_PROMOTE_ERROR_DEMOTE_EQUALS.sender()
						.replace("<player>", user.getName())
						.replace("<group>", group.get().getFriendlyIdentifier().orElse(group_name))
						.replace("<parent>", oldGroup.get().getSubjectIdentifier())
						.replace("<type>", type_user.get())
						.sendTo(staff);
				} else {
					EPMessages.USER_PROMOTE_ERROR_DEMOTE_STAFF.sender()
						.replace("<player>", user.getName())
						.replace("<group>", group.get().getFriendlyIdentifier().orElse(group_name))
						.replace("<parent>", oldGroup.get().getSubjectIdentifier())
						.replace("<type>", type_user.get())
						.sendTo(staff);
				}
				return CompletableFuture.completedFuture(false);
			}
		}
		
		return subject.getSubjectData().setGroup(type_user.get(), group.get().asSubjectReference())
			.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return false;
				}
				
				if (staff.getIdentifier().equals(user.getIdentifier())) {
					EPMessages.USER_PROMOTE_EQUALS.sender()
						.replace("<player>", user.getName())
						.replace("<group>", group.get().getFriendlyIdentifier().orElse(group_name))
						.replace("<type>", type_user.get())
						.sendTo(staff);
					
					this.plugin.getService().broadcastMessage(staff, 
						EPMessages.USER_PROMOTE_BROADCAST_EQUALS.sender()
							.replace("<staff>", staff.getName())
							.replace("<player>", user.getName())
							.replace("<group>", group.get().getFriendlyIdentifier().orElse(group_name))
							.replace("<type>", type_user.get()));
				} else {
					EPMessages.USER_PROMOTE_STAFF.sender()
						.replace("<player>", user.getName())
						.replace("<group>", group.get().getFriendlyIdentifier().orElse(group_name))
						.replace("<type>", type_user.get())
						.sendTo(staff);
					
					EPMessages.USER_PROMOTE_PLAYER.sender()
						.replace("<staff>", staff.getName())
						.replace("<group>", group.get().getFriendlyIdentifier().orElse(group_name))
						.replace("<type>", type_user.get())
						.sendTo(user);
			
					this.plugin.getService().broadcastMessage(staff, user.getUniqueId(), 
						EPMessages.USER_PROMOTE_BROADCAST_PLAYER.sender()
							.replace("<staff>", staff.getName())
							.replace("<player>", user.getName())
							.replace("<group>", group.get().getFriendlyIdentifier().orElse(group_name))
							.replace("<type>", type_user.get()));
				}
				return true;
			});
	}
}
