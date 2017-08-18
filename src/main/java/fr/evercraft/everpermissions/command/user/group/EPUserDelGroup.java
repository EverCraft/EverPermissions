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
import org.spongepowered.api.service.permission.Subject;
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
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EPUserDelGroup extends ECommand<EverPermissions> {
	
	public EPUserDelGroup(final EverPermissions plugin) {
        super(plugin, "permudelg", "permudel", "manudelg", "manudel");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_DEL_GROUP.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_DEL_GROUP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permudel <" + EAMessages.ARGS_PLAYER.getString() + "> "
									+ "[" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/permudel "))
					.color(TextColors.RED).build();
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
		if (args.size() == 1) {
			Optional<EUser> optUser = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optUser.isPresent()){
				// Si la source est un joueur
				if (source instanceof EPlayer) {
					return this.command(source, optUser.get(), ((EPlayer) source).getWorld().getName());
				// La source n'est pas un joueur
				} else {
					return this.command(source, optUser.get(), this.plugin.getGame().getServer().getDefaultWorldName());
				}
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EPMessages.PREFIX)
					.replace("<player>", args.get(0))
					.sendTo(source);
			}
		// On connais le monde
		} else if (args.size() == 2) {
			Optional<EUser> optPlayer = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()){
				return this.command(source, optPlayer.get(), args.get(1));
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
	
	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if (!type_user.isPresent() || !type_group.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		EUserSubject subject = this.plugin.getService().getUserSubjects().get(user.getIdentifier());
		// User inexistant
		if (subject == null) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("<player>", user.getIdentifier())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		Set<Context> contexts = EContextCalculator.of(world_name);
		Optional<Subject> group = subject.getSubjectData().getGroup(contexts);
		
		// Le groupe n'a pas été supprimé
		if (!group.isPresent() || !subject.getSubjectData().removeParent(contexts)) {
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_DEL_GROUP_ERROR_EQUALS.sender()
					.replace("<player>", user.getName())
					.replace("<type>", type_user.get())
					.sendTo(staff);
			} else {
				EPMessages.USER_DEL_GROUP_ERROR_STAFF.sender()
					.replace("<player>", user.getName())
					.replace("<type>", type_user.get())
					.sendTo(staff);
			}
			return CompletableFuture.completedFuture(false);
		}
		
		if (staff.getIdentifier().equals(user.getIdentifier())) {
			EPMessages.USER_DEL_GROUP_EQUALS.sender()
				.replace("<player>", user.getName())
				.replace("<group>", group.get().getIdentifier())
				.replace("<type>", type_user.get())
				.sendTo(staff);

			this.plugin.getService().broadcastMessage(staff,
				EPMessages.USER_DEL_GROUP_BROADCAST_EQUALS.sender()
					.replace("<staff>", staff.getName())
					.replace("<player>", user.getName())
					.replace("<group>", group.get().getIdentifier())
					.replace("<type>", type_user.get()));
		} else {
			EPMessages.USER_DEL_GROUP_STAFF.sender()
				.replace("<player>", user.getName())
				.replace("<group>", group.get().getIdentifier())
				.replace("<type>", type_user.get())
				.sendTo(staff);
			
			EPMessages.USER_DEL_GROUP_PLAYER.sender()
				.replace("<staff>", staff.getName())
				.replace("<group>", group.get().getIdentifier())
				.replace("<type>", type_user.get())
				.sendTo(user);
			
			if (EPMessages.USER_DEL_GROUP_BROADCAST_PLAYER.has()) {
				this.plugin.getService().broadcastMessage(staff, user.getUniqueId(), 
					EPMessages.USER_DEL_GROUP_BROADCAST_PLAYER.sender()
						.replace("<staff>", staff.getName())
						.replace("<player>", user.getName())
						.replace("<group>", group.get().getIdentifier())
						.replace("<type>", type_user.get()));
			}
		}
		return CompletableFuture.completedFuture(true);
	}
}
