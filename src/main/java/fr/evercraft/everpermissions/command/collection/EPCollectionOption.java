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
package fr.evercraft.everpermissions.command.collection;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.command.EParentSubCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPCollectionOption extends EParentSubCommand<EverPermissions> {
	
	private final EPCollection parent;
	
	public EPCollectionOption(final EverPermissions plugin, final EPCollection parent) {
		super(plugin, parent, "option");
		
		this.parent = parent;
		
		new EPCollectionOptionInfo(this.plugin, this);
		new EPCollectionOptionAdd(this.plugin, this);
		new EPCollectionOptionRemove(this.plugin, this);
		new EPCollectionOptionCheck(this.plugin, this);
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.COLLECTION_OPTION_EXECUTE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EPMessages.COLLECTION_OPTION_DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return true;
	}
	
	public EPCollection getParent() {
		return this.parent;
	}
}
