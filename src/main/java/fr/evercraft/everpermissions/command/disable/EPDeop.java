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
package fr.evercraft.everpermissions.command.disable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EverPermissions;

public class EPDeop extends ECommand<EverPermissions> {
	
	public EPDeop(final EverPermissions plugin) {
        super(plugin, "deop");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission("minecraft.command.deop");
	}

	public Text description(final CommandSource source) {
		return null;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/perms help").onClick(TextActions.suggestCommand("/perms help"))
					.color(TextColors.RED).build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		source.sendMessage(EPMessages.PREFIX.getText().concat(EPMessages.COMMAND_DEOP.getText()));
		return CompletableFuture.completedFuture(false);
	}
}
