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
package fr.evercraft.everpermissions.command.group;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.command.EParentSubCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupInheritance extends EParentSubCommand<EverPermissions> {
	
	private final EPGroup parent;
	
	public EPGroupInheritance(final EverPermissions plugin, final EPGroup parent) {
		super(plugin, parent, "inheritance");
		
		this.parent = parent;
		
		new EPGroupInheritanceAdd(this.plugin, this);
		new EPGroupInheritanceRemove(this.plugin, this);
		new EPGroupInheritanceInfo(this.plugin, this);
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_INHERITANCE_EXECUTE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EPMessages.GROUP_INHERITANCE_DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return true;
	}
	
	public EPGroup getParent() {
		return this.parent;
	}
}
