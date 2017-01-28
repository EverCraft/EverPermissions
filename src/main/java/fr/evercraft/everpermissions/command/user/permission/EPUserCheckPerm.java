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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;

public class EPUserCheckPerm extends ECommand<EverPermissions> {
	
	public EPUserCheckPerm(final EverPermissions plugin) {
        super(plugin, "permucheckp", "manucheckp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_CHECK_PERMISSION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_CHECK_PERMISSION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permucheckp <" + EAMessages.ARGS_PLAYER.getString() + "> "
									   + "<" + EAMessages.ARGS_PERMISSION.getString() + "> "
									   + "[" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/permucheckp "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.getAllPlayers(source, false);
		} else if (args.size() == 2) {
			return this.getAllPermissions();
		} else if (args.size() == 3) {
			return this.getAllWorlds();
		}
		return Arrays.asList();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 2) {
			Optional<EUser> optUser = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optUser.isPresent()){
				// Si la source est un joueur
				if (source instanceof EPlayer) {
					resultat = this.command(source, optUser.get(), args.get(1), ((EPlayer) source).getWorld().getName());
				// La source n'est pas un joueur
				} else {
					resultat = this.command(source, optUser.get(), args.get(1), this.plugin.getGame().getServer().getDefaultWorldName());
				}
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EPMessages.PREFIX)
					.sendTo(source);
			}
		// On connais le joueur
		} else if (args.size() == 3) {
			Optional<EUser> optPlayer = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()){
				resultat = this.command(source, optPlayer.get(), args.get(1), args.get(2));
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
	
	private boolean command(final CommandSource staff, final EUser user, final String permission, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		// Monde existant
		if (!type_user.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(staff);
			return false;
		}
		
		Set<Context> contexts = EContextCalculator.getContextWorld(world_name);
		
		Tristate value = user.getPermissionValue(contexts, permission);
		// Permission : True
		if (value.equals(Tristate.TRUE)) {
			// La source et le joueur sont identique
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_CHECK_PERMISSION_TRUE_EQUALS.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
			// La source et le joueur ne sont pas identique
			} else {
				EPMessages.USER_CHECK_PERMISSION_TRUE_STAFF.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
			}
		// Permission : False
		} else if (value.equals(Tristate.FALSE)) {
			// La source et le joueur sont identique
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_CHECK_PERMISSION_FALSE_EQUALS.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
			// La source et le joueur ne sont pas identique
			} else {
				EPMessages.USER_CHECK_PERMISSION_FALSE_STAFF.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
			}
		// Permission : Undefined
		} else {
			// La source et le joueur sont identique
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_CHECK_PERMISSION_UNDEFINED_EQUALS.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
			// La source et le joueur ne sont pas identique
			} else {
				EPMessages.USER_CHECK_PERMISSION_UNDEFINED_STAFF.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
			}
		}
		return true;
	}
}
