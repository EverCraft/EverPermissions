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
import java.util.Map;
import java.util.Map.Entry;
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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherListOption extends ECommand<EverPermissions> {
	
	public EPOtherListOption(final EverPermissions plugin) {
        super(plugin, "permolisto", "manolistv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("OTHER_LIST_OPTION"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("OTHER_LIST_OPTION_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permolisto <" + this.plugin.getEverAPI().getMessages().getArg("subject") + ">")
					.onClick(TextActions.suggestCommand("/permolisto "))
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
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 1) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le joueur existe
			if(optSubject.isPresent()){
				resultat = command(source, optSubject.get());
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("OTHER_NOT_FOUND")));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final EOtherSubject subject) {
		Set<Context> contexts = new HashSet<Context>();
		List<Text> list = new ArrayList<Text>();
		
		// La liste des options
		Map<String, String> options = subject.getSubjectData().getOptions(contexts);
		if(options.isEmpty()) {
			list.add(this.plugin.getMessages().getText("OTHER_LIST_OPTION_OPTION_EMPTY"));
		} else {
			list.add(this.plugin.getMessages().getText("OTHER_LIST_OPTION_OPTION"));
			for(Entry<String, String> permission : options.entrySet()) {
				list.add(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("OTHER_LIST_OPTION_OPTION_LINE")
							.replaceAll("<option>", permission.getKey()))
						.replace("<value>", Text.builder(permission.getValue())
							.color(EChat.getTextColor(this.plugin.getMessages().getMessage("OTHER_LIST_OPTION_OPTION_NAME_COLOR")))
							.build())
						.build());
			}
		}
		
		// La liste des options temporaires
		options = subject.getTransientSubjectData().getOptions(contexts);
		if(!options.isEmpty()) {
			list.add(this.plugin.getMessages().getText("OTHER_LIST_OPTION_TRANSIENT"));
			for(Entry<String, String> permission : options.entrySet()) {
				list.add(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("OTHER_LIST_OPTION_TRANSIENT_LINE")
							.replaceAll("<option>", permission.getKey()))
						.replace("<value>", Text.builder(permission.getValue())
							.color(EChat.getTextColor(this.plugin.getMessages().getMessage("OTHER_LIST_OPTION_TRANSIENT_NAME_COLOR")))
							.build())
						.build());
			}
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(
				this.plugin.getMessages().getMessage("OTHER_LIST_OPTION_TITLE")
				.replaceAll("<subject>", subject.getIdentifier())), 
				list, staff);
		return true;
	}
}
