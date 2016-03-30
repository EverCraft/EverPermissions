/**
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
package fr.evercraft.everpermissions.commands.group.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPGroupListGroup extends ECommand<EverPermissions> {
	
	public EPGroupListGroup(final EverPermissions plugin) {
        super(plugin, "permglist", "permglistg", "manglist");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("GROUP_LIST_GROUP"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("GROUP_LIST_GROUP_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permglist [" + this.plugin.getEverAPI().getMessages().getArg("world") + "]").onClick(TextActions.suggestCommand("/permglist "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = command(source, ((EPlayer) source).getWorld().getName());
			// La source n'est pas un joueur
			} else {
				resultat = command(source, this.plugin.getGame().getServer().getDefaultWorldName());
			}
		// On connait le monde
		} else if(args.size() == 1) {
			resultat = command(source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource player, final String world_name) {
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if(type_group.isPresent()) {
			List<Text> list = new ArrayList<Text>();
			Set<EGroupSubject> groups = this.plugin.getService().getGroupSubjects().getGroups(type_group.get());
			
			// Aucun groupe
			if(groups.isEmpty()) {
				list.add(this.plugin.getMessages().getText("GROUP_LIST_GROUP_EMPTY"));
			// Les groupes
			} else {
				// Le groupe par défaut
				Optional<EGroupSubject> subject = this.plugin.getService().getGroupSubjects().getDefaultGroup(type_group.get());
				if(subject.isPresent()) {
					list.add(EChat.of(this.plugin.getMessages().getMessage("GROUP_LIST_GROUP_DEFAULT")
							.replaceAll("<group>", subject.get().getIdentifier())));
				}
				
				// La liste des groupes
				list.add(this.plugin.getMessages().getText("GROUP_LIST_GROUP_NAME"));
				for (EGroupSubject group : groups) {
					list.add(EChat.of(this.plugin.getMessages().getMessage("GROUP_LIST_GROUP_LINE")
							.replaceAll("<group>", group.getIdentifier())));
				}
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(
					this.plugin.getMessages().getMessage("GROUP_LIST_GROUP_TITLE")
					.replaceAll("<type>", type_group.get())), 
					list, player);
			return true;
		// Le monde est introuvable
		} else {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("WORLD_NOT_FOUND")
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
