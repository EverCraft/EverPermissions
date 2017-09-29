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

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.file.EnumMessage;
import fr.evercraft.everpermissions.EPMessage.EPMessages;

public class GroupNotFoundWorldException extends EMessageException {
	private static final long serialVersionUID = 8556926768806244620L;
	
	private final CommandSource source;
	private final String world;
	
	public GroupNotFoundWorldException(final CommandSource source, final String world) {
		super();
		
		this.source = source;
		this.world = world;
	}
	
	public void execute(EnumMessage prefix) {
		EAMessages.WORLD_NOT_FOUND.sender()
			.prefix(EPMessages.PREFIX)
			.replace("{world}", world)
			.sendTo(this.source);
	}
}
