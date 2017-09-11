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
import java.util.concurrent.CompletableFuture;

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
		return Text.builder("/permodelo <" + EAMessages.ARGS_SUBJECT.getString() + "> "
									 + "<" + EAMessages.ARGS_OPTION.getString() + ">")
					.onClick(TextActions.suggestCommand("/permodelo "))
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
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 2) {
			/*Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le joueur existe
			if (optSubject.isPresent()){
				return this.command(source, optSubject.get(), args.get(1));
			// Le joueur est introuvable
			} else {
				EPMessages.OTHER_NOT_FOUND.sender()
					.replace("{other}", args.get(0))
					.sendTo(source);
			}*/
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	/*
	private CompletableFuture<Boolean> command(final CommandSource staff, final EOtherSubject subject, final String option) {
		// L'option n'a pas été supprimé
		if (!subject.getSubjectData().setOption(EContextCalculator.EMPTY, option, null)) {
			EPMessages.OTHER_DEL_OPTION_ERROR.sender()
				.replace("{subject}", subject.getIdentifier())
				.replace("{option}", option)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		EPMessages.OTHER_DEL_OPTION_PLAYER.sender()
			.replace("{subject}", subject.getIdentifier())
			.replace("{option}", option)
			.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}*/
}
