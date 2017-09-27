/*
 * This file is part of EverAPI.
 *
 * EverAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.everpermissions.exception;

import org.spongepowered.api.command.CommandSource;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.file.EnumMessage;
import fr.evercraft.everpermissions.EPMessage.EPMessages;

public class SubjectNotFoundException extends EMessageException {
	private static final long serialVersionUID = 8556926768806244620L;
	
	private final CommandSource source;
	private final String collection;
	private final String subject;
	
	public SubjectNotFoundException(final CommandSource source, final String collection, final String subject) {
		super();
		
		this.source = source;
		this.collection = collection;
		this.subject = subject;
	}
	
	public void execute(EnumMessage prefix) {
		EAMessages.SUBJECT_NOT_FOUND.sender()
			.prefix(EPMessages.PREFIX)
			.replace("{collection}", this.collection)
			.replace("{subject}", this.subject)
			.sendTo(this.source);
	}
}
