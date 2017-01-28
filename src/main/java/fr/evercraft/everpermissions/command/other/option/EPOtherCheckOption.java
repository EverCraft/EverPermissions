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
package fr.evercraft.everpermissions.command.other.option;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherCheckOption extends ECommand<EverPermissions> {
	
	public EPOtherCheckOption(final EverPermissions plugin) {
        super(plugin, "permochecko", "manocheckv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.OTHER_CHECK_OPTION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.OTHER_CHECK_OPTION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permochecko <" + EAMessages.ARGS_SUBJECT.getString() + "> "
									   + "<" + EAMessages.ARGS_OPTION.getString() + ">")
					.onClick(TextActions.suggestCommand("/permochecko "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.plugin.getService().getSuggestsOthers();
		} else if (args.size() == 2) {
			return Arrays.asList("prefix", "suffix");
		}
		return Arrays.asList();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if (args.size() == 2) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le subject existe
			if (optSubject.isPresent()){
				resultat = this.command(source, optSubject.get(), args.get(1));
			// Le subject est introuvable
			} else {
				EPMessages.OTHER_NOT_FOUND.sender()
					.replace("<other>", args.get(0))
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final EOtherSubject subject, final String type) {
		String name = subject.getSubjectData().getOptions(EContextCalculator.EMPTY).get(type);
		// Il n'y a pas de valeur
		if (name == null) {
			EPMessages.OTHER_CHECK_OPTION_UNDEFINED.sender()
				.replace("<subject>", subject.getIdentifier())
				.replace("<option>", type)
				.sendTo(staff);
			return false;
		}
		
		EPMessages.OTHER_CHECK_OPTION_DEFINED.sender()
			.replace("<subject>", subject.getIdentifier())
			.replace("<option>", type)
			.replace("<value>", Text.of(name))
			.sendTo(staff);
		return true;
	}
}
