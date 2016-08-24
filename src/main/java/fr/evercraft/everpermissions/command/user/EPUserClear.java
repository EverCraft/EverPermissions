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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
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
		return Text.builder("/permuclear <" + EAMessages.ARGS_PLAYER.get() + ">")
					.onClick(TextActions.suggestCommand("/permuclear "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests = null;
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if (args.size() == 1) {
			Optional<User> optUser = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if (optUser.isPresent()){
				resultat = command(source, optUser.get());
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final User user) {
		EUserSubject subject = this.plugin.getService().getUserSubjects().get(user.getIdentifier());
		// User existant
		if (subject != null) {
			subject.getSubjectData().clearParents();
			subject.getSubjectData().clearSubParents();
			subject.getSubjectData().clearPermissions();
			subject.getSubjectData().clearOptions();
			subject.reload();
			if (staff.equals(user)) {
				staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_CLEAR_EQUALS.get()
						.replaceAll("<player>", user.getName())));
				if (EPMessages.USER_CLEAR_BROADCAST_EQUALS.has()) {
					this.plugin.getService().broadcastMessage(staff,
						EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_CLEAR_STAFF.get()
							.replaceAll("<staff>", staff.getName())
							.replaceAll("<player>", user.getName())));
				}
			} else {
				staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_CLEAR_STAFF.get()
						.replaceAll("<player>", user.getName())));
				if (EPMessages.USER_CLEAR_BROADCAST_PLAYER.has()) {
					this.plugin.getService().broadcastMessage(staff, user.getUniqueId(),
						EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_CLEAR_BROADCAST_PLAYER.get()
							.replaceAll("<staff>", staff.getName())
							.replaceAll("<player>", user.getName())));
				}
			}
		} else {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
		}
		return true;
	}
}
