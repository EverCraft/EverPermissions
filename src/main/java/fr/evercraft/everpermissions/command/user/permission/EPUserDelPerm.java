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
package fr.evercraft.everpermissions.command.user.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;

public class EPUserDelPerm extends ECommand<EverPermissions> {
	
	public EPUserDelPerm(final EverPermissions plugin) {
        super(plugin, "permudelp", "manudelp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_DEL_PERMISSION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_DEL_PERMISSION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permudelp <" + EAMessages.ARGS_PLAYER.get() + "> "
									 + "<" + EAMessages.ARGS_PERMISSION.get() + "> "
									 + "[" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/permudelp "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests = null;
		} else if(args.size() == 2) {
			suggests.add("ever");
		} else if(args.size() == 3) {
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
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
		// On connais le joueur
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
	
	private boolean command(final CommandSource staff, final User user, final String permission, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		// Monde existant
		if(type_user.isPresent()) {
			// La permission a bien été supprimé
			if(user.getSubjectData().setPermission(EContextCalculator.getContextWorld(world_name), permission, Tristate.UNDEFINED)) {
				if(staff.equals(user)) {
					staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_PERMISSION_EQUALS.get()
							.replaceAll("<player>", user.getName())
							.replaceAll("<permission>", permission)
							.replaceAll("<type>", type_user.get())));
					
					if(EPMessages.USER_DEL_PERMISSION_BROADCAST_EQUALS.has()) {
						this.plugin.getService().broadcastMessage(staff,
							EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_PERMISSION_BROADCAST_EQUALS.get()
								.replaceAll("<staff>", staff.getName())
								.replaceAll("<player>", user.getName())
								.replaceAll("<permission>", permission)
								.replaceAll("<type>", type_user.get())));
					}
				} else {
					staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_PERMISSION_STAFF.get()
							.replaceAll("<player>", user.getName())
							.replaceAll("<permission>", permission)
							.replaceAll("<type>", type_user.get())));
					
					if(EPMessages.USER_DEL_PERMISSION_BROADCAST_PLAYER.has()) {
						this.plugin.getService().broadcastMessage(staff,
							EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_PERMISSION_BROADCAST_PLAYER.get()
								.replaceAll("<staff>", staff.getName())
								.replaceAll("<player>", user.getName())
								.replaceAll("<permission>", permission)
								.replaceAll("<type>", type_user.get())));
					}
				}
				return true;
			// La permission n'a pas été supprimé
			} else {
				if(staff.equals(user)) {
					staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_PERMISSION_ERROR_EQUALS.get()
							.replaceAll("<player>", user.getName())
							.replaceAll("<permission>", permission)
							.replaceAll("<type>", type_user.get())));
				} else {
					staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.USER_DEL_PERMISSION_ERROR_STAFF.get()
							.replaceAll("<player>", user.getName())
							.replaceAll("<permission>", permission)
							.replaceAll("<type>", type_user.get())));
				}
			}
		// Le monde est introuvable
		} else {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
