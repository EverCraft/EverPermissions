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

public class EPGroupAddGroup extends ECommand<EverPermissions> {
	
	public EPGroupAddGroup(final EverPermissions plugin) {
        super(plugin, "permgadd", "mangadd");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("GROUP_ADD_GROUP"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("GROUP_ADD_GROUP_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permgadd <" + this.plugin.getEverAPI().getMessages().getArg("group") + "> [" + this.plugin.getEverAPI().getMessages().getArg("world") + "]")
				.onClick(TextActions.suggestCommand("/permgadd "))
				.color(TextColors.RED)
				.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("");
		} else if(args.size() == 2) {
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if(args.size() == 1) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = command(source, args.get(0), ((EPlayer) source).getWorld().getName());
			// La source n'est pas un joueur
			} else {
				resultat = command(source, args.get(0), this.plugin.getGame().getServer().getDefaultWorldName());
			}
		// On connais le monde
		} else if(args.size() == 2) {
			resultat = command(source, args.get(0), args.get(1));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource player, final String group_name, final String world_name) {
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if(type_group.isPresent()) {
			EGroupSubject group = this.plugin.getService().getGroupSubjects().get(group_name);
			// Groupe introuvable
			if(group == null || !group.hasWorld(type_group.get())) {
				// Le groupe a bien été ajouté
				if(this.plugin.getService().getGroupSubjects().register(group_name, type_group.get())) {
					player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GROUP_ADD_GROUP_STAFF")
							.replaceAll("<group>", group_name)
							.replaceAll("<type>", type_group.get())));
					return true;
				// Le groupe n'a pas été ajouté
				} else {
					player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("COMMAND_ERROR")));
				}
			// Groupe existant
			} else {
				player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GROUP_ADD_GROUP_ERROR")
						.replaceAll("<group>", group_name)
						.replaceAll("<type>", type_group.get())));
			}
		// Le monde est introuvable
		} else {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("WORLD_NOT_FOUND")
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
