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

public class EPUserListGroup extends ECommand<EverPermissions> {
	
	public EPUserListGroup(final EverPermissions plugin) {
        super(plugin, "permulist", "permulistg", "manwhois");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_LIST_GROUP.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_LIST_GROUP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permulist <" + EAMessages.ARGS_PLAYER.getString() + "> "
									 + "[" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/permulist "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.getAllUsers(args.get(0), source);
		} else if (args.size() == 2) {
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
					.sendTo(source);
			}
		// On connait le monde
		} else if (args.size() == 2) {
			Optional<EUser> optPlayer = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()){
				return this.command(source, optPlayer.get(), args.get(1));
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
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		// Monde existant
		if (!type_user.isPresent()) {
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
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		List<Text> list = new ArrayList<Text>();
		Set<Context> contexts = EContextCalculator.getContextWorld(world_name);
		Optional<Subject> group = subject.getSubjectData().getParent(contexts);
		
		// Le groupe
		if (group.isPresent()) {
			list.add(EPMessages.USER_LIST_GROUP_GROUP.getFormat().toText("<group>", group.get().getIdentifier()));
		} else {
			list.add(EPMessages.USER_LIST_GROUP_GROUP_EMPTY.getText());
		}
		
		// Les sous-groupes
		List<Subject> groups = subject.getSubjectData().getSubParents(contexts);
		if (groups.isEmpty()) {
			list.add(EPMessages.USER_LIST_GROUP_SUBGROUP_EMPTY.getText());
		} else {
			list.add(EPMessages.USER_LIST_GROUP_SUBGROUP.getText());
			for (Subject sub : groups) {
				list.add(EPMessages.USER_LIST_GROUP_SUBGROUP_LINE.getFormat().toText("<group>", sub.getIdentifier()));
			}
		}
		
		// Les groupes temporaires
		groups = subject.getTransientSubjectData().getParents(contexts);
		if (!groups.isEmpty()) {
			list.add(EPMessages.USER_LIST_GROUP_TRANSIENT.getText());
			for (Subject tempo : groups) {
				list.add(EPMessages.USER_LIST_GROUP_TRANSIENT_LINE.getFormat().toText("<group>", tempo.getIdentifier()));
			}
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.USER_LIST_GROUP_TITLE.getFormat().toText(
					"<player>", user.getName(),
					"<type>", type_user.get()), 
				list, staff);
		return CompletableFuture.completedFuture(true);
	}
}
