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
import fr.evercraft.everapi.java.UtilsBoolean;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;

public class EPUserAddPerm extends ECommand<EverPermissions> {
	
	public EPUserAddPerm(final EverPermissions plugin) {
        super(plugin, "permuaddp", "manuaddp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_ADD_PERMISSION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_ADD_PERMISSION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permuaddp <" + EAMessages.ARGS_PLAYER.getString() + "> "
									 + "<" + EAMessages.ARGS_PERMISSION.getString() + "> "
									 + "<true|false> "
									 + "[" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/permuaddp "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests = null;
		} else if (args.size() == 2) {
			suggests.add("ever");
		} else if (args.size() == 3) {
			suggests.add("true");
			suggests.add("false");
		} else if (args.size() == 4) {
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 3) {
			Optional<EUser> optUser = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optUser.isPresent()){
				// Si la source est un joueur
				if (source instanceof EPlayer) {
					resultat = this.command(source, optUser.get(), args.get(1), args.get(2), ((EPlayer) source).getWorld().getName());
				// La source n'est pas un joueur
				} else {
					resultat = this.command(source, optUser.get(), args.get(1), args.get(2), this.plugin.getGame().getServer().getDefaultWorldName());
				}
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EPMessages.PREFIX)
					.sendTo(source);
			}
		// On connais le joueur
		} else if (args.size() == 4) {
			Optional<EUser> optPlayer = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()){
				resultat = this.command(source, optPlayer.get(), args.get(1), args.get(2), args.get(3));
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
	
	private boolean command(final CommandSource staff, final EUser user, final String permission, final String value_name, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		// Monde existant
		if (!type_user.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(staff);
			return false;
		}
		
		Optional<Boolean> value = UtilsBoolean.parseBoolean(value_name);
		// La value n'est pas un boolean
		if (!value.isPresent()) {
			EPMessages.ERROR_BOOLEAN.sender()
				.replace("<boolean>", value_name)
				.sendTo(staff);
			return false;
		}
		
		Set<Context> contexts = EContextCalculator.getContextWorld(world_name);
		
		// La permission n'a pas été ajouté
		if (!user.getSubjectData().setPermission(contexts, permission, Tristate.fromBoolean(value.get()))) {
			// Permission : True
			if (value.get()) {
				if (staff.getIdentifier().equals(user.getIdentifier())) {
					EPMessages.USER_ADD_PERMISSION_TRUE_ERROR_EQUALS.sender()
						.replace("<player>", user.getName())
						.replace("<permission>", permission)
						.replace("<type>", type_user.get())
						.sendTo(staff);
				} else {
					EPMessages.USER_ADD_PERMISSION_TRUE_ERROR_STAFF.sender()
						.replace("<player>", user.getName())
						.replace("<permission>", permission)
						.replace("<type>", type_user.get())
						.sendTo(staff);
				}
			// Permission : False
			} else {
				if (staff.getIdentifier().equals(user.getIdentifier())) {
					EPMessages.USER_ADD_PERMISSION_FALSE_ERROR_EQUALS.sender()
						.replace("<player>", user.getName())
						.replace("<permission>", permission)
						.replace("<type>", type_user.get())
						.sendTo(staff);
				} else {
					EPMessages.USER_ADD_PERMISSION_FALSE_ERROR_STAFF.sender()
						.replace("<player>", user.getName())
						.replace("<permission>", permission)
						.replace("<type>", type_user.get())
						.sendTo(staff);
				}
			}
		}
		
		// Permission : True
		if (value.get()) {
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_ADD_PERMISSION_TRUE_EQUALS.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
				
				this.plugin.getService().broadcastMessage(staff,
					EPMessages.USER_ADD_PERMISSION_TRUE_BROADCAST_EQUALS.sender()
						.replace("<staff>", staff.getName())
						.replace("<player>", user.getName())
						.replace("<permission>", permission)
						.replace("<type>", type_user.get()));
			} else {
				EPMessages.USER_ADD_PERMISSION_TRUE_STAFF.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
				
				this.plugin.getService().broadcastMessage(staff,
					EPMessages.USER_ADD_PERMISSION_TRUE_BROADCAST_PLAYER.sender()
						.replace("<staff>", staff.getName())
						.replace("<player>", user.getName())
						.replace("<permission>", permission)
						.replace("<type>", type_user.get()));
			}
		// Permission : False
		} else {
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_ADD_PERMISSION_FALSE_EQUALS.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
				
				this.plugin.getService().broadcastMessage(staff,
					EPMessages.USER_ADD_PERMISSION_FALSE_BROADCAST_EQUALS.sender()
						.replace("<staff>", staff.getName())
						.replace("<player>", user.getName())
						.replace("<permission>", permission)
						.replace("<type>", type_user.get()));
			} else {
				EPMessages.USER_ADD_PERMISSION_FALSE_STAFF.sender()
					.replace("<player>", user.getName())
					.replace("<permission>", permission)
					.replace("<type>", type_user.get())
					.sendTo(staff);
				
				this.plugin.getService().broadcastMessage(staff,
					EPMessages.USER_ADD_PERMISSION_FALSE_BROADCAST_PLAYER.sender()
						.replace("<staff>", staff.getName())
						.replace("<player>", user.getName())
						.replace("<permission>", permission)
						.replace("<type>", type_user.get()));
			}
		}
		return true;
	}
}
