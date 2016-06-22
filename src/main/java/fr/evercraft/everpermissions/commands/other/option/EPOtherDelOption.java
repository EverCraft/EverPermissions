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
package fr.evercraft.everpermissions.commands.other.option;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherDelOption extends ECommand<EverPermissions> {
	
	public EPOtherDelOption(final EverPermissions plugin) {
        super(plugin, "permodelo", "manodelv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.OTHER_DEL_OPTION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.OTHER_DEL_OPTION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permodelo <" + EAMessages.ARGS_SUBJECT.get() + "> "
									 + "<" + EAMessages.ARGS_OPTION.get() + ">")
					.onClick(TextActions.suggestCommand("/permodelo "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(Subject subject : this.plugin.getService().getSytemSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
			for(Subject subject : this.plugin.getService().getCommandBlockSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
		} else if(args.size() == 2) {
			suggests.add("prefix");
			suggests.add("suffix");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 2) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le joueur existe
			if(optSubject.isPresent()){
				resultat = command(source, optSubject.get(), args.get(1));
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.OTHER_NOT_FOUND.get()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final EOtherSubject subject, final String option) {
		// L'option a bien été supprimé
		if(subject.getSubjectData().setOption(new HashSet<Context>(), option, null)) {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.OTHER_DEL_OPTION_PLAYER.get()
					.replaceAll("<subject>", subject.getIdentifier())
					.replaceAll("<option>", option)));
			return true;
		// L'option n'a pas été supprimé
		} else {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.OTHER_DEL_OPTION_ERROR.get()
					.replaceAll("<subject>", subject.getIdentifier())
					.replaceAll("<option>", option)));
		}
		return false;
	}
}
