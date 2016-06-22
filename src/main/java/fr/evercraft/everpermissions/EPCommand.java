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
package fr.evercraft.everpermissions;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import fr.evercraft.everapi.plugin.command.EParentCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;

public class EPCommand extends EParentCommand<EverPermissions> {
	
	public EPCommand(final EverPermissions plugin) {
		super(plugin, "everpermissions", "permissions", "perms", "perm");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.EVERPERMISSIONS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EPMessages.DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EPPermissions.HELP.get());
	}
}