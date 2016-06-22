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
package fr.evercraft.everpermissions.command.user.option;

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
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EPUserDelOption extends ECommand<EverPermissions> {
	
	public EPUserDelOption(final EverPermissions plugin) {
        super(plugin, "permudelo", "manudelv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_DEL_OPTION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_DEL_OPTION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permudelo <" + EAMessages.ARGS_PLAYER.get() + "> "
									 + "<" + EAMessages.ARGS_OPTION.get() + "> "
									 + "[" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/permudelo "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests = null;
		} else if(args.size() == 2) {
			suggests.add("prefix");
			suggests.add("suffix");
		} else if(args.size() == 3) {
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if(args.size() == 2) {
			Optional<User> optUser = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if(optUser.isPresent()){
				// Si la source est un joueur
				if(source instanceof EPlayer) {
					resultat = command(source, optUser.get(), args.get(1), ((EPlayer) source).getWorld().getName());
				// La source n'est pas un joueur
				} else {
					resultat = command(source, optUser.get(), args.get(1), this.plugin.getGame().getServer().getDefaultWorldName());
				}
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
			}
		// On connais le monde
		} else if(args.size() == 3) {
			Optional<User> optPlayer = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				resultat = command(source, optPlayer.get(), args.get(1), args.get(2));
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
	
	private boolean command(final CommandSource staff, final User user, final String type, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		// Monde existant
		if(type_user.isPresent()) {
			EUserSubject subject = this.plugin.getService().getUserSubjects().get(user.getIdentifier());
			// Joueur existant
			if(subject != null) {
				// L'option a bien été supprimé
				if(subject.getSubjectData().setOption(EContextCalculator.getContextWorld(world_name), type, null)) {
					if(staff.equals(user)) {
						staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_OPTION_EQUALS.get()
								.replaceAll("<player>", user.getName())
								.replaceAll("<option>", type)
								.replaceAll("<type>", type_user.get())));
					} else {
						staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_OPTION_STAFF.get()
								.replaceAll("<player>", user.getName())
								.replaceAll("<option>", type)
								.replaceAll("<type>", type_user.get())));
					}
					return true;
				// L'option n'a pas été supprimé
				} else {
					if(staff.equals(user)) {
						staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_OPTION_ERROR_EQUALS.get()
								.replaceAll("<player>", user.getName())
								.replaceAll("<option>", type)
								.replaceAll("<type>", type_user.get())));
					} else {
						staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_OPTION_ERROR_STAFF.get()
								.replaceAll("<player>", user.getName())
								.replaceAll("<option>", type)
								.replaceAll("<type>", type_user.get())));
					}
				}
			// Le joueur n'existe pas dans le service de permissions
			} else {
				staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
			}
		// Le monde est introuvable
		} else {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}