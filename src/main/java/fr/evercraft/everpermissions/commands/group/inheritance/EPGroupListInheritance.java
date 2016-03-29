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
package fr.evercraft.everpermissions.commands.group.inheritance;

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

import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsChat;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPGroupListInheritance extends ECommand<EverPermissions> {
	
	public EPGroupListInheritance(final EverPermissions plugin) {
        super(plugin, "permglisti", "manglisti");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("GROUP_LIST_INHERITANCE"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("GROUP_LIST_INHERITANCE_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permglisti " + this.plugin.getEverAPI().getMessages().getArg("group") + "> [" + this.plugin.getEverAPI().getMessages().getArg("world") + "]")
					.onClick(TextActions.suggestCommand("/permglisti "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(Subject subject : this.plugin.getService().getGroupSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
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
		// On connait le monde
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
			Set<Context> contexts = EContextCalculator.getContextWorld(type_group.get());
			EGroupSubject group = this.plugin.getService().getGroupSubjects().get(group_name);
			// Groupe existant
			if(group != null && group.hasWorld(type_group.get())) {
				List<Text> list = new ArrayList<Text>();

				// La liste des inheritances
				List<Subject> groups = group.getSubjectData().getParents(contexts);
				if(groups.isEmpty()) {
					list.add(this.plugin.getMessages().getText("GROUP_LIST_INHERITANCE_INHERITANCE_EMPTY"));
				} else {
					list.add(this.plugin.getMessages().getText("GROUP_LIST_INHERITANCE_INHERITANCE"));
					for(Subject inheritance : groups) {
						list.add(UtilsChat.of(this.plugin.getMessages().getMessage("GROUP_LIST_INHERITANCE_INHERITANCE_LINE").replaceAll("<inheritance>", inheritance.getIdentifier())));
					}
				}
				
				// La liste des inheritances temporaires
				groups = group.getTransientSubjectData().getParents(contexts);
				if(!groups.isEmpty()) {
					list.add(this.plugin.getMessages().getText("GROUP_LIST_INHERITANCE_TRANSIENT"));
					for(Subject inheritance : groups) {
						list.add(UtilsChat.of(this.plugin.getMessages().getMessage("GROUP_LIST_INHERITANCE_TRANSIENT_LINE").replaceAll("<inheritance>", inheritance.getIdentifier())));
					}
				}
				
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(UtilsChat.of(
						this.plugin.getMessages().getMessage("GROUP_LIST_INHERITANCE_TITLE")
						.replaceAll("<group>", group.getIdentifier())
						.replaceAll("<type>", type_group.get())), 
						list, player);
				return true;
			// Le groupe n'existe pas dans le service de permissions
			} else {
				player.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GROUP_NOT_FOUND")
						.replaceAll("<group>", group_name)
						.replaceAll("<type>", type_group.get())));
			}
		// Le monde est introuvable
		} else {
			player.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("WORLD_NOT_FOUND")
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
