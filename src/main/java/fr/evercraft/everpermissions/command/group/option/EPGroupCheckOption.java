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
package fr.evercraft.everpermissions.command.group.option;

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

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPGroupCheckOption extends ECommand<EverPermissions> {
	
	public EPGroupCheckOption(final EverPermissions plugin) {
        super(plugin, "permgchecko", "mangcheckv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_CHECK_OPTION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_CHECK_OPTION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permgchecko <" + EAMessages.ARGS_GROUP.getString() + "> "
									   + "<" + EAMessages.ARGS_OPTION.getString() + "> "
									   + "[" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/permgchecko "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.getAllGroups();
		} else if (args.size() == 2) {
			return Arrays.asList("prefix", "suffix");
		} else if (args.size() == 3) {
			return this.getAllWorlds();
		}
		return Arrays.asList();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if (args.size() == 2) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.command(source, args.get(0), args.get(1), ((EPlayer) source).getWorld().getName());
			// La source n'est pas un joueur
			} else {
				resultat = this.command(source, args.get(0), args.get(1), this.plugin.getGame().getServer().getDefaultWorldName());
			}
		// On connais le monde
		} else if (args.size() == 3) {
			resultat = this.command(source, args.get(0), args.get(1), args.get(2));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource player, final String group_name, final String option, final String world_name) {
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde introuvable
		if (!type_group.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(player);
			return false;
		}
		
		EGroupSubject group = this.plugin.getService().getGroupSubjects().get(group_name);
		// Groupe introuvable
		if (group == null || !group.hasWorld(type_group.get())) {
			EPMessages.GROUP_NOT_FOUND_WORLD.sender()
				.replace("<group>", group_name)
				.replace("<type>", type_group.get())
				.sendTo(player);
			return false;
		}


		Set<Context> contexts = EContextCalculator.getContextWorld(type_group.get());
		String name = group.getSubjectData().getOptions(contexts).get(option);
		// Si il y a une valeur
		if (name == null) {
			EPMessages.GROUP_CHECK_OPTION_DEFINED.sender()
				.replace("<group>", group.getIdentifier())
				.replace("<option>", option)
				.replace("<type>", type_group.get())
				.replace("<value>", Text.of(name))
				.sendTo(player);
		// Il n'y a pas de valeur
		} else {
			EPMessages.GROUP_CHECK_OPTION_UNDEFINED.sender()
				.replace("<group>", group.getIdentifier())
				.replace("<option>", option)
				.replace("<type>", type_group.get())
				.sendTo(player);
		}
		return true;
	}
}
