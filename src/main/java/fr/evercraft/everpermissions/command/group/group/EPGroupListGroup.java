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
package fr.evercraft.everpermissions.command.group.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPGroupListGroup extends ECommand<EverPermissions> {
	
	public EPGroupListGroup(final EverPermissions plugin) {
        super(plugin, "permglist", "permglistg", "manglist");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_LIST_GROUP.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_LIST_GROUP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permglist [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/permglist "))
					.color(TextColors.RED).build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			return this.getAllWorlds();
		}
		return suggests;
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le monde
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.command(source, ((EPlayer) source).getWorld().getName());
			// La source n'est pas un joueur
			} else {
				return this.command(source, this.plugin.getGame().getServer().getDefaultWorldName());
			}
		// On connait le monde
		} else if (args.size() == 1) {
			return this.command(source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> command(final CommandSource player, final String world_name) {
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde introuvable
		if (!type_group.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		List<Text> list = new ArrayList<Text>();
		Set<EGroupSubject> groups = this.plugin.getService().getGroupSubjects().getGroups(type_group.get());
		
		// Aucun groupe
		if (groups.isEmpty()) {
			list.add(EPMessages.GROUP_LIST_GROUP_EMPTY.getText());
		// Les groupes
		} else {
			// Le groupe par défaut
			Optional<EGroupSubject> subject = this.plugin.getService().getGroupSubjects().getDefaultGroup(type_group.get());
			if (subject.isPresent()) {
				list.add(EPMessages.GROUP_LIST_GROUP_DEFAULT.getFormat()
						.toText("<group>", subject.get().getIdentifier()));
			}
			
			// La liste des groupes
			list.add(EPMessages.GROUP_LIST_GROUP_NAME.getText());
			for (EGroupSubject group : groups) {
				list.add(EPMessages.GROUP_LIST_GROUP_LINE.getFormat()
						.toText("<group>", group.getIdentifier()));
			}
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.GROUP_LIST_GROUP_TITLE.getFormat()
					.toText("<type>", type_group.get()), 
				list, player);
		return CompletableFuture.completedFuture(true);
	}
}
