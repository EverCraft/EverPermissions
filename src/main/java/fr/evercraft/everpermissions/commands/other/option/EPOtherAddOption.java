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

import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.sponge.UtilsChat;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherAddOption extends ECommand<EverPermissions> {
	
	public EPOtherAddOption(final EverPermissions plugin) {
        super(plugin, "permoaddo", "manoaddv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("OTHER_ADD_OPTION"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("OTHER_ADD_OPTION_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permoaddo <" + this.plugin.getEverAPI().getMessages().getArg("subject") + "> "
									 + "<" + this.plugin.getEverAPI().getMessages().getArg("option") + "> "
									 + "<" + this.plugin.getEverAPI().getMessages().getArg("value") + ">")
					.onClick(TextActions.suggestCommand("/permoaddo "))
					.color(TextColors.RED).build();
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
		} else if(args.size() == 3) {
			suggests.add("&7");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 3) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le subject existe
			if(optSubject.isPresent()){
				resultat = command(source, optSubject.get(), args.get(1), args.get(2));
			// Le subject est introuvable
			} else {
				source.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("OTHER_NOT_FOUND")));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final EOtherSubject subject, final String option, String value) {
		value = value.replaceAll("_", " ");
		// L'option a bien été ajouté
		if(subject.getSubjectData().setOption(new HashSet<Context>(), option, value)) {
			staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
					.append(this.plugin.getMessages().getMessage("OTHER_ADD_OPTION_PLAYER")
						.replaceAll("<subject>", subject.getIdentifier())
						.replaceAll("<option>", option))
					.replace("<value>", Text.builder(value)
						.color(UtilsChat.getTextColor(this.plugin.getMessages().getMessage("OTHER_ADD_OPTION_NAME_COLOR")))
						.build())
					.build());
			return true;
		// L'option n'a pas été ajouté
		} else {
			staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("COMMAND_ERROR")));
		}
		return false;
	}
}
