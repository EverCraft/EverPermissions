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
package fr.evercraft.everpermissions.command.other.permission;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsBoolean;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherAddPerm extends ECommand<EverPermissions> {
	
	public EPOtherAddPerm(final EverPermissions plugin) {
        super(plugin, "permoaddp", "manoaddp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.OTHER_ADD_PERMISSION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.OTHER_ADD_PERMISSION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permoaddp <" + EAMessages.ARGS_SUBJECT.getString() + "> "
									 + "<" + EAMessages.ARGS_PERMISSION.getString() + "> "
									 + "<true|false>")
					.onClick(TextActions.suggestCommand("/permoaddp "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.plugin.getService().getSuggestsOthers();
		} else if (args.size() == 2) {
			return this.getAllPermissions();
		} else if (args.size() == 3) {
			return Arrays.asList("true", "false");
		}
		return Arrays.asList();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 3) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le joueur existe
			if (optSubject.isPresent()){
				return this.command(source, optSubject.get(), args.get(1), args.get(2));
			// Le joueur est introuvable
			} else {
				EPMessages.OTHER_NOT_FOUND.sender()
					.replace("<other>", args.get(0))
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> command(final CommandSource staff, final Subject subject, final String permission, final String value_name) {
		Optional<Boolean> value = UtilsBoolean.parseBoolean(value_name);
		// La value n'est pas un boolean
		if (!value.isPresent()) {
			EPMessages.ERROR_BOOLEAN.sender()
				.replace("<boolean>", value_name)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		// La permission n'a pas été ajouté
		if (!subject.getSubjectData().setPermission(EContextCalculator.EMPTY, permission, Tristate.fromBoolean(value.get()))) {
			// Permission : True
			if (value.get()) {
				EPMessages.OTHER_ADD_PERMISSION_ERROR_TRUE.sender()
					.replace("<subject>", subject.getIdentifier())
					.replace("<permission>", permission)
					.sendTo(staff);
			// Permission : False
			} else {
				EPMessages.OTHER_ADD_PERMISSION_ERROR_FALSE.sender()
					.replace("<subject>", subject.getIdentifier())
					.replace("<permission>", permission)
					.sendTo(staff);
			}
			return CompletableFuture.completedFuture(false);
		}
		
		// Permission : True
		if (value.get()) {
			EPMessages.OTHER_ADD_PERMISSION_TRUE.sender()
				.replace("<subject>", subject.getIdentifier())
				.replace("<permission>", permission)
				.sendTo(staff);
		// Permission : False
		} else {
			EPMessages.OTHER_ADD_PERMISSION_FALSE.sender()
				.replace("<subject>", subject.getIdentifier())
				.replace("<permission>", permission)
				.sendTo(staff);
		}
		return CompletableFuture.completedFuture(true);
	}
}
