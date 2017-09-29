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
package fr.evercraft.everpermissions.exception;

import org.spongepowered.api.command.CommandSource;

import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.file.EnumMessage;
import fr.evercraft.everpermissions.EPMessage.EPMessages;

public class GroupTypeWorldNotFoundException extends EMessageException {
	private static final long serialVersionUID = 8556926768806244620L;
	
	private final CommandSource source;
	private final String group;
	private final String typeWorld;
	
	public GroupTypeWorldNotFoundException(final CommandSource source, final String group, final String typeWorld) {
		super();
		
		this.source = source;
		this.group = group;
		this.typeWorld = typeWorld;
	}
	
	public void execute(EnumMessage prefix) {
		EPMessages.GROUP_NOT_FOUND_WORLD.sender()
			.replace("{group}", this.group)
			.replace("{type}", this.typeWorld)
			.sendTo(this.source);
	}
}
