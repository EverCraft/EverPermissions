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
package fr.evercraft.everpermissions.commands.other.permission;

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
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.java.UtilsBoolean;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.sponge.UtilsChat;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherAddPerm extends ECommand<EverPermissions> {
	
	public EPOtherAddPerm(final EverPermissions plugin) {
        super(plugin, "permoaddp", "manoaddp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("OTHER_ADD_PERMISSION"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("OTHER_ADD_PERMISSION_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permoaddp <" + this.plugin.getEverAPI().getMessages().getArg("subject") + "> "
									 + "<" + this.plugin.getEverAPI().getMessages().getArg("permission") + "> "
									 + "<true|false>")
					.onClick(TextActions.suggestCommand("/permoaddp "))
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
			suggests.add("ever");
		} else if(args.size() == 3) {
			suggests.add("true");
			suggests.add("false");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 3) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le joueur existe
			if(optSubject.isPresent()){
				resultat = command(source, optSubject.get(), args.get(1), args.get(2));
			// Le joueur est introuvable
			} else {
				source.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("OTHER_NOT_FOUND")));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final Subject subject, final String permission, final String value_name) {
		Optional<Boolean> value = UtilsBoolean.parseBoolean(value_name);
		// La value est un boolean
		if(value.isPresent()) {
			// La permission a bien été ajouté
			if(subject.getSubjectData().setPermission(new HashSet<Context>(), permission, Tristate.fromBoolean(value.get()))) {
				// Permission : True
				if(value.get()) {
					staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("OTHER_ADD_PERMISSION_TRUE")
							.replaceAll("<subject>", subject.getIdentifier())
							.replaceAll("<permission>", permission)));
				// Permission : False
				} else {
					staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("OTHER_ADD_PERMISSION_FALSE")
							.replaceAll("<subject>", subject.getIdentifier())
							.replaceAll("<permission>", permission)));
				}
				return true;
			// La permission n'a pas été ajouté
			} else {
				// Permission : True
				if(value.get()) {
					staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("OTHER_ADD_PERMISSION_ERROR_TRUE")
							.replaceAll("<subject>", subject.getIdentifier())
							.replaceAll("<permission>", permission)));
				// Permission : False
				} else {
					staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("OTHER_ADD_PERMISSION_ERROR_FALSE")
							.replaceAll("<subject>", subject.getIdentifier())
							.replaceAll("<permission>", permission)));
				}
			}
		// La value n'est pas un boolean
		} else {
			staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("ERROR_BOOLEAN")));
		}
		return false;
	}
}
