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

public class EPOtherListPerm extends ECommand<EverPermissions> {
	
	public EPOtherListPerm(final EverPermissions plugin) {
        super(plugin, "permolistp", "manolistp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.OTHER_LIST_PERMISSION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.OTHER_LIST_PERMISSION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permolistp <" + EAMessages.ARGS_SUBJECT.getString() + ">")
					.onClick(TextActions.suggestCommand("/permolistp "))
					.color(TextColors.RED)
					.build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.plugin.getService().getSuggestsOthers();
		}
		return Arrays.asList();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			/*Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le joueur existe
			if (optSubject.isPresent()){
				return this.command(source, optSubject.get());
			// Le joueur est introuvable
			} else {
				EPMessages.OTHER_NOT_FOUND.sender()
					.replace("<other>", args.get(0))
					.sendTo(source);
			}*/
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	/*
	private CompletableFuture<Boolean> command(final CommandSource staff, final Subject subject) {
		List<Text> list = new ArrayList<Text>();
		
		// La liste des permissions
		Map<String, Boolean> permissions = subject.getSubjectData().getPermissions(EContextCalculator.EMPTY);
		if (permissions.isEmpty()) {
			list.add(EPMessages.OTHER_LIST_PERMISSION_PERMISSION_EMPTY.getText());
		} else {
			list.add(EPMessages.OTHER_LIST_PERMISSION_PERMISSION.getText());
			for (Entry<String, Boolean> permission : permissions.entrySet()) {
				if (permission.getValue()) {
					list.add(EPMessages.OTHER_LIST_PERMISSION_PERMISSION_LINE_TRUE.getFormat()
								.toText("<permission>", permission.getKey()));
				} else {
					list.add(EPMessages.OTHER_LIST_PERMISSION_PERMISSION_LINE_FALSE.getFormat()
								.toText("<permission>", permission.getKey()));
				}
			}
		}
		
		// La liste des permissions temporaires
		permissions = subject.getTransientSubjectData().getPermissions(EContextCalculator.EMPTY);
		if (!permissions.isEmpty()) {
			list.add(EPMessages.OTHER_LIST_PERMISSION_TRANSIENT.getText());
			for (Entry<String, Boolean> permission : permissions.entrySet()) {
				if (permission.getValue()) {
					list.add(EPMessages.OTHER_LIST_PERMISSION_TRANSIENT_LINE_TRUE.getFormat()
								.toText("<permission>", permission.getKey()));
				} else {
					list.add(EPMessages.OTHER_LIST_PERMISSION_TRANSIENT_LINE_FALSE.getFormat()
								.toText("<permission>", permission.getKey()));
				}
			}
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.OTHER_LIST_PERMISSION_TITLE.getFormat()
					.toText("<subject>", subject.getIdentifier()), 
				list, staff);
		return CompletableFuture.completedFuture(true);
	}*/
}
