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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EPUserDemoteGroup extends ECommand<EverPermissions> {
	
	public EPUserDemoteGroup(final EverPermissions plugin) {
        super(plugin, "permudemote", "mandemote");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_DEMOTE_GROUP.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_DEMOTE_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permudemote <" + EAMessages.ARGS_PLAYER.getString() + "> "
									   + "<" + EAMessages.ARGS_GROUP.getString() + "> "
									   + "[" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/permudemote "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests = null;
		} else if (args.size() == 2) {
			for (Subject subject : this.plugin.getService().getGroupSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
		} else if (args.size() == 3) {
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
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
		// On connait le monde
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
	
	private boolean command(final CommandSource staff, final EUser user, final String group_name, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if (!type_user.isPresent() || !type_group.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(staff);
			return false;
		}
		
		EGroupSubject group = this.plugin.getService().getGroupSubjects().get(group_name);
		// Groupe introuvable
		if (group == null || !group.hasWorld(type_group.get())) {
			EPMessages.GROUP_NOT_FOUND_WORLD.sender()
				.replace("<group>", group_name)
				.replace("<type>", type_group.get())
				.sendTo(staff);
			return false;
		}
		
		EUserSubject subject = this.plugin.getService().getUserSubjects().get(user.getIdentifier());
		// User inexistant
		if (subject == null) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.sendTo(staff);
			return false;
		}
		
		Set<Context> contexts = EContextCalculator.getContextWorld(world_name);
		Optional<Subject> parent = subject.getSubjectData().getParent(contexts);
		
		// Le groupe du joueur est égale au nouveau groupe
		if (parent.isPresent() && parent.get().equals(group)) {
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_DEMOTE_ERROR_EQUALS.sender()
					.replace("<player>", user.getName())
					.replace("<group>", group.getIdentifier())
					.replace("<type>", type_user.get())
					.sendTo(staff);
			} else {
				EPMessages.USER_DEMOTE_ERROR_STAFF.sender()
					.replace("<player>", user.getName())
					.replace("<group>", group.getIdentifier())
					.replace("<type>", type_user.get())
					.sendTo(staff);
			}
			return false;
		}
		
		// Le groupe est supérieur au groupe actuelle du joueur
		if (parent.isPresent() && group.isChildOf(EContextCalculator.getContextWorld(type_group.get()), parent.get())) {
			if (staff.getIdentifier().equals(user.getIdentifier())) {
				EPMessages.USER_DEMOTE_ERROR_PROMOTE_EQUALS.sender()
					.replace("<player>", user.getName())
					.replace("<group>", group.getIdentifier())
					.replace("<parent>", parent.get().getIdentifier())
					.replace("<type>", type_user.get())
					.sendTo(staff);
			} else {
				EPMessages.USER_DEMOTE_ERROR_PROMOTE_STAFF.sender()
					.replace("<player>", user.getName())
					.replace("<group>", group.getIdentifier())
					.replace("<parent>", parent.get().getIdentifier())
					.replace("<type>", type_user.get())
					.sendTo(staff);
			}
			return false;
		}
		
		// Le groupe n'a pas été changé
		if (!user.getSubjectData().addParent(contexts, group)) {
			EAMessages.COMMAND_ERROR.sender()
				.prefix(EPMessages.PREFIX)
				.sendTo(staff);
			return false;
		}
		
		if (staff.getIdentifier().equals(user.getIdentifier())) {
			EPMessages.USER_DEMOTE_EQUALS.sender()
				.replace("<player>", user.getName())
				.replace("<group>", group.getIdentifier())
				.replace("<type>", type_user.get())
				.sendTo(staff);
			
			this.plugin.getService().broadcastMessage(staff,
				EPMessages.USER_DEMOTE_BROADCAST_EQUALS.sender()
					.replace("<staff>", staff.getName())
					.replace("<player>", user.getName())
					.replace("<group>", user.getIdentifier())
					.replace("<type>", type_user.get()));
		} else {
			EPMessages.USER_DEMOTE_STAFF.sender()
				.replace("<player>", user.getName())
				.replace("<group>", group.getIdentifier())
				.replace("<type>", type_user.get())
				.sendTo(staff);
			
			EPMessages.USER_DEMOTE_PLAYER.sender()
				.replace("<staff>", staff.getName())
				.replace("<group>", group.getIdentifier())
				.replace("<type>", type_user.get())
				.sendTo(user);
			
			this.plugin.getService().broadcastMessage(staff, user.getUniqueId(), 
				EPMessages.USER_DEMOTE_BROADCAST_PLAYER.sender()
					.replace("<staff>", staff.getName())
					.replace("<player>", user.getName())
					.replace("<group>", user.getIdentifier())
					.replace("<type>", type_user.get()));
		}
		return true;
	}
}
