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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EPUserClear extends ECommand<EverPermissions> {
	
	public EPUserClear(final EverPermissions plugin) {
        super(plugin, "permuclear", "manuclear");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_CLEAR.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_CLEAR_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permuclear <" + EAMessages.ARGS_PLAYER.getString() + ">")
					.onClick(TextActions.suggestCommand("/permuclear "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return this.getAllPlayers(source, false);
		}
		return Arrays.asList();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if (args.size() == 1) {
			Optional<EUser> optUser = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optUser.isPresent()){
				resultat = this.command(source, optUser.get());
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EPMessages.PREFIX)
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final EUser user) {
		EUserSubject subject = this.plugin.getService().getUserSubjects().get(user.getIdentifier());
		// User inexistant
		if (subject == null) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.sendTo(staff);
			return false;
		}
		
		subject.getSubjectData().clearParents();
		subject.getSubjectData().clearSubParents();
		subject.getSubjectData().clearPermissions();
		subject.getSubjectData().clearOptions();
		subject.reload();
		if (staff.getIdentifier().equals(user.getIdentifier())) {
			EPMessages.USER_CLEAR_EQUALS.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
			this.plugin.getService().broadcastMessage(staff,
				EPMessages.USER_CLEAR_BROADCAST_EQUALS.sender()
					.replace("<staff>", staff.getName())
					.replace("<player>", user.getName()));
		} else {
			EPMessages.USER_CLEAR_STAFF.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
			EPMessages.USER_CLEAR_PLAYER.sender()
				.replace("<staff>", staff.getName())
				.replace("<player>", user.getName())
				.sendTo(user);
			this.plugin.getService().broadcastMessage(staff, user.getUniqueId(),
				EPMessages.USER_CLEAR_BROADCAST_PLAYER.sender()
					.replace("<staff>", staff.getName())
					.replace("<player>", user.getName()));
		}
		return true;
	}
}
